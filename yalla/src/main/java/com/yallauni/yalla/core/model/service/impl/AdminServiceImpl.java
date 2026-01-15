package com.yallauni.yalla.core.model.service.impl;

// Admin entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Admin;
// Repository for admin data (already commented elsewhere)
import com.yallauni.yalla.core.model.repository.AdminRepository;
// Admin service interface
import com.yallauni.yalla.core.model.service.AdminService;

// Marks this class as a Spring service (already commented elsewhere)
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin createAdmin(Admin admin) {
        // Create a new admin and save
        return adminRepository.save(admin);
    }

    @Override
    public Optional<Admin> findById(Long id) {
        // Find admin by id
        return adminRepository.findById(id);
    }

    @Override
    public List<Admin> findAll() {
        // Return all admins
        return adminRepository.findAll();
    }

    @Override
    public Admin updateAdmin(Long id, Admin admin) {
        // Update admin fields and save
        admin.setId(id);
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(Long id) {
        // Delete admin by id
        adminRepository.deleteById(id);
    }
}
