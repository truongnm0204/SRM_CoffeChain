<%-- 
    Document   : ingredient-list.jsp
    Description: Trang hiển thị danh sách Nguyên vật liệu.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Ingredient List</title>

        <link href="https://fonts.googleapis.com/css?family=Lato:400,700,900&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Montserrat:400,500,700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Material+Icons|Material+Icons+Outlined" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/connect.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/custom.css" rel="stylesheet">
    </head>
    <body>
        <div class="connect-container align-content-stretch d-flex flex-wrap">
            
            <%-- Sidebar --%>
            <c:import url="sidebar.jsp">
                <c:param name="currentPage" value="ingredient-list"/>
            </c:import>
            
            <div class="page-container">
                <%-- Header --%>
                <c:import url="header.jsp"/>

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Inventory</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Ingredient List</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">
                        
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="h3">Ingredients</h2>
                            <a href="<c:url value='/ingredients/add'/>" class="btn btn-primary">
                                Add New Ingredient
                            </a>
                        </div>

                        <div class="card">
                            <div class="card-body">
                                <form action="<c:url value='/inventory/ingredients'/>" method="GET">
                                    <div class="row mb-4">
                                        <div class="col-md-8">
                                            <div class="input-group">
                                                <input type="text" class="form-control" name="search" placeholder="Search by name..." value="${searchTerm}">
                                                <div class="input-group-append">
                                                    <button class="btn btn-outline-secondary" type="submit">Search</button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                             <div class="input-group">
                                                <select class="custom-select" name="category" onchange="this.form.submit()">
                                                    <option value="">All Categories</option>
                                                    <%-- Giả sử Servlet gửi qua một list các category --%>
                                                    <c:forEach var="cat" items="${categoryList}">
                                                        <option value="${cat.categoryID}" ${cat.categoryID == categoryId ? 'selected' : ''}>${cat.categoryName}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </form>

                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th scope="col">Name</th>
                                                <th scope="col">Category</th>
                                                <th scope="col">Unit</th>
                                                <th scope="col" class="text-right">Current Stock</th>
                                                <th scope="col" class="text-right">Min Stock</th>
                                                <th scope="col">Status</th>
                                                <th scope="col" class="text-center">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="ing" items="${ingredientList}">
                                                <tr>
                                                    <td><strong>${ing.ingredientName}</strong></td>
                                                    <td>${ing.categoryName}</td>
                                                    <td>${ing.unitName}</td>
                                                    <td class="text-right">${ing.currentStock}</td>
                                                    <td class="text-right">${ing.minStock}</td>
                                                    <td>
                                                        <span class="badge badge-${ing.status == 'Active' ? 'success' : 'danger'}">${ing.status}</span>
                                                    </td>
                                                    <td class="text-center">
                                                        <a href="<c:url value='/ingredients/detail?id=${ing.ingredientID}'/>" class="btn btn-sm btn-light">
                                                            <i class="material-icons-outlined">edit</i>
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty ingredientList}">
                                                <tr>
                                                    <td colspan="7" class="text-center">No ingredients found.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="card-footer d-flex justify-content-between align-items-center">
                                <span>Showing ${ingredientList.size()} of ${totalRecords} results</span>
                                <nav>
                                  <ul class="pagination mb-0">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="<c:url value='/inventory/ingredients?page=${currentPage - 1}&search=${searchTerm}&category=${categoryId}'/>">Previous</a>
                                    </li>
                                    <c:forEach var="i" begin="1" end="${totalPages}">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="<c:url value='/inventory/ingredients?page=${i}&search=${searchTerm}&category=${categoryId}'/>">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="<c:url value='/inventory/ingredients?page=${currentPage + 1}&search=${searchTerm}&category=${categoryId}'/>">Next</a>
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
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/js/connect.min.js"></script>
    </body>
</html>