package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.Vehicle;
import com.yallauni.yalla.core.model.Booking;
import com.yallauni.yalla.core.model.service.RideService;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.repository.VehicleRepository;
import com.yallauni.yalla.core.model.repository.RideRepository;
import com.yallauni.yalla.core.model.repository.BookingRepository;
import com.yallauni.yalla.dto.ride.RideCreateDTO;
import com.yallauni.yalla.dto.ride.RideResponseDTO;
import com.yallauni.yalla.dto.booking.BookingResponseDTO;
import com.yallauni.yalla.dto.booking.BookingCreateDTO;
import com.yallauni.yalla.dto.driver.DriverStatsDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing rides.
 * Provides endpoints for creating, viewing, updating, and managing rides.
 * Includes booking functionality for passengers.
 */
@RestController
@RequestMapping("/api/rides")
@Transactional(readOnly = true)
public class RideController {
    private final RideService rideService;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;

    public RideController(RideService rideService, UserRepository userRepository,
            VehicleRepository vehicleRepository, RideRepository rideRepository,
            BookingRepository bookingRepository) {
        this.rideService = rideService;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
    }

    // Create ride - driver uses their own account and vehicle
    @PostMapping("/create")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> createRide(
            @Valid @RequestBody RideCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId()).orElse(null);
        if (vehicle == null) {
            return ResponseEntity.badRequest().body("Vehicle not found");
        }

        // Verify driver owns this vehicle
        boolean isAdmin = driver.getUserType() == User.UserType.ADMIN;
        if (!isAdmin && (vehicle.getDriver() == null || !vehicle.getDriver().getUserID().equals(driver.getUserID()))) {
            return ResponseEntity.status(403).body("Access denied: You can only create rides with your own vehicles");
        }

        Ride ride = new Ride();
        ride.setStartingPoint(dto.getStartingPoint());
        ride.setDestination(dto.getDestination());

        // Set price
        if (dto.getPrice() != null) {
            ride.setPrice(dto.getPrice());
        } else {
            ride.setPrice(0.0);
        }

        // Set departure time
        if (dto.getDepartureTime() != null && !dto.getDepartureTime().isEmpty()) {
            ride.setDepartureTime(LocalDateTime.parse(dto.getDepartureTime()));
        }

        // Set available seats
        if (dto.getAvailableSeats() != null) {
            ride.setAvailableSeats(dto.getAvailableSeats());
        } else {
            ride.setAvailableSeats(vehicle.getCapacity());
        }

        // Set driver notes
        ride.setDriverNotes(dto.getDriverNotes());

