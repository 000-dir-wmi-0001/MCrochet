package com.example.thecrochetfactory.model;

public class ShippingAddress {

    private String addressId;
    private String userId;
    private String addressLine1;
    private String addressLine2; // Optional
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String mobileNo; // Added field for mobile number
    private String email;    // Added field for email address

    // Default constructor for Firebase
    public ShippingAddress() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the fields
    public ShippingAddress(String addressId, String userId, String addressLine1, String addressLine2,
                           String city, String state, String postalCode, String country,
                           String mobileNo, String email) {
        this.addressId = addressId;
        this.userId = userId;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.setPostalCode(postalCode);  // Set postal code using setter to apply validation
        this.country = country;
        this.mobileNo = mobileNo;  // Initialize mobileNo
        this.email = email;        // Initialize email
    }

    // Getter and setter methods for each field
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    // Optional: validate postal code format based on your country or rules
    public void setPostalCode(String postalCode) {
        // Example validation for postal code (basic check: only digits, 5-10 characters)
        if (postalCode.matches("\\d{5,10}")) {
            this.postalCode = postalCode;
        } else {
            throw new IllegalArgumentException("Invalid postal code format.");
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobileNo() {
        return mobileNo;  // Getter for mobile number
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;  // Setter for mobile number
    }

    public String getEmail() {
        return email;  // Getter for email
    }

    public void setEmail(String email) {
        this.email = email;  // Setter for email
    }

    @Override
    public String toString() {
        return "ShippingAddress{" +
                "addressId='" + addressId + '\'' +
                ", userId='" + userId + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    // Optional: Override equals() and hashCode() for object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShippingAddress that = (ShippingAddress) obj;
        return addressId != null && addressId.equals(that.addressId);
    }

    @Override
    public int hashCode() {
        return addressId != null ? addressId.hashCode() : 0;
    }
}
