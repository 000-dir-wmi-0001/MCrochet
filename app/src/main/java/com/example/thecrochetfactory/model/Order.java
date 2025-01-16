package com.example.thecrochetfactory.model;

import java.util.List;

public class Order {

    private String orderId;
    private String userId;
    private String username;  // Add this field for the username
    private List<OrderItem> items;  // List of products in the order
    private OrderStatus status;     // Enum for Order status
    private PaymentStatus paymentStatus;   // Enum for Payment status
    private ShippingAddress shippingAddress; // Shipping address for the order (using ShippingAddress model)
    private double totalAmount;     // Total order cost (calculated)

    // Default constructor for Firebase
    public Order() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the fields
    public Order(String orderId, String userId, String username, List<OrderItem> items,
                 OrderStatus status, PaymentStatus paymentStatus, ShippingAddress shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.username = username;  // Initialize the username
        this.items = items;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.shippingAddress = shippingAddress;
        this.totalAmount = calculateTotalAmount(); // Calculate total dynamically
    }

    // Method to calculate total amount dynamically based on order items
    private double calculateTotalAmount() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    // Getter and setter methods for each field
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username; // Add this getter method
    }

    public void setUsername(String username) {
        this.username = username; // Add this setter method
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.totalAmount = calculateTotalAmount(); // Recalculate total when items change
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +  // Include username in toString
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", shippingAddress=" + shippingAddress +
                ", totalAmount=" + totalAmount +
                '}';
    }

    // Enum for OrderStatus (e.g., PENDING, SHIPPED, DELIVERED)
    public enum OrderStatus {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    public enum PaymentStatus {
        PAID,
        UNPAID,
        PENDING,
        REFUNDED
    }

    // Inner class for OrderItem (contains individual product details in the order)
    public static class OrderItem {
        private String productId;  // Reference to the product
        private int quantity;      // Quantity ordered
        private double price;      // Price at the time of order

        // Default constructor for Firebase
        public OrderItem() {
            // Empty constructor for Firebase
        }

        // Parameterized constructor to initialize the fields
        public OrderItem(String productId, int quantity, double price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        // Getter and setter methods for each field
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "OrderItem{" +
                    "productId='" + productId + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }
    }
}
