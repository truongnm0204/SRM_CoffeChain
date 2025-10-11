<%-- 
    Document   : header
    Created on : 4 thg 10, 2025, 01:10:09
    Author     : DELL
--%>

<%-- 
    Document   : header.jsp
    Description: Thanh điều hướng trên cùng, có thể tái sử dụng.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="page-header">
    <nav class="navbar navbar-expand">
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <ul class="navbar-nav">
            <li class="nav-item small-screens-sidebar-link">
                <a href="#" class="nav-link"><i class="material-icons-outlined">menu</i></a>
            </li>
            <li class="nav-item nav-profile dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <img src="<%= request.getContextPath() %>/assets/images/avatars/profile-image-1.png" alt="profile image">
                    <%-- Hiển thị tên người dùng từ session --%>
                    <span>${sessionScope.user.fullName != null ? sessionScope.user.fullName : "User Name"}</span><i class="material-icons dropdown-icon">keyboard_arrow_down</i>
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="#">Settings & Privacy</a>
                    <div class="dropdown-divider"></div>
                    <a class="dropdown-item" href="logout">Log out</a>
                </div>
            </li>
        </ul>
    </nav>
</div>
