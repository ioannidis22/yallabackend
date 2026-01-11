package com.yallauni.yalla.service;

import com.yallauni.yalla.model.Vehicle;
import com.yallauni.yalla.model.User;
import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Vehicle registerVehicle(Vehicle vehicle, User driver);

    Optional<Vehicle> findById(Long id);

    List<Vehicle> findByDriver(User driver);

    List<Vehicle> findAll();

    Vehicle updateVehicle(Long id, Vehicle vehicle);

    void deleteVehicle(Long id);
}
