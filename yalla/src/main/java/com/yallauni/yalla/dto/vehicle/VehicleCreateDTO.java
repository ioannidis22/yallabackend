package com.yallauni.yalla.dto.vehicle;

import jakarta.validation.constraints.*;

public class VehicleCreateDTO {
    @NotBlank
    private String make;
    @NotBlank
    private String model;
    @NotBlank
    private String licensePlate;
    @NotBlank
    private String color;
    @Min(2)
    @Max(9)
    private int capacity;
    // Προσθέστε και άλλα πεδία αν χρειάζεται

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
