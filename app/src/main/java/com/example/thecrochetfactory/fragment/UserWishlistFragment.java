package com.example.thecrochetfactory.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.WishlistAdapter;
import com.example.thecrochetfactory.model.Wishlist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

public class UserWishlistFragment extends Fragment implements WishlistAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private WishlistAdapter wishlistAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView emptyWishlistText;  // TextView to show an empty wishlist message
    private List<Wishlist.WishlistItem> wishlistItems = new ArrayList<>();  // Initialize the list to hold wishlist items

    public UserWishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore and FirebaseAuth instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_wishlist, container, false);

        // Initialize RecyclerView and set LayoutManager
        recyclerView = rootView.findViewById(R.id.recyclerViewW);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize empty state TextView
        emptyWishlistText = rootView.findViewById(R.id.emptyWishlistText);  // Ensure TextView ID is correct

        // Fetch wishlist data for the current user
        fetchWishlistData();

        return rootView;
    }

    /**
     * Fetches the current user's wishlist data from Firestore and updates the RecyclerView.
     */
    private void fetchWishlistData() {
        if (auth.getCurrentUser() == null) {
            // Handle the case when the user is not logged in
            showError("User is not logged in.");
            return;
        }

        String userId = auth.getCurrentUser().getUid();  // Get the currently logged-in user ID

        // Fetch the user's wishlist data from Firestore
        db.collection("Wishlist").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Map the document data to the Wishlist model
                                Wishlist wishlist = document.toObject(Wishlist.class);
                                if (wishlist != null && wishlist.getItems() != null && !wishlist.getItems().isEmpty()) {
                                    wishlistItems = wishlist.getItems();  // Assign the list to the adapter
                                    updateRecyclerView();  // Update RecyclerView with new data
                                } else {
                                    showEmptyWishlist();  // Show empty state if no items are found
                                }
                            } else {
                                showEmptyWishlist();  // Show empty state if no wishlist document is found
                            }
                        } else {
                            // If the task failed, show an error message
                            showError("Failed to load wishlist. Please try again later.");
                        }
                    }
                });
    }

    /**
     * Update RecyclerView and show the wishlist items.
     */
    private void updateRecyclerView() {
        // Set up the adapter with the fetched wishlist items
        if (wishlistAdapter == null) {
            wishlistAdapter = new WishlistAdapter(wishlistItems, UserWishlistFragment.this);
            recyclerView.setAdapter(wishlistAdapter);
        } else {
            wishlistAdapter.notifyDataSetChanged();  // Notify adapter if data is already set
        }

        // Hide the empty wishlist text view
        emptyWishlistText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);  // Show RecyclerView if data exists
    }

    /**
     * Show an empty state message when the wishlist is empty or does not exist.
     */
    private void showEmptyWishlist() {
        emptyWishlistText.setVisibility(View.VISIBLE);  // Show empty message
        recyclerView.setVisibility(View.GONE);  // Hide RecyclerView
    }

    /**
     * Show an error message.
     */
    private void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called when the user clicks to remove an item from the wishlist.
     * @param item The item to remove from the wishlist.
     */
    @Override
    public void onRemoveClick(Wishlist.WishlistItem item) {
        String userId = auth.getCurrentUser().getUid();  // Get the current user's UID

        // Remove the item from the Firestore wishlist
        db.collection("Wishlist").document(userId)
                .update("items", FieldValue.arrayRemove(item)) // Remove the specific item from the wishlist array
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully removed the item from Firestore
                        Toast.makeText(getActivity(), "Item removed from wishlist.", Toast.LENGTH_SHORT).show();
                        removeItemFromList(item);  // Update the UI by removing the item from the list
                    } else {
                        // If there was an issue with the removal
                        showError("Failed to remove item. Please try again.");
                    }
                });
    }

    /**
     * Remove the item from the wishlist and update the RecyclerView.
     * @param item The item to remove from the list.
     */
    private void removeItemFromList(Wishlist.WishlistItem item) {
        wishlistItems.remove(item);  // Remove the item from the list
        wishlistAdapter.notifyDataSetChanged();  // Notify adapter to update the UI

        // If the wishlist is empty, show the empty state message
        if (wishlistItems.isEmpty()) {
            showEmptyWishlist();
        }
    }
}


