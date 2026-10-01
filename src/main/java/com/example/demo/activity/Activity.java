package com.example.demo.activity;

import java.time.LocalTime;

import com.example.demo.itinerary.Itinerary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityName;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private String category; // FOOD, SIGHTSEEING, TRANSPORT, etc.
    @Column(length = 2048)
    private String websiteUrl; // Official website URL for the activity

    @ManyToOne
    private Itinerary itinerary;

    public Activity() {
    }

    public Activity(String activityName, String location, LocalTime startTime, LocalTime endTime, String category,
            String websiteUrl) {
        this.activityName = activityName;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.websiteUrl = websiteUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public void setStartTime(LocalTime starttTime) {
        this.startTime = starttTime;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public Itinerary getItinerary() {
        return this.itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        System.out.println("Setting website URL: " + websiteUrl);
        this.websiteUrl = websiteUrl;
    }

}
