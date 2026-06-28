package dto;

public class AdminVehicleModelRow {
    private final String modelId;
    private final String categoryId;
    private final String categoryName;
    private final String name;
    private final String brand;
    private final Integer seatCount;
    private final Double pricePerDay;
    private final String description;
    private final Long vehicleCount;
    private final Long imageCount;

    public AdminVehicleModelRow(String modelId, String categoryId, String categoryName, String name,
            String brand, Integer seatCount, Double pricePerDay, String description,
            Long vehicleCount, Long imageCount) {
        this.modelId = modelId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.brand = brand;
        this.seatCount = seatCount;
        this.pricePerDay = pricePerDay;
        this.description = description;
        this.vehicleCount = vehicleCount;
        this.imageCount = imageCount;
    }

    public String getModelId() { return modelId; }
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public Integer getSeatCount() { return seatCount; }
    public Double getPricePerDay() { return pricePerDay; }
    public String getDescription() { return description; }
    public Long getVehicleCount() { return vehicleCount; }
    public Long getImageCount() { return imageCount; }
}
