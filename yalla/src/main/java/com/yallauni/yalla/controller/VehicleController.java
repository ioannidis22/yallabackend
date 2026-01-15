package com.yallauni.yalla.controller;


// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Vehicle entity
import com.yallauni.yalla.core.model.Vehicle;
// Service for vehicle logic
import com.yallauni.yalla.core.model.service.VehicleService;
// DTO for creating vehicle
import com.yallauni.yalla.dto.vehicle.VehicleCreateDTO;
// DTO for returning vehicle data
import com.yallauni.yalla.dto.vehicle.VehicleResponseDTO;


// Used for HTTP responses (already commented elsewhere)
import org.springframework.http.ResponseEntity;
// Spring REST controller annotations (already commented elsewhere)
import org.springframework.web.bind.annotation.*;

// Annotation for method-level security (already commented elsewhere)
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles") // All endpoints in this controller start with /api/vehicles
public class VehicleController {
    private final VehicleService vehicleService;

    
    public VehicleController(VehicleService vehicleService) {
        // Inject the vehicle service
        this.vehicleService = vehicleService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> registerVehicle(@RequestBody VehicleCreateDTO vehicleDto, @RequestParam Long driverId) {
        // Register a new vehicle for a driver and return the response DTO
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
        // Find vehicle by id and return as DTO, or 404 if not found
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
        // Return all vehicles as a list of DTOs
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
        // Update an existing vehicle with new data
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
        // Delete vehicle by id
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
