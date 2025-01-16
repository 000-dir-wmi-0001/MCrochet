package com.example.thecrochetfactory.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String productId;   // Unique ID for the product
    private String name;        // Name of the product
    private String category;    // Category of the product
    private String imageUrl;    // Image URL for the product
    private double price;       // Price of the product
    private int stockQuantity;  // Quantity of the product in stock

    // Default constructor for Firebase
    public Product() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the fields
    public Product(String productId, String name, String category, String imageUrl, double price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.setPrice(price);  // Validate price through setter
        this.setStockQuantity(stockQuantity);  // Ensure non-negative stock
    }

    // Getter and setter methods for each field
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    // Ensure price is never negative
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Ensure stockQuantity is never negative
    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity >= 0) {
            this.stockQuantity = stockQuantity;
        } else {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    // Method to reduce stock quantity when an order is placed (to ensure stock integrity)
    public void reduceStock(int quantity) {
        if (quantity <= stockQuantity) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Not enough stock available");
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
