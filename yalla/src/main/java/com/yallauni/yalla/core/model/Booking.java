package com.yallauni.yalla.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking", indexes = {
        @Index(name = "idx_booking_ride", columnList = "ride_id"),
        @Index(name = "idx_booking_passenger", columnList = "passenger_id"),
        @Index(name = "idx_booking_status", columnList = "status")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Size(max = 500)
    @Column(name = "passenger_message")
    private String passengerMessage;

    @Size(max = 500)
    @Column(name = "driver_response")
    private String driverResponse;

    public enum BookingStatus {
        PENDING, // Waiting for driver approval
        ACCEPTED, // Driver accepted the booking
        REJECTED, // Driver rejected the booking
        CANCELLED // Passenger cancelled their request
    }

    // Getters and Setters
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
