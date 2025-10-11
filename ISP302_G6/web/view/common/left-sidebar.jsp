<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="page-sidebar">
    <div class="logo-box"><a href="#" class="logo-text">Connect</a><a href="#" id="sidebar-close"><i class="material-icons">close</i></a> <a href="#" id="sidebar-state"><i class="material-icons">adjust</i><i class="material-icons compact-sidebar-icon">panorama_fish_eye</i></a></div>
    <div class="page-sidebar-inner slimscroll">
        <ul class="accordion-menu">
            
            <c:forEach items="${permissions}" var="per">
                <li>
                    <a href="<%= request.getContextPath() %>${per.permissionPath}">${per.description}</a>
                </li>
            </c:forEach>
            <!--            <li class="sidebar-title">
                            Apps
                        </li>
                        <li class="active-page">
                            <a href="index.html" class="active"><i class="material-icons-outlined">dashboard</i>Dashboard</a>
                        </li>
                        <li>
                            <a href="mailbox.html"><i class="material-icons-outlined">inbox</i>Mailbox</a>
                        </li>
                        <li>
                            <a href="profile.html"><i class="material-icons-outlined">account_circle</i>Profile</a>
                        </li>
                        <li>
                            <a href="file-manager.html"><i class="material-icons">cloud_queue</i>File Manager</a>
                        </li>
                        <li>
                            <a href="calendar.html"><i class="material-icons-outlined">calendar_today</i>Calendar</a>
                        </li>
                        <li>
                            <a href="todo.html"><i class="material-icons">done</i>Todo</a>
                        </li>-->

            
           
        </ul>
    </div>
</div>
