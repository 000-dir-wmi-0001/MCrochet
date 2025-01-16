package com.example.thecrochetfactory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.FeedbackAdapter;
import com.example.thecrochetfactory.model.Feedback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllFeedbackFragment extends Fragment {

    private RecyclerView feedbackRecyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList = new ArrayList<>();
    private ProgressBar progressBar;  // Loading indicator

    public AllFeedbackFragment() {
        // Required empty public constructor
    }

    public static AllFeedbackFragment newInstance(String param1, String param2) {
        AllFeedbackFragment fragment = new AllFeedbackFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_feedback, container, false);

        // Set up RecyclerView
        feedbackRecyclerView = rootView.findViewById(R.id.feedbackRecyclerView);
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedbackAdapter = new FeedbackAdapter(getActivity(), feedbackList);  // Updated constructor usage
        feedbackRecyclerView.setAdapter(feedbackAdapter);

        // Set up ProgressBar
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Fetch feedback from Firestore
        fetchFeedbackFromFirestore();

        return rootView;
    }

    private void fetchFeedbackFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Feedbacks")  // Assuming "feedback" is the collection name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Feedback> feedbacks = queryDocumentSnapshots.toObjects(Feedback.class);
                        // Now fetch the username for each feedback
                        fetchUsernamesForFeedback(feedbacks);
                    } else {
                        Toast.makeText(getActivity(), "No feedback available.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);  // Hide the progress bar
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load feedback.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);  // Hide the progress bar
                });
    }

    private void fetchUsernamesForFeedback(List<Feedback> feedbacks) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Feedback> updatedFeedbackList = new ArrayList<>();

        // Loop through the feedback and fetch the username for each feedback
        for (Feedback feedback : feedbacks) {
            String userId = feedback.getUserId();

            db.collection("users")  // Assuming "users" is the collection storing user details
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            feedback.setUsername(username != null ? username : "Anonymous");  // Fallback to "Anonymous"
                        } else {
                            feedback.setUsername("Anonymous");  // Fallback in case no user is found
                        }
                        updatedFeedbackList.add(feedback);

                        // Once all feedbacks are processed, update the RecyclerView
                        if (updatedFeedbackList.size() == feedbacks.size()) {
                            feedbackList.clear();
                            feedbackList.addAll(updatedFeedbackList);
                            feedbackAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);  // Hide the progress bar once data is loaded
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch username for some feedbacks.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);  // Hide the progress bar
                    });
        }
    }
}
