package model;

public class ProductIngredient {
    private int productId;
    private int ingredientId;
    private double requiredQuantity;
    private String ingredientName; // For display purposes

    public ProductIngredient() {
    }

    public ProductIngredient(int productId, int ingredientId, double requiredQuantity) {
        this.productId = productId;
        this.ingredientId = ingredientId;
        this.requiredQuantity = requiredQuantity;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
