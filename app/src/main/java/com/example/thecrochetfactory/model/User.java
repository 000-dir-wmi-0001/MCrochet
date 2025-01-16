package com.example.thecrochetfactory.model;

import java.util.Objects;

public class User {

    private String userId;      // Unique ID for the user (Typically Firebase-generated)
    private String name;
    private String mobileNo;
    private String email;
    private String role;        // User role (user/admin)

    // Default constructor required for Firebase Realtime Database or Firestore
    public User() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the fields (Including userId)
    public User(String userId, String name, String mobileNo, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.mobileNo = mobileNo;
        this.email = email;
        this.role = role;
    }

    // Getter and setter methods for each field
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Optional: Overriding equals and hashCode to compare User objects by userId or email
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) || Objects.equals(email, user.email);  // Compare by userId or email
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);  // Generate hashcode based on userId or email
    }

    // Optional: Enum for Role (useful for restricting possible roles)
    public enum Role {
        USER("user"),
        ADMIN("admin");

        private final String role;

        Role(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}


