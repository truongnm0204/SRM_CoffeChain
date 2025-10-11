/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author DELL
 */
import java.math.BigDecimal;

public class Ingredient {

    private int ingredientID;
    private String ingredientName;
    private String description;
    private BigDecimal currentStock;
    private BigDecimal minStock;
    private Integer unitSettingID;
    private Integer categoryID;
    private String status;

    private String categoryName; // Để chứa tên danh mục từ JOIN
    private String unitName;

    // Constructors, Getters, and Setters
    public Ingredient() {
    }

    public Ingredient(int ingredientID, String ingredientName, String description, BigDecimal currentStock, BigDecimal minStock, Integer unitSettingID, Integer categoryID, String categoryName, String unitName) {
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.description = description;
        this.currentStock = currentStock;
        this.minStock = minStock;
        this.unitSettingID = unitSettingID;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.unitName = unitName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    // --- Getters and Setters ---
    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public Integer getUnitSettingID() {
        return unitSettingID;
    }

    public void setUnitSettingID(Integer unitSettingID) {
        this.unitSettingID = unitSettingID;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }
}
