package model;

/**
 * Model cho Product (Sản phẩm)
 */
public class Product {
    private Integer productId;
    private String productName;
    private String sku;
    private Double price;
    private String description;

    // Constructors
    public Product() {
    }

    public Product(Integer productId, String productName, String sku, Double price, String description) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.price = price;
        this.description = description;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}

