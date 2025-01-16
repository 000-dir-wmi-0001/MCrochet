package com.example.thecrochetfactory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Cart;
import com.example.thecrochetfactory.model.Wishlist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Cart.CartItem> cartItems;
    private OnItemClickListener onItemClickListener;

    public CartAdapter(List<Cart.CartItem> cartItems, OnItemClickListener listener) {
        this.cartItems = cartItems;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart.CartItem cartItem = cartItems.get(position);

        // Set product name and quantity
        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText("₹" + cartItem.getPrice());
        holder.productQuantity.setText("Quantity: " + cartItem.getQuantity());

        // Load image using Picasso
        Picasso.get().load(cartItem.getImageUrl()).into(holder.productImage);

        // Handle remove button click
        holder.removeButton.setOnClickListener(v -> onItemClickListener.onRemoveClick(cartItem));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface OnItemClickListener {
        void onRemoveClick(Cart.CartItem cartItem);
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage, removeButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            removeButton = itemView.findViewById(R.id.removeButton);  // Assuming there's a remove button
        }
    }
}

