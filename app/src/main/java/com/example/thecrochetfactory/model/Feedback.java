package com.example.thecrochetfactory.model;

public class Feedback {

    private String feedbackId;     // Unique ID for each feedback entry
    private String userId;         // ID of the user who gave the feedback
    private String username;       // Username of the user who gave the feedback
    private String feedbackText;   // The actual feedback text (comments about the app)
    private int rating;            // Rating value (1 to 5, for example)
    private long timestamp;        // Time when the feedback was submitted (Unix timestamp)

    // Default constructor required for Firebase
    public Feedback() {}

    // Parameterized constructor to initialize the fields
    public Feedback(String feedbackId, String userId, String username, String feedbackText, int rating, long timestamp) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.username = username;
        this.feedbackText = feedbackText;
        this.setRating(rating); // Use setter to ensure validation
        this.timestamp = timestamp;
    }

    // Getter and setter methods for each field
    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public int getRating() {
        return rating;
    }

    // Validate rating to be between 1 and 5
    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;  // Ensure minimum rating is 1
        } else if (rating > 5) {
            this.rating = 5;  // Ensure maximum rating is 5
        } else {
            this.rating = rating;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId='" + feedbackId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", feedbackText='" + feedbackText + '\'' +
                ", rating=" + rating +
                ", timestamp=" + timestamp +
                '}';
    }
}

