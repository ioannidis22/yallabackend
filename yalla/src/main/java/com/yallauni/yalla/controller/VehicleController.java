package com.yallauni.yalla.controller;


import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.service.VehicleService;
import com.yallauni.yalla.dto.vehicle.VehicleCreateDTO;
import com.yallauni.yalla.dto.vehicle.VehicleResponseDTO;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> registerVehicle(@RequestBody VehicleCreateDTO vehicleDto, @RequestParam Long driverId) {
        User driver = new User();
        driver.setUserID(driverId);
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setColor(vehicleDto.getColor());
        vehicle.setCapacity(vehicleDto.getCapacity());
        vehicle.setDriver(driver);
        Vehicle saved = vehicleService.registerVehicle(vehicle, driver);
        VehicleResponseDTO response = new VehicleResponseDTO();
        response.setId(saved.getCarId());
        response.setMake(saved.getMake());
        response.setModel(saved.getModel());
        response.setLicensePlate(saved.getLicensePlate());
        response.setColor(saved.getColor());
        response.setCapacity(saved.getCapacity());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.findById(id);
        if (vehicle.isPresent()) {
            Vehicle v = vehicle.get();
            VehicleResponseDTO dto = new VehicleResponseDTO();
            dto.setId(v.getCarId());
            dto.setMake(v.getMake());
            dto.setModel(v.getModel());
            dto.setLicensePlate(v.getLicensePlate());
            dto.setColor(v.getColor());
            dto.setCapacity(v.getCapacity());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<VehicleResponseDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.findAll();
        return vehicles.stream().map(v -> {
            VehicleResponseDTO dto = new VehicleResponseDTO();
            dto.setId(v.getCarId());
            dto.setMake(v.getMake());
            dto.setModel(v.getModel());
            dto.setLicensePlate(v.getLicensePlate());
            dto.setColor(v.getColor());
            dto.setCapacity(v.getCapacity());
            return dto;
        }).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleCreateDTO vehicleDto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setCarId(id);
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setColor(vehicleDto.getColor());
        vehicle.setCapacity(vehicleDto.getCapacity());
        Vehicle updated = vehicleService.updateVehicle(id, vehicle);
        VehicleResponseDTO response = new VehicleResponseDTO();
        response.setId(updated.getCarId());
        response.setMake(updated.getMake());
        response.setModel(updated.getModel());
        response.setLicensePlate(updated.getLicensePlate());
        response.setColor(updated.getColor());
        response.setCapacity(updated.getCapacity());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
