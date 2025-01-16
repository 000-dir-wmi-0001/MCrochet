package com.example.thecrochetfactory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Feedback;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private List<Feedback> feedbackList;  // Use List instead of ArrayList for flexibility

    // Updated constructor to accept List<Feedback> instead of ArrayList<Feedback>
    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        // Set feedback data into views
        holder.usernameTextView.setText(feedback.getUsername());
        holder.feedbackMessageTextView.setText(feedback.getFeedbackText());
        holder.userIdTextView.setText("User ID: " + feedback.getUserId());
        holder.ratingBar.setRating(feedback.getRating());
        holder.timestampTextView.setText(String.valueOf(feedback.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, feedbackMessageTextView, userIdTextView, timestampTextView;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            feedbackMessageTextView = itemView.findViewById(R.id.feedbackMessage);
            userIdTextView = itemView.findViewById(R.id.userId);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            timestampTextView = itemView.findViewById(R.id.timestamp);
        }
    }
}

