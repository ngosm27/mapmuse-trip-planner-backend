package com.example.demo.itinerary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItineraryService {
    private final ItineraryRepository itineraryRepository;

    @Autowired
    public ItineraryService(ItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    public Itinerary createItinerary(Itinerary itinerary) {
        return itineraryRepository.save(itinerary);
    }

    public List<Itinerary> getItinerariesByTripId(Long tripId) {
        return itineraryRepository.findByTripId(tripId);
    }

    public Itinerary getItineraryById(Long id) {
        return itineraryRepository.findById(id).orElse(null);
    }

    public void deleteItinerary(Long id) {
        itineraryRepository.deleteById(id);
    }

    public Itinerary updateItinerary(Long id, Itinerary updatedItinerary) {
        return itineraryRepository.findById(id)
                .map(itinerary -> {
                    itinerary.setNotes(updatedItinerary.getNotes());
                    itinerary.setDate(updatedItinerary.getDate());

                    return itineraryRepository.save(itinerary);
                })
                .orElse(null);

    }

    public List<Itinerary> getAllItineraries() {
        return itineraryRepository.findAll();
    }
}
