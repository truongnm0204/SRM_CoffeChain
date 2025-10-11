/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author DELL
 */
public class IngredientCategory {

    private int categoryID;
    private String categoryName;
    private String description;
    private boolean hasExpiry;
    private String status;

    // Constructors, Getters, and Setters
    public IngredientCategory() {
    }

    public IngredientCategory(int categoryID, String categoryName, String description, boolean hasExpiry, String status) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.description = description;
        this.hasExpiry = hasExpiry;
        this.status = status;
    }

    // --- Getters and Setters ---
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasExpiry() {
        return hasExpiry;
    }

    public void setHasExpiry(boolean hasExpiry) {
        this.hasExpiry = hasExpiry;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
