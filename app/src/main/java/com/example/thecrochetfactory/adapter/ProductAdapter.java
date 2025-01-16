package com.example.thecrochetfactory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductDeleteListener onProductDeleteListener;

    // Interface for delete action
    public interface OnProductDeleteListener {
        void onProductDelete(int position);  // Handle product deletion
    }

    // Constructor for the adapter
    public ProductAdapter(Context context, List<Product> productList, OnProductDeleteListener onProductDeleteListener) {
        this.context = context;
        this.productList = productList;
        this.onProductDeleteListener = onProductDeleteListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each product item
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set the product data to the views
        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: ₹" + product.getPrice());
        holder.productCategory.setText(product.getCategory());
        holder.productStock.setText("Stock: " + product.getStockQuantity());

        // Set the delete button listener
        holder.deleteProductButton.setOnClickListener(v -> {
            if (onProductDeleteListener != null) {
                onProductDeleteListener.onProductDelete(position);  // Trigger the delete listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();  // Return the number of items in the list
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productCategory, productStock;
        Button deleteProductButton;

        public ProductViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
            productStock = itemView.findViewById(R.id.productStock);
            deleteProductButton = itemView.findViewById(R.id.deleteProductButton);
        }
    }
}


