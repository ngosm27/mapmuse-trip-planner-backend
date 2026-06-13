package com.example.demo.recommendation.dto;

import java.util.List;

public record TravelRecommendationResponse(
        List<DestinationRecommendation> destinations) {
}
