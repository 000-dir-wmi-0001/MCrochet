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

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrdersAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdText.setText("Order ID: " + order.getOrderId());
        holder.totalAmountText.setText("Total Amount: ₹" + order.getTotalAmount());
        holder.statusText.setText("Status: " + order.getStatus());
        holder.paymentStatusText.setText("Payment Status: " + order.getPaymentStatus());
        holder.shippingAddressText.setText("Address: " + order.getShippingAddress().getAddressLine1());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, totalAmountText, statusText, paymentStatusText, shippingAddressText;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            totalAmountText = itemView.findViewById(R.id.totalAmountText);
            statusText = itemView.findViewById(R.id.orderStatus);
            paymentStatusText = itemView.findViewById(R.id.paymentStatusText);
            shippingAddressText = itemView.findViewById(R.id.shippingAddressText);
        }
    }
}
