package com.example.demo.recommendation;

import java.time.LocalDate;
import java.util.List;

public class TravelRecommendationResponse {
    private Long tripId;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String overview;
    private List<ItineraryDetail> itinerary;

    public TravelRecommendationResponse() {}

    public TravelRecommendationResponse(
            Long tripId,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            String title,
            String overview,
            List<ItineraryDetail> itinerary) {
        this.tripId = tripId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.overview = overview;
        this.itinerary = itinerary;
    }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public List<ItineraryDetail> getItinerary() { return itinerary; }
    public void setItinerary(List<ItineraryDetail> itinerary) { this.itinerary = itinerary; }
}

class ItineraryDetail {
    private Long itineraryId;
    private Integer dayNumber;
    private String title;
    private String description;
    private List<ActivityDetail> activities;

    public ItineraryDetail() {}

    public ItineraryDetail(
            Long itineraryId,
            Integer dayNumber,
            String title,
            String description,
            List<ActivityDetail> activities) {
        this.itineraryId = itineraryId;
        this.dayNumber = dayNumber;
        this.title = title;
        this.description = description;
        this.activities = activities;
    }

    public Long getItineraryId() { return itineraryId; }
    public void setItineraryId(Long itineraryId) { this.itineraryId = itineraryId; }

    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ActivityDetail> getActivities() { return activities; }
    public void setActivities(List<ActivityDetail> activities) { this.activities = activities; }
}

class ActivityDetail {
    private Long activityId;
    private String name;
    private String startTime;
    private String endTime;
    private String description;

    public ActivityDetail() {}

    public ActivityDetail(Long activityId, String name, String startTime, String endTime, String description) {
        this.activityId = activityId;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
