package models;

import enums.VehicleModelImageType;
import java.io.Serializable;

/**
 * Entity class representing a Vehicle_Model_Image
 */
public class VehicleModelImage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String imageId;
    private String modelId;
    private String imageUrl;
    private VehicleModelImageType imageType;

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


