<%-- 
    Document   : sidebar
    Created on : 4 thg 10, 2025, 01:16:36
    Author     : DELL
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%-- Lấy trang hiện tại được truyền từ file cha --%>
<c:set var="currentPage" value="${param.currentPage}" />
<c:set var="isPoPage" value="${currentPage == 'po-list' || currentPage == 'add-po'}" />
<c:set var="isStockPage" value="${currentPage == 'release-stock' || currentPage == 'stock-details'}" />

<div class="page-sidebar" style="background-color: #1a1a1a;">
    <div class="logo-box"><a href="<c:url value="/dashboard/inventory"/>" class="logo-text">My Inventory</a><a href="#" id="sidebar-close"><i class="material-icons">close</i></a> <a href="#" id="sidebar-state"><i class="material-icons">adjust</i><i class="material-icons compact-sidebar-icon">panorama_fish_eye</i></a></div>
    <div class="page-sidebar-inner slimscroll">


        <ul class="accordion-menu">
            <li class="sidebar-title">
                Management
            </li>
            <li class="${currentPage == 'dashboard' ? 'active-page' : ''}">
                <a href="<c:url value="/dashboard/inventory"/>">
                    <i class="material-icons-outlined">dashboard</i>Dashboard
                </a>
            </li>

            <li class="${currentPage == 'ingredient-list' ? 'active-page' : ''}">
                <a href="<c:url value="/ingredients/list"/>"><i class="material-icons-outlined">science</i>Ingredients</a>
            </li>

            <li class="${isPoPage ? 'active-page open' : ''}">
                <a href="<c:url value="/purchase-orders/list"/>"><i class="material-icons">shopping_cart</i>Purchase Order</a>

            </li>

            <li class="${isStockPage ? 'active-page open' : ''}">
                <a href="#"><i class="material-icons">inventory</i>Stock</a>
            </li>


            <li class="sidebar-title">
                Account
            </li>
            <li>
                <a href="profile.jsp"><i class="material-icons-outlined">account_circle</i>Profile</a>
            </li>
            <li>
                <a href="<c:url value="/auth/logout"/>"><i class="material-icons-outlined">logout</i>Log out</a>
            </li>
        </ul>


    </div>
</div>
