package com.example.thecrochetfactory.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Cart;
import com.example.thecrochetfactory.model.Product;
import com.example.thecrochetfactory.model.Wishlist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserProductDetailsFragment extends Fragment {

    // UI components
    private ImageView Img, add, remove, back;
    private TextView name, price, type, quantity;
    private Button addtoCart, wishlist;

    private int totalQuantity = 1;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Product object
    private Product products = null;

    public UserProductDetailsFragment() {
        // Required empty public constructor
    }

    // Use this method to pass product to the fragment
    public static UserProductDetailsFragment newInstance(Product product) {
        UserProductDetailsFragment fragment = new UserProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("details", product);  // Passing the product object
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the product data passed from the activity
        if (getArguments() != null) {
            products = (Product) getArguments().getSerializable("details");
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_user_product_details, container, false);

        // Initialize UI components
        Img = rootView.findViewById(R.id.Dimage);
        quantity = rootView.findViewById(R.id.Dquantity);
        name = rootView.findViewById(R.id.Dnamefill);
        price = rootView.findViewById(R.id.Dpricefill);
        type = rootView.findViewById(R.id.Dtypefill);
        addtoCart = rootView.findViewById(R.id.Dcart);
        wishlist = rootView.findViewById(R.id.Dwishlist);
        add = rootView.findViewById(R.id.Dplus);
        remove = rootView.findViewById(R.id.Dminus);
        back = rootView.findViewById(R.id.Dback);

        // Load product details into the UI
        if (products != null) {
            Glide.with(getActivity())
                    .load(products.getImageUrl())
                    .into(Img);
            name.setText(products.getName());
            price.setText(String.format("₹%.2f", products.getPrice()));
            type.setText(products.getCategory());
        }

        // Increase quantity
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity < 10) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        // Decrease quantity
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        // Back button functionality
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Add to Cart functionality
        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        // Add to Wishlist functionality
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWishlist();
            }
        });

        return rootView;
    }

    private void addToCart() {
        // Ensure that only the current open product is added to the cart
        String productId = products.getProductId();
        String productName = products.getName();  // Add product name
        String productImageUrl = products.getImageUrl(); // Add image URL
        int quantity = totalQuantity;  // Use the selected quantity
        double price = products.getPrice();

        // Create a CartItem with productId, productName, productImageUrl, quantity, and price
        Cart.CartItem cartItem = new Cart.CartItem(productId, productName, productImageUrl, quantity, price);

        // Reference to the user's cart in Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference cartRef = FirebaseFirestore.getInstance()
                .collection("Cart")
                .document(userId);  // Assuming the cart is stored per user

        // Fetch current cart to update
        cartRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If the cart exists, update it
                        Cart cart = document.toObject(Cart.class);

                        // Check if the item already exists in the cart
                        if (cart != null) {
                            List<Cart.CartItem> currentItems = cart.getItems();
                            boolean itemFound = false;

                            // Check if the product is already in the cart
                            for (Cart.CartItem item : currentItems) {
                                if (item.getProductId().equals(productId)) {
                                    item.setQuantity(item.getQuantity() + quantity);  // Increment quantity
                                    itemFound = true;
                                    break;
                                }
                            }

                            if (!itemFound) {
                                currentItems.add(cartItem);  // Add new item if not found
                            }

                            // Save the updated cart back to Firestore
                            cart.setItems(currentItems);
                            cartRef.set(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        // If no cart exists for the user, create a new cart with this item
                        List<Cart.CartItem> newItems = new ArrayList<>();
                        newItems.add(cartItem);
                        Cart newCart = new Cart(userId, newItems);

                        // Save the new cart to Firestore
                        cartRef.set(newCart).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToWishlist() {
        // Ensure that only the current open product is added to the wishlist
        String productId = products.getProductId();
        String productName = products.getName();
        double productPrice = products.getPrice();
        String productImage = products.getImageUrl(); // Assuming this is how you fetch the image URL

        // Create a WishlistItem with the productId, productName, productPrice, and productImage
        Wishlist.WishlistItem wishlistItem = new Wishlist.WishlistItem(productId, productName, productPrice, productImage);

        // Reference to the user's wishlist in Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference wishlistRef = FirebaseFirestore.getInstance()
                .collection("Wishlist")
                .document(userId);  // Assuming the wishlist is stored per user

        // Fetch current wishlist to update
        wishlistRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If the wishlist already exists, update it
                        Wishlist wishlist = document.toObject(Wishlist.class);

                        // Add the new WishlistItem to the list of items
                        if (wishlist != null) {
                            List<Wishlist.WishlistItem> currentItems = wishlist.getItems();
                            boolean itemFound = false;

                            // Check if the product is already in the wishlist
                            for (Wishlist.WishlistItem item : currentItems) {
                                if (item.getProductId().equals(productId)) {
                                    itemFound = true;
                                    break;
                                }
                            }

                            if (!itemFound) {
                                currentItems.add(wishlistItem); // Add item if not found
                            }

                            // Save the updated wishlist back to Firestore
                            wishlist.setItems(currentItems);
                            wishlistRef.set(wishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to add to Wishlist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        // If no wishlist exists for the user, create a new one
                        List<Wishlist.WishlistItem> newItems = new ArrayList<>();
                        newItems.add(wishlistItem);
                        Wishlist newWishlist = new Wishlist(userId, newItems);

                        // Save the new wishlist to Firestore
                        wishlistRef.set(newWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to add to Wishlist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch wishlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}


