package com.example.demo.recommendation.dto;

import java.util.List;

public record DestinationRecommendation(
        String city,
        String country,
        String reason,
        List<ActivityRecommendation> activities,
        String budgetNote,
        String bestTimeToVisit) {
}
