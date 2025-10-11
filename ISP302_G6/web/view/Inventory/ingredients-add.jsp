<%-- 
    Document   : addnewpo.jsp
    Description: Trang tạo mới Đơn nhập hàng.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                                <li class="breadcrumb-item"><a href="<c:url value='/ingredients/list'/>">Ingredient List</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Add New</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">

                        <div class="row">
                            <div class="col-lg-8 col-md-12">
                                <div class="card">
                                    <div class="card-body">
                                        <h3 class="card-title">New Ingredient Details</h3>

                                        <form action="<c:url value='/ingredients/add'/>" method="POST">

                                            <div class="form-group">
                                                <label for="ingredientName">Ingredient Name</label>
                                                <input type="text" class="form-control" id="ingredientName" name="ingredient_name" placeholder="E.g., Robusta Coffee Beans" required>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group col-md-6">
                                                    <label for="category">Category</label>
                                                    <select id="category" name="category_id" class="form-control" required>
                                                        <option selected disabled value="">Choose...</option>
                                                        <c:forEach var="cat" items="${categoryList}">
                                                            <option value="${cat.categoryID}">${cat.categoryName}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-6">
                                                    <label for="unit">Unit</label>
                                                    <select id="unit" name="unit_setting_id" class="form-control" required>
                                                        <option selected disabled value="">Choose...</option>
                                                        <c:forEach var="unit" items="${unitList}">
                                                            <option value="${unit.settingID}">${unit.settingValue}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="minStock">Minimum Stock Level</label>
                                                <input type="number" class="form-control" id="minStock" name="min_stock" value="0" step="0.1" min="0" required>
                                                <small class="form-text text-muted">The system will alert you when stock falls below this level.</small>
                                            </div>

                                            <div class="form-group">
                                                <label for="description">Description</label>
                                                <textarea class="form-control" id="description" name="description" rows="3" placeholder="Enter a brief description..."></textarea>
                                            </div>

                                            <div class="mt-4 text-right">
                                                <a href="<c:url value='/ingredients/list'/>" class="btn btn-secondary">Cancel</a>
                                                <button type="submit" class="btn btn-primary">Save Ingredient</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
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