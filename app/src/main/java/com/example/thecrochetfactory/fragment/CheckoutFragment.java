package com.example.thecrochetfactory.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.SuccessPay;
import com.example.thecrochetfactory.model.Cart;
import com.example.thecrochetfactory.model.Order;
import com.example.thecrochetfactory.model.Payment;
import com.example.thecrochetfactory.model.Payment.PaymentMethod;
import com.example.thecrochetfactory.model.ShippingAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutFragment extends Fragment {

    private TextView totalAmountText;
    private Button placeOrderButton;
    private RadioGroup addressRadioGroup;
    private RadioGroup paymentMethodRadioGroup;
    private ImageView qrCodeImageView;

    private Cart cart;
    private ShippingAddress selectedAddress;
    private FirebaseFirestore firestore;

    // Constructor for CheckoutFragment, taking Cart as argument
    public CheckoutFragment() {
        // Default constructor required for Fragment
    }

    // Factory method to create a new instance of CheckoutFragment with Cart
    public static CheckoutFragment newInstance(Cart cart) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putParcelable("cart", cart);  // Pass Cart object as Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve Cart object from Bundle
        if (getArguments() != null) {
            cart = getArguments().getParcelable("cart");
        }

        // Initialize views
        totalAmountText = view.findViewById(R.id.total_amount);
        placeOrderButton = view.findViewById(R.id.place_order_button);
        addressRadioGroup = view.findViewById(R.id.address_radio_group);
        paymentMethodRadioGroup = view.findViewById(R.id.payment_method_group);
        qrCodeImageView = view.findViewById(R.id.qr_code_image);

        firestore = FirebaseFirestore.getInstance();

        // Set the total price dynamically from cart
        updateTotalAmount();

        // Fetch and display shipping addresses
        loadShippingAddresses();

        // Handle place order button click
        placeOrderButton.setOnClickListener(v -> handlePlaceOrder());

        // Add listener to handle showing/hiding QR code based on payment method selection
        if (paymentMethodRadioGroup != null) {
            paymentMethodRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton selectedPaymentRadioButton = view.findViewById(checkedId);
                String paymentMethod = selectedPaymentRadioButton.getText().toString().trim();

                // Show or hide QR code image depending on the selected payment method
                if ("QR Code Payment".equals(paymentMethod)) {
                    qrCodeImageView.setVisibility(View.VISIBLE);  // Show QR Code image for QR payment
                } else {
                    qrCodeImageView.setVisibility(View.GONE);  // Hide QR Code image for COD
                }
            });
        }
    }

    private void updateTotalAmount() {
        double totalAmount = cart.getTotalPrice();  // Assuming Cart has a method to get the total price
        totalAmountText.setText("₹" + totalAmount);
    }

    private void loadShippingAddresses() {
        // Get the current user ID from FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId == null) {
            Toast.makeText(getActivity(), "User is not logged in", Toast.LENGTH_SHORT).show();
            return;  // Exit the function if the user is not logged in
        }

        // Fetch the user's shipping addresses from Firestore based on the user ID
        firestore.collection("users")  // Assuming addresses are stored under a subcollection "shipping_addresses" of a "users" document
                .document(userId)  // Access the user's document by their user ID
                .collection("shipping_addresses")  // Collection where the addresses are stored
                .get()  // Get all the documents in the "shipping_addresses" collection
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<ShippingAddress> addressList = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            ShippingAddress address = queryDocumentSnapshots.getDocuments().get(i).toObject(ShippingAddress.class);
                            if (address != null) {
                                addressList.add(address);  // Add the address to the list
                                addRadioButtonForAddress(address);  // Method to add a RadioButton for each address
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "No addresses found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error fetching addresses", Toast.LENGTH_SHORT).show();
                });
    }


    private void addRadioButtonForAddress(ShippingAddress address) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(address.getAddressLine1()+", "+address.getAddressLine2() + ", " + address.getCity()+", "+address.getState()+", "+address.getPostalCode()+", "+address.getCountry());
        radioButton.setTag(address);  // Store the address as the tag for later use
        addressRadioGroup.addView(radioButton);  // Add the RadioButton to the RadioGroup
    }

    private void handlePlaceOrder() {
        // Ensure an address is selected
        int selectedAddressId = addressRadioGroup.getCheckedRadioButtonId();
        if (selectedAddressId == -1) {
            Toast.makeText(getActivity(), "Please select a shipping address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected address
        RadioButton selectedAddressRadioButton = getView().findViewById(selectedAddressId);
        selectedAddress = (ShippingAddress) selectedAddressRadioButton.getTag();

        // Ensure a payment method is selected
        int selectedPaymentId = paymentMethodRadioGroup.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(getActivity(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected payment method
        RadioButton selectedPaymentRadioButton = getView().findViewById(selectedPaymentId);
        String paymentMethod = selectedPaymentRadioButton.getText().toString().trim();

        // Fetch username from Firebase (currently using FirebaseAuth)
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (username == null) {
            username = "Unknown User"; // Default value if username is not set
        }

        // Create Order
        String orderId = "ORD-" + System.currentTimeMillis();  // Generate unique order ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Fetch user ID from Firebase
        List<Order.OrderItem> items = convertCartToOrderItems(cart.getItems());  // Convert Cart items to Order items

        Order order = new Order(
                orderId,              // Order ID (e.g., "ORD-001")
                userId,               // User ID (e.g., "user123")
                username,             // User's username (retrieved from Firebase)
                items,                // List of OrderItems
                Order.OrderStatus.PENDING,  // Correct enum for OrderStatus
                Order.PaymentStatus.PENDING,  // Correct enum for PaymentStatus
                selectedAddress       // Selected shipping address
        );

        // Create Payment instance (you can modify this logic for real payment handling)
        Payment.PaymentMethod method = paymentMethod.equals("COD") ? Payment.PaymentMethod.COD : Payment.PaymentMethod.QR_CODE;
        Payment payment = new Payment(orderId, orderId, cart.getTotalPrice(), method, Payment.PaymentStatus.PENDING);

        // Save Order and Payment to Firestore
        firestore.collection("orders").document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    firestore.collection("payments").document(orderId)
                            .set(payment)
                            .addOnSuccessListener(aVoid1 -> {

                                clearCart();
                                Intent intent = new Intent(getActivity(), SuccessPay.class);
                                startActivity(intent);



                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Payment failed!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Order failed!", Toast.LENGTH_SHORT).show();
                });
    }


    private void clearCart() {
        // Get the current user ID from FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId == null) {
            return;  // Return if the user is not logged in
        }

        // Reference to the user's document in the Cart collection
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference cartRef = firestore.collection("Cart").document(userId);

        // Create a map to reset the cart fields to default values
        Map<String, Object> cartResetData = new HashMap<>();
        cartResetData.put("items", new ArrayList<>());  // Clear cartItems field
        cartResetData.put("totalPrice", 0);  // Reset totalPrice to 0

        // Update the user's cart document with the reset data
        cartRef.update(cartResetData);
    }







    private List<Order.OrderItem> convertCartToOrderItems(List<Cart.CartItem> cartItems) {
        // Convert CartItems to OrderItems
        List<Order.OrderItem> orderItems = new ArrayList<>();
        for (Cart.CartItem cartItem : cartItems) {
            orderItems.add(new Order.OrderItem(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getPrice()));
        }
        return orderItems;
    }
}
