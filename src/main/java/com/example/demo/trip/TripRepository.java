package com.example.demo.trip;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByOwnerId(Long ownerId);

    // check if a trip belongs to a specific user (useful for ownership validation)
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
