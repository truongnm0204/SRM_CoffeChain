-- -----------------------------------------------------------------------------
-- LƯỢC ĐỒ CƠ SỞ DỮ LIỆU ĐÃ HOÀN THIỆN (POSTGRESQL)
-- -----------------------------------------------------------------------------

-- XÓA BẢNG CŨ (Cần thiết khi chạy lại script)
DROP TABLE IF EXISTS "Issue";
DROP TABLE IF EXISTS "PurchaseOrderDetail";
DROP TABLE IF EXISTS "PurchaseOrder";
DROP TABLE IF EXISTS "ProductIngredient";
DROP TABLE IF EXISTS "SupplierIngredient";
DROP TABLE IF EXISTS "Ingredient";
DROP TABLE IF EXISTS "SupplierProduct";
DROP TABLE IF EXISTS "Supplier";
DROP TABLE IF EXISTS "OrderDetail";
DROP TABLE IF EXISTS "Orders";
DROP TABLE IF EXISTS "Product";
DROP TABLE IF EXISTS "Shop";
DROP TABLE IF EXISTS "RolePermission"; -- Bổ sung để xóa đúng thứ tự
DROP TABLE IF EXISTS "Permission";     -- Bổ sung để xóa đúng thứ tự
DROP TABLE IF EXISTS "User";
DROP TABLE IF EXISTS "Setting";


