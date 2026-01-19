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

    // Find rides by driver and status
    List<Ride> findByDriverAndStatus(User driver, Ride.RideStatus status);

    // Count completed rides by driver
    long countByDriverAndStatus(User driver, Ride.RideStatus status);

    // Find rides by driver and departure time range (for date filtering)
    List<Ride> findByDriverAndDepartureTimeBetween(User driver, java.time.LocalDateTime start,
            java.time.LocalDateTime end);

    // Find rides by driver ordered by departure time
    List<Ride> findByDriverOrderByDepartureTimeDesc(User driver);

    // ==================== PASSENGER SEARCH QUERIES ====================

    // Find available rides (SCHEDULED status) ordered by departure time
    List<Ride> findByStatusOrderByDepartureTimeAsc(Ride.RideStatus status);

    // Search by destination (case-insensitive partial match)
    List<Ride> findByStatusAndDestinationContainingIgnoreCase(Ride.RideStatus status, String destination);

    // Search by origin (case-insensitive partial match)
    List<Ride> findByStatusAndStartingPointContainingIgnoreCase(Ride.RideStatus status, String origin);

    // Search by origin and destination
    List<Ride> findByStatusAndStartingPointContainingIgnoreCaseAndDestinationContainingIgnoreCase(
            Ride.RideStatus status, String origin, String destination);

    // Search by departure time range
    List<Ride> findByStatusAndDepartureTimeBetween(Ride.RideStatus status,
            java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Search by destination and departure time range
    List<Ride> findByStatusAndDestinationContainingIgnoreCaseAndDepartureTimeBetween(
            Ride.RideStatus status, String destination,
            java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Search by origin, destination, and departure time range
    List<Ride> findByStatusAndStartingPointContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDepartureTimeBetween(
            Ride.RideStatus status, String origin, String destination,
            java.time.LocalDateTime start, java.time.LocalDateTime end);
}
