package com.yallauni.yalla.service;

import com.yallauni.yalla.model.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminService {
    Admin createAdmin(Admin admin);

    Optional<Admin> findById(Long id);

    List<Admin> findAll();

    Admin updateAdmin(Long id, Admin admin);

    void deleteAdmin(Long id);
}
