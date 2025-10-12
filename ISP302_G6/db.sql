

-- =============================================================================
-- DROP OLD TABLES
-- =============================================================================
DROP TABLE IF EXISTS auditlog CASCADE;
DROP TABLE IF EXISTS inventorytransaction CASCADE;
DROP TABLE IF EXISTS importreceiptdetail CASCADE;
DROP TABLE IF EXISTS ingredientbatch CASCADE;
DROP TABLE IF EXISTS importreceipt CASCADE;
DROP TABLE IF EXISTS purchaseorderdetail CASCADE;
DROP TABLE IF EXISTS issue CASCADE;
DROP TABLE IF EXISTS purchaseorder CASCADE;
DROP TABLE IF EXISTS orderdetail CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS productingredient CASCADE;
DROP TABLE IF EXISTS supplierproduct CASCADE;
DROP TABLE IF EXISTS ingredient CASCADE;
DROP TABLE IF EXISTS ingredientcategory CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;
DROP TABLE IF EXISTS rolepermission CASCADE;
DROP TABLE IF EXISTS permission CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS shop CASCADE;
DROP TABLE IF EXISTS setting CASCADE;

-- =============================================================================
-- SETTING
-- =============================================================================
CREATE TABLE setting (
    settingid SERIAL PRIMARY KEY,
    settingtype VARCHAR(50) NOT NULL,
    settingvalue VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'Active'
);

-- =============================================================================
-- SHOP
-- =============================================================================
CREATE TABLE shop (
    shopid SERIAL PRIMARY KEY,
    shopname VARCHAR(255) NOT NULL,
    address TEXT,
    status VARCHAR(20) DEFAULT 'Active'
);

-- =============================================================================
-- USER
-- =============================================================================
CREATE TABLE "user" (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    passwordhash VARCHAR(255) NOT NULL,
    fullname VARCHAR(255),
    phonenumber VARCHAR(20),
    address TEXT,
    status VARCHAR(20) DEFAULT 'Active',
    rolesettingid INT REFERENCES setting(settingid),
    shopid INT REFERENCES shop(shopid)
);

-- =============================================================================
-- PRODUCT
-- =============================================================================
CREATE TABLE product (
    productid SERIAL PRIMARY KEY,
    productname VARCHAR(255) NOT NULL,
    sku VARCHAR(50) UNIQUE,
    price NUMERIC(10,2) NOT NULL,
    description TEXT
);

-- =============================================================================
-- INGREDIENT
-- =============================================================================
CREATE TABLE ingredientcategory (
    categoryid SERIAL PRIMARY KEY,
    categoryname VARCHAR(100) NOT NULL,
    description TEXT,
    hasexpiry BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'Active'
);

CREATE TABLE ingredient (
    ingredientid SERIAL PRIMARY KEY,
    ingredientname VARCHAR(255) NOT NULL,
    description TEXT,
    currentstock NUMERIC(10,2) DEFAULT 0,
    minstock NUMERIC(10,2) DEFAULT 0,
    categoryid INT REFERENCES ingredientcategory(categoryid)
);

CREATE TABLE productingredient (
    productid INT NOT NULL REFERENCES product(productid),
    ingredientid INT NOT NULL REFERENCES ingredient(ingredientid),
    requiredquantity NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (productid, ingredientid)
);

-- =============================================================================
-- SUPPLIER
-- =============================================================================
CREATE TABLE supplier (
    supplierid SERIAL PRIMARY KEY,
    suppliername VARCHAR(255) NOT NULL,
    contactemail VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    status VARCHAR(20) DEFAULT 'Active'
);

CREATE TABLE supplierproduct (
    supplierid INT NOT NULL REFERENCES supplier(supplierid),
    productid INT NOT NULL REFERENCES product(productid),
    supplyprice NUMERIC(10,2),
    PRIMARY KEY (supplierid, productid)
);

