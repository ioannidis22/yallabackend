
package com.yallauni.yalla.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "ride", indexes = {
        @Index(name = "idx_ride_status", columnList = "status"),
        @Index(name = "idx_ride_driver", columnList = "driver_id"),
        @Index(name = "idx_ride_destination", columnList = "destination")
})
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ride_id")
    private Long rideId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "starting_point", nullable = false)
    private String startingPoint;

    @NotBlank
    @Size(max = 255)
    @Column(name = "destination", nullable = false)
    private String destination;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RideStatus status;
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(name = "ride_passengers", joinColumns = @JoinColumn(name = "ride_id"), inverseJoinColumns = @JoinColumn(name = "passenger_id"))
    private List<User> passengers;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private Double price;

    @Size(max = 500)
    @Column(name = "driver_notes")
    private String driverNotes;

    // Getters and setters
    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Utility method to check if adding a passenger exceeds vehicle capacity.
    public boolean canAddPassenger() {
        if (vehicle == null)
            return false;
        return passengers == null || passengers.size() < vehicle.getCapacity();
    }

    public enum RideStatus {
        REQUESTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDriverNotes() {
        return driverNotes;
    }

    public void setDriverNotes(String driverNotes) {
        this.driverNotes = driverNotes;
    }
}
