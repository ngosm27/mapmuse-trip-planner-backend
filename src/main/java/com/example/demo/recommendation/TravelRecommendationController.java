package com.example.demo.recommendation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class TravelRecommendationController {
    private final TravelRecommendationService recommendationService;

    public TravelRecommendationController(TravelRecommendationService recommendationService) {
        System.out.println("========== RECOMMENDATION CONTROLLER HIT ==========");
        this.recommendationService = recommendationService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<TravelRecommendationResponse> generateForUser(@PathVariable Long userId,
            @RequestBody TravelRecommendationRequest request) {

        System.out.println("Generating recommendation for user: " + userId);
        return ResponseEntity.ok(recommendationService.generateRecommendationForUser(userId,
                request));
    }

    // @PostMapping("/{userId}")
    // public ResponseEntity<TravelRecommendationResponse> generateForUser(
    // @PathVariable Long userId,
    // @RequestBody TravelRecommendationRequest request) {

    // System.out.println("BEFORE SERVICE CALL");

    // TravelRecommendationResponse result =
    // recommendationService.generateRecommendationForUser(userId, request);

    // System.out.println("AFTER SERVICE CALL");

    // return ResponseEntity.ok(result);
    // }
    // @PostMapping("/{userId}")
    // public ResponseEntity<String> generateForUser(
    // @PathVariable Long userId) {

    // System.out.println("========== GENERATE ENDPOINT HIT ==========");
    // System.out.println("User ID: " + userId);

    // return ResponseEntity.ok("User ID received: " + userId);
    // }

    @PostMapping("/test")
    public ResponseEntity<String> testPost(
            @RequestBody(required = false) String body) {

        System.out.println("========== POST TEST HIT ==========");
        System.out.println("Body: " + body);

        return ResponseEntity.ok("POST works");
    }
}
