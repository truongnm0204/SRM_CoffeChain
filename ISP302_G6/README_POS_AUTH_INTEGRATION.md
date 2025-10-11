# 🔐 POS – Warehouse Integration via JWT Authentication

## 🧩 1️⃣ Mục tiêu

Thiết lập quy trình **xác thực giữa phần mềm Quản lý Kho** và **POS Server**, sử dụng JSON Web Token (JWT).  
Mục tiêu: cho phép hệ thống kho lấy dữ liệu đơn hàng, sản phẩm… từ POS một cách **an toàn và có kiểm soát**.

---

## ⚙️ 2️⃣ Cấu hình POS Server

POS server được mô phỏng bằng [JSON Server](https://github.com/typicode/json-server) kết hợp JWT.

### 📂 File cấu trúc
```
pos_server_api/
│
├── db.json
├── pos-server.js
├── package.json
└── node_modules/
```

### ▶️ Chạy server
```bash
npm install
node pos-server.js
```

Khi chạy thành công, bạn sẽ thấy:
```
✅ POS Mock Server running at http://localhost:4000
POST /auth/login → get JWT #2
GET  /orders → fetch orders (need JWT)
```

---

## 🔑 3️⃣ API Xác thực (JWT Login)

### **Endpoint**
```
POST http://localhost:4000/auth/login
```

### **Request body**
```json
{
  "client_id": "warehouse_system",
  "client_secret": "abc123"
}
```

### **Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
}
```

### **Thông tin token**
| Thuộc tính | Giá trị |
|-------------|----------|
| `iss` | pos-system |
| `client` | warehouse_system |
| `role` | integration |
| `exp` | hết hạn sau 1h |

---

## 🧠 4️⃣ Cách hệ thống Quản lý Kho xác thực và lấy dữ liệu

### **Step 1 – Gửi request lấy JWT**
Ví dụ Java (Servlet hoặc Spring Boot):

```java
URL url = new URL("http://localhost:4000/auth/login");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("POST");
conn.setRequestProperty("Content-Type", "application/json");
conn.setDoOutput(true);

String json = "{"client_id":"warehouse_system","client_secret":"abc123"}";
try (OutputStream os = conn.getOutputStream()) {
    byte[] input = json.getBytes("utf-8");
    os.write(input, 0, input.length);
}

BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
StringBuilder response = new StringBuilder();
String line;
while ((line = br.readLine()) != null) {
    response.append(line.trim());
}

System.out.println("Token: " + response.toString());
```

Token này sẽ được lưu tạm (ví dụ trong biến `session` hoặc `application`).

---

### **Step 2 – Gọi API có xác thực JWT**

```java
String token = "..."; // token nhận từ bước 1

URL url = new URL("http://localhost:4000/orders");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestProperty("Authorization", "Bearer " + token);

BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
StringBuilder data = new StringBuilder();
String line;
while ((line = br.readLine()) != null) {
    data.append(line.trim());
}
System.out.println("Orders: " + data.toString());
```

Kết quả:
```json
[
  {
    "id": 1,
    "shopId": 1,
    "status": "Completed",
    "total": 95000
  }
]
```

---

## 🔐 5️⃣ Xác minh và Bảo mật

1. **JWT chỉ hợp lệ trong 1 giờ** (`expiresIn: 1h`).
2. Khi token hết hạn, kho cần **gọi lại `/auth/login`** để lấy token mới.
3. POS Server chỉ cấp token khi:
   - `client_id` = `"warehouse_system"`
   - `client_secret` = `"abc123"`
4. Tất cả request khác đều bị từ chối nếu thiếu hoặc sai token (`401 Unauthorized`).

---

## 🔁 6️⃣ Tích hợp thực tế

| Mục đích | Endpoint POS | Mô tả |
|-----------|---------------|-------|
| Lấy token | `/auth/login` | Đăng nhập và nhận JWT |
| Đồng bộ đơn hàng | `/orders` | Lấy danh sách đơn hàng |
| Đồng bộ sản phẩm | `/products` (nếu có) | Lấy dữ liệu sản phẩm |
| Kiểm tra token hợp lệ | Middleware trên POS server | Xác thực trước khi trả dữ liệu |

---

## 🧩 7️⃣ Test nhanh bằng PowerShell

```powershell
Invoke-RestMethod -Uri "http://localhost:4000/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body '{"client_id":"warehouse_system","client_secret":"abc123"}'
```

---

## ✅ 8️⃣ Kết quả mong đợi

- Hệ thống kho có thể **gọi API POS** an toàn qua JWT.
- Mọi dữ liệu đều được bảo vệ bởi middleware xác thực.
- Mô hình này sẵn sàng để thay thế POS thật khi triển khai chính thức.

---

**Tác giả:**  
> 🧠 _Integration Guide by Đỗ Đường Bách_  
> 📅 Cập nhật: 2025-10-11  
> 🚀 Phiên bản: 1.0
