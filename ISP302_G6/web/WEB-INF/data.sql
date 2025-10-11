-- -----------------------------------------------------
-- THÊM DỮ LIỆU MẪU
-- -----------------------------------------------------

-- Bảng Setting (Thêm các vai trò và đơn vị tính)
-- SettingID: 1=Admin, 2=Manager, 3=Staff, 4=kg, 5=gram, 6=lít, 7=ml, 8=cái
INSERT INTO "Setting" (SettingType, SettingValue, Description) VALUES
('Role', 'Admin', 'Quản trị viên hệ thống'),
('Role', 'Manager', 'Quản lý cửa hàng'),
('Role', 'Staff', 'Nhân viên bán hàng'),
('Unit', 'kg', 'Kilogram'),
('Unit', 'gram', 'Gram'),
('Unit', 'lít', 'Lít'),
('Unit', 'ml', 'Mililít'),
('Unit', 'cái', 'Đơn vị đếm');

-- Bảng Shop
-- ShopID: 1
INSERT INTO "Shop" (ShopName, Address) VALUES
('Gemini Coffee & Tea', '123 Đường Lập Trình, Quận AI, TP. Google');

-- Bảng User (Tạo user quản lý và nhân viên cho shop)
-- UserID: 1 (Manager), 2 (Staff)
INSERT INTO "User" (UserName, Email, PasswordHash, FullName, PhoneNumber, Address, RoleSettingID, ShopID) VALUES
('manager01', 'manager@gemini.com', 'hashed_password_123', 'Nguyễn Văn Quản Lý', '0909111222', '456 Đường ABC', 2, 1),
('staff01', 'staff@gemini.com', 'hashed_password_456', 'Trần Thị Nhân Viên', '0909333444', '789 Đường XYZ', 3, 1);

-- Bảng Supplier (Nhà cung cấp)
-- SupplierID: 1, 2
INSERT INTO "Supplier" (SupplierName, ContactEmail) VALUES
('Nhà Cung Cấp Nông Sản Sạch', 'contact@nongsansach.com'),
('Công ty TNHH Nguyên Liệu Pha Chế', 'info@nguyenlieupc.vn');

-- Bảng Product (Sản phẩm bán ra)
-- ProductID: 1, 2
INSERT INTO "Product" (ProductName, SKU, Price, Description) VALUES
('Cà Phê Sữa', 'CFS001', 25000, 'Cà phê Robusta pha phin truyền thống cùng sữa đặc.'),
('Trà Đào Cam Sả', 'TDCS01', 35000, 'Trà đen ủ lạnh kết hợp với đào, cam và sả tươi.');

-- Bảng Ingredient (Nguyên liệu trong kho)
-- IngredientID: 1-Hạt cafe, 2-Sữa đặc, 3-Đường, 4-Trà túi lọc, 5-Siro đào, 6-Đào miếng, 7-Cam, 8-Sả
INSERT INTO "Ingredient" (IngredientName, CurrentStock, MinStock, UnitSettingID) VALUES
('Hạt cà phê Robusta', 10, 2, 4),    -- 10kg, MinStock 2kg (UnitID=4)
('Sữa đặc', 20, 5, 8),               -- 20 lon (cái), MinStock 5 lon (UnitID=8)
('Đường cát trắng', 50, 10, 4),      -- 50kg, MinStock 10kg (UnitID=4)
('Trà đen túi lọc', 100, 20, 8),     -- 100 túi (cái), MinStock 20 túi (UnitID=8)
('Siro đào', 5, 1, 6),               -- 5 lít, MinStock 1 lít (UnitID=6)
('Đào ngâm', 15, 3, 8),              -- 15 hộp (cái), MinStock 3 hộp (UnitID=8)
('Cam tươi', 20, 5, 4),               -- 20kg, MinStock 5kg (UnitID=4)
('Sả cây', 5, 1, 4);                  -- 5kg, MinStock 1kg (UnitID=4)

-- Bảng ProductIngredient (Công thức cho sản phẩm)
INSERT INTO "ProductIngredient" (ProductID, IngredientID, RequiredQuantity) VALUES
(1, 1, 0.025), -- Product 1 (CFS) cần Ingredient 1 (Cafe hạt), 25 gram (0.025kg)
(1, 2, 0.040), -- Product 1 (CFS) cần Ingredient 2 (Sữa đặc), 40 ml (giả sử 1 lon 1l)
(1, 3, 0.010), -- Product 1 (CFS) cần Ingredient 3 (Đường), 10 gram (0.010kg)
(2, 4, 1),     -- Product 2 (Trà đào) cần Ingredient 4 (Trà túi lọc), 1 cái
(2, 5, 0.030), -- Product 2 (Trà đào) cần Ingredient 5 (Siro đào), 30 ml (0.030 lít)
(2, 6, 0.2);   -- Product 2 (Trà đào) cần Ingredient 6 (Đào miếng), 0.2 hộp

-- Bảng Orders (Một đơn hàng được tạo)
-- OrderID: 1
INSERT INTO "Orders" (OrderDate, TotalAmount, UserID, ShopID) VALUES
(NOW(), 60000, 2, 1); -- Nhân viên 2 tạo đơn tại shop 1

-- Bảng OrderDetail (Chi tiết đơn hàng trên)
INSERT INTO "OrderDetail" (OrderID, ProductID, Quantity, Subtotal) VALUES
(1, 1, 1, 25000), -- Đơn 1, mua 1 Cà Phê Sữa
(1, 2, 1, 35000); -- Đơn 1, mua 1 Trà Đào Cam Sả

-- Bảng PurchaseOrder (Tạo đơn đặt hàng nguyên liệu)
-- PurchaseOrderID: 1
INSERT INTO "PurchaseOrder" (OrderDate, ExpectedDeliveryDate, Status, SupplierID) VALUES
(NOW(), CURRENT_DATE + INTERVAL '3 day', 'Pending', 1); -- Đặt hàng nhà cung cấp 1

-- Bảng PurchaseOrderDetail (Chi tiết đặt nguyên liệu gì)
INSERT INTO "PurchaseOrderDetail" (PurchaseOrderID, IngredientID, Quantity, PricePerUnit) VALUES
(1, 1, 10, 150000), -- Đơn đặt hàng 1, đặt 10 kg Cafe hạt, giá 150.000/kg
(1, 3, 20, 20000);  -- Đơn đặt hàng 1, đặt 20 kg Đường, giá 20.000/kg

-- Bảng Issue (Tạo một sự cố liên quan đến đơn đặt hàng)
INSERT INTO "Issue" (IssueTitle, IssueDescription, IssueDate, PurchaseOrderID) VALUES
('Giao hàng chậm trễ', 'NCC báo sẽ giao hàng trễ 2 ngày so với dự kiến.', NOW(), 1);