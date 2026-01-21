package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.service.VehicleService;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.dto.vehicle.VehicleCreateDTO;
import com.yallauni.yalla.dto.vehicle.VehicleResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing vehicles.
 * Drivers can register, view, update, and delete their vehicles.
 * Admins can view and manage all vehicles.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    private final UserRepository userRepository;

    public VehicleController(VehicleService vehicleService, UserRepository userRepository) {
        this.vehicleService = vehicleService;
        this.userRepository = userRepository;
    }

    // Register vehicle - uses authenticated user as driver
    @PostMapping("/register")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> registerVehicle(@RequestBody VehicleCreateDTO vehicleDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setColor(vehicleDto.getColor());
        vehicle.setCapacity(vehicleDto.getCapacity());
        vehicle.setDriver(driver);
        Vehicle saved = vehicleService.registerVehicle(vehicle, driver);
        return ResponseEntity.ok(mapToDto(saved));
    }

    // Get my vehicles - driver's own vehicles
    @GetMapping("/my")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyVehicles(@AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Vehicle> vehicles = vehicleService.findByDriver(driver);
        return ResponseEntity.ok(vehicles.stream().map(this::mapToDto).toList());
    }

    // Get vehicle by ID - only own vehicle or admin
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Vehicle> vehicleOpt = vehicleService.findById(id);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle vehicle = vehicleOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isOwner = vehicle.getDriver() != null
                && vehicle.getDriver().getUserID().equals(currentUser.getUserID());

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only view your own vehicles");
        }

        return ResponseEntity.ok(mapToDto(vehicle));
    }

    // Get all vehicles - admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleService.findAll().stream().map(this::mapToDto).toList();
    }

    // Update vehicle - only own vehicle or admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody VehicleCreateDTO vehicleDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Vehicle> vehicleOpt = vehicleService.findById(id);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle existing = vehicleOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isOwner = existing.getDriver() != null
                && existing.getDriver().getUserID().equals(currentUser.getUserID());

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only update your own vehicles");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setColor(vehicleDto.getColor());
        vehicle.setCapacity(vehicleDto.getCapacity());
        Vehicle updated = vehicleService.updateVehicle(id, vehicle);
        return ResponseEntity.ok(mapToDto(updated));
    }

    // Delete vehicle - only own vehicle or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Vehicle> vehicleOpt = vehicleService.findById(id);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle existing = vehicleOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isOwner = existing.getDriver() != null
                && existing.getDriver().getUserID().equals(currentUser.getUserID());

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only delete your own vehicles");
        }

        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    private VehicleResponseDTO mapToDto(Vehicle v) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(v.getCarId());
        dto.setMake(v.getMake());
        dto.setModel(v.getModel());
        dto.setLicensePlate(v.getLicensePlate());
        dto.setColor(v.getColor());
        dto.setCapacity(v.getCapacity());
        return dto;
    }
}
