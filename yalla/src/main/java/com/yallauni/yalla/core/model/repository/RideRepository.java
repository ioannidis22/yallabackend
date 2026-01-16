package com.yallauni.yalla.core.model.repository;

// Spring Data JPA base repository (already commented elsewhere)
import org.springframework.data.jpa.repository.JpaRepository;

// Ride entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    // Find all rides for a specific driver
    List<Ride> findByDriver(User driver);

    // Find all rides where a user is a passenger
    List<Ride> findByPassengersContaining(User passenger);

    // Find all rides by status
    List<Ride> findByStatus(Ride.RideStatus status);

    // Count rides by status
    long countByStatus(Ride.RideStatus status);
}