-- -----------------------------------------------------
-- Bảng Setting - CẬP NHẬT: Thêm Status
-- -----------------------------------------------------
CREATE TABLE "Setting" (
    SettingID SERIAL PRIMARY KEY,
    SettingType VARCHAR(50) NOT NULL,
    SettingValue VARCHAR(255) NOT NULL,
    Description VARCHAR(255),
    Status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

-- -----------------------------------------------------
-- Bảng Shop - CẬP NHẬT: Thêm API và Status
-- -----------------------------------------------------
CREATE TABLE "Shop" (
    ShopID SERIAL PRIMARY KEY,
    ShopName VARCHAR(255) NOT NULL,
    Address TEXT,
    APIEndpoint VARCHAR(255),
    APIKey VARCHAR(255),
    Status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

-- -----------------------------------------------------
-- Bảng User - CẬP NHẬT: Thêm FullName, Phone, Address, Status
-- -----------------------------------------------------
CREATE TABLE "User" (
    UserID SERIAL PRIMARY KEY,
    UserName VARCHAR(255) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    FullName VARCHAR(255),
    PhoneNumber VARCHAR(20),
    Address TEXT,
    Status VARCHAR(20) NOT NULL DEFAULT 'Active',
    
    RoleSettingID INT,
    FOREIGN KEY (RoleSettingID) REFERENCES "Setting"(SettingID),
    
    ShopID INT,
    FOREIGN KEY (ShopID) REFERENCES "Shop"(ShopID)
);

-- -----------------------------------------------------
-- Bảng Product - CẬP NHẬT: Thêm Description
-- -----------------------------------------------------
CREATE TABLE "Product" (
    ProductID SERIAL PRIMARY KEY,
    ProductName VARCHAR(255) NOT NULL,
    SKU VARCHAR(50) UNIQUE,
    Price NUMERIC(10, 2) NOT NULL,
    Description TEXT
);

-- -----------------------------------------------------
-- Bảng Ingredient - CẬP NHẬT: Thêm MinStock, Description
-- ĐÃ DI CHUYỂN LÊN TRƯỚC BẢNG "ProductIngredient" ĐỂ SỬA LỖI
-- -----------------------------------------------------
CREATE TABLE "Ingredient" (
    IngredientID SERIAL PRIMARY KEY,
    IngredientName VARCHAR(255) NOT NULL,
    Description TEXT,
    CurrentStock NUMERIC(10, 2) NOT NULL DEFAULT 0,
    MinStock NUMERIC(10, 2) NOT NULL DEFAULT 0,
    
    UnitSettingID INT,
    FOREIGN KEY (UnitSettingID) REFERENCES "Setting"(SettingID)
);

-- -----------------------------------------------------
-- Bảng ProductIngredient (Công thức/Recipe)
-- -----------------------------------------------------
CREATE TABLE "ProductIngredient" (
    ProductID INT NOT NULL,
    IngredientID INT NOT NULL,
    RequiredQuantity NUMERIC(10, 2) NOT NULL,
    
    PRIMARY KEY (ProductID, IngredientID),
    FOREIGN KEY (ProductID) REFERENCES "Product"(ProductID),
    FOREIGN KEY (IngredientID) REFERENCES "Ingredient"(IngredientID)
);

-- -----------------------------------------------------
-- Bảng Orders
-- -----------------------------------------------------
CREATE TABLE "Orders" (
    OrderID SERIAL PRIMARY KEY,
    OrderDate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    TotalAmount NUMERIC(10, 2) NOT NULL,
    
    UserID INT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES "User"(UserID),
    
    ShopID INT,
    FOREIGN KEY (ShopID) REFERENCES "Shop"(ShopID)
);

-- -----------------------------------------------------
-- Bảng OrderDetail
-- -----------------------------------------------------
CREATE TABLE "OrderDetail" (
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    Subtotal NUMERIC(10, 2) NOT NULL,
    
    PRIMARY KEY (OrderID, ProductID),
    FOREIGN KEY (OrderID) REFERENCES "Orders"(OrderID),
    FOREIGN KEY (ProductID) REFERENCES "Product"(ProductID)
);

-- -----------------------------------------------------
-- Bảng Supplier
-- -----------------------------------------------------
CREATE TABLE "Supplier" (
    SupplierID SERIAL PRIMARY KEY,
    SupplierName VARCHAR(255) NOT NULL,
    ContactEmail VARCHAR(255)
);

-- -----------------------------------------------------
-- Bảng SupplierProduct (Liên kết N:M)
-- -----------------------------------------------------
CREATE TABLE "SupplierProduct" (
    SupplierID INT NOT NULL,
    ProductID INT NOT NULL,
    SupplyPrice NUMERIC(10, 2),
    
    PRIMARY KEY (SupplierID, ProductID),
    FOREIGN KEY (SupplierID) REFERENCES "Supplier"(SupplierID),
    FOREIGN KEY (ProductID) REFERENCES "Product"(ProductID)
);

-- -----------------------------------------------------
-- Bảng PurchaseOrder
-- -----------------------------------------------------
CREATE TABLE "PurchaseOrder" (
    PurchaseOrderID SERIAL PRIMARY KEY,
    OrderDate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ExpectedDeliveryDate DATE,
    TotalAmount NUMERIC(10, 2),
    Status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    
    SupplierID INT,
    FOREIGN KEY (SupplierID) REFERENCES "Supplier"(SupplierID)
);

-- -----------------------------------------------------
-- Bảng PurchaseOrderDetail
-- -----------------------------------------------------
CREATE TABLE "PurchaseOrderDetail" (
    PurchaseOrderID INT NOT NULL,
    IngredientID INT NOT NULL,
    Quantity INT NOT NULL,
    PricePerUnit NUMERIC(10, 2) NOT NULL,
    
    PRIMARY KEY (PurchaseOrderID, IngredientID),
    FOREIGN KEY (PurchaseOrderID) REFERENCES "PurchaseOrder"(PurchaseOrderID),
    FOREIGN KEY (IngredientID) REFERENCES "Ingredient"(IngredientID)
);

-- -----------------------------------------------------
-- Bảng Issue
-- -----------------------------------------------------
CREATE TABLE "Issue" (
    IssueID SERIAL PRIMARY KEY,
    IssueTitle VARCHAR(255) NOT NULL,
    IssueDescription TEXT,
    IssueDate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    
    PurchaseOrderID INT,
    FOREIGN KEY (PurchaseOrderID) REFERENCES "PurchaseOrder"(PurchaseOrderID)
);

-- -----------------------------------------------------
-- Bảng Permission - Danh sách các quyền (hành động)
-- BẢNG MỚI BỔ SUNG
-- -----------------------------------------------------
CREATE TABLE "Permission" (
    PermissionID SERIAL PRIMARY KEY,
    PermissionName VARCHAR(255) NOT NULL UNIQUE, -- VD: 'VIEW_USER', 'CREATE_ORDER'
    Description TEXT
);

-- -----------------------------------------------------
-- Bảng RolePermission - Liên kết giữa Role (Setting) và Permission
-- BẢNG MỚI BỔ SUNG
-- -----------------------------------------------------
CREATE TABLE "RolePermission" (
    RoleSettingID INT NOT NULL,               -- Tham chiếu đến Setting (Type='Role')
    PermissionID INT NOT NULL,                -- Tham chiếu đến Permission
    PRIMARY KEY (RoleSettingID, PermissionID),
    FOREIGN KEY (RoleSettingID) REFERENCES "Setting"(SettingID)
        ON DELETE CASCADE,
    FOREIGN KEY (PermissionID) REFERENCES "Permission"(PermissionID)
        ON DELETE CASCADE
);
alter table "Permission"
ADD COLUMN IF NOT EXISTS permission_path TEXT;