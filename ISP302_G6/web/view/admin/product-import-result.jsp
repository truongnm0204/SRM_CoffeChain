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
            max-width: 900px;
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
        .summary-box {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 30px;
        }
        .summary-item {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            text-align: center;
            border: 1px solid #dee2e6;
        }
        .summary-item.success {
            background-color: #d4edda;
            border-color: #c3e6cb;
        }
        .summary-item.warning {
            background-color: #fff3cd;
            border-color: #ffeaa7;
        }
        .summary-item.info {
            background-color: #d1ecf1;
            border-color: #bee5eb;
        }
        .summary-number {
            font-size: 36px;
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        .summary-label {
            font-size: 14px;
            color: #666;
        }
        .errors-section {
            margin-top: 30px;
        }
        .errors-section h2 {
            color: #d32f2f;
            margin-bottom: 15px;
        }
        .error-list {
            background-color: #ffebee;
            border: 1px solid #ef9a9a;
            border-radius: 4px;
            padding: 15px;
            max-height: 400px;
            overflow-y: auto;
        }
        .error-list ul {
            margin: 0;
            padding-left: 20px;
        }
        .error-list li {
            margin-bottom: 8px;
            color: #c62828;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
            text-decoration: none;
            color: white;
        }
        .btn-primary {
            background-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .actions {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #dee2e6;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ ${pageTitle}</h1>

        <c:if test="${not empty importResult}">
            <div class="summary-box">
                <div class="summary-item info">
                    <div class="summary-number">${importResult.totalRows}</div>
                    <div class="summary-label">Tổng số dòng</div>
                </div>
                <div class="summary-item success">
                    <div class="summary-number">${importResult.successCount}</div>
                    <div class="summary-label">Thành công</div>
                </div>
                <div class="summary-item warning">
                    <div class="summary-number">${importResult.failedCount}</div>
                    <div class="summary-label">Thất bại</div>
                </div>
            </div>

            <div class="summary-box">
                <div class="summary-item">
                    <div class="summary-number">${importResult.insertedCount}</div>
                    <div class="summary-label">Sản phẩm mới</div>
                </div>
                <div class="summary-item">
                    <div class="summary-number">${importResult.updatedCount}</div>
                    <div class="summary-label">Đã cập nhật</div>
                </div>
            </div>

            <c:if test="${not empty importResult.errors && importResult.errors.size() > 0}">
                <div class="errors-section">
                    <h2>⚠️ Danh sách lỗi (${importResult.errors.size()} lỗi)</h2>
                    <div class="error-list">
                        <ul>
                            <c:forEach var="error" items="${importResult.errors}">
                                <li>${error}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty importResult.errors || importResult.errors.size() == 0}">
                <p style="color: #28a745; font-size: 18px; text-align: center; margin: 20px 0;">
                    🎉 Tất cả sản phẩm đã được import thành công!
                </p>
            </c:if>
        </c:if>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/admin/product/import" class="btn btn-primary">
                📤 Import thêm file khác
            </a>
            <a href="${pageContext.request.contextPath}/admin/product/list" class="btn btn-success">
                📋 Xem danh sách sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/admin/product/add" class="btn btn-secondary">
                ➕ Thêm sản phẩm mới
            </a>
        </div>
    </div>
</body>
</html>
