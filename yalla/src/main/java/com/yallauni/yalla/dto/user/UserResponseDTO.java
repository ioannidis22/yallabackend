package com.yallauni.yalla.dto.user;

import com.yallauni.yalla.core.model.User;

/**
 * Data Transfer Object for returning user information in API responses.
 * Contains user profile data excluding sensitive information like passwords.
 * Includes a factory method to convert from User entity to DTO.
 */
public class UserResponseDTO {
    /** Unique identifier of the user */
    private Long id;
    /** User's first name */
    private String firstName;
    /** User's last name */
    private String lastName;
    /** User's email address (used for login) */
    private String email;
    /** User's mobile phone number */
    private String phoneNumber;
    /** Type of user: DRIVER, PASSENGER, or ADMIN */
    private String userType;
    /** User's average rating (1.0 - 5.0) */
    private Double rating;
    /** Whether the user account is banned */
    private boolean banned;
    /** Reason for the ban (if banned) */
    private String banReason;

    public static UserResponseDTO fromEntity(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getUserID());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmailAddress());
        dto.setPhoneNumber(user.getMobilePhoneNumber());
        dto.setUserType(user.getUserType() != null ? user.getUserType().name() : null);
        dto.setRating(user.getRating());
        dto.setBanned(user.isBanned());
        dto.setBanReason(user.getBanReason());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }
}