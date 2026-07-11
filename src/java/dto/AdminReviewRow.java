package dto;
import java.sql.Timestamp;
public class AdminReviewRow {
    private final String reviewId, rentalId, customerName, customerEmail, modelName, comment; private final Integer rating; private final Timestamp createdAt;
    public AdminReviewRow(String reviewId,String rentalId,String customerName,String customerEmail,String modelName,Integer rating,String comment,Timestamp createdAt){this.reviewId=reviewId;this.rentalId=rentalId;this.customerName=customerName;this.customerEmail=customerEmail;this.modelName=modelName;this.rating=rating;this.comment=comment;this.createdAt=createdAt;}
    public String getReviewId(){return reviewId;} public String getRentalId(){return rentalId;} public String getCustomerName(){return customerName;} public String getCustomerEmail(){return customerEmail;} public String getModelName(){return modelName;} public Integer getRating(){return rating;} public String getComment(){return comment;} public Timestamp getCreatedAt(){return createdAt;}
}
