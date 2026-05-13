package com.example.demo.trip;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/trip")
public class TripController {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;

    }

    @GetMapping
    public List<Trip> getTrips() {
        return tripService.getTrips();
    }

    @PostMapping
    public void newTrip(@RequestBody Trip trip) {
        tripService.addTrip(trip);
    }

    @DeleteMapping(path = "{tripId}")
    public void deleteTrip(@PathVariable("tripId") Long id) {
        tripService.deleteTrip(id);
    }
}
