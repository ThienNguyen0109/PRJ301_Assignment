package models;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Review
 */
@Entity
@Table(name = "Review")
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "review_id", columnDefinition = "uniqueidentifier")
    private String reviewId;
    @Column(name = "rental_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String rentalId;
    @Column(name = "customer_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String customerId;
    @Column(name = "model_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String modelId;
    @Column(name = "rating", nullable = false)
    private Integer rating;
    @Lob
    @Column(name = "comment", columnDefinition = "NVARCHAR(MAX)")
    private String comment;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", referencedColumnName = "rental_id", insertable = false, updatable = false)
    private Rental rental;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private Account customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "model_id", insertable = false, updatable = false)
    private VehicleModel model;

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

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public Account getCustomer() {
        return customer;
    }

    public void setCustomer(Account customer) {
        this.customer = customer;
    }

    public VehicleModel getModel() {
        return model;
    }

    public void setModel(VehicleModel model) {
        this.model = model;
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

