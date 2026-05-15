package com.example.demo.itinerary;

import com.example.demo.activity.Activity;
import com.example.demo.trip.*;

import java.time.LocalDate;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate date;
    private String notes;

    @ManyToOne
    private Trip trip;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL)
    private List<Activity> activities;

    public Itinerary(Long id, String name, LocalDate date, String notes) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.notes = notes;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public Long getID() {
        return this.id;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return this.activities;
    }

}
