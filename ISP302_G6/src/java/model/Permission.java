package model;

/**
 * Model cho Permission
 */
public class Permission {
    private Integer permissionID;
    private String permissionName;
    private String permissionPath;
    private String description;

    public Permission() {}

    public Permission(Integer permissionID, String permissionName, String permissionPath, String description) {
        this.permissionID = permissionID;
        this.permissionName = permissionName;
        this.permissionPath = permissionPath;
        this.description = description;
    }

    public Integer getPermissionID() {
        return permissionID;
    }

    public void setPermissionID(Integer permissionID) {
        this.permissionID = permissionID;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionPath() {
        return permissionPath;
    }

    public void setPermissionPath(String permissionPath) {
        this.permissionPath = permissionPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "permissionID=" + permissionID +
                ", permissionName='" + permissionName + '\'' +
                ", permissionPath='" + permissionPath + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
