package com.example.thecrochetfactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thecrochetfactory.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Duration in milliseconds (3 seconds)
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firebase Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check user login status after splash delay
        new Handler().postDelayed(() -> {
            if (mAuth.getCurrentUser() != null) {
                // User is logged in, check their role and other details
                String userId = mAuth.getCurrentUser().getUid();
                checkUserRole(userId);
            } else {
                // User is not logged in, redirect to UserLogin activity
                Intent intent = new Intent(SplashActivity.this, UserLogin.class);
                startActivity(intent);
                finish(); // Finish the splash activity so it doesn't return on back button
            }
        }, SPLASH_DURATION);
    }

    private void checkUserRole(String userId) {
        // Fetch the user document from Firestore to get user details
        db.collection("users")  // Assuming you have a "users" collection in Firestore
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve user data based on the User model
                            User user = document.toObject(User.class);
                            if (user != null) {
                                // Check the user role
                                String role = user.getRole();

                                if (role != null) {
                                    // Redirect based on the role
                                    if (role.equals("admin")) {
                                        // Admin role - Redirect to Admin Dashboard
                                        Intent intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Regular user role - Redirect to User Dashboard
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    // If the role is not found, handle as needed
                                    // Default to regular user dashboard if role is missing
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                // If the user document does not map to a User object (null), handle as needed
                                Intent intent = new Intent(SplashActivity.this, UserLogin.class);
                                startActivity(intent);
                            }
                        } else {
                            // If document doesn't exist, handle as needed (user data might be incomplete)
                            Intent intent = new Intent(SplashActivity.this, UserLogin.class);
                            startActivity(intent);
                        }
                    } else {
                        // If fetching user data fails, redirect to login
                        Intent intent = new Intent(SplashActivity.this, UserLogin.class);
                        startActivity(intent);
                    }
                    finish(); // Close the splash screen after role-based redirection
                });
    }
}

