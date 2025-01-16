package com.example.thecrochetfactory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thecrochetfactory.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserLogin extends AppCompatActivity {

    // Declare the UI elements
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize the views
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.pass);
        progressBar = findViewById(R.id.progressBar2);
        mLoginBtn = findViewById(R.id.btnlogin);
        mCreateBtn = findViewById(R.id.textViewSignUp);
        forgotTextLink = findViewById(R.id.forgotPass);

        // Initialize Firebase instances
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Go to registration page
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserLogin.this, UserRegistration.class);
                startActivity(i);
            }
        });

        // Login button click handler
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // Validations for email and password
                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Valid Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Authenticate the user with Firebase Auth
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the current user's UID
                            FirebaseUser user = fAuth.getCurrentUser();
                            if (user != null) {
                                userID = user.getUid();

                                // Check the user's role in Firestore
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<com.google.firebase.firestore.DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                // Retrieve the role of the user from Firestore
                                                String role = task.getResult().getString("role");

                                                // Redirect to admin or user home based on role
                                                if ("admin".equals(role)) {
                                                    // Redirect to admin activity if the role is admin
                                                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                                                } else if ("user".equals(role)) {
                                                    // Redirect to user activity if the role is user
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                } else {
                                                    // In case role is not set correctly
                                                    Toast.makeText(UserLogin.this, "Role not found.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(UserLogin.this, "User role not found in Firestore.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(UserLogin.this, "Error fetching user role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(UserLogin.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        // If the user is already logged in, skip the login process
        if (fAuth.getCurrentUser() != null) {
            userID = fAuth.getCurrentUser().getUid();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // Handle forgot password flow
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Extract the email and send a reset link
                        String mail = resetMail.getText().toString();

                        // Check if the email exists in Firebase before sending the reset link
                        fAuth.fetchSignInMethodsForEmail(mail).addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<com.google.firebase.auth.SignInMethodQueryResult> task) {
                                if (task.isSuccessful() && task.getResult().getSignInMethods().isEmpty()) {
                                    // Notify user that the email is not registered
                                    Toast.makeText(UserLogin.this, "This email is not registered.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Proceed with sending the reset email
                                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UserLogin.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserLogin.this, "Error! Reset Link Not Sent: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }
}

