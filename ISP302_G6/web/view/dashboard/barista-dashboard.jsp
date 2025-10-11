<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="../common/css.jsp"/>
        <style>
            .welcome-banner {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 40px;
                border-radius: 15px;
                margin-bottom: 30px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            }
            
            .stat-card {
                border-radius: 12px;
                padding: 25px;
                color: white;
                transition: transform 0.3s, box-shadow 0.3s;
                margin-bottom: 20px;
                position: relative;
                overflow: hidden;
            }
            
            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            }
            
            .stat-card::before {
                content: '';
                position: absolute;
                top: 0;
                right: 0;
                width: 100px;
                height: 100px;
                background: rgba(255,255,255,0.1);
                border-radius: 50%;
                transform: translate(30%, -30%);
            }
            
            .stat-card .icon {
                font-size: 48px;
                opacity: 0.9;
            }
            
            .stat-card h3 {
                font-size: 36px;
                font-weight: bold;
                margin: 10px 0;
            }
            
            .stat-card p {
                margin: 0;
                opacity: 0.9;
                font-size: 14px;
                text-transform: uppercase;
                letter-spacing: 1px;
            }
            
            .card-primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
            .card-warning { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
            .card-info { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
            .card-success { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
            
            .quick-action-card {
                text-align: center;
                padding: 30px 20px;
                border-radius: 12px;
                transition: all 0.3s;
                cursor: pointer;
                border: 2px solid #e9ecef;
                background: white;
                height: 100%;
            }
            
            .quick-action-card:hover {
                border-color: #667eea;
                box-shadow: 0 5px 20px rgba(102, 126, 234, 0.3);
                transform: translateY(-3px);
            }
            
            .quick-action-card .icon {
                font-size: 64px;
                color: #667eea;
                margin-bottom: 15px;
            }
            
            .quick-action-card h5 {
                color: #333;
                font-weight: 600;
                margin-bottom: 10px;
            }
            
            .quick-action-card p {
                color: #6c757d;
                font-size: 13px;
                margin: 0;
            }
            
            .badge-status {
                padding: 5px 12px;
                border-radius: 20px;
                font-size: 11px;
                font-weight: 600;
                text-transform: uppercase;
            }
            
            .page-title {
                margin-bottom: 20px;
            }
            
            .page-title h4 {
                font-weight: 600;
                color: #333;
            }
            
            .page-desc {
                color: #6c757d;
                margin-bottom: 0;
            }
        </style>
    </head>
    <body>
        <div class='loader'>
            <div class='spinner-grow text-primary' role='status'>
                <span class='sr-only'>Loading...</span>
            </div>
        </div>
        
        <div class="connect-container align-content-stretch d-flex flex-wrap">
            <jsp:include page="../common/left-sidebar.jsp"/>
            <div class="page-container">
                <jsp:include page="../common/header-bar.jsp"/>
                <div class="page-content">
                    <!-- Breadcrumb -->
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Home</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Barista Dashboard</li>
                            </ol>
                        </nav>
                    </div>
                    
                    <div class="main-wrapper">
                        <!-- Welcome Banner -->
                        <div class="welcome-banner">
                            <div class="row align-items-center">
                                <div class="col-md-8">
                                    <h1 style="font-size: 32px; font-weight: bold; margin-bottom: 10px;">
                                        ☕ Chào mừng, ${userName != null ? userName : 'Barista'}!
                                    </h1>
                                    <p style="font-size: 16px; margin: 0; opacity: 0.9;">
                                        <i class="material-icons-outlined" style="vertical-align: middle; font-size: 20px;">access_time</i>
                                        <fmt:formatDate value="<%= new java.util.Date() %>" pattern="EEEE, dd/MM/yyyy - HH:mm"/>
                                    </p>
                                </div>
                                <div class="col-md-4 text-right">
                                    <span class="badge badge-light" style="font-size: 14px; padding: 10px 20px;">
                                        <i class="material-icons-outlined" style="vertical-align: middle;">work</i>
                                        Role: ${userRole}
                                    </span>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Statistics Cards -->
                        <div class="row">
                            <div class="col-md-3">
                                <div class="stat-card card-primary">
                                    <i class="material-icons-outlined icon">shopping_cart</i>
                                    <h3>${totalOrders}</h3>
                                    <p>Tổng đơn hàng</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stat-card card-warning">
                                    <i class="material-icons-outlined icon">pending_actions</i>
                                    <h3>${pendingOrders}</h3>
                                    <p>Đơn chờ xử lý</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stat-card card-info">
                                    <i class="material-icons-outlined icon">autorenew</i>
                                    <h3>${processingOrders}</h3>
                                    <p>Đang xử lý</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stat-card card-success">
                                    <i class="material-icons-outlined icon">check_circle</i>
                                    <h3>${completedOrders}</h3>
                                    <p>Hoàn thành</p>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Quick Actions -->
                        <div class="row">
                            <div class="col-md-12">
                                <div class="page-title">
                                    <h4><i class="material-icons-outlined" style="vertical-align: middle;">flash_on</i> Thao tác nhanh</h4>
                                    <p class="page-desc">Truy cập nhanh các chức năng thường dùng của Barista</p>
                                </div>
                            </div>
                        </div>
                        <div class="row mb-4">
                            <div class="col-md-3">
                                <a href="${pageContext.request.contextPath}/order/create" style="text-decoration: none;">
                                    <div class="quick-action-card">
                                        <i class="material-icons-outlined icon">add_shopping_cart</i>
                                        <h5>Tạo đơn hàng</h5>
                                        <p>Tạo đơn hàng mới cho khách</p>
                                    </div>
                                </a>
                            </div>
                            <div class="col-md-3">
                                <a href="${pageContext.request.contextPath}/order/list" style="text-decoration: none;">
                                    <div class="quick-action-card">
                                        <i class="material-icons-outlined icon">list_alt</i>
                                        <h5>Xem đơn hàng</h5>
                                        <p>Quản lý tất cả đơn hàng</p>
                                    </div>
                                </a>
                            </div>
                            <div class="col-md-3">
                                <a href="${pageContext.request.contextPath}/products" style="text-decoration: none;">
                                    <div class="quick-action-card">
                                        <i class="material-icons-outlined icon">local_cafe</i>
                                        <h5>Menu sản phẩm</h5>
                                        <p>Xem danh sách đồ uống</p>
                                    </div>
                                </a>
                            </div>
                            <div class="col-md-3">
                                <a href="${pageContext.request.contextPath}/reports" style="text-decoration: none;">
                                    <div class="quick-action-card">
                                        <i class="material-icons-outlined icon">assessment</i>
                                        <h5>Báo cáo</h5>
                                        <p>Xem thống kê và báo cáo</p>
                                    </div>
                                </a>
                            </div>
                        </div>
                        
                        <!-- Charts Section -->
                        <div class="row">
                            <div class="col-md-12">
                                <div class="page-title">
                                    <h4><i class="material-icons-outlined" style="vertical-align: middle;">bar_chart</i> Biểu đồ thống kê</h4>
                                    <p class="page-desc">Phân tích trực quan về đơn hàng và doanh thu</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <!-- Orders Status Chart -->
                            <div class="col-lg-6">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">Thống kê đơn hàng theo trạng thái</h5>
                                        <div id="orderStatusChart"></div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Revenue Chart -->
                            <div class="col-lg-6">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">Doanh thu tổng quan</h5>
                                        <div id="revenueChart"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Recent Orders & Products -->
                        <div class="row">
                            <div class="col-md-12">
                                <div class="page-title">
                                    <h4><i class="material-icons-outlined" style="vertical-align: middle;">history</i> Hoạt động gần đây</h4>
                                    <p class="page-desc">Theo dõi các đơn hàng và sản phẩm</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <!-- Recent Orders -->
                            <div class="col-md-8">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="mb-0">
                                            <i class="material-icons-outlined" style="vertical-align: middle;">schedule</i>
                                            Đơn hàng gần đây
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${empty recentOrders}">
                                            <div class="text-center text-muted py-4">
                                                <i class="material-icons-outlined" style="font-size: 48px;">inbox</i>
                                                <p>Chưa có đơn hàng nào</p>
                                            </div>
                                        </c:if>
                                        
                                        <c:if test="${not empty recentOrders}">
                                            <div class="table-responsive">
                                                <table class="table table-hover">
                                                    <thead>
                                                        <tr>
                                                            <th>Mã đơn</th>
                                                            <th>Thời gian</th>
                                                            <th>Tổng tiền</th>
                                                            <th>Trạng thái</th>
                                                            <th></th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach items="${recentOrders}" var="order">
                                                            <tr>
                                                                <td><strong>#${order.orderId}</strong></td>
                                                                <td>
                                                                    <fmt:formatDate value="${order.orderDate}" 
                                                                                  pattern="dd/MM HH:mm"/>
                                                                </td>
                                                                <td>
                                                                    <strong>
                                                                        <fmt:formatNumber value="${order.totalAmount}" 
                                                                                        type="currency" 
                                                                                        currencySymbol="₫"/>
                                                                    </strong>
                                                                </td>
                                                                <td>
                                                                    <span class="badge badge-${order.status.toLowerCase() == 'pending' ? 'warning' : (order.status.toLowerCase() == 'completed' ? 'success' : 'info')}">
                                                                        ${order.status}
                                                                    </span>
                                                                </td>
                                                                <td>
                                                                    <a href="${pageContext.request.contextPath}/order/detail/${order.orderId}" 
                                                                       class="btn btn-sm btn-outline-primary">
                                                                        <i class="material-icons" style="font-size: 16px; vertical-align: middle;">visibility</i>
                                                                        Xem
                                                                    </a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div class="text-center mt-3">
                                                <a href="${pageContext.request.contextPath}/order/list" 
                                                   class="btn btn-primary">
                                                    Xem tất cả đơn hàng
                                                    <i class="material-icons" style="vertical-align: middle;">arrow_forward</i>
                                                </a>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Revenue & Products -->
                            <div class="col-md-4">
                                <!-- Revenue Card -->
                                <div class="card mb-4">
                                    <div class="card-body text-center" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 12px;">
                                        <i class="material-icons-outlined" style="font-size: 48px; margin-bottom: 10px;">payments</i>
                                        <h5 style="margin-bottom: 15px; opacity: 0.9;">Tổng doanh thu</h5>
                                        <h2 style="font-size: 32px; font-weight: bold;">
                                            <fmt:formatNumber value="${totalRevenue}" 
                                                            type="currency" 
                                                            currencySymbol="₫"/>
                                        </h2>
                                        <p style="margin: 0; opacity: 0.8; font-size: 13px;">
                                            Từ ${completedOrders} đơn hoàn thành
                                        </p>
                                    </div>
                                </div>
                                
                                <!-- Products Available -->
                                <div class="card">
                                    <div class="card-header">
                                        <h6 class="mb-0">
                                            <i class="material-icons-outlined" style="vertical-align: middle;">local_cafe</i>
                                            Sản phẩm có sẵn
                                        </h6>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${empty products}">
                                            <p class="text-muted text-center">Không có sản phẩm</p>
                                        </c:if>
                                        
                                        <c:if test="${not empty products}">
                                            <ul class="list-unstyled">
                                                <c:forEach items="${products}" var="product">
                                                    <li class="mb-2 pb-2" style="border-bottom: 1px solid #f0f0f0;">
                                                        <div class="d-flex justify-content-between align-items-center">
                                                            <div>
                                                                <strong>${product.productName}</strong>
                                                                <br>
                                                                <small class="text-muted">${product.sku}</small>
                                                            </div>
                                                            <div class="text-right">
                                                                <span class="badge badge-success">
                                                                    <fmt:formatNumber value="${product.price}" 
                                                                                    type="currency" 
                                                                                    currencySymbol="₫"/>
                                                                </span>
                                                            </div>
                                                        </div>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Page Footer -->
                <div class="page-footer">
                    <div class="row">
                        <div class="col-md-12">
                            <span class="footer-text">2025 © ISP302 Coffee Chain - Barista Dashboard</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <jsp:include page="../common/js.jsp"/>
        
        <!-- ApexCharts Script -->
        <script>
            // Orders Status Donut Chart
            var orderStatusOptions = {
                series: [${pendingOrders}, ${processingOrders}, ${completedOrders}],
                chart: {
                    type: 'donut',
                    height: 350
                },
                labels: ['Đơn chờ xử lý', 'Đang xử lý', 'Hoàn thành'],
                colors: ['#f093fb', '#4facfe', '#43e97b'],
                legend: {
                    position: 'bottom'
                },
                plotOptions: {
                    pie: {
                        donut: {
                            size: '65%',
                            labels: {
                                show: true,
                                total: {
                                    show: true,
                                    label: 'Tổng đơn',
                                    formatter: function () {
                                        return ${totalOrders}
                                    }
                                }
                            }
                        }
                    }
                },
                responsive: [{
                    breakpoint: 480,
                    options: {
                        chart: {
                            height: 300
                        },
                        legend: {
                            position: 'bottom'
                        }
                    }
                }]
            };
            var orderStatusChart = new ApexCharts(document.querySelector("#orderStatusChart"), orderStatusOptions);
            orderStatusChart.render();
            
            // Revenue Bar Chart
            var revenueOptions = {
                series: [{
                    name: 'Doanh thu',
                    data: [${totalRevenue}]
                }],
                chart: {
                    type: 'bar',
                    height: 350
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: '55%',
                        endingShape: 'rounded',
                        distributed: true
                    }
                },
                dataLabels: {
                    enabled: true,
                    formatter: function (val) {
                        return val.toLocaleString('vi-VN') + '₫';
                    }
                },
                xaxis: {
                    categories: ['Tổng doanh thu']
                },
                yaxis: {
                    labels: {
                        formatter: function (val) {
                            return val.toLocaleString('vi-VN') + '₫';
                        }
                    }
                },
                colors: ['#667eea'],
                legend: {
                    show: false
                }
            };
            var revenueChart = new ApexCharts(document.querySelector("#revenueChart"), revenueOptions);
            revenueChart.render();
        </script>
        
        <jsp:include page="../common/message.jsp"/>
    </body>
</html>
