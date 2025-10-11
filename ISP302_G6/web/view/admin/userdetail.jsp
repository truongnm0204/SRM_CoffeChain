<%-- 
    Document   : userdetail
    Created on : Oct 8, 2025, 1:14:06 AM
    Author     : Minh's PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../common/css.jsp"/>
        <title>User Detail</title>
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
                            <div class="col-lg-8 offset-lg-2">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="card-title mb-0">User Detail</h5>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/users/update" method="post">
                                            <input type="hidden" name="userID" value="${user.userID}"/>

                                            <div class="form-group mb-3">
                                                <label>Full Name</label>
                                                <input type="text" name="fullName" value="${user.fullName}" class="form-control" required/>
                                            </div>

                                            <div class="form-group mb-3">
                                                <label>Email</label>
                                                <input type="email" name="email" value="${user.email}" class="form-control" ${user.userID != null ? 'readonly' : ''}/>
                                            </div>

                                            <div class="form-group mb-3">
                                                <label>Role</label>
                                                <input type="text" name="role" value="${user.userName}" class="form-control" required/>
                                            </div>

                                            <div class="form-group mb-3">
                                                <label>Phone Number</label>
                                                <input type="text" name="phoneNumber" value="${user.phoneNumber}" class="form-control"/>
                                            </div>

                                            <div class="form-group mb-3">
                                                <label>Status</label>
                                                <select name="status" class="form-control">
                                                    <option value="active" ${user.status == 'active' ? 'selected' : ''}>Active</option>
                                                    <option value="inactive" ${user.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                                </select>
                                            </div>

                                            <div class="text-end mt-4">
                                                <button type="submit" class="btn btn-primary">Save</button>
                                                <a href="${pageContext.request.contextPath}/users/list" class="btn btn-secondary ms-2">Cancel</a>
                                                <c:if test="${not empty message}">
                                                    <span class="ms-3 text-success">${message}</span>
                                                </c:if>
                                            </div>
                                        </form>
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

