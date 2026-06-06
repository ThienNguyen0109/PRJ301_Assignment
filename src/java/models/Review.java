package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Review
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reviewId;
    private String rentalId;
    private String customerId;
    private String modelId;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;

    public Review() {
    }

    public Review(String rentalId, String customerId, String modelId, Integer rating, String comment) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.modelId = modelId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review(String reviewId, String rentalId, String customerId, String modelId,
                  Integer rating, String comment, Timestamp createdAt) {
        this.reviewId = reviewId;
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.modelId = modelId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", modelId='" + modelId + '\'' +
                ", rating=" + rating +
                '}';
    }
}
