package com.example.thecrochetfactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.thecrochetfactory.fragment.AllFeedbackFragment;
import com.example.thecrochetfactory.fragment.FeedBackFragment;
import com.example.thecrochetfactory.fragment.UserCartFragment;
import com.example.thecrochetfactory.fragment.UserHomeFragment;
import com.example.thecrochetfactory.fragment.UserProfileFragment;
import com.example.thecrochetfactory.fragment.UserWishlistFragment;
import com.example.thecrochetfactory.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firebase Firestore instance
    private BottomNavigationView bottomNavigationView; // Bottom Navigation view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottombtn);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Check if the user is logged in
        if (mAuth.getCurrentUser() != null) {
            // User is logged in, check their role
            String userId = mAuth.getCurrentUser().getUid();
            checkUserRole(userId);
        } else {
            // User is not logged in, redirect to UserLogin activity
            redirectToLogin();
        }
    }

    private void checkUserRole(String userId) {
        // Fetch the user document from Firestore to get the role
        db.collection("users")  // Assuming you have a "users" collection in Firestore
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get user data from Firestore document
                            User user = document.toObject(User.class);
                            if (user != null) {
                                String role = user.getRole();

                                // Redirect based on the role
                                if ("admin".equals(role)) {
                                    // Redirect to Admin Dashboard
                                    Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                    startActivity(intent);
                                    finish(); // Close the MainActivity
                                } else {
                                    // Load user-specific home fragment
                                    loadUserHomeFragment();
                                }
                            } else {
                                // If user data is null, redirect to login
                                redirectToLogin();
                            }
                        } else {
                            // If document doesn't exist (user data might be incomplete), redirect to login
                            redirectToLogin();
                        }
                    } else {
                        // If the task failed (e.g., Firestore issues), redirect to login
                        redirectToLogin();
                    }
                });
    }

    private void loadUserHomeFragment() {
        // If the user is authenticated but not an admin, load the user home fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fraglay, new UserHomeFragment()) // Default fragment
                .commit();
    }

    private void redirectToLogin() {
        // Redirect to UserLogin activity if the user is not logged in or on error
        Intent intent = new Intent(MainActivity.this, UserLogin.class);
        startActivity(intent);
        finish(); // Finish MainActivity to ensure user cannot return to it
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;

            // Use if-else statements to handle navigation based on the selected item
            if (item.getItemId() == R.id.home) {
                selectedFragment = new UserHomeFragment();  // Navigate to the Home Fragment
            } else if (item.getItemId() == R.id.wishlist) {
                selectedFragment = new UserWishlistFragment();  // Navigate to the Wishlist Fragment
            } else if (item.getItemId() == R.id.cart) {
                selectedFragment = new UserCartFragment();  // Navigate to the Cart Fragment
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new UserProfileFragment();  // Navigate to the Profile Fragment
            } else if (item.getItemId() == R.id.contact) {
                selectedFragment = new FeedBackFragment();  // Navigate to the Feedback Fragment
            } else {
                selectedFragment = new UserHomeFragment();  // Default to Home Fragment if no match
            }

            // Replace the fragment on the screen based on the selected item
            getSupportFragmentManager().beginTransaction().replace(R.id.fraglay, selectedFragment).commit();
            return true;
        }
    };
}
