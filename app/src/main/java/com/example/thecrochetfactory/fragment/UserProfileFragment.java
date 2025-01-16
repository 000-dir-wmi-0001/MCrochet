package com.example.thecrochetfactory.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.UserLogin;
import com.example.thecrochetfactory.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {

    private Button logout, editProfile, resetPassLocal, addAddressButton, orderHistoryButton;
    private TextView profileName, profileEmail, profilePhone;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private ProgressDialog pd;
    private String msg = "Fetching data....";
    private Dialog editDialog;
    private FirebaseUser firebaseUser;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize views
        editProfile = view.findViewById(R.id.editProfile);
        logout = view.findViewById(R.id.logout);
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhone = view.findViewById(R.id.profilePhone);
        resetPassLocal = view.findViewById(R.id.resetPasswordLocal);
        addAddressButton = view.findViewById(R.id.addAddressButton);
        orderHistoryButton = view.findViewById(R.id.OrderHistoryButton);


        // Initialize Firebase instances
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        firebaseUser = fauth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Please sign in to view your profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), UserLogin.class));
            getActivity().finish();
            return view;  // Return early to avoid further code execution
        }

        String userId = firebaseUser.getUid();

        // Initialize ProgressDialog
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();

        // Fetch user data
        fetchUserData(userId);

        // Add address button
        addAddressButton.setOnClickListener(v -> navigateToAddAddressFragment());
        orderHistoryButton.setOnClickListener(v -> navigateToOrderHistoryFragment());

        // Edit profile button
        editProfile.setOnClickListener(v -> openEditDialog());

        // Logout button
        logout.setOnClickListener(v -> {
            fauth.signOut(); // Log out
            startActivity(new Intent(getActivity(), UserLogin.class));
            getActivity().finish();
        });

        // Reset password dialog
        resetPassLocal.setOnClickListener(v -> openPasswordResetDialog(v));

        return view;
    }

    private void fetchUserData(String userId) {
        // Fetch the user data from Firestore
        fstore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve user data and create a User object
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String mobile = document.getString("mobileNo");

                            // Create a User object and update UI
                            User user = new User(userId, name, mobile, email, "user");
                            updateUserProfile(user);
                        } else {
                            Log.d("UserProfileFragment", "No such document");
                            Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("UserProfileFragment", "Fetch failed: ", task.getException());
                        Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                    if (pd.isShowing()) pd.dismiss();
                });
    }

    private void updateUserProfile(User user) {
        // Ensure the user object is not null before updating UI
        if (user != null) {
            profileName.setText(user.getName());
            profileEmail.setText(user.getEmail());
            profilePhone.setText(user.getMobileNo());
        }
    }

    private void openEditDialog() {
        editDialog = new Dialog(getActivity());
        editDialog.setContentView(R.layout.edit_profile_dialog);
        editDialog.setTitle("Edit Profile");

        EditText etName = editDialog.findViewById(R.id.et_name);
        EditText etEmail = editDialog.findViewById(R.id.et_email);
        EditText etMobileNo = editDialog.findViewById(R.id.et_mobile_no);

        // Set initial values from current user profile
        etName.setText(profileName.getText().toString());
        etEmail.setText(profileEmail.getText().toString());
        etMobileNo.setText(profilePhone.getText().toString());

        Button btnSave = editDialog.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String mobileNo = etMobileNo.getText().toString();

            if (isValidInput(name, email, mobileNo)) {
                updateProfile(name, email, mobileNo);
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                editDialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please fill in all fields correctly!", Toast.LENGTH_SHORT).show();
            }
        });

        editDialog.show();
    }

    private void updateProfile(String name, String email, String mobileNo) {
        // Update the user data in Firestore
        User updatedUser = new User(firebaseUser.getUid(), name, mobileNo, email, "user");

        // Update the user in Firestore
        fstore.collection("users").document(firebaseUser.getUid())
                .set(updatedUser)  // Using the User model for the update
                .addOnSuccessListener(aVoid -> Log.d("UserProfileFragment", "Profile updated"))
                .addOnFailureListener(e -> Log.e("UserProfileFragment", "Update failed: ", e));
    }

    private boolean isValidInput(String name, String email, String mobileNo) {
        // Validation for user input
        if (name.isEmpty() || email.isEmpty() || mobileNo.isEmpty()) {
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return isValidMobileNo(mobileNo);
    }

    private boolean isValidMobileNo(String mobileNo) {
        return mobileNo.length() >= 10 && mobileNo.length() <= 12;
    }

    private void navigateToAddAddressFragment() {
        // Replace the current fragment with AddAddressFragment
        AddAddressFragment addAddressFragment = new AddAddressFragment();

        // Begin the fragment transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Optionally, add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fraglay, addAddressFragment);  // Make sure fraglay is the ID of your container
        transaction.addToBackStack(null);  // This allows the user to navigate back to the previous fragment

        // Commit the transaction to apply the changes
        transaction.commit();
    }

    private void navigateToOrderHistoryFragment() {
        // Replace the current fragment with AddAddressFragment
        UserOrdersFragment ordersHistoryFragment = new UserOrdersFragment();

        // Begin the fragment transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Optionally, add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fraglay, ordersHistoryFragment);  // Make sure fraglay is the ID of your container
        transaction.addToBackStack(null);  // This allows the user to navigate back to the previous fragment

        // Commit the transaction to apply the changes
        transaction.commit();
    }

    private void openPasswordResetDialog(View v) {
        final EditText resetPassword = new EditText(v.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter a new password > 6 characters long.");
        passwordResetDialog.setView(resetPassword);

        passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
            String newPassword = resetPassword.getText().toString();
            if (newPassword.length() >= 6) {
                firebaseUser.updatePassword(newPassword)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Password reset successfully.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Password reset failed.", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getActivity(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            }
        });

        passwordResetDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        passwordResetDialog.create().show();
    }
}
