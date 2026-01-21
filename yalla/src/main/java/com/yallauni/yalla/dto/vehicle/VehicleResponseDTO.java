package com.yallauni.yalla.dto.vehicle;

/**
 * Data Transfer Object for returning vehicle information in API responses.
 * Contains vehicle details for display in the UI.
 */
public class VehicleResponseDTO {
    /** Unique identifier of the vehicle */
    private Long id;
    /** Vehicle manufacturer (e.g., Toyota, Honda) */
    private String make;
    /** Vehicle model (e.g., Corolla, Civic) */
    private String model;
    /** License plate number */
    private String licensePlate;
    /** Vehicle color */
    private String color;
    /** Passenger capacity */
    private int capacity;
    // Add more fields if needed

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
