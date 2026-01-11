
package com.yallauni.yalla.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "user_id", columnNames = "user_id"),
        @UniqueConstraint(name = "user_email_address", columnNames = "email_address"),
        @UniqueConstraint(name = "user_phone_number", columnNames = "phone_number")
}, indexes = {
        @Index(name = "idx_user_email_address", columnList = "email_address"),
        @Index(name = "idx_user_phone_number", columnList = "phone_number")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userID;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(name = "email_address", nullable = false, length = 100)
    private String emailAddress;

    @NotNull
    @NotBlank
    @Size(max = 18)
    @Column(name = "mobile_phone_number", nullable = false, length = 18)
    private String mobilePhoneNumber; // E164 format.

    @Size(max = 255)
    private String profilePictureUrl;

    @Size(max = 255)
    private String address;

    @NotNull
    @Column(nullable = false)
    @Size(min = 0, max = 5)
    private double rating;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserGender gender;

    @NotNull
    @Size(max = 500)
    private String about;

    @NotNull
    private List<String> preferences;

    // Getters and setters
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public enum UserGender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum UserType {
        PASSENGER,
        DRIVER,
        ADMIN
    }

    // Driver-specific fields (nullable for non-drivers)
    @Size(max = 30)
    @Column(name = "driver_license")
    private String driverLicense;

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

}
