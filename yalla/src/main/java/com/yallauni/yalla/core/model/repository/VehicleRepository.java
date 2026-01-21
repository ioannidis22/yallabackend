package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;

import java.util.List;

/**
 * Repository interface for Vehicle model database operations.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 * Used by VehicleService for vehicle data access.
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Find all vehicles for a driver.
    List<Vehicle> findByDriver(User driver);
}
