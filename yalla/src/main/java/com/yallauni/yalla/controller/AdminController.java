package com.yallauni.yalla.controller;


import com.yallauni.yalla.core.model.Admin;
import com.yallauni.yalla.core.model.service.AdminService;
import com.yallauni.yalla.dto.admin.AdminCreateDTO;
import com.yallauni.yalla.dto.admin.AdminResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminService adminService;

    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody AdminCreateDTO adminDto) {
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
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
