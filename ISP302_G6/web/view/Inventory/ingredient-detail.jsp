<%-- 
    Document   : ingredient-detail.jsp
    Description: Trang xem chi tiết và cập nhật Nguyên vật liệu.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

            <c:import url="sidebar.jsp">
                <c:param name="currentPage" value="ingredient-list"/>
            </c:import>

            <div class="page-container">
                <c:import url="header.jsp"/>

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Inventory</a></li>
                                <li class="breadcrumb-item"><a href="<c:url value='/inventory/ingredients'/>">Ingredient List</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Details / Update</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">
                        <div class="row">
                            <div class="col-lg-8 col-md-12">
                                <div class="card">
                                    <div class="card-body">
                                        <h3 class="card-title">Update Ingredient: ${ingredient.ingredientName}</h3>

                                        <form action="<c:url value='/ingredients/detail'/>" method="POST">
                                            <input type="hidden" name="ingredientId" value="${ingredient.ingredientID}">

                                            <div class="form-group">
                                                <label for="ingredientName">Ingredient Name</label>
                                                <input type="text" class="form-control" id="ingredientName" name="ingredientName" value="${ingredient.ingredientName}" required>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group col-md-6">
                                                    <label for="category">Category</label>
                                                    <select id="category" name="categoryId" class="form-control" required>
                                                        <c:forEach var="cat" items="${categoryList}">
                                                            <option value="${cat.categoryID}" ${cat.categoryID == ingredient.categoryID ? 'selected' : ''}>${cat.categoryName}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-6">
                                                    <label for="unit">Unit</label>
                                                    <select id="unit" name="unitSettingId" class="form-control" required>
                                                        <c:forEach var="unit" items="${unitList}">
                                                            <option value="${unit.settingID}" ${unit.settingID == ingredient.unitSettingID ? 'selected' : ''}>${unit.settingValue}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="minStock">Minimum Stock Level</label>
                                                <input type="number" class="form-control" id="minStock" name="minStock" value="${ingredient.minStock}" step="0.1" min="0" required>
                                            </div>

                                            <div class="form-group">
                                                <label for="description">Description</label>
                                                <textarea class="form-control" id="description" name="description" rows="3">${ingredient.description}</textarea>
                                            </div>

                                            <div class="mt-4 text-right">
                                                <a href="<c:url value='/ingredients/list'/>" class="btn btn-secondary">Cancel</a>
                                                <button type="submit" class="btn btn-primary">Save Changes</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-12">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">Stock Info</h5>
                                        <p>Current Stock: <strong>${ingredient.currentStock} ${ingredient.unitName}</strong></p>
                                        <p>Status: <span class="badge badge-${ingredient.status == 'Active' ? 'success' : 'danger'}">${ingredient.status}</span></p>
                                        <small class="text-muted">Current stock is updated automatically via transactions and cannot be edited here.</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>