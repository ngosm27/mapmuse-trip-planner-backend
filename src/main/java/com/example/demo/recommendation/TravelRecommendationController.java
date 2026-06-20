package com.example.demo.recommendation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class TravelRecommendationController {
    private final TravelRecommendationService recommendationService;

    public TravelRecommendationController(TravelRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<TravelRecommendationResponse> generateForUser(@PathVariable Long userId, @RequestBody TravelRecommendationRequest request) {
        return ResponseEntity.ok(recommendationService.generateRecommendationForUser(userId, request));
    }
}
