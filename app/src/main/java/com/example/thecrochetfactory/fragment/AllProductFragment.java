package com.example.thecrochetfactory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.ProductAdapter;
import com.example.thecrochetfactory.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllProductFragment extends Fragment implements ProductAdapter.OnProductDeleteListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore firestore;

    public AllProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_product, container, false);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter and set it to RecyclerView
        adapter = new ProductAdapter(getContext(), productList, this);
        recyclerView.setAdapter(adapter);

        // Fetch products from Firestore
        fetchProductsFromFirestore();

        return view;
    }

    private void fetchProductsFromFirestore() {
        firestore.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        productList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Product product = document.toObject(Product.class);
                            product.setProductId(document.getId());  // Set product ID
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching products", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onProductDelete(int position) {
        Product productToDelete = productList.get(position);

        // Delete product from Firestore
        firestore.collection("products")
                .document(productToDelete.getProductId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error deleting product", Toast.LENGTH_SHORT).show();
                });
    }
}
