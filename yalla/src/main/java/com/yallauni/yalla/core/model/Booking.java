package com.yallauni.yalla.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entity representing a booking request from a passenger for a ride.
 * Passengers can request to join a ride, and drivers can accept or reject.
 */
@Entity
@Table(name = "booking", indexes = {
        @Index(name = "idx_booking_ride", columnList = "ride_id"),
        @Index(name = "idx_booking_passenger", columnList = "passenger_id"),
        @Index(name = "idx_booking_status", columnList = "status")
})
public class Booking {
    // Unique identifier for the booking
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The ride this booking is for
    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    // The passenger who made the booking request
    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    // Current status of the booking (PENDING, ACCEPTED, REJECTED, CANCELLED)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    // Timestamp when booking was created
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Timestamp when booking was last updated
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Optional message from passenger to driver
    @Size(max = 500)
    @Column(name = "passenger_message")
    private String passengerMessage;

    // Optional response from driver to passenger
    @Size(max = 500)
    @Column(name = "driver_response")
    private String driverResponse;

    public enum BookingStatus {
        PENDING, // Waiting for driver approval
        ACCEPTED, // Driver accepted the booking
        REJECTED, // Driver rejected the booking
        CANCELLED // Passenger cancelled their request
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPassengerMessage() {
        return passengerMessage;
    }

    public void setPassengerMessage(String passengerMessage) {
        this.passengerMessage = passengerMessage;
    }

    public String getDriverResponse() {
        return driverResponse;
    }

    public void setDriverResponse(String driverResponse) {
        this.driverResponse = driverResponse;
    }
}
