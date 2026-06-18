package models;

import enums.VehicleModelImageType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Vehicle_Model_Image
 */
@Entity
@Table(name = "Vehicle_Model_Image")
public class VehicleModelImage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "image_id", columnDefinition = "uniqueidentifier")
    private String imageId;
    @Column(name = "model_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String modelId;
    @Lob
    @Column(name = "image_url", columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false, length = 20)
    private VehicleModelImageType imageType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "model_id", insertable = false, updatable = false)
    private VehicleModel model;

    public VehicleModelImage() {
    }

    public VehicleModelImage(String modelId, String imageUrl, VehicleModelImageType imageType) {
        this.modelId = modelId;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

    public VehicleModelImage(String imageId, String modelId, String imageUrl, VehicleModelImageType imageType) {
        this.imageId = imageId;
        this.modelId = modelId;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public VehicleModelImageType getImageType() {
        return imageType;
    }

    public void setImageType(VehicleModelImageType imageType) {
        this.imageType = imageType;
    }

    public VehicleModel getModel() {
        return model;
    }

    public void setModel(VehicleModel model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "VehicleModelImage{" +
                "imageId='" + imageId + '\'' +
                ", modelId='" + modelId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageType=" + imageType +
                '}';
    }
}


