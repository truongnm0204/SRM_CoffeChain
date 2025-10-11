<%--
    Document   : InventoryDashboard.jsp
    Created on : Oct 3, 2025
    Author     : Your Name
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Inventory Dashboard</title>

        <link href="https://fonts.googleapis.com/css?family=Lato:400,700,900&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Montserrat:400,500,700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Material+Icons|Material+Icons+Outlined|Material+Icons+Two+Tone|Material+Icons+Round|Material+Icons+Sharp" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/font-awesome/css/all.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/connect.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/custom.css" rel="stylesheet">
    </head>
    <body>
        <div class='loader'>
            <div class='spinner-grow text-primary' role='status'>
                <span class='sr-only'>Loading...</span>
            </div>
        </div>
        <div class="connect-container align-content-stretch d-flex flex-wrap">

            <%-- Import Sidebar và truyền tham số để biết trang hiện tại là 'dashboard' --%>
            <c:import url="sidebar.jsp">
                <c:param name="currentPage" value="dashboard"/>
            </c:import>
            
            <div class="page-container">
                <%-- Import Header --%>
                <c:import url="header.jsp"/>

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Inventory</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Dashboard</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">
                        <div class="row stats-row">
                            <div class="col-lg-4 col-md-12">
                                <div class="card card-transparent stats-card">
                                    <div class="card-body">
                                        <div class="stats-info">
                                            <h5 class="card-title">15<span class="stats-change stats-change-success">+1</span></h5>
                                            <p class="stats-text">Pending POs</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-12">
                                <div class="card card-transparent stats-card">
                                    <div class="card-body">
                                        <div class="stats-info">
                                            <h5 class="card-title">23<span class="stats-change stats-change-danger">-3</span></h5>
                                            <p class="stats-text">Items Low on Stock</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-12">
                                <div class="card card-transparent stats-card">
                                    <div class="card-body">
                                        <div class="stats-info">
                                            <h5 class="card-title">7</h5>
                                            <p class="stats-text">Approved POs Today</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-lg-12">
                                <div class="card card-transactions">
                                    <div class="card-body">
                                        <h5 class="card-title">Recent Activities</h5>
                                        <div class="table-responsive">
                                            <table class="table table-striped">
                                                <thead>
                                                    <tr>
                                                        <th scope="col">Activity</th>
                                                        <th scope="col">User</th>
                                                        <th scope="col">Details</th>
                                                        <th scope="col">Time</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <tr>
                                                        <td>PO Approved</td>
                                                        <td>manager01</td>
                                                        <td>PO #0776 for $18,560</td>
                                                        <td><span class="badge badge-success">10 mins ago</span></td>
                                                    </tr>
                                                    <tr>
                                                        <td>Stock Released</td>
                                                        <td>staff01</td>
                                                        <td>For PO #0759</td>
                                                        <td><span class="badge badge-info">35 mins ago</span></td>
                                                    </tr>
                                                </tbody>
                                            </table> 
                                        </div>   
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="page-footer">
                    <div class="row">
                        <div class="col-md-12">
                            <span class="footer-text">2025 © Your Company</span>
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


