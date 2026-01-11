package com.yallauni.yalla.repository;

import com.yallauni.yalla.model.Vehicle;
import com.yallauni.yalla.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByDriver(User driver);
}
