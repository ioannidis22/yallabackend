package com.yallauni.yalla.controller;


// Admin entity
import com.yallauni.yalla.core.model.Admin;
// Service for admin logic
import com.yallauni.yalla.core.model.service.AdminService;
// DTO for creating admin
import com.yallauni.yalla.dto.admin.AdminCreateDTO;
// DTO for returning admin data
import com.yallauni.yalla.dto.admin.AdminResponseDTO;

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

    
    public AdminController(AdminService adminService) {
        // Inject the admin service
        this.adminService = adminService;
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
