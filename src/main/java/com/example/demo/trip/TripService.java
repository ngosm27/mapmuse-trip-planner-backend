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

    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }

    public void addTrip(Trip trip) {
        tripRepository.save(trip);
    }

    public Trip updateTrip(Long id, Trip updatedTrip) {
        return tripRepository.findById(id)
                .map(trip -> {
                    trip.setName(updatedTrip.getName());
                    trip.setDestination(updatedTrip.getDestination());
                    trip.setStartDate(updatedTrip.getStartDate());
                    trip.setEndDate(updatedTrip.getEndDate());
                    return tripRepository.save(trip);
                })
                .orElse(null);
    }

    public void deleteTrip(Long id) {
        boolean exist = tripRepository.existsById(id);
        if (!exist) {
            throw new IllegalStateException("Trip not found");
        }
        tripRepository.deleteById(id);
    }

}
