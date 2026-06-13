package com.example.demo.recommendation;

import com.example.demo.recommendation.dto.TravelRecommendationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final TravelRecommendationService recommendationService;

    public RecommendationController(TravelRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<TravelRecommendationResponse> generateForUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                recommendationService.generateRecommendationForUser(userId));
    }
}
