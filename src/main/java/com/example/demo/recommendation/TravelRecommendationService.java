package com.example.demo.recommendation;

import com.example.demo.activity.Activity;
import com.example.demo.activity.ActivityRepository;
import com.example.demo.itinerary.Itinerary;
import com.example.demo.itinerary.ItineraryRepository;
import com.example.demo.trip.Trip;
import com.example.demo.trip.TripRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserPreferences;
import com.example.demo.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TravelRecommendationService {

    private final String apiKey;
    private final String model;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final ActivityRepository activityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TravelRecommendationService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model,
            UserRepository userRepository,
            TripRepository tripRepository,
            ItineraryRepository itineraryRepository,
            ActivityRepository activityRepository
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public TravelRecommendationResponse generateRecommendationForUser(Long userId, TravelRecommendationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserPreferences preferences = user.getPreferences();
        if (preferences == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not completed travel preferences");
        }

        if (request.getStartDate() == null || request.getEndDate() == null || request.getDestination() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is missing start date, end date or destination");
        }

        // Generate itinerary from Gemini
        GeneratedItinerary generatedItinerary = generateFromGemini(preferences, request);

        // Save to database and return response
        return saveItineraryToDatabase(user, request, generatedItinerary);
    }

    private GeneratedItinerary generateFromGemini(UserPreferences preferences, TravelRecommendationRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GOOGLE_API_KEY is not configured");
        }

        try {
            String preferencesJson = objectMapper.writeValueAsString(preferences);
            String prompt = buildPrompt(preferencesJson, request);

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    // Schema enforcement not working reliably, relying on prompt instead
                    .candidateCount(1)
                    .build();

            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentResponse response = client.models.generateContent(model, prompt, config);

            String responseJson = response.text();
            if (responseJson == null || responseJson.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini returned an empty response");
            }

            GeneratedItinerary itinerary = objectMapper.readValue(responseJson, GeneratedItinerary.class);

            // FALLBACK: If Gemini didn't return days, generate default structure
            if (itinerary.getDays() == null || itinerary.getDays().isEmpty()) {
                System.out.println("Days missing from Gemini response. Generating default day structure...");
                itinerary.setDays(generateDefaultDays(preferences, request));
            }

            return itinerary;
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini returned invalid recommendation JSON", exception);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini request failed", exception);
        }
    }

    @Transactional
    private TravelRecommendationResponse saveItineraryToDatabase(
            User user,
            TravelRecommendationRequest request,
            GeneratedItinerary generatedItinerary) {

        // Create Trip
        Trip trip = new Trip();
        trip.setName(request.getDestination() + " Trip");
        trip.setDestination(request.getDestination());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setOwner(user);
        trip = tripRepository.save(trip);

        List<ItineraryDetail> savedDays = new ArrayList<>();

        // Create Itinerary entries for each day
        if (generatedItinerary.getDays() != null && !generatedItinerary.getDays().isEmpty()) {
            System.out.println("Processing " + generatedItinerary.getDays().size() + " days");
            for (DayPlan dayPlan : generatedItinerary.getDays()) {
                Itinerary itinerary = new Itinerary();
                itinerary.setTrip(trip);
                itinerary.setDate(request.getStartDate().plusDays(dayPlan.getDay() - 1));
                itinerary.setNotes(dayPlan.getDescription());
                itinerary = itineraryRepository.save(itinerary);

                List<ActivityDetail> savedActivities = new ArrayList<>();

                // Create Activities for each itinerary entry
                if (dayPlan.getActivities() != null) {
                    for (int i = 0; i < dayPlan.getActivities().size(); i++) {
                        ActivityItem activityItem = dayPlan.getActivities().get(i);
                        Activity activity = new Activity();
                        activity.setItinerary(itinerary);
                        activity.setActivityName(activityItem.getName());
                        activity.setLocation(activityItem.getLocation() != null && !activityItem.getLocation().isBlank()
                                ? activityItem.getLocation()
                                : ""); // Use location from Gemini if provided
                        activity.setStartTime(parseStringToLocalTime(activityItem.getTime()));
                        activity.setEndTime(calculateEndTime(activityItem.getTime(), activityItem.getDuration()));
                        activity.setCategory(activityItem.getCategory() != null && !activityItem.getCategory().isBlank()
                                ? activityItem.getCategory()
                                : "Other"); // Use category from Gemini, default to "Other"
                        activity = activityRepository.save(activity);

                        savedActivities.add(new ActivityDetail(
                                activity.getId(),
                                activity.getActivityName(),
                                activity.getStartTime().toString(),
                                activity.getEndTime().toString(),
                                activityItem.getDescription()
                        ));
                    }
                }

                savedDays.add(new ItineraryDetail(
                        itinerary.getId(),
                        dayPlan.getDay(),
                        dayPlan.getTitle(),
                        itinerary.getNotes(),
                        savedActivities
                ));
            }
        } else {
            System.out.println("WARNING: Days list is null or empty!");
        }

        return new TravelRecommendationResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getName(),
                generatedItinerary.getOverview(),
                savedDays
        );
    }

    /**
     * Converts time string (HH:mm) to LocalTime
     */
    private LocalTime parseStringToLocalTime(String timeString) {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(timeString, timeFormatter);
        } catch (Exception e) {
            return LocalTime.of(9, 0); // Default to 9:00 AM if parsing fails
        }
    }

    /**
     * Calculates end time based on start time and duration
     * Duration format expected: "1.5 hours", "30 minutes", "2 hours", etc.
     */
    private LocalTime calculateEndTime(String startTime, String duration) {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime start = LocalTime.parse(startTime, timeFormatter);

            // Parse duration (e.g., "1.5 hours", "30 minutes")
            int minutes = parseDurationToMinutes(duration);
            return start.plusMinutes(minutes);
        } catch (Exception e) {
            // If parsing fails, return 2 hours after start time
            return parseStringToLocalTime(startTime).plusHours(2);
        }
    }

    private int parseDurationToMinutes(String duration) {
        if (duration == null || duration.isBlank()) return 120; // Default 2 hours

        duration = duration.toLowerCase().trim();

        if (duration.contains("hour")) {
            try {
                String[] parts = duration.split(" ");
                double hours = Double.parseDouble(parts[0]);
                return (int) (hours * 60);
            } catch (Exception e) {
                return 120;
            }
        } else if (duration.contains("minute")) {
            try {
                String[] parts = duration.split(" ");
                return Integer.parseInt(parts[0]);
            } catch (Exception e) {
                return 60;
            }
        }

        return 120; // Default fallback
    }

    /**
     * Generates default day structure if Gemini fails to provide days
     * This serves as a fallback to create at least a basic itinerary
     */
    private List<DayPlan> generateDefaultDays(UserPreferences preferences, TravelRecommendationRequest request) {
        List<DayPlan> days = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();
        long dayCount = java.time.temporal.ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        for (int i = 1; i <= dayCount; i++) {
            DayPlan day = new DayPlan();
            day.setDay(i);
            day.setTitle("Day " + i + " - Explore " + request.getDestination());
            day.setDescription("Discover the highlights of " + request.getDestination() +
                    ". This day includes cultural experiences, dining, and local attractions suited to your preferences.");

            List<ActivityItem> activities = new ArrayList<>();

            // Generate sample activities for the day
            if (i == 1) {
                activities.add(new ActivityItem("Arrival & Check-in", "14:00", "2 hours",
                        "Arrive at your destination and check into your accommodation", "Hotel/Airport", "Accommodation"));
                activities.add(new ActivityItem("Local Orientation Walk", "16:30", "1.5 hours",
                        "Take a leisurely walk around the neighborhood to get oriented", "City Center", "Sightseeing"));
                activities.add(new ActivityItem("Welcome Dinner", "19:00", "2 hours",
                        "Enjoy dinner at a recommended local restaurant", "Downtown Area", "Dining"));
            } else if (i == dayCount) {
                activities.add(new ActivityItem("Last Minute Shopping", "09:00", "2 hours",
                        "Browse local shops for souvenirs and gifts", "Shopping District", "Shopping"));
                activities.add(new ActivityItem("Lunch", "12:00", "1.5 hours",
                        "Enjoy lunch at a favorite spot", "City Center", "Dining"));
                activities.add(new ActivityItem("Depart for Airport", "15:00", "1 hour",
                        "Travel to airport for departure", "Airport", "Transportation"));
            } else {
                activities.add(new ActivityItem("Breakfast", "09:00", "1 hour",
                        "Start your day with a local breakfast", "Hotel/Café", "Dining"));
                activities.add(new ActivityItem("Main Activity", "10:30", "3 hours",
                        "Explore major attractions and landmarks of " + request.getDestination(), "City Center", "Sightseeing"));
                activities.add(new ActivityItem("Lunch", "13:30", "1.5 hours",
                        "Enjoy lunch at a local restaurant", "Downtown", "Dining"));
                activities.add(new ActivityItem("Afternoon Activity", "15:00", "2 hours",
                        "Visit museums, markets, or hidden gems", "Various Neighborhoods", "Sightseeing"));
                activities.add(new ActivityItem("Dinner", "19:00", "2 hours",
                        "Experience local or international dining", "Downtown", "Dining"));
            }

            day.setActivities(activities);
            days.add(day);
            currentDate = currentDate.plusDays(1);
        }

        System.out.println("Generated " + days.size() + " default days for " + dayCount + " day trip");
        return days;
    }

    private String buildPrompt(String preferencesJson, TravelRecommendationRequest request) {
        return """
                You are a travel recommendation assistant. Your response MUST be valid JSON with the exact structure specified.
                
                IMPORTANT: You MUST include a "days" array in your response. Each day must have: day number, title, description, and activities array.
                
                Trip Details:
                - Destination: %s
                - Start Date: %s
                - End Date: %s
                - Traveler Preferences: %s
                
                Requirements:
                1. Create exactly one day plan for each day of the trip (if trip is 3 days, create 3 day objects)
                2. Each day MUST have: day (integer 1,2,3...), title (string), description (string), activities (array)
                3. Include 4-6 activities per day with specific times in HH:mm format
                4. Keep recommendations practical and feasible
                5. Do not invent exact prices, opening hours, or claims requiring live verification
                6. Provide helpful descriptions for each activity
                7. Consider the traveler's preferences (budget, interests, pace, etc.)
                8. Activities should be realistic and in chronological order
                9. For each activity, MUST include location (specific place name or area) and category (type of activity)
                
                Activity Categories: Dining, Accommodation, Museum, Sightseeing, Shopping, Class/Workshop, Nightlife, Transportation, Spa/Wellness, Other
                
                RESPONSE FORMAT (REQUIRED):
                {
                  "overview": "string with trip summary",
                  "days": [
                    {
                      "day": 1,
                      "title": "Day title",
                      "description": "Day description",
                      "activities": [
                        {
                          "name": "activity name",
                          "time": "HH:mm",
                          "duration": "X hours" or "X minutes",
                          "description": "activity details",
                          "location": "specific place name or neighborhood",
                          "category": "Dining|Accommodation|Museum|Sightseeing|Shopping|Class|Nightlife|Transportation|Spa|Other"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(
                request.getDestination(),
                request.getStartDate(),
                request.getEndDate(),
                preferencesJson
        );
    }

    private Schema buildResponseSchema() {
        // Build activity schema
        Schema activitySchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "name", stringSchema(),
                        "time", stringSchema(),
                        "duration", stringSchema(),
                        "description", stringSchema()
                ))
                .build();

        // Build day schema with activities array
        Schema daySchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "day", Schema.builder().type(Type.Known.INTEGER).build(),
                        "title", stringSchema(),
                        "description", stringSchema(),
                        "activities", Schema.builder()
                                .type(Type.Known.ARRAY)
                                .items(activitySchema)
                                .build()
                ))
                .build();

        // Build root schema with overview and days array
        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "overview", stringSchema(),
                        "days", Schema.builder()
                                .type(Type.Known.ARRAY)
                                .items(daySchema)
                                .build()
                ))
                .build();
    }

    private static Schema stringSchema() {
        return Schema.builder()
                .type(Type.Known.STRING)
                .build();
    }

    // ==================== Inner Classes ====================

    public static class GeneratedItinerary {
        private String overview;
        private List<DayPlan> days;

        public GeneratedItinerary() {}

        public GeneratedItinerary(String overview, List<DayPlan> days) {
            this.overview = overview;
            this.days = days;
        }

        public String getOverview() { return overview; }
        public void setOverview(String overview) { this.overview = overview; }
        public List<DayPlan> getDays() { return days; }
        public void setDays(List<DayPlan> days) { this.days = days; }
    }

    public static class DayPlan {
        private Integer day;
        private String title;
        private String description;
        private List<ActivityItem> activities;

        public DayPlan() {}

        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<ActivityItem> getActivities() { return activities; }
        public void setActivities(List<ActivityItem> activities) { this.activities = activities; }
    }

    public static class ActivityItem {
        private String name;
        private String time;
        private String duration;
        private String description;
        private String location;
        private String category;

        public ActivityItem() {}

        public ActivityItem(String name, String time, String duration, String description) {
            this(name, time, duration, description, "", "Activity");
        }

        public ActivityItem(String name, String time, String duration, String description, String location, String category) {
            this.name = name;
            this.time = time;
            this.duration = duration;
            this.description = description;
            this.location = location;
            this.category = category;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}