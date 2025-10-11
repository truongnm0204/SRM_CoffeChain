# Hướng dẫn sử dụng Config Properties

## File config.properties
File này chứa tất cả các cấu hình quan trọng của ứng dụng:

### 1. Supabase Configuration
- `supabase.url`: URL của Supabase project
- `supabase.anon.key`: Anonymous key từ Supabase
- `supabase.service.role.key`: Service role key từ Supabase (cẩn thận với key này!)

### 2. Database Configuration
- `db.url`: JDBC URL để kết nối database
- `db.username`: Username database
- `db.password`: Password database
- `db.driver`: Driver class cho PostgreSQL

### 3. Security Configuration
- `jwt.secret`: Secret key để sign JWT tokens
- `jwt.expiration`: Thời gian hết hạn token (milliseconds)

### 4. File Upload Configuration
- `upload.max.size`: Kích thước file tối đa (bytes)
- `upload.allowed.types`: Các loại file được phép upload

## Cách sử dụng ConfigService

```java
// Lấy instance của ConfigService
ConfigService config = ConfigService.getInstance();

// Lấy Supabase URL
String supabaseUrl = config.getSupabaseUrl();

// Lấy database connection info
String dbUrl = config.getDatabaseUrl();
String dbUser = config.getDatabaseUsername();
String dbPass = config.getDatabasePassword();

// Lấy JWT secret
String jwtSecret = config.getJwtSecret();

// Lấy bất kỳ property nào
String customProperty = config.getProperty("custom.key", "default-value");
```

## Lưu ý bảo mật
1. **KHÔNG** commit file config.properties với thông tin thật vào Git
2. Tạo file `config.properties.example` với các giá trị mẫu
3. Thêm `config.properties` vào `.gitignore`
4. Mỗi environment (dev, staging, prod) nên có file config riêng

## Ví dụ file .gitignore
```
src/java/config.properties
*.properties
!*.properties.example
```