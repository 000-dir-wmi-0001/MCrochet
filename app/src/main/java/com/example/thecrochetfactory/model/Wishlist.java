package com.example.thecrochetfactory.model;

import java.util.ArrayList;
import java.util.List;

public class Wishlist {

    private String userId;            // User who owns the wishlist
    private List<WishlistItem> items; // List of saved products

    // Default constructor for Firebase (initialize items to an empty list)
    public Wishlist() {
        this.items = new ArrayList<>();  // Avoid null reference when accessing items
    }

    // Parameterized constructor to initialize the fields
    public Wishlist(String userId, List<WishlistItem> items) {
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();  // Prevent null list
    }

    // Getter and setter methods for each field
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<WishlistItem> getItems() {
        return items;
    }

    public void setItems(List<WishlistItem> items) {
        this.items = items != null ? items : new ArrayList<>();  // Prevent null list
    }

    // WishlistItem class for holding individual product details
    public static class WishlistItem {
        private String productId;    // Product ID
        private String productName;  // Product name
        private double productPrice; // Product price
        private String productImage; // Product image URL (Optional)

        // Default constructor for Firebase
        public WishlistItem() {
            // Empty constructor for Firebase
        }

        // Parameterized constructor with image
        public WishlistItem(String productId, String productName, double productPrice, String productImage) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.productImage = productImage;  // Image is optional
        }

        // Constructor without image
        public WishlistItem(String productId, String productName, double productPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
        }

        // Getter and setter methods
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(double productPrice) {
            this.productPrice = productPrice;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        @Override
        public String toString() {
            return "WishlistItem{" +
                    "productId='" + productId + '\'' +
                    ", productName='" + productName + '\'' +
                    ", productPrice=" + productPrice +
                    ", productImage='" + productImage + '\'' +
                    '}';
        }
    }
}