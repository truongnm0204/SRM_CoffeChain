<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <jsp:include page="../common/css.jsp" />
                <style>
                    .product-header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px;
                        border-radius: 10px;
                        margin-bottom: 30px;
                    }

                    .product-info-card {
                        border-left: 4px solid #667eea;
                    }

                    .btn-action {
                        margin-right: 10px;
                    }

                    .info-label {
                        font-weight: 600;
                        color: #6c757d;
                        font-size: 13px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }

                    .info-value {
                        font-size: 16px;
                        color: #212529;
                        font-weight: 500;
                    }

                    .ingredient-list-item {
                        display: flex;
                        justify-content: space-between;
                        padding: 8px 0;
                        border-bottom: 1px dashed #eee;
                    }

                    .ingredient-list-item:last-child {
                        border-bottom: none;
                    }

                    .ingredient-name {
                        font-weight: 500;
                    }

                    .ingredient-quantity {
                        color: #667eea;
                        font-weight: 600;
                    }
                </style>
            </head>

            <body>
                <div class="connect-container align-content-stretch d-flex flex-wrap">
                    <jsp:include page="../common/left-sidebar.jsp" />
                    <div class="page-container">
                        <jsp:include page="../common/header-bar.jsp" />
                        <div class="page-content">
                            <!-- Breadcrumb -->
                            <div class="page-info">
                                <nav aria-label="breadcrumb">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/product/list">Sản
                                                phẩm</a></li>
                                        <li class="breadcrumb-item active" aria-current="page">Chi tiết sản phẩm
                                            #${product.productId}</li>
                                    </ol>
                                </nav>
                            </div>

                            <!-- Main Content -->
                            <div class="main-wrapper">
                                <!-- Product Header -->
                                <div class="product-header">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <h2 class="mb-2">
                                                <i class="material-icons-outlined">local_cafe</i>
                                                ${product.productName}
                                            </h2>
                                            <p class="mb-0">
                                                <i class="material-icons-outlined"
                                                    style="font-size: 18px; vertical-align: middle;">vpn_key</i>
                                                SKU: <code>${product.sku}</code>
                                            </p>
                                        </div>
                                        <div class="col-md-4 text-right">
                                            <h3 class="mb-0">
                                                <strong>
                                                    <fmt:formatNumber value="${product.price}" type="currency"
                                                        currencySymbol="₫" />
                                                </strong>
                                            </h3>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <!-- Product Information -->
                                    <div class="col-md-4">
                                        <div class="card product-info-card mb-4">
                                            <div class="card-header">
                                                <h5 class="card-title mb-0">
                                                    <i class="material-icons-outlined">info</i>
                                                    Thông tin sản phẩm
                                                </h5>
                                            </div>
                                            <div class="card-body">
                                                <div class="mb-3">
                                                    <div class="info-label">Mã sản phẩm</div>
                                                    <div class="info-value">#${product.productId}</div>
                                                </div>
                                                <div class="mb-3">
                                                    <div class="info-label">Tên sản phẩm</div>
                                                    <div class="info-value">${product.productName}</div>
                                                </div>
                                                <div class="mb-3">
                                                    <div class="info-label">SKU</div>
                                                    <div class="info-value"><code>${product.sku}</code></div>
                                                </div>
                                                <div class="mb-3">
                                                    <div class="info-label">Giá</div>
                                                    <div class="info-value">
                                                        <fmt:formatNumber value="${product.price}" type="currency"
                                                            currencySymbol="₫" />
                                                    </div>
                                                </div>
                                                <div>
                                                    <div class="info-label">Mô tả</div>
                                                    <div class="info-value">${product.description}</div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Actions -->
                                        <div class="card">
                                            <div class="card-header">
                                                <h5 class="card-title mb-0">
                                                    <i class="material-icons-outlined">settings</i>
                                                    Thao tác
                                                </h5>
                                            </div>
                                            <div class="card-body">
                                                <a href="${pageContext.request.contextPath}/admin/product/edit/${product.productId}"
                                                    class="btn btn-warning btn-block mb-2">
                                                    <i class="material-icons">edit</i>
                                                    Chỉnh sửa sản phẩm
                                                </a>
                                                <button type="button" class="btn btn-danger btn-block"
                                                    onclick="confirmDelete(${product.productId}, '${product.productName}')">
                                                    <i class="material-icons">delete</i>
                                                    Xóa sản phẩm
                                                </button>
                                                <hr>
                                                <a href="${pageContext.request.contextPath}/admin/product/list"
                                                    class="btn btn-secondary btn-block">
                                                    <i class="material-icons">arrow_back</i>
                                                    Quay lại danh sách
                                                </a>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Product Ingredients -->
                                    <div class="col-md-8">
                                        <div class="card">
                                            <div class="card-header">
                                                <h5 class="card-title mb-0">
                                                    <i class="material-icons-outlined">science</i>
                                                    Nguyên liệu cần thiết
                                                </h5>
                                            </div>
                                            <div class="card-body">
                                                <c:if test="${empty productIngredients}">
                                                    <div class="alert alert-warning">
                                                        Không có nguyên liệu nào được định nghĩa cho sản phẩm này.
                                                    </div>
                                                </c:if>

                                                <c:if test="${not empty productIngredients}">
                                                    <div class="list-group">
                                                        <c:forEach items="${productIngredients}" var="ingredient">
                                                            <div class="ingredient-list-item">
                                                                <span
                                                                    class="ingredient-name">${ingredient.ingredientName}</span>
                                                                <span class="ingredient-quantity">
                                                                    <fmt:formatNumber
                                                                        value="${ingredient.requiredQuantity}"
                                                                        maxFractionDigits="2" />
                                                                    đơn vị
                                                                </span>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <jsp:include page="../common/js.jsp" />
                <jsp:include page="../common/message.jsp" />
                <script>
                    function confirmDelete(productId, productName) {
                        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm \"" + productName + "\" (ID: " + productId + ") không?")) {
                            window.location.href = "${pageContext.request.contextPath}/admin/product/delete/" + productId;
                        }
                    }
                </script>
            </body>

            </html>