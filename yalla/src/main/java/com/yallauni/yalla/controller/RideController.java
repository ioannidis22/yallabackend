package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.service.RideService;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.repository.VehicleRepository;
import com.yallauni.yalla.dto.ride.RideCreateDTO;
import com.yallauni.yalla.dto.ride.RideResponseDTO;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RideController(RideService rideService, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.rideService = rideService;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<RideResponseDTO> createRide(
            @Valid @RequestBody RideCreateDTO dto,
            @RequestParam Long driverId,
            @RequestParam Long vehicleId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        Ride ride = new Ride();
        ride.setStartingPoint(dto.getOrigin());
        ride.setDestination(dto.getDestination());

        Ride saved = rideService.createRide(ride, driver, vehicle);
        return ResponseEntity.ok(mapToDto(saved));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RideResponseDTO> getRideById(@PathVariable Long id) {
        Optional<Ride> ride = rideService.findById(id);
        return ride.map(r -> ResponseEntity.ok(mapToDto(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<RideResponseDTO> getAllRides() {
        return rideService.findAll().stream().map(this::mapToDto).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<RideResponseDTO> updateRide(@PathVariable Long id, @RequestBody RideCreateDTO dto) {
        Ride ride = new Ride();
        ride.setStartingPoint(dto.getOrigin());
        ride.setDestination(dto.getDestination());

        Ride updated = rideService.updateRide(id, ride);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rideId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinRide(@PathVariable Long rideId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User passenger = userRepository.findByEmailAddress(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            boolean added = rideService.addPassenger(rideId, passenger);
            return added ? ResponseEntity.ok().body("Successfully joined the ride")
                    : ResponseEntity.badRequest().body("Could not join ride");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> leaveRide(@PathVariable Long rideId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User passenger = userRepository.findByEmailAddress(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            boolean removed = rideService.removePassenger(rideId, passenger);
            return removed ? ResponseEntity.ok().body("Successfully left the ride")
                    : ResponseEntity.badRequest().body("Could not leave ride");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private RideResponseDTO mapToDto(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(ride.getRideId());
        dto.setOrigin(ride.getStartingPoint());
        dto.setDestination(ride.getDestination());
        return dto;
    }
}
