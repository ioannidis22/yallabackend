package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yallauni.yalla.core.model.Booking;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity.
 * Provides CRUD operations and custom queries for booking management.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Find all bookings for a specific ride
    List<Booking> findByRide(Ride ride);

    // Find all bookings for a ride by status
    List<Booking> findByRideAndStatus(Ride ride, Booking.BookingStatus status);

    // Find all bookings by a passenger
    List<Booking> findByPassenger(User passenger);

    // Find all bookings by passenger and status
    List<Booking> findByPassengerAndStatus(User passenger, Booking.BookingStatus status);

    // Find booking for a specific ride and passenger
    Optional<Booking> findByRideAndPassenger(Ride ride, User passenger);

    // Count bookings by status for a ride
    long countByRideAndStatus(Ride ride, Booking.BookingStatus status);

    // Find all bookings for rides where user is a driver, by status
    List<Booking> findByRide_DriverAndStatus(User driver, Booking.BookingStatus status);

    // Find all bookings for rides where user is a driver
    List<Booking> findByRide_Driver(User driver);
}
