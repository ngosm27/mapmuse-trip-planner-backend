package com.example.demo.trip;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }

    public Trip addTrip(Trip trip) {
        Long ownerId = trip.getOwner().getId(); // get the owner id from the hollow User object
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));
        trip.setOwner(owner); // replace the hollow User with the real fetched one
        return tripRepository.save(trip);

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

    public List<Trip> getTripsByUserId(Long userId) {
        return tripRepository.findByUserId(userId);
    }

}
