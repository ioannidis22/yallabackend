package com.yallauni.yalla.core.model.repository;

// Spring Data JPA base repository
import org.springframework.data.jpa.repository.JpaRepository;

// Admin entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
