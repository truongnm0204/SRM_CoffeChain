<%-- 
    Document   : userlist
    Created on : Oct 8, 2025, 1:11:11 AM
    Author     : Minh's PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../common/css.jsp"/>
        <title>User List</title>
    </head>
    <body>
        <div class="connect-container align-content-stretch d-flex flex-wrap">

            <!-- Sidebar -->
            <c:import url="../admin/sidebar.jsp">
                <c:param name="currentPage" value="userlist"/>
            </c:import>

            <!-- Page Container -->
            <div class="page-container">

                <!-- Header -->
                <c:import url="../admin/header.jsp"/>

                <!-- Content -->
                <div class="page-content">
                    <div class="main-wrapper">
                        <div class="row">
                            <div class="col-lg-12">
                                <div class="card">
                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <h5 class="card-title mb-0">User List</h5>
                                        <a href="${pageContext.request.contextPath}/userdetail?mode=new" class="btn btn-primary">
                                            <i class="material-icons-outlined">add</i> Add New User
                                        </a>
                                    </div>

                                    <div class="card-body">
                                        <!-- Search -->
                                        <form class="mb-3" method="get" action="${pageContext.request.contextPath}/userlist">
                                            <div class="input-group">
                                                <input type="text" name="keyword" value="${param.keyword}" class="form-control" placeholder="Search by name or email">
                                                <button type="submit" class="btn btn-outline-primary">Search</button>
                                            </div>
                                        </form>

                                        <!-- Table -->
                                        <div class="table-responsive">
                                            <table class="table table-bordered table-hover">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>ID</th>
                                                        <th>Full Name</th>
                                                        <th>Email</th>
                                                        <th>Role</th>
                                                        <th>Status</th>
                                                        <th>Action</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="user" items="${users}">
                                                        <tr>
                                                            <td>${user.userID}</td>
                                                            <td>${user.fullName}</td>
                                                            <td>${user.email}</td>
                                                            <td>${user.userName}</td>
                                                            <td>
                                                                <span class="badge ${user.status == 'active' ? 'bg-success' : 'bg-secondary'}">
                                                                    ${user.status}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <a href="<c:url value='/users/update?id=${user.userID}'/>" class="btn btn-sm btn-outline-info">Edit</a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Footer -->
                <div class="page-footer">
                    <div class="row">
                        <div class="col-md-12 text-center">
                            <span class="footer-text">2025 © Connect Dashboard</span>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <jsp:include page="../common/js.jsp"/>
    </body>
</html>
