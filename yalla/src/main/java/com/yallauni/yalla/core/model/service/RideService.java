package com.yallauni.yalla.core.model.service;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface RideService {

    Ride createRide(Ride ride, User driver, Vehicle vehicle);

    Optional<Ride> findById(Long id);

    List<Ride> findByDriver(User driver);

    List<Ride> findByPassenger(User passenger);

    List<Ride> findAll();

    Ride updateRide(Long id, Ride ride);

    void deleteRide(Long id);

    boolean addPassenger(Long rideId, User passenger);

    boolean removePassenger(Long rideId, User passenger);
}