        Ride saved = rideService.createRide(ride, driver, vehicle);
        return ResponseEntity.ok(mapToDto(saved));
    }

    // Get user's rides - rides where user is driver or passenger.
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

    // Get ride by ID - only if involved or admin.
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

    // Update ride - only driver of said ride or admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
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

        // Update fields if provided
        if (dto.getStartingPoint() != null) {
            existing.setStartingPoint(dto.getStartingPoint());
        }
        if (dto.getDestination() != null) {
            existing.setDestination(dto.getDestination());
        }
        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }
        if (dto.getDepartureTime() != null && !dto.getDepartureTime().isEmpty()) {
            existing.setDepartureTime(LocalDateTime.parse(dto.getDepartureTime()));
        }
        if (dto.getAvailableSeats() != null) {
            existing.setAvailableSeats(dto.getAvailableSeats());
        }
        if (dto.getDriverNotes() != null) {
            existing.setDriverNotes(dto.getDriverNotes());
        }

        Ride updated = rideService.updateRide(id, existing);
        return ResponseEntity.ok(mapToDto(updated));
    }

    // Delete ride - only driver of ride or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
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
    @Transactional
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
    @Transactional
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

    // -------------------------------RIDE STATUS MANAGEMENT -------------------------------

    // Driver/Admin starts the ride.
    @PostMapping("/{id}/start") 
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
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

    // Driver/Admin completes the ride.
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
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

    // Driver/Admin cancels the ride.
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
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

    // Get rides by status, admin only
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

    // Get user's own rides with specific status
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

    // Method to check if user is involved in a ride (driver or passenger)
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
        dto.setStartingPoint(ride.getStartingPoint());
        dto.setDestination(ride.getDestination());
        dto.setStatus(ride.getStatus() != null ? ride.getStatus().name() : null);
        dto.setPrice(ride.getPrice());
        dto.setAvailableSeats(ride.getAvailableSeats());
        dto.setRemainingSeats(ride.getRemainingSeats());
        dto.setPassengerCount(ride.getPassengers() != null ? ride.getPassengers().size() : 0);
        dto.setDriverNotes(ride.getDriverNotes());
        if (ride.getDepartureTime() != null) {
            dto.setDepartureTime(ride.getDepartureTime().toString());
        }
        if (ride.getDriver() != null) {
            dto.setDriverId(ride.getDriver().getUserID());
            dto.setDriverName(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        }
        return dto;
    }

    private RideResponseDTO mapToDtoWithPassengers(Ride ride) {
        RideResponseDTO dto = mapToDto(ride);
        if (ride.getPassengers() != null) {
            dto.setPassengers(ride.getPassengers().stream()
                    .map(p -> new RideResponseDTO.PassengerInfo(
                            p.getUserID(),
                            p.getFirstName() + " " + p.getLastName(),
                            p.getEmailAddress()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // ------------------------------- DRIVER SPECIFIC ENDPOINTS -------------------------------

    // Get ride with passengers (for driver to view passengers)
    @GetMapping("/{id}/passengers")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> getRidePassengers(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
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
        boolean isDriver = ride.getDriver() != null && ride.getDriver().getUserID().equals(currentUser.getUserID());

        if (!isDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: Only the ride driver can view passengers");
        }

        return ResponseEntity.ok(mapToDtoWithPassengers(ride));
    }

    // Get driver's ride history filtered by date
    @GetMapping("/driver/history")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDriverRideHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Ride> rides;
        if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);
            rides = rideRepository.findByDriverAndDepartureTimeBetween(driver, start, end);
        } else {
            rides = rideRepository.findByDriverOrderByDepartureTimeDesc(driver);
        }

        return ResponseEntity.ok(rides.stream().map(this::mapToDto).toList());
    }

    // Get driver's stats
    @GetMapping("/driver/stats")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDriverStats(@AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        DriverStatsDTO stats = new DriverStatsDTO();
        stats.setDriverId(driver.getUserID());
        stats.setDriverName(driver.getFirstName() + " " + driver.getLastName());
        stats.setTotalTripsCompleted(rideRepository.countByDriverAndStatus(driver, Ride.RideStatus.COMPLETED));
        stats.setTotalTripsCancelled(rideRepository.countByDriverAndStatus(driver, Ride.RideStatus.CANCELLED));
        stats.setScheduledRides(rideRepository.countByDriverAndStatus(driver, Ride.RideStatus.SCHEDULED));
        stats.setInProgressRides(rideRepository.countByDriverAndStatus(driver, Ride.RideStatus.IN_PROGRESS));

        // Booking stats
        List<Booking> allBookings = bookingRepository.findByRide_Driver(driver);
        stats.setPendingBookings(
                allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count());
        stats.setAcceptedBookings(
                allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.ACCEPTED).count());
        stats.setRejectedBookings(
                allBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.REJECTED).count());

        stats.setAverageRating(driver.getRating());

        return ResponseEntity.ok(stats);
    }

    // -------------------------------RIDE BOOKING MANAGEMENT -------------------------------

    // Passenger requests to book a ride.
    @PostMapping("/{rideId}/book")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<?> requestBooking(@PathVariable Long rideId,
            @RequestBody(required = false) BookingCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User passenger = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (passenger == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Ride> rideOpt = rideService.findById(rideId);
        if (rideOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ride ride = rideOpt.get();

        // Checks if ride is still accepting bookings.
        if (ride.getStatus() != Ride.RideStatus.SCHEDULED) {
            return ResponseEntity.badRequest().body("This ride is no longer accepting bookings");
        }

        // Checks if there are available seats.
        if (ride.getRemainingSeats() <= 0) {
            return ResponseEntity.badRequest().body("No available seats on this ride");
        }

        // Checks if ride is already booked by user.
        Optional<Booking> existingBooking = bookingRepository.findByRideAndPassenger(ride, passenger);
        if (existingBooking.isPresent()) {
            return ResponseEntity.badRequest()
                    .body("You already have a booking for this ride. Status: " + existingBooking.get().getStatus());
        }

        // Checks if user is trying to book their own ride.
        if (ride.getDriver().getUserID().equals(passenger.getUserID())) {
            return ResponseEntity.badRequest().body("You cannot book your own ride");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setStatus(Booking.BookingStatus.PENDING);
        if (dto != null && dto.getMessage() != null) {
            booking.setPassengerMessage(dto.getMessage());
        }

        bookingRepository.save(booking);

        return ResponseEntity.ok(mapBookingToDto(booking));
    }

    // Driver views all bookings for their rides
    @GetMapping("/driver/bookings")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDriverBookings(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Booking> bookings;
        if (status != null && !status.isEmpty()) {
            try {
                Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
                bookings = bookingRepository.findByRide_DriverAndStatus(driver, bookingStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status. Use: PENDING, ACCEPTED, REJECTED, CANCELLED");
            }
        } else {
            bookings = bookingRepository.findByRide_Driver(driver);
        }

        return ResponseEntity.ok(bookings.stream().map(this::mapBookingToDto).toList());
    }

    // Driver accepts a booking
    @PostMapping("/bookings/{bookingId}/accept")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> acceptBooking(@PathVariable Long bookingId,
            @RequestBody(required = false) java.util.Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();
        boolean isAdmin = driver.getUserType() == User.UserType.ADMIN;
        boolean isRideDriver = booking.getRide().getDriver().getUserID().equals(driver.getUserID());

        if (!isRideDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: Only the ride driver can accept bookings");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            return ResponseEntity.badRequest().body("Booking is not in PENDING status");
        }

        // Check seats
        if (booking.getRide().getRemainingSeats() <= 0) {
            return ResponseEntity.badRequest().body("No available seats remaining");
        }

        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        booking.setUpdatedAt(LocalDateTime.now());
        if (body != null && body.get("response") != null) {
            booking.setDriverResponse(body.get("response"));
        }

        // Add passenger to ride
        Ride ride = booking.getRide();
        if (ride.getPassengers() == null) {
            ride.setPassengers(new java.util.ArrayList<>());
        }
        ride.getPassengers().add(booking.getPassenger());
        rideRepository.save(ride);

        bookingRepository.save(booking);

        return ResponseEntity.ok(mapBookingToDto(booking));
    }

    // Driver rejects a booking
    @PostMapping("/bookings/{bookingId}/reject")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId,
            @RequestBody(required = false) java.util.Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        User driver = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (driver == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();
        boolean isAdmin = driver.getUserType() == User.UserType.ADMIN;
        boolean isRideDriver = booking.getRide().getDriver().getUserID().equals(driver.getUserID());

        if (!isRideDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: Only the ride driver can reject bookings");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            return ResponseEntity.badRequest().body("Booking is not in PENDING status");
        }

        booking.setStatus(Booking.BookingStatus.REJECTED);
        booking.setUpdatedAt(LocalDateTime.now());
        if (body != null && body.get("reason") != null) {
            booking.setDriverResponse(body.get("reason"));
        }

        bookingRepository.save(booking);

        return ResponseEntity.ok(mapBookingToDto(booking));
    }

    // Passenger cancels their booking
    @PostMapping("/bookings/{bookingId}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User passenger = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (passenger == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();
        boolean isAdmin = passenger.getUserType() == User.UserType.ADMIN;
        boolean isBookingOwner = booking.getPassenger().getUserID().equals(passenger.getUserID());

        if (!isBookingOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: Only the booking owner can cancel");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            return ResponseEntity.badRequest().body("Booking is already cancelled");
        }

        // If accepted, remove from passengers
        if (booking.getStatus() == Booking.BookingStatus.ACCEPTED) {
            Ride ride = booking.getRide();
            if (ride.getPassengers() != null) {
                ride.getPassengers().removeIf(p -> p.getUserID().equals(passenger.getUserID()));
                rideRepository.save(ride);
            }
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return ResponseEntity.ok(mapBookingToDto(booking));
    }

    // Passenger views their bookings
    @GetMapping("/my/bookings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyBookings(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User passenger = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (passenger == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Booking> bookings;
        if (status != null && !status.isEmpty()) {
            try {
                Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
                bookings = bookingRepository.findByPassengerAndStatus(passenger, bookingStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status. Use: PENDING, ACCEPTED, REJECTED, CANCELLED");
            }
        } else {
            bookings = bookingRepository.findByPassenger(passenger);
        }

        return ResponseEntity.ok(bookings.stream().map(this::mapBookingToDto).toList());
    }

    private BookingResponseDTO mapBookingToDto(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setRideId(booking.getRide().getRideId());
        dto.setRideStartingPoint(booking.getRide().getStartingPoint());
        dto.setRideDestination(booking.getRide().getDestination());
        if (booking.getRide().getDepartureTime() != null) {
            dto.setRideDepartureTime(booking.getRide().getDepartureTime().toString());
        }
        dto.setPassengerId(booking.getPassenger().getUserID());
        dto.setPassengerName(booking.getPassenger().getFirstName() + " " + booking.getPassenger().getLastName());
        dto.setPassengerEmail(booking.getPassenger().getEmailAddress());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt().toString());
        dto.setPassengerMessage(booking.getPassengerMessage());
        dto.setDriverResponse(booking.getDriverResponse());
        return dto;
    }

    // ----------------- PASSENGER SEARCH & FILTER ENDPOINTS -------------------

    // Search for available rides with filters
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> searchAvailableRides(
            @RequestParam(required = false) String startingPoint,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Ride> rides;
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // Parse date/time parameters
        if (date != null && !date.isEmpty()) {
            LocalDate searchDate = LocalDate.parse(date);
            if (startTime != null && !startTime.isEmpty()) {
                startDateTime = searchDate.atTime(java.time.LocalTime.parse(startTime));
            } else {
                startDateTime = searchDate.atStartOfDay();
            }
            if (endTime != null && !endTime.isEmpty()) {
                endDateTime = searchDate.atTime(java.time.LocalTime.parse(endTime));
            } else {
                endDateTime = searchDate.atTime(23, 59, 59);
            }
        }

        // Search based on provided filters
        boolean hasStartingPoint = startingPoint != null && !startingPoint.isEmpty();
        boolean hasDestination = destination != null && !destination.isEmpty();
        boolean hasDateRange = startDateTime != null && endDateTime != null;

        if (hasStartingPoint && hasDestination && hasDateRange) {
            rides = rideRepository
                    .findByStatusAndStartingPointContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDepartureTimeBetween(
                            Ride.RideStatus.SCHEDULED, startingPoint, destination, startDateTime, endDateTime);
        } else if (hasDestination && hasDateRange) {
            rides = rideRepository.findByStatusAndDestinationContainingIgnoreCaseAndDepartureTimeBetween(
                    Ride.RideStatus.SCHEDULED, destination, startDateTime, endDateTime);
        } else if (hasStartingPoint && hasDestination) {
            rides = rideRepository.findByStatusAndStartingPointContainingIgnoreCaseAndDestinationContainingIgnoreCase(
                    Ride.RideStatus.SCHEDULED, startingPoint, destination);
        } else if (hasDateRange) {
            rides = rideRepository.findByStatusAndDepartureTimeBetween(
                    Ride.RideStatus.SCHEDULED, startDateTime, endDateTime);
        } else if (hasDestination) {
            rides = rideRepository.findByStatusAndDestinationContainingIgnoreCase(
                    Ride.RideStatus.SCHEDULED, destination);
        } else if (hasStartingPoint) {
            rides = rideRepository.findByStatusAndStartingPointContainingIgnoreCase(
                    Ride.RideStatus.SCHEDULED, startingPoint);
        } else {
            rides = rideRepository.findByStatusOrderByDepartureTimeAsc(Ride.RideStatus.SCHEDULED);
        }

        // Filter out rides with no remaining seats and rides where user is already
        // involved
        List<RideResponseDTO> availableRides = rides.stream()
                .filter(r -> r.getRemainingSeats() > 0)
                .filter(r -> !isUserInvolved(r, currentUser))
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(availableRides);
    }

    // Filter rides by destination only.
    @GetMapping("/filter/destination/{destination}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> filterByDestination(@PathVariable String destination,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Ride> rides = rideRepository.findByStatusAndDestinationContainingIgnoreCase(
                Ride.RideStatus.SCHEDULED, destination);

        List<RideResponseDTO> availableRides = rides.stream()
                .filter(r -> r.getRemainingSeats() > 0)
                .filter(r -> !isUserInvolved(r, currentUser))
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(availableRides);
    }

    // Get all available rides (public listing for passengers)
    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAvailableRides(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Ride> rides = rideRepository.findByStatusOrderByDepartureTimeAsc(Ride.RideStatus.SCHEDULED);

        List<RideResponseDTO> availableRides = rides.stream()
                .filter(r -> r.getRemainingSeats() > 0)
                .filter(r -> !isUserInvolved(r, currentUser))
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(availableRides);
    }

    // -------------------------- PASSENGER BOOKING HISTORY --------------------------



    // Get booking history (accepted bookings for completed rides)
    @GetMapping("/my/bookings/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPastBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User passenger = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (passenger == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Booking> allBookings = bookingRepository.findByPassenger(passenger);

        // Filter for accepted bookings where ride is COMPLETED
        List<BookingResponseDTO> pastBookings = allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.ACCEPTED)
                .filter(b -> b.getRide().getStatus() == Ride.RideStatus.COMPLETED)
                .map(this::mapBookingToDto)
                .toList();

        return ResponseEntity.ok(pastBookings);
    }

    // Get active bookings (pending or accepted, ride not completed)
    @GetMapping("/my/bookings/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getActiveBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User passenger = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (passenger == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Booking> allBookings = bookingRepository.findByPassenger(passenger);

        // Filter for pending or accepted bookings where ride is not completed/cancelled
        List<BookingResponseDTO> activeBookings = allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING ||
                        b.getStatus() == Booking.BookingStatus.ACCEPTED)
                .filter(b -> b.getRide().getStatus() == Ride.RideStatus.SCHEDULED ||
                        b.getRide().getStatus() == Ride.RideStatus.IN_PROGRESS)
                .map(this::mapBookingToDto)
                .toList();

        return ResponseEntity.ok(activeBookings);
    }

    // Track a specific booking's status with ride details
    @GetMapping("/bookings/{bookingId}/track")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> trackBooking(@PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isBookingOwner = booking.getPassenger().getUserID().equals(currentUser.getUserID());
        boolean isRideDriver = booking.getRide().getDriver().getUserID().equals(currentUser.getUserID());

        if (!isBookingOwner && !isRideDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied");
        }

        // Build detailed tracking response
        java.util.Map<String, Object> trackingInfo = new java.util.HashMap<>();
        trackingInfo.put("bookingId", booking.getId());
        trackingInfo.put("bookingStatus", booking.getStatus().name());
        trackingInfo.put("createdAt", booking.getCreatedAt().toString());
        if (booking.getUpdatedAt() != null) {
            trackingInfo.put("updatedAt", booking.getUpdatedAt().toString());
        }
        trackingInfo.put("passengerMessage", booking.getPassengerMessage());
        trackingInfo.put("driverResponse", booking.getDriverResponse());

        // Ride details
        Ride ride = booking.getRide();
        java.util.Map<String, Object> rideInfo = new java.util.HashMap<>();
        rideInfo.put("rideId", ride.getRideId());
        rideInfo.put("startingPoint", ride.getStartingPoint());
        rideInfo.put("destination", ride.getDestination());
        rideInfo.put("rideStatus", ride.getStatus().name());
        rideInfo.put("price", ride.getPrice());
        if (ride.getDepartureTime() != null) {
            rideInfo.put("departureTime", ride.getDepartureTime().toString());
        }
        rideInfo.put("availableSeats", ride.getAvailableSeats());
        rideInfo.put("remainingSeats", ride.getRemainingSeats());
        rideInfo.put("driverName", ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        rideInfo.put("driverNotes", ride.getDriverNotes());

        trackingInfo.put("ride", rideInfo);

        return ResponseEntity.ok(trackingInfo);
    }

    // Get passenger's ride history (rides they completed as a passenger)
    @GetMapping("/passenger/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPassengerRideHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        User passenger = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (passenger == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<Booking> allBookings = bookingRepository.findByPassengerAndStatus(passenger,
                Booking.BookingStatus.ACCEPTED);

        // Filter by date range if provided
        List<BookingResponseDTO> history = allBookings.stream()
                .filter(b -> {
                    if (startDate == null || endDate == null)
                        return true;
                    if (b.getRide().getDepartureTime() == null)
                        return false;
                    LocalDate rideDate = b.getRide().getDepartureTime().toLocalDate();
                    LocalDate start = LocalDate.parse(startDate);
                    LocalDate end = LocalDate.parse(endDate);
                    return !rideDate.isBefore(start) && !rideDate.isAfter(end);
                })
                .map(b -> {
                    BookingResponseDTO dto = mapBookingToDto(b);
                    // Add ride status to the response
                    dto.setRideStatus(b.getRide().getStatus().name());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(history);
    }
}
