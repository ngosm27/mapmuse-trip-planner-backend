package com.example.demo.recommendation;

import java.time.LocalDate;

public class TravelRecommendationRequest {
    LocalDate startDate;
    LocalDate endDate;
    String destination;

    public LocalDate getStartDate(){
        return this.startDate;
    }

    public LocalDate getEndDate(){
        return this.endDate;
    }

    public String getDestination(){
        return this.destination;
    }
}
