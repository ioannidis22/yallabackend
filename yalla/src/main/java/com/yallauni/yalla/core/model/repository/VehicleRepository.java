package com.yallauni.yalla.core.model.repository;

// Spring Data JPA base repository (already commented elsewhere)
import org.springframework.data.jpa.repository.JpaRepository;

// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Vehicle entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Find all vehicles for a specific driver
    List<Vehicle> findByDriver(User driver);
}
