package com.yallauni.yalla.dto.user;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for user creation and updates.
 * Contains fields for user registration and profile updates.
 */
public class UserCreateDTO {

    // User's first name (max 100 characters)
    @Size(max = 100)
    private String firstName;

    // User's last name (max 100 characters)
    @Size(max = 100)
    private String lastName;

    // User's email address (must be valid email format)
    @Email
    private String email;

    // User's password (min 8 characters for security)
    @Size(min = 8, max = 100)
    private String password;

    // User's phone number
    @Size(max = 20)
    private String phoneNumber;

    // User type: PASSENGER, DRIVER, or ADMIN
    private String userType;

    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}