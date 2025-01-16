package com.example.thecrochetfactory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.OrdersAdapter;
import com.example.thecrochetfactory.model.Order;
import com.example.thecrochetfactory.model.ShippingAddress;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore firestore;

    public AllOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_order, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();  // Initialize Firestore

        fetchOrdersFromFirestore();

        return view;
    }

    // Method to fetch orders from Firestore
    private void fetchOrdersFromFirestore() {
        CollectionReference ordersRef = firestore.collection("orders");

        ordersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Order order = document.toObject(Order.class);  // Map Firestore document to Order object
                    if (order != null) {
                        orderList.add(order);  // Add order to the list
                    }
                }
                adapter.notifyDataSetChanged();  // Notify adapter that data has changed
            } else {
                Toast.makeText(getActivity(), "Error getting documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Call this method when you need to update data, like fetching from the database
    public void updateOrderList(List<Order> newOrderList) {
        orderList.clear();
        orderList.addAll(newOrderList);
        adapter.notifyDataSetChanged();
    }
}
