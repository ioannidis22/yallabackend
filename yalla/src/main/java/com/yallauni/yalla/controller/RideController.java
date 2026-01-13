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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/create")
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
    public ResponseEntity<RideResponseDTO> getRideById(@PathVariable Long id) {
        Optional<Ride> ride = rideService.findById(id);
        if (ride.isPresent()) {
            Ride r = ride.get();
            RideResponseDTO dto = new RideResponseDTO();
            dto.setId(r.getRideId());
            dto.setOrigin(r.getStartingPoint());
            dto.setDestination(r.getDestination());
            // dto.setDate(...); // Προσθέστε αν υπάρχει πεδίο ημερομηνίας
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<RideResponseDTO> getAllRides() {
        List<Ride> rides = rideService.findAll();
        return rides.stream().map(r -> {
            RideResponseDTO dto = new RideResponseDTO();
            dto.setId(r.getRideId());
            dto.setOrigin(r.getStartingPoint());
            dto.setDestination(r.getDestination());
            // dto.setDate(...); // Προσθέστε αν υπάρχει πεδίο ημερομηνίας
            return dto;
        }).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponseDTO> updateRide(@PathVariable Long id, @RequestBody RideCreateDTO dto) {
        // Θα χρειαστεί να μετατρέψετε το DTO σε entity Ride
        Ride ride = new Ride();
        ride.setRideId(id);
        ride.setStartingPoint(dto.getOrigin());
        ride.setDestination(dto.getDestination());
        // ride.setDate(...); // Προσθέστε αν υπάρχει πεδίο ημερομηνίας
        Ride updated = rideService.updateRide(id, ride);
        RideResponseDTO response = new RideResponseDTO();
        response.setId(updated.getRideId());
        response.setOrigin(updated.getStartingPoint());
        response.setDestination(updated.getDestination());
        // response.setDate(...);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rideId}/add-passenger")
    public ResponseEntity<?> addPassenger(@PathVariable Long rideId, @RequestParam Long passengerId) {
        try {
            User passenger = new User();
            passenger.setUserID(passengerId);
            boolean added = rideService.addPassenger(rideId, passenger);
            return added ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("Could not add passenger");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/remove-passenger")
    public ResponseEntity<?> removePassenger(@PathVariable Long rideId, @RequestParam Long passengerId) {
        try {
            User passenger = new User();
            passenger.setUserID(passengerId);
            boolean removed = rideService.removePassenger(rideId, passenger);
            return removed ? ResponseEntity.ok().build()
                    : ResponseEntity.badRequest().body("Could not remove passenger");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
