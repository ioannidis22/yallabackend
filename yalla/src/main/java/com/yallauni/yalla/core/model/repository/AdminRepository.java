package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yallauni.yalla.core.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
