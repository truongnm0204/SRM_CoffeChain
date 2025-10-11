<%-- 
    Document   : po-detail.jsp
    Description: Trang xem chi tiết và cập nhật Đơn nhập hàng (PO).
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>PO Details - PO-${po.purchaseOrderID}</title>
        <link href="https://fonts.googleapis.com/css?family=Material+Icons|Material+Icons+Outlined" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/connect.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="connect-container align-content-stretch d-flex flex-wrap">

            <c:import url="sidebar.jsp">
                <c:param name="currentPage" value="po-list"/>
            </c:import>

            <div class="page-container">
                <c:import url="header.jsp"/>

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Inventory</a></li>
                                <li class="breadcrumb-item"><a href="<c:url value='/purchase-orders/list'/>">Purchase Order List</a></li>
                                <li class="breadcrumb-item active" aria-current="page">${po.status == 'Pending' ? 'Update' : 'Details'}</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">

                        <form action="<c:url value='/purchase-orders/update'/>" method="POST" id="poForm" >
                            <input type="hidden" name="poId" value="${po.purchaseOrderID}">

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h2 class="h3">${po.status == 'Pending' ? 'Update' : 'Details'} for PO #${po.purchaseOrderID}</h2>
                                <a href="<c:url value='/purchase-orders/list'/>" class="btn btn-sm btn-secondary">Back to List</a>
                            </div>

                            <div class="card">
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-8">
                                            <div class="form-row">
                                                <div class="form-group col-md-6">
                                                    <label>Supplier</label>
                                                    <c:choose>
                                                        <c:when test="${po.status == 'Pending'}">
                                                            <select name="supplierId" class="form-control">
                                                                <c:forEach var="supplier" items="${supplierList}">
                                                                    <option value="${supplier.supplierID}" ${po.supplierID == supplier.supplierID ? 'selected' : ''}>${supplier.supplierName}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <p class="form-control-plaintext"><strong>${po.supplierName}</strong></p>
                                                                </c:otherwise>
                                                            </c:choose>
                                                </div>
                                                <div class="form-group col-md-6">
                                                    <label>Expected Delivery Date</label>
                                                    <c:choose>
                                                        <c:when test="${po.status == 'Pending'}">
                                                            <input type="date" name="expectedDate" class="form-control" value="<fmt:formatDate value='${po.expectedDeliveryDate}' pattern='yyyy-MM-dd' />">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <p class="form-control-plaintext"><fmt:formatDate value="${po.expectedDeliveryDate}" pattern="dd/MM/yyyy" /></p> 
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            <div class="form-row">
                                                <div class="form-group col-md-6">
                                                    <label>Creation Date</label>
                                                    <p class="form-control-plaintext"><fmt:formatDate value="${po.orderDate}" pattern="dd/MM/yyyy HH:mm" /></p>
                                                </div>
                                                <div class="form-group col-md-6">
                                                    <label>Created By</label>
                                                    <p class="form-control-plaintext">${po.creatorName}</p>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label>Status</label>
                                                <p>
                                                    <span class="badge badge-${po.status == 'Pending' ? 'warning' : po.status == 'Approved' ? 'success' : 'secondary'}">${po.status}</span>
                                                </p>
                                            </div>
                                            <div class="form-group">
                                                <label>Total Amount</label>
                                                <h4 class="text-primary">
                                                    <fmt:formatNumber value="${po.totalAmount}" type="currency" currencySymbol="$"/>
                                                </h4>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card mt-4">
                                <div class="card-body">
                                    <h5 class="card-title">Order Items</h5>
                                    <div class="table-responsive">
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th>Material / Product</th>
                                                    <th class="text-right" style="width: 15%;">Quantity</th>
                                                    <th class="text-right" style="width: 15%;">Price per Unit</th>
                                                    <th class="text-right" style="width: 15%;">Subtotal</th>
                                                        <c:if test="${po.status == 'Pending'}">
                                                        <th style="width: 5%;"></th>
                                                        </c:if>
                                                </tr>
                                            </thead>
                                            <tbody id="itemsBody">
                                                <c:forEach var="item" items="${po.items}" >
                                                    <tr>
                                                        <td>${item.ingredientName}</td>
                                                        <td class="text-right">
                                                            <c:if test="${po.status == 'Pending'}">
                                                                <input type="number" name="quantity_${item.ingredientID}" class="form-control form-control-sm text-right" value="${item.quantity}">
                                                            </c:if>
                                                            <c:if test="${po.status != 'Pending'}">
                                                                <fmt:formatNumber value="${item.quantity}" minFractionDigits="2"/>
                                                            </c:if>
                                                        </td>
                                                        <td class="text-right">
                                                            <c:if test="${po.status == 'Pending'}">
                                                                <input type="number" step="0.01" name="price_${item.ingredientID}" class="form-control form-control-sm text-right" value="${item.pricePerUnit}">
                                                            </c:if>
                                                            <c:if test="${po.status != 'Pending'}">
                                                                <fmt:formatNumber value="${item.pricePerUnit}" type="currency" currencySymbol="$"/>
                                                            </c:if>
                                                        </td>
                                                        <td class="text-right">
                                                            <fmt:formatNumber value="${item.quantity * item.pricePerUnit}" type="currency" currencySymbol="$"/>
                                                        </td>
                                                        <c:if test="${po.status == 'Pending'}">
                                                            <td>
                                                                <button type="button" class="btn btn-sm btn-danger remove-item-btn" >&times;</button>
                                                            </td>
                                                        </c:if>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>


                                    </div>
                                    <!-- ADD ITEM BUTTON -->
                                    <c:if test="${po.status == 'Pending'}">
                                        <button type="button" class="btn btn-light" id="addItemBtn">
                                            <i class="material-icons-outlined">add</i> Add Item
                                        </button>
                                    </c:if>
                                </div>
                            </div>

                            <c:if test="${po.status == 'Pending'}">
                                <div class="mt-4 text-right">
                                    <a href="<c:url value='/purchase-orders/list'/>" class="btn btn-secondary">Cancel</a>
                                    <button type="submit" class="btn btn-primary">Save Changes</button>
                                </div>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- SCRIPTS -->
        <script src="<%= request.getContextPath() %>/assets/plugins/jquery/jquery-3.4.1.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>

        <c:if test="${po.status == 'Pending'}">
            <script>
                $(document).ready(function () {
                    $(document).on('change', '.ingredient-select', function () {
                        const selectedId = $(this).val(); // Lấy ID của ingredient vừa được chọn
                        const row = $(this).closest('tr'); // Tìm đến dòng <tr> chứa dropdown này

                        if (selectedId) {
                            // Cập nhật thuộc tính 'name' cho input quantity và price
                            row.find('.quantity-input').attr('name', 'quantity_' + selectedId);
                            row.find('.price-input').attr('name', 'price_' + selectedId);

                            // Quan trọng: Cập nhật luôn cả 'name' của chính dropdown này để servlet có thể đọc được
                            $(this).attr('name', 'ingredientId_' + selectedId);
                        }
                    });
                    // Template cho row mới
                    var newRowTemplate = `
                        <tr class="item-row">
                            <td>
                                <select name="ingredientId" class="form-control form-control-sm ingredient-select" required>
                                    <option value="">-- Select Ingredient --</option>
                <c:forEach var="ing" items="${ingredientList}">
                                        <option value="${ing.ingredientID}">${ing.ingredientName}</option>
                </c:forEach>
                                </select>
                            </td>
                            <td class="text-right">
                                <input type="number" name="quantity" class="form-control form-control-sm text-right quantity-input" 
                                       value="1" step="0.01" min="0.01" required>
                            </td>
                            <td class="text-right">
                                <input type="number" step="0.01" name="pricePerUnit" 
                                       class="form-control form-control-sm text-right price-input" 
                                       value="0" min="0.01" required>
                            </td>
                            <td class="text-right">
                                <strong class="subtotal-display">$0.00</strong>
                            </td>
                            <td class="text-center">
                                <button type="button" class="btn btn-sm btn-danger remove-item-btn">
                                    &times;
                                </button>
                            </td>
                        </tr>
                    `;
                    // Add Item
                    $('#addItemBtn').on('click', function () {
                        $('#itemsBody').append(newRowTemplate);
                        updateTotal();
                    });
                    // Remove Item
                    $(document).on('click', '.remove-item-btn', function () {
                        if ($('.item-row').length >= 1) {
                            $(this).closest('tr').remove();
                            updateTotal();
                        } else {
                            alert('Cannot remove the last item. A Purchase Order must have at least one item.');
                        }
                    });
                    // Calculate subtotal when quantity or price changes
                    $(document).on('input', '.quantity-input, .price-input', function () {
                        var row = $(this).closest('tr');
                        var qty = parseFloat(row.find('.quantity-input').val()) || 0;
                        var price = parseFloat(row.find('.price-input').val()) || 0;
                        var subtotal = qty * price;
                        row.find('.subtotal-display').text('$' + subtotal.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ","));
                        updateTotal();
                    });
                    // Update total amount
                    function updateTotal() {
                        var total = 0;
                        $('.item-row').each(function () {
                            var qty = parseFloat($(this).find('.quantity-input').val()) || 0;
                            var price = parseFloat($(this).find('.price-input').val()) || 0;
                            total += qty * price;
                        });
                        $('#totalValue').text(total.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ","));
                    }

                    // Validate form before submit
                    $('#poForm').on('submit', function (e) {
                        var itemCount = $('.item-row').length;
                        if (itemCount === 0) {
                            e.preventDefault();
                            alert('Please add at least one item to the Purchase Order.');
                            return false;
                        }

                        // Check for duplicate ingredients
                        var ingredients = [];
                        var hasDuplicate = false;
                        $('.ingredient-select').each(function () {
                            var val = $(this).val();
                            if (val && ingredients.includes(val)) {
                                hasDuplicate = true;
                                return false;
                            }
                            if (val)
                                ingredients.push(val);
                        });
                        if (hasDuplicate) {
                            e.preventDefault();
                            alert('Cannot add the same ingredient multiple times. Please remove duplicates.');
                            return false;
                        }

                        return true;
                    });
                });
            </script>
        </c:if>
    </body>
</html>