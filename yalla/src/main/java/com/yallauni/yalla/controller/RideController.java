package com.yallauni.yalla.controller;

// Ride entity
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Vehicle entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Vehicle;
// Service for ride logic
import com.yallauni.yalla.core.model.service.RideService;
// Repository for user data (already commented elsewhere)
import com.yallauni.yalla.core.model.repository.UserRepository;
// Repository for vehicle data
import com.yallauni.yalla.core.model.repository.VehicleRepository;
// DTO for creating ride
import com.yallauni.yalla.dto.ride.RideCreateDTO;
// DTO for returning ride data
import com.yallauni.yalla.dto.ride.RideResponseDTO;
// For validating request bodies (already commented elsewhere)
import jakarta.validation.Valid;

// Used for HTTP responses (already commented elsewhere)
import org.springframework.http.ResponseEntity;
// Spring REST controller annotations (already commented elsewhere)
import org.springframework.web.bind.annotation.*;
// Annotation for method-level security (already commented elsewhere)
import org.springframework.security.access.prepost.PreAuthorize;
// For getting the authenticated user
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// User details for authentication
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides") // All endpoints in this controller start with /api/rides
public class RideController {
    private final RideService rideService;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RideController(RideService rideService, UserRepository userRepository, VehicleRepository vehicleRepository) {
        // Inject the ride, user, and vehicle services
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
        // Create a new ride for a driver and vehicle, then return the response DTO
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
        // Find ride by id and return as DTO, or 404 if not found
        Optional<Ride> ride = rideService.findById(id);
        return ride.map(r -> ResponseEntity.ok(mapToDto(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<RideResponseDTO> getAllRides() {
        // Return all rides as a list of DTOs
        return rideService.findAll().stream().map(this::mapToDto).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<RideResponseDTO> updateRide(@PathVariable Long id, @RequestBody RideCreateDTO dto) {
        // Update an existing ride with new data
        Ride ride = new Ride();
        ride.setStartingPoint(dto.getOrigin());
        ride.setDestination(dto.getDestination());

        Ride updated = rideService.updateRide(id, ride);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        // Delete ride by id
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rideId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinRide(@PathVariable Long rideId, @AuthenticationPrincipal UserDetails userDetails) {
        // Add the authenticated user as a passenger to the ride
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
        // Remove the authenticated user from the ride's passengers
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
