package com.example.thecrochetfactory.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.GalleryAdapter;
import com.example.thecrochetfactory.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserHomeFragment extends Fragment {

    private RecyclerView mRecycleView;
    private GalleryAdapter mAdapter;
    private RecyclerView.LayoutManager mManager;
    private ArrayList<Product> list = new ArrayList<>();
    private FirebaseFirestore firestore;
    private EditText search;
    private ProgressDialog pd;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        mRecycleView = view.findViewById(R.id.recyclerView3);
        mRecycleView.setHasFixedSize(true);
        mManager = new LinearLayoutManager(getActivity());

        // Set up the adapter and pass in the OnItemClickListener
        mAdapter = new GalleryAdapter(getContext(), list, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // When a product is clicked, open the details fragment
                openProductDetails(product);
            }
        });

        mRecycleView.setLayoutManager(mManager);
        mRecycleView.setAdapter(mAdapter);

        search = view.findViewById(R.id.Gsearch);

        // Initialize ProgressDialog
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Fetching data...");
        pd.show();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve products from Firestore
        retrieveProducts();

        // Search functionality
        setupSearchFilter();

        return view;
    }

    private void retrieveProducts() {
        CollectionReference productsRef = firestore.collection("products");

        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    list.clear();  // Clear previous list before adding new data
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product p = document.toObject(Product.class);  // Now works with Product model
                        if (p != null) {
                            Log.d("UserHomeFragment", "Product retrieved: " + p.getName());
                            list.add(p);
                        } else {
                            Log.e("UserHomeFragment", "Product data is null.");
                        }
                    }
                    mAdapter.notifyDataSetChanged();  // Update the RecyclerView with new data
                } else {
                    Log.e("UserHomeFragment", "Error getting products: ", task.getException());
                    Toast.makeText(getActivity(), "Error retrieving products", Toast.LENGTH_SHORT).show();
                }
                if (pd.isShowing()) pd.dismiss();  // Dismiss progress dialog once data is fetched
            }
        });
    }


    // Setup search functionality to filter products by name or description
    private void setupSearchFilter() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);  // Filter products based on the query entered
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Open the product details fragment
    private void openProductDetails(Product product) {
        // Create a new instance of UserProductDetailsFragment
        UserProductDetailsFragment detailsFragment = UserProductDetailsFragment.newInstance(product);

        // Start the fragment transaction to show the product details
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fraglay, detailsFragment);  // Replace the current fragment
        transaction.addToBackStack(null);  // Add this transaction to the back stack
        transaction.commit();
    }
}

