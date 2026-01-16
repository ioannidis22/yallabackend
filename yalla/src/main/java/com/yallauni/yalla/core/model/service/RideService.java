package com.yallauni.yalla.core.model.service;

// Ride entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Vehicle entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface RideService {

    // Create a new ride for a driver and vehicle
    Ride createRide(Ride ride, User driver, Vehicle vehicle);

    // Find ride by id
    Optional<Ride> findById(Long id);

    // Find all rides for a specific driver
    List<Ride> findByDriver(User driver);

    // Find all rides where a user is a passenger
    List<Ride> findByPassenger(User passenger);

    // Return all rides
    List<Ride> findAll();

    // Find all rides by status
    List<Ride> findByStatus(Ride.RideStatus status);

    // Update ride fields
    Ride updateRide(Long id, Ride ride);

    // Delete ride by id
    void deleteRide(Long id);

    // Add a passenger to a ride
    boolean addPassenger(Long rideId, User passenger);

    // Remove a passenger from a ride
    boolean removePassenger(Long rideId, User passenger);
}
