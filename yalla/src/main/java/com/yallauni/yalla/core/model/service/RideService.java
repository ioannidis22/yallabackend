package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.dto.ride.RideCreateDTO;
import com.yallauni.yalla.dto.ride.RideResponseDTO;

public interface RideService {
    RideResponseDTO createRide(RideCreateDTO dto, Long driverId, Long vehicleId);


    Optional<Ride> findById(Long id);

    List<Ride> findByDriver(User driver);

    List<Ride> findByPassenger(User passenger);

    List<Ride> findAll();

    Ride updateRide(Long id, Ride ride);

    void deleteRide(Long id);

    boolean addPassenger(Long rideId, User passenger);

    boolean removePassenger(Long rideId, User passenger);
}
