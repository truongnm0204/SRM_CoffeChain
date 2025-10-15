<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        input[type="file"] {
            display: block;
            width: 100%;
            padding: 10px;
            border: 2px dashed #ddd;
            border-radius: 4px;
            cursor: pointer;
        }
        input[type="checkbox"] {
            margin-right: 8px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-danger {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        .info-box {
            background-color: #e7f3ff;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin-bottom: 20px;
        }
        .info-box h3 {
            margin-top: 0;
            color: #1976D2;
        }
        .info-box ul {
            margin: 10px 0;
            padding-left: 20px;
        }
        .info-box li {
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>${pageTitle}</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                ${error}
            </div>
        </c:if>

        <div class="info-box">
            <h3>📋 Định dạng file Excel</h3>
            <p>File Excel phải có các cột theo thứ tự sau (dòng đầu tiên là tiêu đề):</p>
            <ul>
                <li><strong>Cột A (SKU):</strong> Mã sản phẩm (để trống nếu thêm mới, bắt buộc nếu cập nhật)</li>
                <li><strong>Cột B (Tên sản phẩm):</strong> Tên sản phẩm <em>(bắt buộc)</em></li>
                <li><strong>Cột C (Giá):</strong> Giá sản phẩm <em>(bắt buộc, số dương)</em></li>
                <li><strong>Cột D (Mô tả):</strong> Mô tả sản phẩm <em>(tùy chọn)</em></li>
                <li><strong>Cột E-F (Nguyên liệu 1):</strong> Tên nguyên liệu và số lượng cần <em>(tùy chọn)</em></li>
                <li><strong>Cột G-H (Nguyên liệu 2):</strong> Tên nguyên liệu và số lượng cần <em>(tùy chọn)</em></li>
                <li>...</li>
            </ul>
            <p><strong>Lưu ý:</strong> Tên nguyên liệu phải trùng khớp với tên trong hệ thống.</p>
        </div>

        <form action="${pageContext.request.contextPath}/admin/product/import" method="post"
              enctype="multipart/form-data" onsubmit="return validateForm()">

            <div class="form-group">
                <label for="excelFile">Chọn file Excel:</label>
                <input type="file" id="excelFile" name="excelFile" accept=".xlsx,.xls" required>
            </div>

            <div class="form-group">
                <label>
                    <input type="checkbox" name="updateExisting" value="true" checked>
                    Cập nhật sản phẩm đã tồn tại (theo SKU)
                </label>
                <small style="color: #666; display: block; margin-left: 24px;">
                    Nếu bỏ chọn, các sản phẩm trùng SKU sẽ bị bỏ qua
                </small>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">📤 Upload và Import</button>
                <a href="${pageContext.request.contextPath}/admin/product/list" class="btn btn-secondary">🔙 Quay lại</a>
            </div>
        </form>
    </div>

    <script>
        function validateForm() {
            var fileInput = document.getElementById('excelFile');
            if (!fileInput.files || !fileInput.files[0]) {
                alert('Vui lòng chọn file Excel!');
                return false;
            }

            var fileName = fileInput.files[0].name;
            var fileExt = fileName.split('.').pop().toLowerCase();

            if (fileExt !== 'xlsx' && fileExt !== 'xls') {
                alert('File phải có định dạng .xlsx hoặc .xls!');
                return false;
            }

            // Hiển thị loading indicator
            var submitBtn = document.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '⏳ Đang xử lý...';

            return true;
        }
    </script>
</body>
</html>
