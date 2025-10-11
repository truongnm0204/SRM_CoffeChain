<%-- 
    Document   : polist
    Created on : 4 thg 10, 2025, 22:36:56
    Author     : DELL
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- Thêm thư viện để format số và ngày --%>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Purchase Order List</title>

        <link href="https://fonts.googleapis.com/css?family=Lato:400,700,900&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Montserrat:400,500,700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Material+Icons|Material+Icons+Outlined|Material+Icons+Two+Tone|Material+Icons+Round|Material+Icons+Sharp" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/font-awesome/css/all.min.css" rel="stylesheet">

        <link href="<%= request.getContextPath() %>/assets/css/connect.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/custom.css" rel="stylesheet">
    </head>
    <body>
        <div class="connect-container align-content-stretch d-flex flex-wrap">

            <%-- Sidebar --%>
            <c:import url="sidebar.jsp">
                <c:param name="currentPage" value="po-list"/>
            </c:import>

            <div class="page-container">
                <%-- Header --%>
                <c:import url="header.jsp"/>

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Inventory</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Purchase Order List</li>
                            </ol>
                        </nav>
                    </div>

                    <div class="main-wrapper">

                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="h3">Purchase Orders</h2>
                            <a href="<c:url value='/purchase-orders/create'/>" class="btn btn-primary">
                                Add New PO
                            </a>
                        </div>

                        <div class="card">
                            <div class="card-body">
                                <div class="d-flex justify-content-between mb-4">

                                    <!--=====================================SEARCH=======================-->
                                    <div class="w-50">
                                        <form action="<c:url value='/purchase-orders/list'/>" method="GET">
                                            <div class="input-group">
                                                <input type="text" class="form-control" name="search" placeholder="Search by PO Code or Supplier..." value="${searchTerm}">
                                                <div class="input-group-append">
                                                    <button class="btn btn-outline-secondary" type="submit">Search</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>


                                    <div>
                                        <ul class="nav nav-pills">
                                            <li class="nav-item">
                                                <a class="nav-link ${empty status ? 'active' : ''}" href="<c:url value='/purchase-orders/list'/>">All</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${status == 'Pending' ? 'active' : ''}" href="<c:url value='/purchase-orders/list?status=Pending'/>">Pending</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${status == 'Approved' ? 'active' : ''}" href="<c:url value='/purchase-orders/list?status=Approved'/>">Approved</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${status == 'Completed' ? 'active' : ''}" href="<c:url value='/purchase-orders/list?status=Completed'/>">Completed</a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>

                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>PO Code</th>
                                                <th>Supplier</th>
                                                <th>Creation Date</th>
                                                <th>Total Amount</th>
                                                <th>Creator</th>
                                                <th>Status</th>
                                                <th class="text-center">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="po" items="${poList}">
                                                <tr>
                                                    <td><a href="<c:url value='/purchase-orders/details?id=${po.purchaseOrderID}'/>"><strong>PO-${po.purchaseOrderID}</strong></a></td>
                                                    <td>${po.supplierName}</td>
                                                    <td><fmt:formatDate value="${po.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                    <td><fmt:formatNumber value="${po.totalAmount}" type="currency" currencySymbol="$"/></td>
                                                    <td>${po.creatorName}</td>
                                                    <td>
                                                        <span class="badge badge-${po.status == 'Pending' ? 'warning' : po.status == 'Approved' ? 'success' : 'primary'}">${po.status}</span>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="dropdown">
                                                            <button class="btn btn-sm btn-light dropdown-toggle" type="button" data-toggle="dropdown" aria-expanded="false">
                                                                <i class="material-icons-outlined">more_vert</i>
                                                            </button>
                                                            <div class="dropdown-menu dropdown-menu-right">

                                                                <c:choose>
                                                                    <%-- Kịch bản 1: Nếu trạng thái là Pending --%>
                                                                    <c:when test="${po.status == 'Pending'}">
                                                                        <%-- Gộp chung Edit và View Detail vào một link duy nhất --%>
                                                                        <a class="dropdown-item" href="<c:url value='/purchase-orders/update?id=${po.purchaseOrderID}'/>">
                                                                            <i class="material-icons-outlined">edit</i> Edit / View Details
                                                                        </a>
                                                                        <div class="dropdown-divider"></div>
                                                                        <a class="dropdown-item text-danger" href="<c:url value='/purchase-orders/cancel?id=${po.purchaseOrderID}'/>">
                                                                            <i class="material-icons-outlined">cancel</i> Cancel
                                                                        </a>
                                                                    </c:when>

                                                                    <%-- Kịch bản 2: Cho tất cả các trạng thái khác --%>
                                                                    <c:otherwise>
                                                                        <a class="dropdown-item" href="<c:url value='/purchase-orders/update?id=${po.purchaseOrderID}'/>">
                                                                            <i class="material-icons-outlined">visibility</i> View Details
                                                                        </a>
                                                                    </c:otherwise>
                                                                </c:choose>

                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty poList}">
                                                <tr>
                                                    <td colspan="7" class="text-center">No purchase orders found.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="card-footer d-flex justify-content-between align-items-center">
                                <span>Showing ${poList.size()} of ${totalRecords} results</span>
                                <nav>
                                    <ul class="pagination mb-0">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="<c:url value='/purchase-orders/list?page=${currentPage - 1}&search=${searchTerm}&status=${status}'/>">Previous</a>
                                        </li>
                                        <c:forEach var="i" begin="1" end="${totalPages}">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="<c:url value='/purchase-orders/list?page=${i}&search=${searchTerm}&status=${status}'/>">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="<c:url value='/purchase-orders/list?page=${currentPage + 1}&search=${searchTerm}&status=${status}'/>">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        </div>

                    </div>



                </div>
            </div>
        </div>

        <script src="<%= request.getContextPath() %>/assets/plugins/jquery/jquery-3.4.1.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/popper.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/jquery-slimscroll/jquery.slimscroll.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/js/connect.min.js"></script>
    </body>
</html>


