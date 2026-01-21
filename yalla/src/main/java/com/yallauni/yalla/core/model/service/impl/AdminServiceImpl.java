package com.yallauni.yalla.core.model.service.impl;

import com.yallauni.yalla.core.model.Admin;
import com.yallauni.yalla.core.model.repository.AdminRepository;
import com.yallauni.yalla.core.model.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of AdminService.
 * Provides business logic for admin CRUD operations.
 * Also uses AdminRepository for database interactions.
 */
@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin createAdmin(Admin admin) {
        // Create a new admin
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
