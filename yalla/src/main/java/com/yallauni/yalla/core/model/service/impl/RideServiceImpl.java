package com.yallauni.yalla.core.model.service.impl;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.repository.RideRepository;
import com.yallauni.yalla.core.model.service.RideService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of RideService.
 * Provides business logic for ride management including creation,
 * passenger management, and status updates.
 * Also uses RideRepository for database interactions.
 */
@Service
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;

    public RideServiceImpl(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    // Create a new ride with driver and vehicle, set defaults if needed
    @Override
    public Ride createRide(Ride ride, User driver, Vehicle vehicle) {
        
        if (ride == null || driver == null || vehicle == null) {
            throw new IllegalArgumentException("Ride, driver, and vehicle must not be null");
        }
        if (ride.getStartingPoint() == null || ride.getDestination() == null) {
            throw new IllegalArgumentException("Starting point and destination are required");
        }
        ride.setDriver(driver);
        ride.setVehicle(vehicle);

        // Set default status if not provided
        if (ride.getStatus() == null) {
            ride.setStatus(Ride.RideStatus.SCHEDULED);
        }
        // Set default price if not provided
        if (ride.getPrice() == null) {
            ride.setPrice(0.0);
        }

        return rideRepository.save(ride);
    }

    @Override
    public Optional<Ride> findById(Long id) {
        // Find ride by id
        if (id == null)
            throw new IllegalArgumentException("Ride ID must not be null");
        return rideRepository.findById(id);
    }

    @Override
    public List<Ride> findByDriver(User driver) {
        // Find all rides for a specific driver
        if (driver == null)
            throw new IllegalArgumentException("Driver must not be null");
        return rideRepository.findByDriver(driver);
    }

    @Override
    public List<Ride> findByPassenger(User passenger) {
        // Find all rides where a user is a passenger
        if (passenger == null)
            throw new IllegalArgumentException("Passenger must not be null");
        return rideRepository.findByPassengersContaining(passenger);
    }

    @Override
    public List<Ride> findAll() {
        return rideRepository.findAll();
    }

    @Override
    public List<Ride> findByStatus(Ride.RideStatus status) {
        if (status == null)
            throw new IllegalArgumentException("Status must not be null");
        return rideRepository.findByStatus(status);
    }


    @Override
    public Ride updateRide(Long id, Ride rideUpdate) {
        if (id == null || rideUpdate == null)
            throw new IllegalArgumentException("Ride and ID must not be null");

        Ride existing = rideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found with id: " + id));

        // Update only the provided fields, preserves driver/vehicle/passengers 
        if (rideUpdate.getStartingPoint() != null) {
            existing.setStartingPoint(rideUpdate.getStartingPoint());
        }
        if (rideUpdate.getDestination() != null) {
            existing.setDestination(rideUpdate.getDestination());
        }
        if (rideUpdate.getStatus() != null) {
            existing.setStatus(rideUpdate.getStatus());
        }
        
        return rideRepository.save(existing);
    }

    @Override
    public void deleteRide(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Ride ID must not be null");
        rideRepository.deleteById(id);
    }

    // Adds passenger to ride if not full and not already in the ride
    @Override
    @Transactional
    public boolean addPassenger(Long rideId, User passenger) {
        if (rideId == null || passenger == null)
            throw new IllegalArgumentException("Ride ID and passenger must not be null");
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();

            // Check if passenger is already in ride (by ID)
            boolean alreadyInRide = ride.getPassengers().stream()
                    .anyMatch(p -> p.getUserID().equals(passenger.getUserID()));
            if (alreadyInRide) {
                throw new IllegalStateException("Passenger already in ride");
            }

            if (ride.canAddPassenger()) {
                ride.getPassengers().add(passenger);
                rideRepository.save(ride);
                return true;
            } else {
                throw new IllegalStateException("Ride is full");
            }
        } else {
            throw new IllegalArgumentException("Ride not found");
        }
    }

    // Removes passenger from the ride given the ride's ID.
    @Override
    @Transactional
    public boolean removePassenger(Long rideId, User passenger) {
        if (rideId == null || passenger == null)
            throw new IllegalArgumentException("Ride ID and passenger must not be null");
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();

            // Find and remove passenger by ID
            boolean removed = ride.getPassengers().removeIf(p -> p.getUserID().equals(passenger.getUserID()));
            if (removed) {
                rideRepository.save(ride);
                return true;
            } else {
                throw new IllegalStateException("Passenger not in ride");
            }
        } else {
            throw new IllegalArgumentException("Ride not found");
        }
    }
}
