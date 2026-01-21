package com.yallauni.yalla.config;

import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This class initializes in the database with a default admin user when the
 * application starts.
 * It ensures that there is always at least one administrator account in the
 * system.
 */
// Marks this class as a Spring component so it is detected and managed by the
// Spring container.
@Component
public class DataInitializer implements CommandLineRunner {

    // Logger for outputting information to the application logs.
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    // Repository for accessing and saving User entities.
    private final UserRepository userRepository;
    // Password encoder for securely storing of user passwords.
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for required dependencies.
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // This method is called automatically when the application starts.
    @Override
    public void run(String... args) {
        createDefaultAdminIfNotExists();
    }

    /**
     * Checks if the default admin user exists, and creates it if it is not.
     * This prevents duplicate admin accounts and ensures secure password storage.
     */
    private void createDefaultAdminIfNotExists() {
        String adminEmail = "admin@yalla.com";

        // Checks if an admin user with the specified email already exists.
        if (userRepository.findByEmailAddress(adminEmail).isPresent()) {
            logger.info("Default admin already exists");
            return;
        }

        // Create and configure the default admin user
        User admin = new User();
        admin.setFirstName("System"); // Sets admin's first name
        admin.setLastName("Administrator"); // Sets admin's last name
        admin.setEmailAddress(adminEmail); // Sets admin's email
        admin.setMobilePhoneNumber("+306900000000"); // Sets admin's default phone number
        admin.setPassword(passwordEncoder.encode("admin123")); // Encrypts and sets admin's password
        admin.setUserType(User.UserType.ADMIN); // Sets user type as ADMIN
        admin.setRating(5.0); // Set default rating for admin

        // Save the new admin user to the database
        userRepository.save(admin);
        logger.info("Default admin user created with email: {}", adminEmail);
    }
}