-- ========= INSERT DATA INTO "Permission" TABLE =========
-- Xóa dữ liệu cũ để tránh trùng lặp nếu chạy lại script
TRUNCATE TABLE "Permission" RESTART IDENTITY CASCADE;

INSERT INTO "Permission" ("permissionname", "permission_path", "description") VALUES
-- User & Authentication
('AUTH_LOGIN',           '/auth/login',                  'Đăng nhập hệ thống'),
('AUTH_LOGOUT',          '/auth/logout',                 'Đăng xuất hệ thống'),
('AUTH_RESET_PASSWORD',  '/auth/reset-password',         'Yêu cầu đặt lại mật khẩu'),
('PROFILE_VIEW',         '/profile/view',                'Xem hồ sơ cá nhân'),
('PROFILE_UPDATE',       '/profile/update',              'Cập nhật hồ sơ cá nhân'),
('PROFILE_CHANGE_PASSWORD','/profile/change-password',   'Đổi mật khẩu'),

-- Shop Management
('SHOP_LIST_VIEW',       '/shops/list',                  'Xem danh sách cửa hàng'),
('SHOP_CREATE',          '/shops/create',                'Tạo cửa hàng mới'),
('SHOP_UPDATE',          '/shops/update',                'Cập nhật chi tiết cửa hàng'),

-- HR Management
('HR_DASHBOARD_VIEW',    '/dashboard/hr',                'Xem bảng điều khiển Nhân sự'),
('USER_LIST_VIEW',       '/users/list',                  'Xem danh sách người dùng'),
('USER_CREATE',          '/users/create',                'Tạo người dùng mới'),
('USER_UPDATE',          '/users/update',                'Cập nhật chi tiết người dùng'),
('USER_TOGGLE_STATUS',   '/users/toggle-status',         'Kích hoạt/Vô hiệu hóa người dùng'),

-- Inventory Management
('INVENTORY_DASHBOARD_VIEW','/dashboard/inventory',      'Xem bảng điều khiển Kho'),
('PO_LIST_VIEW',         '/purchase-orders/list',        'Xem danh sách đơn đặt hàng (PO)'),
('PO_CREATE',            '/purchase-orders/create',      'Tạo đơn đặt hàng mới'),
('PO_UPDATE',            '/purchase-orders/update',      'Cập nhật chi tiết đơn đặt hàng'),
('PO_CONFIRM',           '/purchase-orders/confirm',     'Xác nhận đơn đặt hàng'),
('STOCK_RECEIVE',        '/stocks/receive',              'Nhận hàng vào kho'),
('INGREDIENT_LIST_VIEW', '/ingredients/list',            'Xem danh sách nguyên liệu'),
('INGREDIENT_CREATE',    '/ingredients/create',          'Tạo nguyên liệu mới'),
('INGREDIENT_UPDATE',    '/ingredients/update',          'Cập nhật chi tiết nguyên liệu'),
('ISSUE_LIST_VIEW',      '/issues/list',                 'Xem danh sách phiếu xuất kho'),
('ISSUE_CREATE',         '/issues/create',               'Tạo phiếu xuất kho mới'),
('ISSUE_UPDATE',         '/issues/update',               'Cập nhật chi tiết phiếu xuất kho'),

-- Barista Functions
('BARISTA_DASHBOARD_VIEW','/dashboard/barista',         'Xem bảng điều khiển Barista'),
('ISSUE_CONFIRM',        '/issues/confirm',              'Xác nhận phiếu xuất kho'),
('ORDER_LIST_VIEW',      '/orders/list',                 'Xem danh sách đơn hàng'),
('ORDER_UPDATE',         '/orders/update',               'Cập nhật chi tiết đơn hàng'),

-- Admin Functions
('ADMIN_DASHBOARD_VIEW', '/dashboard/admin',             'Xem bảng điều khiển Admin'),
('SETTING_LIST_VIEW',    '/settings/list',               'Xem danh sách cài đặt'),
('SETTING_CREATE',       '/settings/create',             'Tạo cài đặt mới'),
('SETTING_UPDATE',       '/settings/update',             'Cập nhật chi tiết cài đặt'),
('SETTING_TOGGLE_STATUS','/settings/toggle-status',      'Kích hoạt/Vô hiệu hóa cài đặt'),
('PRODUCT_LIST_VIEW',    '/products/list',               'Xem danh sách sản phẩm'),
('PRODUCT_UPDATE',       '/products/update',             'Cập nhật chi tiết sản phẩm'),
('PRODUCT_IMPORT',       '/products/import',             'Nhập danh sách sản phẩm từ file'),

