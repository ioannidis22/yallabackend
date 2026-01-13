package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.service.RideService;
import com.yallauni.yalla.dto.ride.RideCreateDTO;
import com.yallauni.yalla.dto.ride.RideResponseDTO;
 import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;

    
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<RideResponseDTO> createRide(
            @Valid @RequestBody RideCreateDTO dto,
            @RequestParam Long driverId,
            @RequestParam Long vehicleId
    ) {
        return ResponseEntity.ok(
                rideService.createRide(dto, driverId, vehicleId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RideResponseDTO> getRideById(@PathVariable Long id) {
        Optional<RideResponseDTO> rideDto = rideService.findById(id);
        return rideDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<RideResponseDTO> getAllRides() {
        return rideService.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<RideResponseDTO> updateRide(@PathVariable Long id, @RequestBody RideCreateDTO dto) {
        RideResponseDTO updated = rideService.updateRide(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rideId}/add-passenger")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> addPassenger(@PathVariable Long rideId, @RequestParam Long passengerId) {
        try {
            rideService.addPassenger(rideId, passengerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/remove-passenger")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> removePassenger(@PathVariable Long rideId, @RequestParam Long passengerId) {
        try {
            rideService.removePassenger(rideId, passengerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
