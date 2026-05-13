package com.example.demo.trip;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {
    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    public void addTrip(Trip trip) {

        System.out.println(trip);
    }

    public void deleteTrip(Long id) {
        boolean exist = tripRepository.existsById(id);
        if (!exist) {
            throw new IllegalStateException("ID does not exist");
        }
        tripRepository.deleteById(id);
    }

}
