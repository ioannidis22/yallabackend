package com.yallauni.yalla.dto.vehicle;

public class VehicleResponseDTO {
    private Long id;
    private String make;
    private String model;
    private String licensePlate;
    private String color;
    private int capacity;
    // Προσθέστε και άλλα πεδία αν χρειάζεται

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
