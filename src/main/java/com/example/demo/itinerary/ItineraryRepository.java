package com.example.demo.itinerary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    // Custom query method to find itineraries by trip ID
    List<Itinerary> findByTripId(Long tripId);

}
