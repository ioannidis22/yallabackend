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

    // Create ride - driver uses their own account and vehicle
    @PostMapping("/create")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> createRide(
            @Valid @RequestBody RideCreateDTO dto,
            @RequestParam Long vehicleId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) {
            return ResponseEntity.badRequest().body("Vehicle not found");
        }

        // Verify driver owns this vehicle
        boolean isAdmin = driver.getUserType() == User.UserType.ADMIN;
        if (!isAdmin && (vehicle.getDriver() == null || !vehicle.getDriver().getUserID().equals(driver.getUserID()))) {
            return ResponseEntity.status(403).body("Access denied: You can only create rides with your own vehicles");
        }

        Ride ride = new Ride();
        ride.setStartingPoint(dto.getOrigin());
        ride.setDestination(dto.getDestination());

        Ride saved = rideService.createRide(ride, driver, vehicle);
        return ResponseEntity.ok(mapToDto(saved));
    }

    // Get my rides - rides where user is driver or passenger
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyRides(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Ride> allRides = rideService.findAll();
        List<RideResponseDTO> myRides = allRides.stream()
                .filter(r -> isUserInvolved(r, currentUser))
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(myRides);
    }

    // Get ride by ID - only if involved or admin
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRideById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Ride> rideOpt = rideService.findById(id);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ride ride = rideOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;

        if (!isUserInvolved(ride, currentUser) && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only view rides you are involved in");
        }

        return ResponseEntity.ok(mapToDto(ride));
    }

    // Get all rides - admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RideResponseDTO> getAllRides() {
        return rideService.findAll().stream().map(this::mapToDto).toList();
    }

    // Update ride - only driver of ride or admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateRide(@PathVariable Long id, @RequestBody RideCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Ride> rideOpt = rideService.findById(id);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ride existing = rideOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isDriver = existing.getDriver() != null
                && existing.getDriver().getUserID().equals(currentUser.getUserID());

        if (!isDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: Only the ride driver can update this ride");
        }

        Ride ride = new Ride();
        ride.setStartingPoint(dto.getOrigin());
        ride.setDestination(dto.getDestination());

        Ride updated = rideService.updateRide(id, ride);
        return ResponseEntity.ok(mapToDto(updated));
    }

    // Delete ride - only driver of ride or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteRide(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Ride> rideOpt = rideService.findById(id);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ride existing = rideOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isDriver = existing.getDriver() != null
                && existing.getDriver().getUserID().equals(currentUser.getUserID());

        if (!isDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: Only the ride driver can delete this ride");
        }

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

    // ==================== STATUS MANAGEMENT ====================

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> startRide(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        try {
            Ride ride = rideService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

            boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
            boolean isDriver = ride.getDriver() != null && ride.getDriver().getUserID().equals(currentUser.getUserID());

            if (!isDriver && !isAdmin) {
                return ResponseEntity.status(403).body("Access denied: Only the ride driver can start this ride");
            }

            if (ride.getStatus() != Ride.RideStatus.SCHEDULED) {
                return ResponseEntity.badRequest().body("Ride can only be started from SCHEDULED status");
            }

            ride.setStatus(Ride.RideStatus.IN_PROGRESS);
            Ride updated = rideService.updateRide(id, ride);
            return ResponseEntity.ok(mapToDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> completeRide(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        try {
            Ride ride = rideService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

            boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
            boolean isDriver = ride.getDriver() != null && ride.getDriver().getUserID().equals(currentUser.getUserID());

            if (!isDriver && !isAdmin) {
                return ResponseEntity.status(403).body("Access denied: Only the ride driver can complete this ride");
            }

            if (ride.getStatus() != Ride.RideStatus.IN_PROGRESS) {
                return ResponseEntity.badRequest().body("Ride can only be completed from IN_PROGRESS status");
            }

            ride.setStatus(Ride.RideStatus.COMPLETED);
            Ride updated = rideService.updateRide(id, ride);
            return ResponseEntity.ok(mapToDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelRide(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        try {
            Ride ride = rideService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ride not found"));

            boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
            boolean isDriver = ride.getDriver() != null && ride.getDriver().getUserID().equals(currentUser.getUserID());

            if (!isDriver && !isAdmin) {
                return ResponseEntity.status(403).body("Access denied: Only the ride driver can cancel this ride");
            }

            if (ride.getStatus() == Ride.RideStatus.COMPLETED) {
                return ResponseEntity.badRequest().body("Cannot cancel a completed ride");
            }
            if (ride.getStatus() == Ride.RideStatus.CANCELLED) {
                return ResponseEntity.badRequest().body("Ride is already cancelled");
            }

            ride.setStatus(Ride.RideStatus.CANCELLED);
            Ride updated = rideService.updateRide(id, ride);
            return ResponseEntity.ok(mapToDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get rides by status - admin only
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RideResponseDTO> getRidesByStatus(@PathVariable String status) {
        try {
            Ride.RideStatus rideStatus = Ride.RideStatus.valueOf(status.toUpperCase());
            return rideService.findByStatus(rideStatus).stream().map(this::mapToDto).toList();
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    // Get my rides by status - user's own rides with specific status
    @GetMapping("/my/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyRidesByStatus(@PathVariable String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        try {
            Ride.RideStatus rideStatus = Ride.RideStatus.valueOf(status.toUpperCase());
            List<RideResponseDTO> myRides = rideService.findByStatus(rideStatus).stream()
                    .filter(r -> isUserInvolved(r, currentUser))
                    .map(this::mapToDto)
                    .toList();
            return ResponseEntity.ok(myRides);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(List.of());
        }
    }

    // Helper method to check if user is involved in a ride (driver or passenger)
    private boolean isUserInvolved(Ride ride, User user) {
        if (ride.getDriver() != null && ride.getDriver().getUserID().equals(user.getUserID())) {
            return true;
        }
        if (ride.getPassengers() != null) {
            return ride.getPassengers().stream()
                    .anyMatch(p -> p.getUserID().equals(user.getUserID()));
        }
        return false;
    }

    private RideResponseDTO mapToDto(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(ride.getRideId());
        dto.setOrigin(ride.getStartingPoint());
        dto.setDestination(ride.getDestination());
        dto.setStatus(ride.getStatus() != null ? ride.getStatus().name() : null);
        dto.setPassengerCount(ride.getPassengers() != null ? ride.getPassengers().size() : 0);
        return dto;
    }
}
