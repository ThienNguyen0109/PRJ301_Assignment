package dto;

import enums.VehicleModelImageType;

public class AdminVehicleModelImageRow {
    private final String imageId;
    private final String modelId;
    private final String modelName;
    private final String imageUrl;
    private final VehicleModelImageType imageType;

    public AdminVehicleModelImageRow(String imageId, String modelId, String modelName,
            String imageUrl, VehicleModelImageType imageType) {
        this.imageId = imageId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

    public String getImageId() { return imageId; }
    public String getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public String getImageUrl() { return imageUrl; }
    public VehicleModelImageType getImageType() { return imageType; }
}
