package com.example.thecrochetfactory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.User;

import java.util.ArrayList;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<User> userReportList;  // List to hold all users

    // Constructor for the adapter, accepts context and user list
    public UserReportAdapter(Context context, ArrayList<User> userReportList) {
        this.context = context;
        this.userReportList = userReportList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each user entry
        View v = LayoutInflater.from(context).inflate(R.layout.user_items, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the user data from the list
        User user = userReportList.get(position);

        // Bind the user data to the views
        holder.uname.setText(user.getName());  // Display user name
        holder.uemail.setText(user.getEmail());  // Display user email
        holder.umobile.setText(user.getMobileNo());  // Display user phone number
        holder.urole.setText(user.getRole());  // Display user role
    }

    @Override
    public int getItemCount() {
        // Return the size of the user list
        return userReportList.size();
    }

    // ViewHolder class to hold the views for each user item
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView uname, uemail, umobile, urole;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the TextViews for name, email, mobile number, and role
            uname = itemView.findViewById(R.id.uname);
            uemail = itemView.findViewById(R.id.uemail);
            umobile = itemView.findViewById(R.id.umobile);
            urole = itemView.findViewById(R.id.urole);
        }
    }
}
