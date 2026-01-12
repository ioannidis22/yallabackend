package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

import com.yallauni.yalla.core.model.Admin;

public interface AdminService {
    Admin createAdmin(Admin admin);

    Optional<Admin> findById(Long id);

    List<Admin> findAll();

    Admin updateAdmin(Long id, Admin admin);

    void deleteAdmin(Long id);
}
