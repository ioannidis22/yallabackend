// Vehicle entity for user cars (student-style comment)
package com.yallauni.yalla.core.model;

import jakarta.persistence.*; // JPA annotations
import jakarta.validation.constraints.*; // validation constraints

@Entity
@Table(name = "vehicle", indexes = {
        @Index(name = "idx_vehicle_make", columnList = "make"),
        @Index(name = "idx_vehicle_model", columnList = "model")
})
public class Vehicle {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String make;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String model;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String licensePlate;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false)
    private String color;

    @Min(2)
    @Max(9)
    @Column(nullable = false)
    private int capacity;

    @OneToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id", nullable = false, unique = true)
    private User driver;

    // Typical getters and setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }
}
