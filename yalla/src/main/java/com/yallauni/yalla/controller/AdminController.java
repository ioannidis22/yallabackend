package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.Admin;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.SupportTicket;
import com.yallauni.yalla.core.model.service.AdminService;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.repository.RideRepository;
import com.yallauni.yalla.core.model.repository.ReviewRepository;
import com.yallauni.yalla.core.model.repository.SupportTicketRepository;
import com.yallauni.yalla.dto.admin.AdminCreateDTO;
import com.yallauni.yalla.dto.admin.AdminResponseDTO;
import com.yallauni.yalla.dto.admin.DashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for admin operations.
 * Provides endpoints for dashboard statistics, user management,
 * and support ticket handling. Requires ADMIN role.
 */
@RestController
@RequestMapping("/api/admins") // All endpoints in this controller start with /api/admins
public class AdminController {
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final ReviewRepository reviewRepository;
    private final SupportTicketRepository ticketRepository;

    public AdminController(AdminService adminService, UserRepository userRepository,
            RideRepository rideRepository, ReviewRepository reviewRepository,
            SupportTicketRepository ticketRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.reviewRepository = reviewRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO dashboard = new DashboardDTO();

        // User counts
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalDrivers(userRepository.countByUserType(User.UserType.DRIVER));
        dashboard.setTotalPassengers(userRepository.countByUserType(User.UserType.PASSENGER));
        dashboard.setBannedUsers(userRepository.countByBannedTrue());

        // Ride counts
        dashboard.setTotalRides(rideRepository.count());
        dashboard.setActiveRides(rideRepository.countByStatus(Ride.RideStatus.IN_PROGRESS));
        dashboard.setCompletedRides(rideRepository.countByStatus(Ride.RideStatus.COMPLETED));

        // Review stats
        dashboard.setTotalReviews(reviewRepository.count());

        // Calculate average rating from all reviews
        List<com.yallauni.yalla.core.model.Review> reviews = reviewRepository.findAll();
        double avgRating = reviews.isEmpty() ? 0.0
                : reviews.stream().mapToDouble(r -> r.getRating()).average().orElse(0.0);
        dashboard.setAverageRating(Math.round(avgRating * 10.0) / 10.0); // Round to 1 decimal

        // Ticket stats
        dashboard.setTotalTickets(ticketRepository.count());
        dashboard.setPendingTickets(ticketRepository.countByStatus(SupportTicket.TicketStatus.PENDING));

        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody AdminCreateDTO adminDto) {
        // Create a new admin from the DTO and save it
        Admin admin = new Admin();
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setEmail(adminDto.getEmail());
        Admin saved = adminService.createAdmin(admin);
        AdminResponseDTO response = new AdminResponseDTO();
        response.setId(saved.getId());
        response.setUsername(saved.getUsername());
        response.setEmail(saved.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        // Find admin by id and return as DTO, or 404 if not found
        Optional<Admin> admin = adminService.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            AdminResponseDTO dto = new AdminResponseDTO();
            dto.setId(a.getId());
            dto.setUsername(a.getUsername());
            dto.setEmail(a.getEmail());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminResponseDTO> getAllAdmins() {
        // Return all admins as a list.
        List<Admin> admins = adminService.findAll();
        return admins.stream().map(a -> {
            AdminResponseDTO dto = new AdminResponseDTO();
            dto.setId(a.getId());
            dto.setUsername(a.getUsername());
            dto.setEmail(a.getEmail());
            return dto;
        }).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminCreateDTO adminDto) {
        // Update an existing admin with new data
        Admin admin = new Admin();
        admin.setId(id);
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setEmail(adminDto.getEmail());
        Admin updated = adminService.updateAdmin(id, admin);
        AdminResponseDTO response = new AdminResponseDTO();
        response.setId(updated.getId());
        response.setUsername(updated.getUsername());
        response.setEmail(updated.getEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        // Delete admin by id
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    // ----------BAN MANAGEMENT----------

    // Get all banned users
    @GetMapping("/users/banned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBannedUsers() {
        List<User> bannedUsers = userRepository.findByBannedTrue();
        return ResponseEntity.ok(bannedUsers.stream().map(this::mapUserToResponse).toList());
    }

    // Check a specific user's ban status
    @GetMapping("/users/{userId}/ban-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkUserBanStatus(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("userId", user.getUserID());
        status.put("email", user.getEmailAddress());
        status.put("banned", user.isBanned());
        status.put("banReason", user.getBanReason());
        return ResponseEntity.ok(status);
    }

    // Ban a user
    @PostMapping("/users/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(@PathVariable Long userId,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        // Prevent banning admins
        if (user.getUserType() == User.UserType.ADMIN) {
            return ResponseEntity.badRequest().body("Cannot ban an admin user");
        }

        if (user.isBanned()) { // Message for already banned user
            return ResponseEntity.badRequest().body("User is already banned");
        }

        user.setBanned(true);
        String reason = (body != null) ? body.get("reason") : null;
        user.setBanReason(reason);
        userRepository.save(user);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", "User banned successfully");
        response.put("userId", user.getUserID());
        response.put("email", user.getEmailAddress());
        response.put("banReason", reason);
        return ResponseEntity.ok(response);
    }

    // Unban a user
    @PostMapping("/users/{userId}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        if (!user.isBanned()) {
            return ResponseEntity.badRequest().body("User is not banned");
        }

        user.setBanned(false);
        user.setBanReason(null);
        userRepository.save(user);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", "User unbanned successfully");
        response.put("userId", user.getUserID());
        response.put("email", user.getEmailAddress());
        return ResponseEntity.ok(response);
    }

    private java.util.Map<String, Object> mapUserToResponse(User user) {
        java.util.Map<String, Object> dto = new java.util.HashMap<>();
        dto.put("id", user.getUserID());
        dto.put("firstName", user.getFirstName());
        dto.put("lastName", user.getLastName());
        dto.put("email", user.getEmailAddress());
        dto.put("userType", user.getUserType());
        dto.put("banned", user.isBanned());
        dto.put("banReason", user.getBanReason());
        return dto;
    }
}
