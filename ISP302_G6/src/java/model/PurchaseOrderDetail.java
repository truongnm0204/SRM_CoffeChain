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

public class PurchaseOrderDetail {

    private int purchaseOrderID;
    private int ingredientID;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;

    // --- Các thuộc tính mở rộng để hiển thị ---
    private String ingredientName;

    // Constructors, Getters, and Setters
    public PurchaseOrderDetail() {
    }

    public PurchaseOrderDetail(int purchaseOrderID, int ingredientID, BigDecimal quantity, BigDecimal pricePerUnit, String ingredientName) {
        this.purchaseOrderID = purchaseOrderID;
        this.ingredientID = ingredientID;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.ingredientName = ingredientName;
    }

    // --- Getters and Setters ---
    public int getPurchaseOrderID() {
        return purchaseOrderID;
    }

    public void setPurchaseOrderID(int purchaseOrderID) {
        this.purchaseOrderID = purchaseOrderID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
