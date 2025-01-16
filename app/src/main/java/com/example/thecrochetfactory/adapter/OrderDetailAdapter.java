package com.example.thecrochetfactory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Order;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private List<Order> orderList;

    public OrderDetailAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout item_order.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Bind each field to the appropriate TextView
        holder.usernameText.setText("Username: " + order.getUsername());
        holder.orderIdText.setText("Order ID: " + order.getOrderId());
        holder.userIdText.setText("User ID: " + order.getUserId());
        holder.totalAmountText.setText("Total Amount: ₹" + order.getTotalAmount());
        holder.statusText.setText("Status: " + order.getStatus());
        holder.paymentStatusText.setText("Payment Status: " + order.getPaymentStatus());

        // Add shipping address, mobile no, and email
        holder.shippingAddressText.setText("Shipping Address: " + order.getShippingAddress().getAddressLine1());
        holder.mobileNoText.setText("Mobile No.: " + order.getShippingAddress().getMobileNo());
        holder.emailText.setText("Email: " + order.getShippingAddress().getEmail());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, orderIdText, userIdText, totalAmountText, statusText, paymentStatusText;
        TextView shippingAddressText, mobileNoText, emailText;

        public OrderDetailViewHolder(View itemView) {
            super(itemView);

            // Initialize all the TextViews
            usernameText = itemView.findViewById(R.id.username);
            orderIdText = itemView.findViewById(R.id.orderId);
            userIdText = itemView.findViewById(R.id.userId);
            totalAmountText = itemView.findViewById(R.id.totalAmount);
            statusText = itemView.findViewById(R.id.status);
            paymentStatusText = itemView.findViewById(R.id.paymentStatus);
            shippingAddressText = itemView.findViewById(R.id.shippingAddress);
            mobileNoText = itemView.findViewById(R.id.mobileNo);
            emailText = itemView.findViewById(R.id.email);
        }
    }
}
