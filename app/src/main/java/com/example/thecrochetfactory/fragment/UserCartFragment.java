package com.example.thecrochetfactory.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.adapter.CartAdapter;
import com.example.thecrochetfactory.fragment.CheckoutFragment;
import com.example.thecrochetfactory.model.Cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class UserCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalTextView;
    private TextView emptyCartTextView;
    private Button buyNowButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<Cart.CartItem> cartItems; // Declare this at the class level

    public UserCartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_cart, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        totalTextView = rootView.findViewById(R.id.total);
        emptyCartTextView = rootView.findViewById(R.id.total); // Correct reference
        buyNowButton = rootView.findViewById(R.id.btnbuy);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Fetch the user's cart data
        fetchCartData();

        // Handle Buy Now button click
        buyNowButton.setOnClickListener(v -> onBuyNowClick());

        return rootView;
    }

    private void fetchCartData() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("Cart").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Cart cart = document.toObject(Cart.class);
                                if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
                                    cartItems = cart.getItems(); // Assign to class-level variable
                                    double totalPrice = cart.getTotalPrice();  // Get the total price from the cart object

                                    // Create OnItemClickListener
                                    CartAdapter.OnItemClickListener listener = new CartAdapter.OnItemClickListener() {
                                        @Override
                                        public void onRemoveClick(Cart.CartItem cartItem) {
                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            // Fetch the user's cart data from Firestore
                                            db.collection("Cart").document(userId).get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document.exists()) {
                                                                    Cart cart = document.toObject(Cart.class);
                                                                    if (cart != null && cart.getItems() != null) {
                                                                        List<Cart.CartItem> currentItems = cart.getItems();

                                                                        // Remove the item from the cart (by matching productId)
                                                                        for (int i = 0; i < currentItems.size(); i++) {
                                                                            Cart.CartItem item = currentItems.get(i);
                                                                            if (item.getProductId().equals(cartItem.getProductId())) {
                                                                                currentItems.remove(i);  // Remove the item from the list
                                                                                break;
                                                                            }
                                                                        }

                                                                        // Recalculate the total price after removing the item
                                                                        cart.setItems(currentItems); // Update the cart's item list
                                                                        cart.resetTotalPrice(0);  // Recalculate the total price

                                                                        // Update the cart in Firestore
                                                                        db.collection("Cart").document(userId).set(cart)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            // Update the UI (RecyclerView) after the Firestore update
                                                                                            removeItemFromList(cartItem);

                                                                                            // Show success message
                                                                                            Toast.makeText(getActivity(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                                                                                        } else {
                                                                                            // Handle failure to update Firestore
                                                                                            Toast.makeText(getActivity(), "Failed to update cart", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            } else {
                                                                // Handle failure to fetch cart data
                                                                Toast.makeText(getActivity(), "Failed to load cart", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    };

                                    // Pass the listener to the adapter
                                    cartAdapter = new CartAdapter(cartItems, listener);
                                    recyclerView.setAdapter(cartAdapter);

                                    // Show total price
                                    totalTextView.setText("Total: ₹" + totalPrice);

                                    // Show the RecyclerView and hide "Your Cart is Empty" message
                                    recyclerView.setVisibility(View.VISIBLE);
                                    emptyCartTextView.setVisibility(View.GONE);

                                    // Enable Buy Now button
                                    buyNowButton.setEnabled(true);
                                } else {
                                    // Cart is empty
                                    recyclerView.setVisibility(View.GONE);
                                    emptyCartTextView.setVisibility(View.VISIBLE);
                                    buyNowButton.setEnabled(false);
                                }
                            } else {
                                // Cart document not found
                                recyclerView.setVisibility(View.GONE);
                                emptyCartTextView.setVisibility(View.VISIBLE);
                                buyNowButton.setEnabled(false);
                            }
                        } else {
                            // Fetching cart failed
                            Toast.makeText(getActivity(), "Failed to load cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onBuyNowClick() {
        // Fetch the user's cart and pass it to the CheckoutFragment
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("Cart").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Cart cart = document.toObject(Cart.class);

                            // Pass the Cart object to CheckoutFragment using putParcelable
                            CheckoutFragment checkoutFragment = new CheckoutFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("cart", cart);  // Use putParcelable instead of putSerializable
                            checkoutFragment.setArguments(bundle);

                            // Switch to CheckoutFragment
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fraglay, checkoutFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                });
    }

    private void removeItemFromList(Cart.CartItem cartItem) {
        // Remove the item from the list and notify the adapter
        cartItems.remove(cartItem);
        cartAdapter.notifyDataSetChanged();

        // If the cart is empty, show the empty state message
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyCartTextView.setVisibility(View.VISIBLE);
            buyNowButton.setEnabled(false);
        }
    }
}
