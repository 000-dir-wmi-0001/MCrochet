package com.example.thecrochetfactory.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class FeedBackFragment extends Fragment {

    private EditText feedbackEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FeedBackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_feed_back, container, false);

        feedbackEditText = rootView.findViewById(R.id.feedbackEditText);
        ratingBar = rootView.findViewById(R.id.ratingBar);
        submitButton = rootView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        return rootView;
    }

    // Method to handle feedback submission
    private void submitFeedback() {
        String feedbackText = feedbackEditText.getText().toString();
        int rating = (int) ratingBar.getRating();  // Rating from 1 to 5

        // Validate input
        if (feedbackText.isEmpty() || rating == 0) {
            Toast.makeText(getActivity(), "Please provide feedback and a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user ID and username
        String userId = auth.getCurrentUser().getUid();
        String username = auth.getCurrentUser().getDisplayName(); // Fetch the username from FirebaseAuth
        long timestamp = System.currentTimeMillis();  // Timestamp of feedback submission

        // Generate a unique feedback ID
        String feedbackId = UUID.randomUUID().toString();

        // Create a new Feedback object using the provided constructor
        Feedback feedback = new Feedback(feedbackId, userId, username, feedbackText, rating, timestamp);

        // Save feedback to Firestore
        db.collection("Feedbacks")
                .document(feedbackId)  // Use feedbackId as the document ID
                .set(feedback)  // Set the feedback object to the Firestore document
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                            // Optionally clear the fields after submission
                            feedbackEditText.setText("");
                            ratingBar.setRating(0);
                        } else {
                            Toast.makeText(getActivity(), "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
