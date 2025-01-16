package com.example.thecrochetfactory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thecrochetfactory.fragment.UserProfileFragment;
import com.example.thecrochetfactory.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRegistration extends AppCompatActivity {

    // Declare the UI elements
    EditText name, mob, email, pass;
    TextView backtologin;
    Button btnsignup;
    FirebaseFirestore fstore;
    ProgressBar progressBar;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // To make the UI full screen (Optional)
        setContentView(R.layout.activity_user_registration);

        // Initialize the views
        name = findViewById(R.id.name);
        mob = findViewById(R.id.mob);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        backtologin = findViewById(R.id.textViewLogin);
        btnsignup = findViewById(R.id.btnsignup);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase instances
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        // If the user is already logged in, redirect to the profile page
        if (fauth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserProfileFragment.class));
            finish();
        }

        // Set up the sign-up button click listener
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String uname = name.getText().toString().trim();
                String umob = mob.getText().toString().trim();
                String uemail = email.getText().toString().trim();
                String upass = pass.getText().toString().trim();

                // Validate the inputs
                if (TextUtils.isEmpty(uemail)) {
                    email.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(uname)) {
                    name.setError("Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(umob)) {
                    mob.setError("Mobile number is required.");
                    return;
                }
                if (TextUtils.isEmpty(upass)) {
                    pass.setError("Password is required.");
                    return;
                }
                if (upass.length() < 8) {
                    pass.setError("Password must have at least 8 characters.");
                    return;
                }

                // Show progress bar while registering
                progressBar.setVisibility(View.VISIBLE);

                // Register the user using Firebase Authentication
                fauth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the UID of the newly created user
                            String userId = fauth.getCurrentUser().getUid();

                            // Create a reference to the Firestore document
                            DocumentReference documentReference = fstore.collection("users").document(userId);

                            // Create a new User object with the default role "user"
                            User user = new User(userId, uname, umob, uemail, User.Role.USER.getRole());  // Default role is "user"

                            // Save the user data to Firestore
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Show a success message
                                    Toast.makeText(UserRegistration.this, "User is successfully registered.", Toast.LENGTH_SHORT).show();

                                    // Redirect to login screen
                                    startActivity(new Intent(UserRegistration.this, UserLogin.class));
                                    progressBar.setVisibility(View.INVISIBLE);
                                    finish();
                                }
                            }).addOnFailureListener(e -> {
                                // Handle error
                                Toast.makeText(UserRegistration.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            });
                        } else {
                            // Registration failed, show error message
                            Toast.makeText(UserRegistration.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        // Navigate back to the login screen
        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserRegistration.this, UserLogin.class));
            }
        });
    }
}
