package com.example.demo.trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.itinerary.Itinerary;
import com.example.demo.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class Trip {
    @Id
    @SequenceGenerator(name = "trip_sequence", sequenceName = "trip_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trip_sequence")
    private Long id;

    private String name;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Itinerary> itineraries = new ArrayList<>();

    public Trip() {

    }

    public Trip(Long id, String name, String destination, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trip(String name, String destination, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ID: " + this.id +
                " Name: " + this.name +
                " Destination: " + this.destination +
                " Start Date: " + this.startDate +
                " End Date: " + this.endDate;
    }
}