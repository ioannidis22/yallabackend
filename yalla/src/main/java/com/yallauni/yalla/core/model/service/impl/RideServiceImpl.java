package com.yallauni.yalla.core.model.service.impl;

// Ride entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Vehicle entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Vehicle;
// Repository for ride data (already commented elsewhere)
import com.yallauni.yalla.core.model.repository.RideRepository;
// Ride service interface
import com.yallauni.yalla.core.model.service.RideService;

// Marks this class as a Spring service
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;

    public RideServiceImpl(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Override
    public Ride createRide(Ride ride, User driver, Vehicle vehicle) {
        // Create a new ride with driver and vehicle, set defaults if needed
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
            ride.setStatus(Ride.RideStatus.REQUESTED);
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
    public Ride updateRide(Long id, Ride rideUpdate) {
        if (id == null || rideUpdate == null)
            throw new IllegalArgumentException("Ride and ID must not be null");

        Ride existing = rideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found with id: " + id));

        // Update only the provided fields, preserve driver/vehicle/passengers
        if (rideUpdate.getStartingPoint() != null) {
            existing.setStartingPoint(rideUpdate.getStartingPoint());
        }
        if (rideUpdate.getDestination() != null) {
            existing.setDestination(rideUpdate.getDestination());
        }
        // Driver, vehicle, and passengers remain unchanged

        return rideRepository.save(existing);
    }

    @Override
    public void deleteRide(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Ride ID must not be null");
        rideRepository.deleteById(id);
    }

    @Override
    public boolean addPassenger(Long rideId, User passenger) {
        if (rideId == null || passenger == null)
            throw new IllegalArgumentException("Ride ID and passenger must not be null");
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            if (ride.canAddPassenger()) {
                if (ride.getPassengers().contains(passenger)) {
                    throw new IllegalStateException("Passenger already in ride");
                }
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

    @Override
    public boolean removePassenger(Long rideId, User passenger) {
        if (rideId == null || passenger == null)
            throw new IllegalArgumentException("Ride ID and passenger must not be null");
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            if (ride.getPassengers().remove(passenger)) {
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
