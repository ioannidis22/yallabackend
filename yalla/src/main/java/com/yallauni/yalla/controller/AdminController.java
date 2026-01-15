package com.yallauni.yalla.controller;

// Admin entity
import com.yallauni.yalla.core.model.Admin;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
// Service for admin logic
import com.yallauni.yalla.core.model.service.AdminService;
// Repositories for dashboard stats
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.repository.RideRepository;
import com.yallauni.yalla.core.model.repository.ReviewRepository;
// DTO for creating admin
import com.yallauni.yalla.dto.admin.AdminCreateDTO;
// DTO for returning admin data
import com.yallauni.yalla.dto.admin.AdminResponseDTO;
// DTO for dashboard stats
import com.yallauni.yalla.dto.admin.DashboardDTO;

// Used for HTTP responses
import org.springframework.http.ResponseEntity;
// Spring REST controller annotations (already commented elsewhere)
import org.springframework.web.bind.annotation.*;

// Annotation for method-level security
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins") // All endpoints in this controller start with /api/admins
public class AdminController {
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final ReviewRepository reviewRepository;

    public AdminController(AdminService adminService, UserRepository userRepository,
            RideRepository rideRepository, ReviewRepository reviewRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO dashboard = new DashboardDTO();

        // User counts
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalDrivers(userRepository.countByUserType(User.UserType.DRIVER));
        dashboard.setTotalPassengers(userRepository.countByUserType(User.UserType.PASSENGER));

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
        // Return all admins as a list of DTOs
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
}
