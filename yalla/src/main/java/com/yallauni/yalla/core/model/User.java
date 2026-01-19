// User entity for application users
package com.yallauni.yalla.core.model;

import com.fasterxml.jackson.annotation.JsonProperty; // for serialization control
import jakarta.persistence.*; // JPA annotations
import jakarta.validation.constraints.*; // validation constraints
import java.util.List; // list for relationships
import java.util.Objects; // for equals/hashCode

@Entity
@Table(name = "app_user", uniqueConstraints = {
        @UniqueConstraint(name = "user_email_address", columnNames = "email_address"),
        @UniqueConstraint(name = "user_phone_number", columnNames = "mobile_phone_number")
}, indexes = {
        @Index(name = "idx_user_email_address", columnList = "email_address"),
        @Index(name = "idx_user_phone_number", columnList = "mobile_phone_number")
})
public class User {

    @NotNull
    @NotBlank
    @Size(min = 6, max = 100)
    @Column(name = "password", nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
    @Size(min = 10, max = 18)
    @Column(name = "mobile_phone_number", nullable = false, length = 18)
    private String mobilePhoneNumber; // E164 format.

    @Size(max = 255)
    private String profilePictureUrl;

    @Size(max = 255)
    private String address;

    @Column(nullable = false)
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private double rating = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private UserGender gender;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Size(max = 500)
    private String about;

    private List<String> preferences;

    // Typical getters and setters
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
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

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
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
    @Column(name = "driver_license", nullable = true)
    private String driverLicense;

    @Column(nullable = false)
    private boolean banned = false;

    @Size(max = 500)
    @Column(name = "ban_reason")
    private String banReason;

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return userID != null && Objects.equals(userID, user.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

}
