package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;

/**
 * Service interface for vehicle-related operations.
 * Defines business logic for registering and managing driver vehicles.
 * Implemented by VehicleServiceImpl.
 */
public interface VehicleService {
    // Registers a new vehicle for a driver
    Vehicle registerVehicle(Vehicle vehicle, User driver);

    // Finds vehicle by id
    Optional<Vehicle> findById(Long id);

    // Finds all vehicles for a specific driver
    List<Vehicle> findByDriver(User driver);

    // Returns all vehicles
    List<Vehicle> findAll();

    // Updates vehicle fields
    Vehicle updateVehicle(Long id, Vehicle vehicle);

    // Deletes vehicle by id
    void deleteVehicle(Long id);
}
