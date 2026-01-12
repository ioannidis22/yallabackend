package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriver(User driver);

    List<Ride> findByPassengersContaining(User passenger);
}
