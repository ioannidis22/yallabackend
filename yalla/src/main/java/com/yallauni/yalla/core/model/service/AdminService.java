package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;
import com.yallauni.yalla.core.model.Admin;

/**
 * Service interface for admin-related operations.
 * Defines CRUD operations for managing administrator accounts.
 * Implemented by AdminServiceImpl.
 */
public interface AdminService {
    // Create a new admin
    Admin createAdmin(Admin admin);

    // Find admin by id
    Optional<Admin> findById(Long id);

    // Return all admins
    List<Admin> findAll();

    // Update admin fields
    Admin updateAdmin(Long id, Admin admin);

    // Delete admin by id
    void deleteAdmin(Long id);
}
