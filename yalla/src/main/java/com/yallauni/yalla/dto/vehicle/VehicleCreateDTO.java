package com.yallauni.yalla.dto.vehicle;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for registering a new vehicle.
 * Contains vehicle details required for a driver to register their car.
 */
public class VehicleCreateDTO {
    /** Vehicle manufacturer (required) */
    @NotBlank
    private String make;
    /** Vehicle model (required) */
    @NotBlank
    private String model;
    /** License plate number (required) */
    @NotBlank
    private String licensePlate;
    /** Vehicle color (required) */
    @NotBlank
    private String color;
    /** Passenger capacity (2-9 seats) */
    @Min(2)
    @Max(9)
    private int capacity;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