-- API (External Systems)
('API_RECEIVE_PRODUCT',  '/api/products/receive',        'API nhận dữ liệu sản phẩm'),
('API_RECEIVE_ORDER',    '/api/orders/receive',          'API nhận dữ liệu đơn hàng');


-- ========= LINK ROLES AND PERMISSIONS IN "RolePermission" TABLE =========
-- Xóa dữ liệu cũ để đảm bảo tính toàn vẹn
TRUNCATE TABLE "RolePermission";

DO $$
DECLARE
    user_role_id INT := (SELECT "settingid" FROM "Setting" WHERE "settingvalue" = 'User');
    hr_role_id INT := (SELECT "settingid" FROM "Setting" WHERE "settingvalue" = 'HR');
    inventory_role_id INT := (SELECT "settingid" FROM "Setting" WHERE "settingvalue" = 'Inventory');
    barista_role_id INT := (SELECT "settingid" FROM "Setting" WHERE "settingvalue" = 'Barista');
    admin_role_id INT := (SELECT "settingid" FROM "Setting" WHERE "settingvalue" = 'Admin');
BEGIN

-- 1. Gán quyền cơ bản cho vai trò "User"
INSERT INTO "RolePermission" ("rolesettingid", "permissionid")
SELECT user_role_id, "permissionid" FROM "Permission" WHERE "permissionname" IN (
    'AUTH_LOGOUT',
    'PROFILE_VIEW',
    'PROFILE_UPDATE',
    'PROFILE_CHANGE_PASSWORD',
    'SHOP_LIST_VIEW',
    'SHOP_CREATE',
    'SHOP_UPDATE'
);

-- 2. Gán quyền cho vai trò "HR" (Bao gồm quyền của User và quyền riêng)
INSERT INTO "RolePermission" ("rolesettingid", "permissionid")
SELECT hr_role_id, "permissionid" FROM "Permission" WHERE "permissionname" IN (
    -- Quyền kế thừa từ User
    'AUTH_LOGOUT', 'PROFILE_VIEW', 'PROFILE_UPDATE', 'PROFILE_CHANGE_PASSWORD',
    'SHOP_LIST_VIEW', 'SHOP_CREATE', 'SHOP_UPDATE',
    -- Quyền riêng của HR
    'HR_DASHBOARD_VIEW', 'USER_LIST_VIEW', 'USER_CREATE', 'USER_UPDATE', 'USER_TOGGLE_STATUS'
);

-- 3. Gán quyền cho vai trò "Inventory" (Bao gồm quyền của User và quyền riêng)
INSERT INTO "RolePermission" ("rolesettingid", "permissionid")
SELECT inventory_role_id, "permissionid" FROM "Permission" WHERE "permissionname" IN (
    -- Quyền kế thừa từ User
    'AUTH_LOGOUT', 'PROFILE_VIEW', 'PROFILE_UPDATE', 'PROFILE_CHANGE_PASSWORD',
    'SHOP_LIST_VIEW', 'SHOP_CREATE', 'SHOP_UPDATE',
    -- Quyền riêng của Inventory
    'INVENTORY_DASHBOARD_VIEW', 'PO_LIST_VIEW', 'PO_CREATE', 'PO_UPDATE',
    'STOCK_RECEIVE', 'INGREDIENT_LIST_VIEW', 'INGREDIENT_CREATE', 'INGREDIENT_UPDATE',
    'ISSUE_LIST_VIEW', 'ISSUE_CREATE', 'ISSUE_UPDATE'
);

-- 4. Gán quyền cho vai trò "Barista" (Bao gồm quyền của User và quyền riêng)
INSERT INTO "RolePermission" ("rolesettingid", "permissionid")
SELECT barista_role_id, "permissionid" FROM "Permission" WHERE "permissionname" IN (
    -- Quyền kế thừa từ User
    'AUTH_LOGOUT', 'PROFILE_VIEW', 'PROFILE_UPDATE', 'PROFILE_CHANGE_PASSWORD',
    'SHOP_LIST_VIEW', 'SHOP_CREATE', 'SHOP_UPDATE',
    -- Quyền riêng của Barista
    'BARISTA_DASHBOARD_VIEW', 'ISSUE_CONFIRM', 'ORDER_LIST_VIEW', 'ORDER_UPDATE'
);

-- 5. Gán quyền cho vai trò "Admin" (Admin có TẤT CẢ các quyền)
INSERT INTO "RolePermission" ("rolesettingid", "permissionid")
SELECT admin_role_id, "permissionid" FROM "Permission";

END $$;