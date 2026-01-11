package com.yallauni.yalla.repository;

import com.yallauni.yalla.model.Ride;
import com.yallauni.yalla.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriver(User driver);

    List<Ride> findByPassengersContaining(User passenger);
}
