/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DELL
 */
public class PurchaseOrder {

    // --- Các thuộc tính khớp với cột trong Database ---
    private int purchaseOrderID;
    private Timestamp orderDate;
    private Date expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String status;
    private int supplierID;
    private int createdBy;
    private Integer approvedBy; // Dùng Integer để có thể nhận giá trị null

    // --- Các thuộc tính mở rộng để hiển thị (lấy từ JOIN) ---
    private String supplierName;
    private String creatorName;

    private List<PurchaseOrderDetail> items;

    // --- Constructors ---
    public PurchaseOrder() {
        // Constructor rỗng
    }

    public PurchaseOrder(int purchaseOrderID, Timestamp orderDate, Date expectedDeliveryDate, BigDecimal totalAmount, String status, int supplierID, int createdBy, Integer approvedBy, String supplierName, String creatorName, List<PurchaseOrderDetail> items) {
        this.purchaseOrderID = purchaseOrderID;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.supplierID = supplierID;
        this.createdBy = createdBy;
        this.approvedBy = approvedBy;
        this.supplierName = supplierName;
        this.creatorName = creatorName;
        this.items = items;
    }

    public List<PurchaseOrderDetail> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderDetail> items) {
        this.items = items;
    }

    // --- Getters and Setters ---
    public int getPurchaseOrderID() {
        return purchaseOrderID;
    }

    public void setPurchaseOrderID(int purchaseOrderID) {
        this.purchaseOrderID = purchaseOrderID;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