-- =============================================================================
-- PURCHASE ORDER & IMPORT RECEIPT
-- =============================================================================
CREATE TABLE purchaseorder (
    purchaseorderid SERIAL PRIMARY KEY,
    orderdate TIMESTAMP DEFAULT NOW(),
    expecteddeliverydate DATE,
    totalamount NUMERIC(10,2),
    status VARCHAR(50) DEFAULT 'Pending',
    receivedstatus VARCHAR(20) DEFAULT 'Not Received',
    supplierid INT REFERENCES supplier(supplierid),
    createdby INT REFERENCES "user"(userid),
    approvedby INT REFERENCES "user"(userid)
);

CREATE TABLE purchaseorderdetail (
    purchaseorderid INT NOT NULL REFERENCES purchaseorder(purchaseorderid),
    ingredientid INT NOT NULL REFERENCES ingredient(ingredientid),
    quantity NUMERIC(10,2) NOT NULL,
    priceperunit NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (purchaseorderid, ingredientid)
);

CREATE TABLE importreceipt (
    receiptid SERIAL PRIMARY KEY,
    purchaseorderid INT REFERENCES purchaseorder(purchaseorderid),
    receiveddate TIMESTAMP DEFAULT NOW(),
    invoicenumber VARCHAR(50),
    totalreceivedamount NUMERIC(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'Pending',
    note TEXT,
    createdby INT REFERENCES "user"(userid),
    approvedby INT REFERENCES "user"(userid)
);

CREATE TABLE importreceiptdetail (
    receiptid INT NOT NULL REFERENCES importreceipt(receiptid),
    ingredientid INT NOT NULL REFERENCES ingredient(ingredientid),
    batchcode VARCHAR(50),
    receivedquantity NUMERIC(10,2),
    unitprice NUMERIC(10,2),
    expirydate DATE,
    note TEXT,
    PRIMARY KEY (receiptid, ingredientid)
);

CREATE TABLE ingredientbatch (
    batchid SERIAL PRIMARY KEY,
    ingredientid INT NOT NULL REFERENCES ingredient(ingredientid),
    batchcode VARCHAR(50),
    receiveddate TIMESTAMP DEFAULT NOW(),
    expirydate DATE,
    quantity NUMERIC(10,2),
    remainingquantity NUMERIC(10,2),
    purchaseorderid INT REFERENCES purchaseorder(purchaseorderid),
    receiptid INT REFERENCES importreceipt(receiptid)
);

-- =============================================================================
-- CUSTOMER ORDER
-- =============================================================================
CREATE TABLE orders (
    orderid SERIAL PRIMARY KEY,
    orderdate TIMESTAMP DEFAULT NOW(),
    totalamount NUMERIC(10,2),
    status VARCHAR(50) DEFAULT 'Pending',
    userid INT REFERENCES "user"(userid),
    shopid INT REFERENCES shop(shopid)
);

CREATE TABLE orderdetail (
    orderid INT NOT NULL REFERENCES orders(orderid),
    productid INT NOT NULL REFERENCES product(productid),
    quantity INT NOT NULL,
    subtotal NUMERIC(10,2),
    PRIMARY KEY (orderid, productid)
);

-- =============================================================================
-- INVENTORY & LOG
-- =============================================================================
CREATE TABLE issue (
    issueid SERIAL PRIMARY KEY,
    issuetitle VARCHAR(255),
    issuedescription TEXT,
    issuedate TIMESTAMP DEFAULT NOW(),
    status VARCHAR(50) DEFAULT 'Open',
    purchaseorderid INT REFERENCES purchaseorder(purchaseorderid),
    resolvedby INT REFERENCES "user"(userid)
);

CREATE TABLE inventorytransaction (
    transactionid SERIAL PRIMARY KEY,
    ingredientid INT REFERENCES ingredient(ingredientid),
    quantity NUMERIC(10,2),
    transactiontype VARCHAR(20),
    referencetype VARCHAR(50),
    referenceid INT,
    createdat TIMESTAMP DEFAULT NOW(),
    createdby INT REFERENCES "user"(userid),
    note TEXT
);

CREATE TABLE auditlog (
    logid SERIAL PRIMARY KEY,
    userid INT REFERENCES "user"(userid),
    action VARCHAR(100),
    entity VARCHAR(100),
    entityid INT,
    timestamp TIMESTAMP DEFAULT NOW(),
    description TEXT
);

-- =============================================================================
-- PERMISSION & ROLEPERMISSION
-- =============================================================================
CREATE TABLE permission (
    permissionid SERIAL PRIMARY KEY,
    permissionname VARCHAR(100) NOT NULL,
    permission_path VARCHAR(255),
    description TEXT
);

CREATE TABLE rolepermission (
    rolesettingid INT NOT NULL,
    permissionid INT NOT NULL,
    PRIMARY KEY (rolesettingid, permissionid),
    FOREIGN KEY (rolesettingid) REFERENCES setting(settingid) ON DELETE CASCADE,
    FOREIGN KEY (permissionid) REFERENCES permission(permissionid) ON DELETE CASCADE
);

-- =============================================================================
-- SAMPLE DATA
-- =============================================================================

-- Setting (Roles)
INSERT INTO setting (settingtype, settingvalue, description) VALUES
('ROLE','Admin','Toàn quyền hệ thống'),
('ROLE','Inventory Staff','Nhân viên kho'),
('ROLE','Barista','Nhân viên pha chế'),
('ROLE','Manager','Quản lý duyệt đơn');

-- Shops
INSERT INTO shop (shopname, address) VALUES
('CoffeeShop - Nguyễn Huệ','123 Nguyễn Huệ, Q1, HCM'),
('CoffeeShop - Lê Lợi','456 Lê Lợi, Q3, HCM');

-- Users
INSERT INTO "user" (username, email, passwordhash, fullname, rolesettingid, shopid)
VALUES
('admin','admin@coffee.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','Nguyễn Văn Admin',1,1),
('inventory1','inventory@coffee.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','Trần Thị Kho',2,1),
('barista1','barista@coffee.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','Lê Văn Pha',3,1),
('manager1','manager@coffee.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','Hoàng Văn Lý',4,1);

-- Ingredients & Categories
INSERT INTO ingredientcategory (categoryname, description) VALUES
('Milk','Các loại sữa'),
('Syrup','Siro hương liệu'),
('Powder','Bột pha chế');

INSERT INTO ingredient (ingredientname, currentstock, minstock, categoryid) VALUES
('Fresh Milk',100,10,1),
('Chocolate Syrup',50,5,2),
('Coffee Powder',40,5,3),
('Matcha Powder',30,3,3);

-- Products
INSERT INTO product (productname, sku, price, description) VALUES
('Iced Chocolate','IC001',45000,'Chocolate drink'),
('Milk Coffee','CF001',40000,'Classic milk coffee'),
('Matcha Latte','MC001',50000,'Matcha with milk');

INSERT INTO productingredient VALUES
(1,1,0.2),(1,2,0.1),(2,1,0.25),(2,3,0.05),(3,1,0.2),(3,4,0.05);

-- Suppliers
INSERT INTO supplier (suppliername, contactemail, phone, address) VALUES
('DairyFarm Co.','milk@dairy.com','0909111222','Bình Dương'),
('SweetFlavor Ltd.','syrup@sweet.com','0909333444','Thủ Đức');

INSERT INTO supplierproduct VALUES
(1,1,25000),(2,2,15000),(1,3,20000);

-- Permissions
INSERT INTO permission (permissionname, permission_path, description) VALUES
('Manage Ingredients','/inventory/ingredients','Quản lý nguyên liệu'),
('Adjust Stock','/inventory/adjust','Điều chỉnh tồn kho'),
('Manage Suppliers','/suppliers/manage','Quản lý NCC'),
('Create Purchase Order','/purchase-orders/create','Tạo đơn nhập'),
('Approve Purchase Order','/purchase-orders/approve','Duyệt đơn nhập'),
('Create Order','/order/create','Tạo đơn hàng'),
('View Reports','/reports/view','Xem báo cáo'),
('View Audit Log','/audit/view','Xem log'),
('View Orders','/order/list','Xem danh sách đơn hàng'),
('Manage Orders','/order/manage','Quản lý đơn hàng');

-- RolePermission mapping
-- Admin (1): Full permissions
-- Inventory Staff (2): Manage ingredients, stock, suppliers, purchase orders
-- Barista (3): Create order, View reports, View orders
-- Manager (4): Approve purchase orders, View reports, View audit log, Manage orders
INSERT INTO rolepermission VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
(2,1),(2,2),(2,3),(2,4),
(3,6),(3,7),(3,9),
(4,5),(4,7),(4,8),(4,10);

-- Sample Orders
INSERT INTO orders (orderdate, totalamount, status, userid, shopid) VALUES
(NOW() - INTERVAL '2 days', 135000, 'Completed', 3, 1),
(NOW() - INTERVAL '1 day', 90000, 'Completed', 3, 1),
(NOW() - INTERVAL '5 hours', 95000, 'Processing', 3, 1),
(NOW() - INTERVAL '1 hour', 45000, 'Pending', 3, 1),
(NOW() - INTERVAL '3 days', 140000, 'Completed', 1, 2);

-- Sample OrderDetails
-- Order 1: Iced Chocolate (1) + Milk Coffee (2)
INSERT INTO orderdetail (orderid, productid, quantity, subtotal) VALUES
(1, 1, 2, 90000),  -- 2 Iced Chocolate = 2 x 45,000
(1, 2, 1, 40000),  -- 1 Milk Coffee = 40,000
(1, 3, 1, 50000);  -- 1 Matcha Latte = 50,000
-- Total: 180,000 (updated manually above to 135,000 for demo)

-- Order 2: Milk Coffee (2)
INSERT INTO orderdetail (orderid, productid, quantity, subtotal) VALUES
(2, 2, 2, 80000);  -- 2 Milk Coffee = 2 x 40,000

-- Order 3: Iced Chocolate (1) + Matcha Latte (1)
INSERT INTO orderdetail (orderid, productid, quantity, subtotal) VALUES
(3, 1, 1, 45000),  -- 1 Iced Chocolate
(3, 3, 1, 50000);  -- 1 Matcha Latte

-- Order 4: Iced Chocolate (1)
INSERT INTO orderdetail (orderid, productid, quantity, subtotal) VALUES
(4, 1, 1, 45000);  -- 1 Iced Chocolate

-- Order 5: Mix order
INSERT INTO orderdetail (orderid, productid, quantity, subtotal) VALUES
(5, 1, 1, 45000),  -- 1 Iced Chocolate
(5, 2, 1, 40000),  -- 1 Milk Coffee
(5, 3, 1, 50000);  -- 1 Matcha Latte

-- AuditLog
INSERT INTO auditlog (userid, action, entity, entityid, description)
VALUES 
(1,'LOGIN','User',1,'Admin đăng nhập thành công'),
(3,'CREATE','Order',1,'Barista tạo đơn hàng #1'),
(3,'CREATE','Order',2,'Barista tạo đơn hàng #2');

-- =============================================================================
-- MIGRATIONS FOR POS INTEGRATION
-- =============================================================================

-- Step 1: Add columns to 'shop' table for POS configuration and token storage
ALTER TABLE shop 
  ADD COLUMN apiendpoint VARCHAR(255),
  ADD COLUMN pos_auth_url VARCHAR(255),
  ADD COLUMN client_id VARCHAR(100),
  ADD COLUMN client_secret VARCHAR(200),
  ADD COLUMN pos_token TEXT,
  ADD COLUMN pos_token_exp TIMESTAMP;

-- Step 2: Add columns to 'orders' table to track POS orders and prevent duplicates
ALTER TABLE orders ADD COLUMN externalorderid VARCHAR(100) UNIQUE;
ALTER TABLE orders ADD COLUMN source VARCHAR(20) DEFAULT 'POS';

-- Step 3: Update the sample shop with the correct POS server details
UPDATE shop
SET apiendpoint   = 'http://localhost:4000/',
    pos_auth_url  = 'http://localhost:4000/auth/login',
    client_id     = 'warehouse_system',
    client_secret = 'abc123'
WHERE shopid = 1;
