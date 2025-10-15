<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <meta name="description" content="Quản lý sản phẩm">
                <meta name="keywords" content="product,management,coffee">
                <meta name="author" content="Coffee Chain">

                <title>Danh sách sản phẩm - Coffee Chain</title>

                <jsp:include page="../common/css.jsp" />
                <style>
                    /* Table Styles */
                    #product-table {
                        width: 100%;
                        margin-bottom: 1rem;
                    }

                    #product-table thead th {
                        background-color: #f8f9fa;
                        border-bottom: 2px solid #dee2e6;
                        font-weight: 600;
                        padding: 12px;
                        vertical-align: middle;
                    }

                    #product-table tbody td {
                        padding: 12px;
                        vertical-align: middle;
                    }

                    #product-table tbody tr:hover {
                        background-color: #f8f9fa;
                    }

                    /* Fix icon alignment */
                    .table-actions {
                        display: flex;
                        gap: 5px;
                        justify-content: center;
                        align-items: center;
                    }

                    .table-actions .btn {
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        padding: 6px 12px;
                    }

                    .table-actions .material-icons {
                        font-size: 18px;
                        line-height: 1;
                    }

                    /* Search and Filter */
                    .filter-section {
                        margin-bottom: 20px;
                    }

                    .search-box {
                        max-width: 300px;
                    }

                    .entries-select {
                        max-width: 100px;
                    }

                    /* Pagination */
                    .pagination-info {
                        margin-top: 15px;
                        color: #6c757d;
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
                    <jsp:include page="../common/left-sidebar.jsp" />
                    <div class="page-container">
                        <jsp:include page="../common/header-bar.jsp" />
                        <div class="page-content">
                            <div class="page-info">
                                <nav aria-label="breadcrumb">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                        <li class="breadcrumb-item active" aria-current="page">Danh sách sản phẩm</li>
                                    </ol>
                                </nav>
                            </div>
                            <div class="main-wrapper">
                                <div class="row">
                                    <div class="col">
                                        <div class="card">
                                            <div class="card-body">
                                                <div class="d-flex justify-content-between align-items-center mb-3">
                                                    <div>
                                                        <h5 class="card-title">Danh sách sản phẩm</h5>
                                                        <p>Quản lý sản phẩm với tính năng tìm kiếm, sắp xếp và phân
                                                            trang.</p>
                                                    </div>
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/admin/product/add"
                                                            class="btn btn-primary">
                                                            <i class="material-icons">add</i> Thêm sản phẩm mới
                                                        </a>
                                                    </div>
                                                </div>

                                                <c:if test="${empty products}">
                                                    <div class="alert alert-info text-center">
                                                        <i class="material-icons-outlined"
                                                            style="font-size: 48px;">inbox</i>
                                                        <p class="mb-0 mt-2">Chưa có sản phẩm nào</p>
                                                    </div>
                                                </c:if>

                                                <c:if test="${not empty products}">
                                                    <!-- Filter and Search Section -->
                                                    <div class="filter-section">
                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="form-inline">
                                                                    <label class="mr-2">Hiển thị</label>
                                                                    <select id="entriesPerPage"
                                                                        class="form-control entries-select">
                                                                        <option value="5">5</option>
                                                                        <option value="10" selected>10</option>
                                                                        <option value="25">25</option>
                                                                        <option value="50">50</option>
                                                                    </select>
                                                                    <label class="ml-2">mục</label>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <div class="form-group mb-0">
                                                                    <input type="text" id="searchInput"
                                                                        class="form-control search-box float-right"
                                                                        placeholder="Tìm kiếm...">
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="table-responsive">
                                                        <table id="product-table"
                                                            class="table table-hover table-bordered">
                                                            <thead>
                                                                <tr>
                                                                    <th>Mã sản phẩm</th>
                                                                    <th>Tên sản phẩm</th>
                                                                    <th>SKU</th>
                                                                    <th>Giá</th>
                                                                    <th>Mô tả</th>
                                                                    <th>Thao tác</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach items="${products}" var="product">
                                                                    <tr>
                                                                        <td><strong>#${product.productId}</strong></td>
                                                                        <td>${product.productName}</td>
                                                                        <td><code>${product.sku}</code></td>
                                                                        <td>
                                                                            <strong>
                                                                                <fmt:formatNumber
                                                                                    value="${product.price}"
                                                                                    type="currency"
                                                                                    currencySymbol="₫" />
                                                                            </strong>
                                                                        </td>
                                                                        <td>${product.description}</td>
                                                                        <td>
                                                                            <div class="table-actions">
                                                                                <a href="${pageContext.request.contextPath}/admin/product/detail/${product.productId}"
                                                                                    class="btn btn-sm btn-info"
                                                                                    title="Xem chi tiết">
                                                                                    <i
                                                                                        class="material-icons">visibility</i>
                                                                                </a>
                                                                                <a href="${pageContext.request.contextPath}/admin/product/edit/${product.productId}"
                                                                                    class="btn btn-sm btn-warning"
                                                                                    title="Chỉnh sửa">
                                                                                    <i class="material-icons">edit</i>
                                                                                </a>
                                                                                <button type="button"
                                                                                    class="btn btn-sm btn-danger"
                                                                                    onclick="confirmDelete(${product.productId}, '${product.productName}')"
                                                                                    title="Xóa">
                                                                                    <i class="material-icons">delete</i>
                                                                                </button>
                                                                            </div>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </div>

                                                    <!-- Pagination Section -->
                                                    <div class="row mt-3">
                                                        <div class="col-md-6">
                                                            <div class="pagination-info">
                                                                Hiển thị <span id="startEntry">1</span> đến <span
                                                                    id="endEntry">10</span> trong tổng số <span
                                                                    id="totalEntries">${products.size()}</span> mục
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <nav aria-label="Page navigation">
                                                                <ul class="pagination justify-content-end mb-0"
                                                                    id="pagination">
                                                                    <!-- Pagination will be generated by JavaScript -->
                                                                </ul>
                                                            </nav>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="page-footer">
                            <div class="row">
                                <div class="col-md-12">
                                    <span class="footer-text">2025 © Coffee Chain Management System</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Javascripts -->
                <script src="${pageContext.request.contextPath}/assets/plugins/jquery/jquery-3.4.1.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/plugins/bootstrap/popper.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
                <script
                    src="${pageContext.request.contextPath}/assets/plugins/jquery-slimscroll/jquery.slimscroll.min.js"></script>
                <script src="${pageContext.request.contextPath}/assets/js/connect.min.js"></script>

                <script>
                    $(document).ready(function () {
                        "use strict";

                        let currentPage = 1;
                        let entriesPerPage = 10;
                        let allRows = [];
                        let filteredRows = [];

                        // Get all table rows
                        function initTable() {
                            allRows = $('#product-table tbody tr').toArray();
                            filteredRows = allRows;
                            updateTable();
                        }

                        // Filter function
                        function filterTable(searchTerm) {
                            searchTerm = searchTerm.toLowerCase();

                            if (searchTerm === '') {
                                filteredRows = allRows;
                            } else {
                                filteredRows = allRows.filter(function (row) {
                                    let text = $(row).text().toLowerCase();
                                    return text.indexOf(searchTerm) > -1;
                                });
                            }

                            currentPage = 1;
                            updateTable();
                        }

                        // Update table display
                        function updateTable() {
                            let start = (currentPage - 1) * entriesPerPage;
                            let end = start + entriesPerPage;

                            // Hide all rows
                            $(allRows).hide();

                            // Show current page rows
                            let displayRows = filteredRows.slice(start, end);
                            $(displayRows).show();

                            // Update info
                            let totalEntries = filteredRows.length;
                            $('#totalEntries').text(totalEntries);

                            if (totalEntries === 0) {
                                $('#startEntry').text(0);
                                $('#endEntry').text(0);
                            } else {
                                $('#startEntry').text(start + 1);
                                $('#endEntry').text(Math.min(end, totalEntries));
                            }

                            // Update pagination
                            updatePagination();
                        }

                        // Update pagination buttons
                        function updatePagination() {
                            let totalPages = Math.ceil(filteredRows.length / entriesPerPage);
                            let paginationHtml = '';

                            // Previous button
                            paginationHtml += '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">';
                            paginationHtml += '<a class="page-link" href="#" data-page="prev">Trước</a>';
                            paginationHtml += '</li>';

                            // Page numbers
                            let startPage = Math.max(1, currentPage - 2);
                            let endPage = Math.min(totalPages, currentPage + 2);

                            if (startPage > 1) {
                                paginationHtml += '<li class="page-item"><a class="page-link" href="#" data-page="1">1</a></li>';
                                if (startPage > 2) {
                                    paginationHtml += '<li class="page-item disabled"><span class="page-link">...</span></li>';
                                }
                            }

                            for (let i = startPage; i <= endPage; i++) {
                                paginationHtml += '<li class="page-item ' + (i === currentPage ? 'active' : '') + '">';
                                paginationHtml += '<a class="page-link" href="#" data-page="' + i + '">' + i + '</a>';
                                paginationHtml += '</li>';
                            }

                            if (endPage < totalPages) {
                                if (endPage < totalPages - 1) {
                                    paginationHtml += '<li class="page-item disabled"><span class="page-link">...</span></li>';
                                }
                                paginationHtml += '<li class="page-item"><a class="page-link" href="#" data-page="' + totalPages + '">' + totalPages + '</a></li>';
                            }

                            // Next button
                            paginationHtml += '<li class="page-item ' + (currentPage === totalPages || totalPages === 0 ? 'disabled' : '') + '">';
                            paginationHtml += '<a class="page-link" href="#" data-page="next">Sau</a>';
                            paginationHtml += '</li>';

                            $('#pagination').html(paginationHtml);
                        }

                        // Event handlers
                        $('#searchInput').on('keyup', function () {
                            filterTable($(this).val());
                        });

                        $('#entriesPerPage').on('change', function () {
                            entriesPerPage = parseInt($(this).val());
                            currentPage = 1;
                            updateTable();
                        });

                        $(document).on('click', '#pagination a', function (e) {
                            e.preventDefault();

                            let page = $(this).data('page');
                            let totalPages = Math.ceil(filteredRows.length / entriesPerPage);

                            if (page === 'prev' && currentPage > 1) {
                                currentPage--;
                            } else if (page === 'next' && currentPage < totalPages) {
                                currentPage++;
                            } else if (typeof page === 'number') {
                                currentPage = page;
                            }

                            updateTable();
                        });

                        // Initialize
                        initTable();
                    });

                    function confirmDelete(productId, productName) {
                        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm \"" + productName + "\" (ID: " + productId + ") không?")) {
                            window.location.href = "${pageContext.request.contextPath}/admin/product/delete/" + productId;
                        }
                    }
                </script>
                <jsp:include page="../common/message.jsp" />
            </body>

            </html>