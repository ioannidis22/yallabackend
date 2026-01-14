package com.yallauni.yalla.config;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes the database with a default admin user on application startup.
 * This ensures that an administrator account is always available.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createDefaultAdminIfNotExists();
    }

    private void createDefaultAdminIfNotExists() {
        String adminEmail = "admin@yalla.com";
        
        // Check if admin already exists
        if (userRepository.findByEmailAddress(adminEmail).isPresent()) {
            logger.info("Default admin user already exists");
            return;
        }

        // Create default admin user
        User admin = new User();
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setEmailAddress(adminEmail);
        admin.setMobilePhoneNumber("+306900000000");
        admin.setPassword(passwordEncoder.encode("admin123")); // BCrypt encrypted
        admin.setUserType(User.UserType.ADMIN);
        admin.setRating(5.0);

        userRepository.save(admin);
        logger.info("Default admin user created with email: {}", adminEmail);
    }
}