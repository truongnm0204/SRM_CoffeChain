<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="../common/css.jsp"/>
        <style>
            .order-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 30px;
                border-radius: 10px;
                margin-bottom: 30px;
            }
            .order-info-card {
                border-left: 4px solid #667eea;
            }
            .status-badge {
                padding: 8px 16px;
                border-radius: 25px;
                font-size: 14px;
                font-weight: 600;
                text-transform: uppercase;
                display: inline-block;
            }
            .status-pending {
                background-color: #fff3cd;
                color: #856404;
            }
            .status-completed {
                background-color: #d4edda;
                color: #155724;
            }
            .status-cancelled {
                background-color: #f8d7da;
                color: #721c24;
            }
            .product-image {
                width: 60px;
                height: 60px;
                object-fit: cover;
                border-radius: 8px;
            }
            .total-section {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 10px;
                border: 2px solid #667eea;
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
        </style>
    </head>
    <body>
        <div class="connect-container align-content-stretch d-flex flex-wrap">
            <jsp:include page="../common/left-sidebar.jsp"/>
            <div class="page-container">
                <jsp:include page="../common/header-bar.jsp"/>
                <div class="page-content">
                    <!-- Breadcrumb -->
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/order/list">Đơn hàng</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Chi tiết đơn #${order.orderId}</li>
                            </ol>
                        </nav>
                    </div>
                    
                    <!-- Main Content -->
                    <div class="main-wrapper">
                        <!-- Order Header -->
                        <div class="order-header">
                            <div class="row align-items-center">
                                <div class="col-md-6">
                                    <h2 class="mb-2">
                                        <i class="material-icons-outlined">receipt_long</i>
                                        Đơn hàng #${order.orderId}
                                    </h2>
                                    <p class="mb-0">
                                        <i class="material-icons-outlined" style="font-size: 18px; vertical-align: middle;">access_time</i>
                                        Ngày đặt: <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </p>
                                </div>
                                <div class="col-md-6 text-right">
                                    <span class="status-badge status-${order.status.toLowerCase()}">
                                        ${order.status}
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <!-- Order Information -->
                            <div class="col-md-4">
                                <div class="card order-info-card mb-4">
                                    <div class="card-header">
                                        <h5 class="card-title mb-0">
                                            <i class="material-icons-outlined">info</i>
                                            Thông tin đơn hàng
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="mb-3">
                                            <div class="info-label">Khách hàng</div>
                                            <div class="info-value">
                                                <i class="material-icons-outlined" style="vertical-align: middle; font-size: 20px;">person</i>
                                                ${order.userName != null ? order.userName : 'N/A'}
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <div class="info-label">Cửa hàng</div>
                                            <div class="info-value">
                                                <i class="material-icons-outlined" style="vertical-align: middle; font-size: 20px;">store</i>
                                                ${order.shopName != null ? order.shopName : 'N/A'}
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <div class="info-label">User ID</div>
                                            <div class="info-value">#${order.userId}</div>
                                        </div>
                                        <div>
                                            <div class="info-label">Shop ID</div>
                                            <div class="info-value">#${order.shopId}</div>
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
                                        <form method="post" action="${pageContext.request.contextPath}/order/update-status/${order.orderId}">
                                            <div class="form-group">
                                                <label for="status">Cập nhật trạng thái:</label>
                                                <select class="form-control" id="status" name="status">
                                                    <option value="Pending" ${order.status == 'Pending' ? 'selected' : ''}>Pending</option>
                                                    <option value="Processing" ${order.status == 'Processing' ? 'selected' : ''}>Processing</option>
                                                    <option value="Completed" ${order.status == 'Completed' ? 'selected' : ''}>Completed</option>
                                                    <option value="Cancelled" ${order.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                                </select>
                                            </div>
                                            <button type="submit" class="btn btn-primary btn-block">
                                                <i class="material-icons">update</i>
                                                Cập nhật
                                            </button>
                                        </form>
                                        <hr>
                                        <a href="${pageContext.request.contextPath}/order/list" 
                                           class="btn btn-secondary btn-block">
                                            <i class="material-icons">arrow_back</i>
                                            Quay lại
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <!-- Order Details -->
                            <div class="col-md-8">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="card-title mb-0">
                                            <i class="material-icons-outlined">list_alt</i>
                                            Chi tiết sản phẩm
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${empty order.orderDetails}">
                                            <div class="alert alert-warning">
                                                Không có chi tiết sản phẩm
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty order.orderDetails}">
                                            <div class="table-responsive">
                                                <table class="table table-bordered">
                                                    <thead class="thead-light">
                                                        <tr>
                                                            <th>STT</th>
                                                            <th>Sản phẩm</th>
                                                            <th>SKU</th>
                                                            <th>Đơn giá</th>
                                                            <th>Số lượng</th>
                                                            <th>Thành tiền</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach items="${order.orderDetails}" var="detail" varStatus="status">
                                                            <tr>
                                                                <td>${status.index + 1}</td>
                                                                <td>
                                                                    <strong>${detail.productName}</strong>
                                                                </td>
                                                                <td>
                                                                    <code>${detail.productSku}</code>
                                                                </td>
                                                                <td>
                                                                    <fmt:formatNumber value="${detail.productPrice}" 
                                                                                    type="currency" 
                                                                                    currencySymbol="₫"/>
                                                                </td>
                                                                <td>
                                                                    <span class="badge badge-info">${detail.quantity}</span>
                                                                </td>
                                                                <td>
                                                                    <strong>
                                                                        <fmt:formatNumber value="${detail.subtotal}" 
                                                                                        type="currency" 
                                                                                        currencySymbol="₫"/>
                                                                    </strong>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>

                                            <!-- Total Section -->
                                            <div class="total-section mt-4">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <h5>Tổng số lượng:</h5>
                                                    </div>
                                                    <div class="col-md-6 text-right">
                                                        <h5>
                                                            <c:set var="totalQty" value="0"/>
                                                            <c:forEach items="${order.orderDetails}" var="detail">
                                                                <c:set var="totalQty" value="${totalQty + detail.quantity}"/>
                                                            </c:forEach>
                                                            <span class="badge badge-primary" style="font-size: 18px;">
                                                                ${totalQty} sản phẩm
                                                            </span>
                                                        </h5>
                                                    </div>
                                                </div>
                                                <hr>
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <h4><strong>Tổng tiền:</strong></h4>
                                                    </div>
                                                    <div class="col-md-6 text-right">
                                                        <h3 style="color: #667eea;">
                                                            <strong>
                                                                <fmt:formatNumber value="${order.totalAmount}" 
                                                                                type="currency" 
                                                                                currencySymbol="₫"/>
                                                            </strong>
                                                        </h3>
                                                    </div>
                                                </div>
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
        
        <jsp:include page="../common/js.jsp"/>
        <jsp:include page="../common/message.jsp"/>
    </body>
</html>

