package com.example.thecrochetfactory.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.ShippingAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * A fragment to add a shipping address.
 */
public class AddAddressFragment extends Fragment {

    private EditText addressLine1, addressLine2, city, state, postalCode, country, mobileNo, email;
    private Button saveAddressButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Default constructor
    public AddAddressFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_address, container, false);

        // Initialize the views
        addressLine1 = rootView.findViewById(R.id.addressLine1);
        addressLine2 = rootView.findViewById(R.id.addressLine2);
        city = rootView.findViewById(R.id.city);
        state = rootView.findViewById(R.id.state);
        postalCode = rootView.findViewById(R.id.postalCode);
        country = rootView.findViewById(R.id.country);
        mobileNo = rootView.findViewById(R.id.mobileNo);  // Add this field
        email = rootView.findViewById(R.id.email);        // Add this field
        saveAddressButton = rootView.findViewById(R.id.saveAddressButton);

        // Set OnClickListener for save button
        saveAddressButton.setOnClickListener(v -> saveAddressToFirebase());

        return rootView;
    }

    /**
     * Validates and saves the address to Firebase Firestore.
     */
    private void saveAddressToFirebase() {
        // Validate input fields
        if (areFieldsValid()) {
            // Check if the user is logged in
            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
            if (userId == null) {
                Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique address ID using UUID
            String addressId = UUID.randomUUID().toString();

            // Create a new ShippingAddress object with mobileNo and email
            ShippingAddress newAddress = new ShippingAddress(
                    addressId,
                    userId,
                    addressLine1.getText().toString(),
                    addressLine2.getText().toString(),
                    city.getText().toString(),
                    state.getText().toString(),
                    postalCode.getText().toString(),
                    country.getText().toString(),
                    mobileNo.getText().toString(),  // Mobile Number
                    email.getText().toString()      // Email Address
            );

            // Save the address in Firestore under the user's subcollection
            db.collection("users")
                    .document(userId)
                    .collection("shipping_addresses")
                    .document(addressId)
                    .set(newAddress)
                    .addOnSuccessListener(aVoid -> {
                        // Show success message
                        Toast.makeText(getActivity(), "Address saved successfully!", Toast.LENGTH_SHORT).show();
                        Log.d("AddAddressFragment", "Address saved successfully: " + newAddress.toString());

                        // Navigate back to the previous screen
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        // Show error message if saving failed
                        Toast.makeText(getActivity(), "Failed to save address. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("AddAddressFragment", "Error saving address", e);
                    });
        }
    }

    /**
     * Validates if the input fields are filled.
     * @return true if all fields are valid, false otherwise.
     */
    private boolean areFieldsValid() {
        // Check Address Line 1
        if (TextUtils.isEmpty(addressLine1.getText())) {
            addressLine1.setError("Address Line 1 is required.");
            return false;
        }

        // Check City
        if (TextUtils.isEmpty(city.getText())) {
            city.setError("City is required.");
            return false;
        }

        // Check State
        if (TextUtils.isEmpty(state.getText())) {
            state.setError("State is required.");
            return false;
        }

        // Check Postal Code
        if (TextUtils.isEmpty(postalCode.getText())) {
            postalCode.setError("Postal code is required.");
            return false;
        }

        // Check Country
        if (TextUtils.isEmpty(country.getText())) {
            country.setError("Country is required.");
            return false;
        }

        // Check Mobile Number
        if (TextUtils.isEmpty(mobileNo.getText())) {
            mobileNo.setError("Mobile number is required.");
            return false;
        } else if (!TextUtils.isDigitsOnly(mobileNo.getText()) || mobileNo.getText().length() != 10) {
            mobileNo.setError("Mobile number must be 10 digits.");
            return false;
        }

        // Check Email
        if (TextUtils.isEmpty(email.getText())) {
            email.setError("Email is required.");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError("Invalid email format.");
            return false;
        }

        return true;
    }
}
