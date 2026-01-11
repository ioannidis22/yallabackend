package com.yallauni.yalla.service.impl;

import com.yallauni.yalla.model.Ride;
import com.yallauni.yalla.model.User;
import com.yallauni.yalla.model.Vehicle;
import com.yallauni.yalla.repository.RideRepository;
import com.yallauni.yalla.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (ride == null || driver == null || vehicle == null) {
            throw new IllegalArgumentException("Ride, driver, and vehicle must not be null");
        }
        if (ride.getStartingPoint() == null || ride.getDestination() == null) {
            throw new IllegalArgumentException("Starting point and destination are required");
        }
        ride.setDriver(driver);
        ride.setVehicle(vehicle);
        return rideRepository.save(ride);
    }

    @Override
    public Optional<Ride> findById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Ride ID must not be null");
        return rideRepository.findById(id);
    }

    @Override
    public List<Ride> findByDriver(User driver) {
        if (driver == null)
            throw new IllegalArgumentException("Driver must not be null");
        return rideRepository.findByDriver(driver);
    }

    @Override
    public List<Ride> findByPassenger(User passenger) {
        if (passenger == null)
            throw new IllegalArgumentException("Passenger must not be null");
        return rideRepository.findByPassengersContaining(passenger);
    }

    @Override
    public List<Ride> findAll() {
        return rideRepository.findAll();
    }

    @Override
    public Ride updateRide(Long id, Ride ride) {
        if (id == null || ride == null)
            throw new IllegalArgumentException("Ride and ID must not be null");
        ride.setRideId(id);
        return rideRepository.save(ride);
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
