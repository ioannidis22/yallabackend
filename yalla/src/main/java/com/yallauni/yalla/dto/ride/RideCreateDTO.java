package com.yallauni.yalla.dto.ride;

import jakarta.validation.constraints.*;

/**
 * DTO for creating a new ride.
 * Contains all necessary information for a driver to create a ride offer.
 */
public class RideCreateDTO {
    /** ID of the vehicle to use for this ride (required) */
    @NotNull
    private Long vehicleId;

    /** Starting location of the ride (required) */
    @NotBlank
    private String startingPoint;

    /** Destination of the ride (required) */
    @NotBlank
    private String destination;

    /** Departure time in ISO 8601 format (required) */
    private String departureTime;

    /** Price per seat for the ride (minimum 0.0) */
    @DecimalMin("0.0")
    private Double price;

    /** Number of available seats (minimum 1) */
    @Min(1)
    private Integer availableSeats;

    /** Optional notes from the driver about the ride */
    private String driverNotes;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getDriverNotes() {
        return driverNotes;
    }

    public void setDriverNotes(String driverNotes) {
        this.driverNotes = driverNotes;
    }
}