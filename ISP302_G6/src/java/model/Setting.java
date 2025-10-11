/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author DELL
 */
public class Setting {

    private int settingID;
    private String settingType;
    private String settingValue;
    private String description;
    private String status;

    // Constructors, Getters, and Setters
    public Setting() {

    }

    public Setting(int settingID, String settingType, String settingValue, String description, String status) {
        this.settingID = settingID;
        this.settingType = settingType;
        this.settingValue = settingValue;
        this.description = description;
        this.status = status;
    }

    // --- Getters and Setters ---
    public int getSettingID() {
        return settingID;
    }

    public void setSettingID(int settingID) {
        this.settingID = settingID;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
