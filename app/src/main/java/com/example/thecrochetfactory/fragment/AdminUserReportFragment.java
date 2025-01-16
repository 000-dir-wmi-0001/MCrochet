package com.example.thecrochetfactory.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.UserReportAdapter;
import com.example.thecrochetfactory.model.User; // Import the User model
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminUserReportFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private ArrayList<User> userList = new ArrayList<>(); // List to store User objects
    private UserReportAdapter mAdapter;

    public AdminUserReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_user_report, container, false);

        // Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recyclerView2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Setup Adapter
        mAdapter = new UserReportAdapter(getActivity(), userList);
        mRecyclerView.setAdapter(mAdapter);

        // Fetch user data from Firestore
        fetchUserData();

        return view;
    }

    /**
     * Fetch user data from Firestore.
     */
    private void fetchUserData() {
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle Firestore error
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            // Clear previous list to avoid duplication when fetching new data
                            userList.clear();

                            // Update the list with user data from Firestore
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    // Convert Firestore document to User object and add to the list
                                    User user = dc.getDocument().toObject(User.class);
                                    userList.add(user);
                                }
                            }

                            // Notify the adapter of the data change
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}

