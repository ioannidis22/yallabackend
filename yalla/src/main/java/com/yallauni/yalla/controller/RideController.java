package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.service.RideService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createRide(@RequestBody Ride ride, @RequestParam Long driverId,
            @RequestParam Long vehicleId) {
        try {
            User driver = new User();
            driver.setUserID(driverId);
            Vehicle vehicle = new Vehicle();
            vehicle.setCarId(vehicleId);
            return ResponseEntity.ok(rideService.createRide(ride, driver, vehicle));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        Optional<Ride> ride = rideService.findById(id);
        return ride.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ride> updateRide(@PathVariable Long id, @RequestBody Ride ride) {
        return ResponseEntity.ok(rideService.updateRide(id, ride));
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
