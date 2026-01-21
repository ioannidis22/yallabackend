package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yallauni.yalla.core.model.Admin;

/**
 * Repository for Admin database operations.
 * Extends JpaRepository to provide standard CRUD operations.
 * Also used by AdminService for admin data access.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
