package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;

public interface VehicleService {
    Vehicle registerVehicle(Vehicle vehicle, User driver);

    Optional<Vehicle> findById(Long id);

    List<Vehicle> findByDriver(User driver);

    List<Vehicle> findAll();

    Vehicle updateVehicle(Long id, Vehicle vehicle);

    void deleteVehicle(Long id);
}
