package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByDriver(User driver);
}
