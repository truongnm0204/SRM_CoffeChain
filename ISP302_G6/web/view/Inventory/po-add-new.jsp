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
                <c:param name="currentPage" value="add-po"/>
            </c:import>

            <div class="page-container">
                <%-- Header --%>
                <c:import url="header.jsp"/>

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Inventory</a></li>
                                <li class="breadcrumb-item"><a href="<c:url value='/purchase-orders/list'/>">Purchase Order List</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Add New</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">

                        <div class="card">
                            <div class="card-body">
                                <h3 class="card-title">Create New Purchase Order</h3>

                                <%-- Form sẽ gửi dữ liệu đến AddNewPOServlet --%>
                                <form action="<c:url value='/purchase-orders/create'/>" method="POST">
                                    <div class="form-row">
                                        <div class="form-group col-md-6">
                                            <label for="supplier">Supplier</label>
                                            <select id="supplier" name="supplierId" class="form-control" required>
                                                <option selected disabled value="">Choose a supplier...</option>
                                                <%-- Vòng lặp JSTL để hiển thị danh sách nhà cung cấp từ Servlet --%>
                                                <c:forEach var="supplier" items="${requestScope.supplierList}">
                                                    <option value="${supplier.supplierID}">${supplier.supplierName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group col-md-6">
                                            <label for="expectedDate">Expected Delivery Date</label>
                                            <input type="date" class="form-control" id="expectedDate" name="expectedDate" required>
                                        </div>
                                    </div>

                                    <hr>
                                    <h5>Order Items</h5>

                                    <table class="table table-sm">
                                        <thead>
                                            <tr>
                                                <th>Material/Product</th>
                                                <th style="width: 15%;">Quantity</th>
                                                <th style="width: 15%;">Price per Unit</th>
                                                <th style="width: 5%;"></th>
                                            </tr>
                                        </thead>
                                        <tbody id="po-items-table">
                                            <%-- Các dòng sản phẩm sẽ được thêm vào đây bằng JavaScript --%>
                                        </tbody>
                                    </table>

                                    <button type="button" class="btn btn-info btn-sm" onclick="addPoItemRow()">
                                        <i class="material-icons-outlined">add</i> Add Item
                                    </button>

                                    <hr>
                                    <div class="text-right">
                                        <a href="<c:url value='/purchase-orders/list'/>" class="btn btn-secondary">Cancel</a>
                                        <button type="submit" class="btn btn-primary">Save as Pending</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="<%= request.getContextPath() %>/assets/plugins/jquery/jquery-3.4.1.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>

        <script>
                                        // Lưu trữ danh sách nguyên vật liệu từ Servlet vào một biến JavaScript
                                        const ingredientList = [
            <c:forEach var="ing" items="${requestScope.ingredientList}" varStatus="loop">
                                        {id: ${ing.ingredientID}, name: '${ing.ingredientName}'}${!loop.last ? ',' : ''}
            </c:forEach>
                                        ];

                                        // Hàm JavaScript để thêm dòng mới vào bảng sản phẩm
                                        function addPoItemRow() {
                                            const tableBody = document.getElementById('po-items-table');
                                            const newRow = tableBody.insertRow();

                                            // Tạo dropdown options từ danh sách nguyên vật liệu
                                            let ingredientOptions = '';
                                            for (const ingredient of ingredientList) {
                                                ingredientOptions += `<option value="${ingredient.id}">${ingredient.name}</option>`;
                                            }

                                            newRow.innerHTML = `
                    <td>
                        <select name="ingredientId" class="form-control form-control-sm" required>
                            <option value="" disabled selected>Choose material...</option>
            ${ingredientOptions}
                        </select>
                    </td>
                    <td><input type="number" name="quantity" class="form-control form-control-sm" value="1" min="1" required></td>
                    <td><input type="number" name="pricePerUnit" class="form-control form-control-sm" value="0.00" step="0.01" min="0" required></td>
                    <td><button type="button" class="btn btn-danger btn-sm" onclick="this.closest('tr').remove()">&times;</button></td>
                `;
                                        }
        </script>
    </body>
</html>