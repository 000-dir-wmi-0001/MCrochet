package com.example.thecrochetfactory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log; // Import for logging

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Cart;
import com.example.thecrochetfactory.model.Wishlist;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<Wishlist.WishlistItem> wishlistItems;
    private OnItemClickListener onItemClickListener;

    // Constructor to initialize the list and listener
    public WishlistAdapter(List<Wishlist.WishlistItem> wishlistItems, OnItemClickListener listener) {
        this.wishlistItems = wishlistItems;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a wishlist item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_items, parent, false);
        return new WishlistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Wishlist.WishlistItem wishlistItem = wishlistItems.get(position);

        // Set product details (name, price, etc.)
        holder.productName.setText(wishlistItem.getProductName());
        holder.productPrice.setText("₹" + wishlistItem.getProductPrice());

        // Check if the image URL is valid and use Picasso to load the image
        String imageUrl = wishlistItem.getProductImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)  // Placeholder image
                    .error(R.drawable.error_image)       // Error image if the URL is invalid
                    .into(holder.productImage);
        } else {
            // If image URL is not available, set a default image
            holder.productImage.setImageResource(R.drawable.shop);  // Default image
        }

        // Handle remove button click (removes item from wishlist)
        holder.removeButton.setOnClickListener(v -> onItemClickListener.onRemoveClick(wishlistItem));
    }

    @Override
    public int getItemCount() {
        return wishlistItems != null ? wishlistItems.size() : 0; // Ensure null-safe size calculation
    }

    // Interface to handle item removal (click events)
    public interface OnItemClickListener {
        void onRemoveClick(Wishlist.WishlistItem wishlistItem);
    }

    // ViewHolder class for binding the views
    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage, removeButton;

        public WishlistViewHolder(View itemView) {
            super(itemView);
            // Find views from the layout
            productName = itemView.findViewById(R.id.product_name);   // Ensure this matches your XML
            productPrice = itemView.findViewById(R.id.product_price); // Ensure this matches your XML
            productImage = itemView.findViewById(R.id.Wimage);       // Ensure this matches your XML
            removeButton = itemView.findViewById(R.id.Wdelete);      // Ensure this matches your XML
        }
    }
}
