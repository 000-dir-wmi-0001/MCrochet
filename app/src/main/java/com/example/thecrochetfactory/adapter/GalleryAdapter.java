package com.example.thecrochetfactory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Product;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFull;  // To keep a copy of the original list for filtering
    private OnItemClickListener onItemClickListener;

    // Constructor with OnItemClickListener
    public GalleryAdapter(Context context, List<Product> productList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);  // Create a copy of the original list
        this.onItemClickListener = onItemClickListener;
    }

    // ViewHolder to bind the views for each item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public TextView productName;
        public TextView productPrice;
        public TextView category;

        public ViewHolder(View view) {
            super(view);
            productName = itemView.findViewById(R.id.Gnamefill);
            category = itemView.findViewById(R.id.Gtype);
            productPrice = itemView.findViewById(R.id.Gpricefill);
            productImage = itemView.findViewById(R.id.Gimage);

            // Set up click listener for the whole item view
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the clicked product using adapter position
                    Product product = productList.get(getAdapterPosition());
                    onItemClickListener.onItemClick(product);  // Pass the clicked product to the listener
                }
            });
        }
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set the product data into the views
        holder.productName.setText(product.getName());
//        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.productPrice.setText(String.format("₹%.2f", product.getPrice()));
        holder.category.setText(product.getCategory());

        // Load product image with Glide and handle placeholder and error images
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder)  // Optional: Placeholder while loading
                .error(R.drawable.error_image)       // Optional: Error image in case of failure
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    // Implement filtering
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> filteredList = new ArrayList<>();

                // If the query is empty, return the original list
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(productListFull); // Return the original list when query is empty
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    // Iterate over the full list and filter based on name and category
                    for (Product item : productListFull) {
                        if (item.getName().toLowerCase().contains(filterPattern) ||
                                item.getCategory().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                // Return the filtered results
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productList.clear();
                if (results.values != null) {
                    productList.addAll((List) results.values);
                }
                notifyDataSetChanged(); // Notify adapter to refresh view
            }
        };
    }
}