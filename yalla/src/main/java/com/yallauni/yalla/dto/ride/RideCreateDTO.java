// DTO for creating a ride
package com.yallauni.yalla.dto.ride;

import jakarta.validation.constraints.*;

public class RideCreateDTO {
    @NotNull
    private Long vehicleId;

    @NotBlank
    private String startingPoint;

    @NotBlank
    private String destination;

    // Departure time in ISO format: "2026-01-20T14:30:00"
    private String departureTime;

    @DecimalMin("0.0")
    private Double price;

    @Min(1)
    private Integer availableSeats;

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