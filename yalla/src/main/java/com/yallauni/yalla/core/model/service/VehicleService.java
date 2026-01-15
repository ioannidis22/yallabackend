package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Vehicle entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Vehicle;

public interface VehicleService {
    // Register a new vehicle for a driver
    Vehicle registerVehicle(Vehicle vehicle, User driver);

    // Find vehicle by id
    Optional<Vehicle> findById(Long id);

    // Find all vehicles for a specific driver
    List<Vehicle> findByDriver(User driver);

    // Return all vehicles
    List<Vehicle> findAll();

    // Update vehicle fields
    Vehicle updateVehicle(Long id, Vehicle vehicle);

    // Delete vehicle by id
    void deleteVehicle(Long id);
}
