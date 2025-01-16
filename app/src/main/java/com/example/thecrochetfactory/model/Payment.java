package com.example.thecrochetfactory.model;

public class Payment {

    private String paymentId;
    private String orderId;
    private double amount;
    private PaymentMethod paymentMethod;  // COD or QR_CODE
    private PaymentStatus paymentStatus;  // e.g., COMPLETED, PENDING, FAILED

    // Default constructor for Firebase
    public Payment() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the fields
    public Payment(String paymentId, String orderId, double amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.setAmount(amount); // Validate amount
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;  // Default to PENDING if null
    }

    // Getter and setter methods for each field
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount >= 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                '}';
    }

    // Enum for PaymentMethod (COD, QR_CODE)
    public enum PaymentMethod {
        COD,        // Cash on Delivery
        QR_CODE     // QR Code (static image)
    }

    // Enum for PaymentStatus (PENDING, COMPLETED, FAILED)
    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED
    }


}
