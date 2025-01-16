package com.example.thecrochetfactory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Cart implements Parcelable {

    private String userId;           // User who owns the cart
    private List<CartItem> items;    // List of products in the cart
    private double totalPrice;       // Total price of all items in the cart

    // Default constructor for Firebase
    public Cart() {
        // Empty constructor for Firebase
    }

    // Parameterized constructor to initialize the fields
    public Cart(String userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = calculateTotalPrice();  // Calculate total price when cart is initialized
    }



    // Getter and setter methods for each field
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        this.totalPrice = calculateTotalPrice();  // Recalculate total price when items are updated
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Method to calculate the total price of the cart
    private double calculateTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();  // Price * quantity for each item
        }
        return total;
    }

    // Parcelable implementation for Cart
    protected Cart(Parcel in) {
        userId = in.readString();
        items = in.createTypedArrayList(CartItem.CREATOR);
        totalPrice = in.readDouble();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeTypedList(items);
        dest.writeDouble(totalPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Inner class for CartItem, also implementing Parcelable
    public static class CartItem implements Parcelable {
        private String productId;  // Reference to the product in the cart
        private String productName; // Product name
        private String imageUrl;   // Product image URL
        private int quantity;      // Number of this product in the cart
        private double price;      // Price of the product

        // Empty constructor for Firebase
        public CartItem() {
        }

        // Parameterized constructor to initialize fields
        public CartItem(String productId, String productName, String imageUrl, int quantity, double price) {
            this.productId = productId;
            this.productName = productName;
            this.imageUrl = imageUrl;
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

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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
            return "CartItem{" +
                    "productId='" + productId + '\'' +
                    ", productName='" + productName + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }

        // Parcelable implementation for CartItem
        protected CartItem(Parcel in) {
            productId = in.readString();
            productName = in.readString();
            imageUrl = in.readString();
            quantity = in.readInt();
            price = in.readDouble();
        }

        public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
            @Override
            public CartItem createFromParcel(Parcel in) {
                return new CartItem(in);
            }

            @Override
            public CartItem[] newArray(int size) {
                return new CartItem[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(productId);
            dest.writeString(productName);
            dest.writeString(imageUrl);
            dest.writeInt(quantity);
            dest.writeDouble(price);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }

    public void resetTotalPrice(int i) {
        this.totalPrice = calculateTotalPrice();  // Recalculate the total price based on the current items
    }
}
