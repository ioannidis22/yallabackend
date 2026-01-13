package com.yallauni.yalla.core.model.service.impl;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.repository.VehicleRepository;
import com.yallauni.yalla.core.model.service.VehicleService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Vehicle registerVehicle(Vehicle vehicle, User driver) {
        vehicle.setDriver(driver);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public List<Vehicle> findByDriver(User driver) {
        return vehicleRepository.findByDriver(driver);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle updateVehicle(Long id, Vehicle vehicleUpdate) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        // Update only provided fields, preserve driver
        if (vehicleUpdate.getMake() != null) {
            existing.setMake(vehicleUpdate.getMake());
        }
        if (vehicleUpdate.getModel() != null) {
            existing.setModel(vehicleUpdate.getModel());
        }
        if (vehicleUpdate.getLicensePlate() != null) {
            existing.setLicensePlate(vehicleUpdate.getLicensePlate());
        }
        if (vehicleUpdate.getColor() != null) {
            existing.setColor(vehicleUpdate.getColor());
        }
        if (vehicleUpdate.getCapacity() > 0) {
            existing.setCapacity(vehicleUpdate.getCapacity());
        }
        // Driver is NOT updated - it stays the same as before

        return vehicleRepository.save(existing);
    }

    @Override
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}
