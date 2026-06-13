package com.example.demo.recommendation;

import com.example.demo.recommendation.dto.TravelRecommendationResponse;
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
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TravelRecommendationService {
    private static final Schema ACTIVITY_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of(
                    "name", stringSchema(),
                    "category", stringSchema(),
                    "description", stringSchema()))
            .required("name", "category", "description")
            .build();

    private static final Schema DESTINATION_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of(
                    "city", stringSchema(),
                    "country", stringSchema(),
                    "reason", stringSchema(),
                    "activities", Schema.builder()
                            .type(Type.Known.ARRAY)
                            .minItems(5L)
                            .maxItems(5L)
                            .items(ACTIVITY_SCHEMA)
                            .build(),
                    "budgetNote", stringSchema(),
                    "bestTimeToVisit", stringSchema()))
            .required(
                    "city",
                    "country",
                    "reason",
                    "activities",
                    "budgetNote",
                    "bestTimeToVisit")
            .build();

    private static final Schema RESPONSE_SCHEMA = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of(
                    "destinations", Schema.builder()
                            .type(Type.Known.ARRAY)
                            .minItems(3L)
                            .maxItems(3L)
                            .items(DESTINATION_SCHEMA)
                            .build()))
            .required("destinations")
            .build();

    private final String apiKey;
    private final String model;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TravelRecommendationService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model,
            UserRepository userRepository) {
        this.apiKey = apiKey;
        this.model = model;
        this.userRepository = userRepository;
    }

    public TravelRecommendationResponse generateRecommendationForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));

        UserPreferences preferences = user.getPreferences();
        if (preferences == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User has not completed travel preferences");
        }

        return generateFromPreferences(preferences);
    }

    private TravelRecommendationResponse generateFromPreferences(
            UserPreferences preferences) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "GOOGLE_API_KEY is not configured");
        }

        try {
            String preferencesJson =
                    objectMapper.writeValueAsString(preferences);
            String prompt = buildPrompt(preferencesJson);

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .responseSchema(RESPONSE_SCHEMA)
                    .candidateCount(1)
                    .build();

            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentResponse response =
                    client.models.generateContent(model, prompt, config);

            String responseJson = response.text();
            if (responseJson == null || responseJson.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Gemini returned an empty response");
            }

            return objectMapper.readValue(
                    responseJson,
                    TravelRecommendationResponse.class);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini returned invalid recommendation JSON",
                    exception);
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini request failed",
                    exception);
        }
    }

    private String buildPrompt(String preferencesJson) {
        return """
                You are a travel recommendation assistant.

                Recommend exactly one destination based on the traveler preferences.
                For each destination, explain why it matches and provide exactly three
                specific activities. Keep the recommendations practical and concise.
                Do not invent exact prices, opening hours, or claims that require live
                verification.

                Traveler preferences JSON:
                %s
                """.formatted(preferencesJson);
    }

    private static Schema stringSchema() {
        return Schema.builder()
                .type(Type.Known.STRING)
                .build();
    }
}
