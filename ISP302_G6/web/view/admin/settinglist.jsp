<%-- 
    Document   : SettingList
    Created on : 4 thg 10, 2025, 00:09:05
    Author     : DELL
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>System Settings</title>

        <link href="https://fonts.googleapis.com/css?family=Lato:400,700,900&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Montserrat:400,500,700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css?family=Material+Icons|Material+Icons+Outlined|Material+Icons+Two+Tone|Material+Icons+Round|Material+Icons+Sharp" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/plugins/font-awesome/css/all.min.css" rel="stylesheet">

        <link href="<%= request.getContextPath() %>/assets/css/connect.min.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/dark_theme.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/assets/css/custom.css" rel="stylesheet">
    </head>
    <body>
        <div class="connect-container align-content-stretch d-flex flex-wrap">

            <%-- Import Sidebar --%>
            <c:import url="sidebar.jsp">
                <c:param name="currentPage" value="settings" />
            </c:import>

            <div class="page-container">
                <%-- Import Header --%>
                <c:import url="header.jsp" />

                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Admin</a></li>
                                <li class="breadcrumb-item active" aria-current="page">System Settings</li>
                            </ol>
                        </nav>
                    </div>
                    <div class="main-wrapper">

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h2 class="h3">System Settings</h2>
                            <a href="#" class="btn btn-primary" data-toggle="modal" data-target="#addNewSettingModal">
                                 Add New Setting
                            </a>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-8">
                                <div class="input-group">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text"><i class="material-icons">search</i></span>
                                    </div>
                                    <input type="text" class="form-control" placeholder="Search by Key or Value...">
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="input-group">
                                    <div class="input-group-prepend">
                                        <label class="input-group-text" for="filterType"><i class="material-icons">filter_list</i></label>
                                    </div>
                                    <select class="custom-select" id="filterType">
                                        <option selected>Filter by Type...</option>
                                        <%-- Giả sử bạn gửi list các type từ Servlet --%>
                                        <c:forEach var="type" items="${requestScope.settingTypes}">
                                            <option value="${type}">${type}</option>
                                        </c:forEach>
                                        <option value="ROLE">ROLE (Mẫu)</option>
                                        <option value="SYSTEM">SYSTEM (Mẫu)</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xl">
                                <div class="card">
                                    <div class="card-body">
                                        <div class="table-responsive">
                                            <table class="table table-hover">
                                                <thead>
                                                    <tr>
                                                        <th scope="col">ID</th>
                                                        <th scope="col">Type</th>
                                                        <th scope="col">Value</th>
                                                        <th scope="col">Status</th>
                                                        <th scope="col">Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <%-- Vòng lặp JSTL để hiển thị dữ liệu động --%>
                                                    <c:forEach var="setting" items="${requestScope.settingsList}">
                                                        <tr>
                                                            <td><strong>${setting.id}</strong></td>
                                                            <td>${setting.settingtype}</td>
                                                            <td>${setting.settingvalue}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${setting.status == 'Active'}">
                                                                        <span class="badge badge-success">Active</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge badge-danger">Inactive</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <a href="edit-setting?id=${setting.settingid}" class="btn btn-sm btn-outline-secondary">
                                                                    <i class="material-icons">edit</i>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>

                                                    <%-- Dữ liệu mẫu để xem trước giao diện --%>
                                                    <c:if test="${empty requestScope.settingsList}">
                                                        <tr>
                                                            <td><strong>1</strong></td>
                                                            <td>ROLE</td>
                                                            <td>User</td>
                                                            <td><span class="badge badge-success">Active</span></td>

                                                            <td>
                                                                <a href="#" class="btn btn-sm btn-outline-secondary"
                                                                   data-toggle="modal"
                                                                   data-target="#editSettingModal"
                                                                   data-id="${setting.settingid}"
                                                                   data-id="${setting.id}" <%-- Giả sử Model của bạn có trường key --%>
                                                                   data-type="${setting.settingtype}"
                                                                   data-value="${setting.settingvalue}"
                                                                   data-description="${setting.description}"
                                                                   data-status="${setting.status}">
                                                                    <i class="material-icons">edit</i>
                                                                </a>
                                                            </td>

                                                        </tr>
                                                        <tr>
                                                            <td><strong>2</strong></td>
                                                            <td>SYSTEM</td>
                                                            <td>30</td>
                                                            <td><span class="badge badge-success">Active</span></td>
                                                            <td><a href="" class="btn btn-sm btn-outline-secondary"><i class="material-icons">edit</i></a></td>
                                                        </tr>
                                                        <tr>
                                                            <td><strong>3</strong></td>
                                                            <td>SECURITY</td>
                                                            <td>enabled</td>
                                                            <td><span class="badge badge-danger">Inactive</span></td>
                                                            <td><a href="edit-setting?id=${setting.id}" class="btn btn-sm btn-outline-secondary"><i class="material-icons">edit</i></a></td>
                                                        </tr>
                                                    </c:if>
                                                </tbody>
                                            </table>    
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="addNewSettingModal" tabindex="-1" role="dialog" aria-labelledby="addNewSettingModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">

                        <div class="modal-header">
                            <h5 class="modal-title" id="addNewSettingModalLabel">Create New System Setting</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>

                        <div class="modal-body">
                            <form id="newSettingForm" action="add-setting" method="POST">

                                <div class="form-group">
                                    <label for="settingType">Setting Type</label>
                                    <select class="form-control" name="settingType" id="settingType" required>
                                        <option value="" disabled selected>-- Select a type --</option>
                                        <option value="ROLE">ROLE</option>
                                        <option value="SYSTEM">SYSTEM</option>
                                        <option value="SECURITY">SECURITY</option>
                                        <!-- Thêm các loại khác nếu cần -->
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="settingValue">Setting Value</label>
                                    <input type="text" class="form-control" name="settingValue" id="settingValue" placeholder="E.g., User, 30, enabled" required>
                                </div>

                                <div class="form-group">
                                    <label for="settingDescription">Description</label>
                                    <textarea class="form-control" name="settingDescription" id="settingDescription" rows="3" placeholder="Explain the purpose of this setting..."></textarea>
                                </div>

                                <div class="form-group">
                                    <label for="settingStatus">Status</label>
                                    <select class="form-control" name="settingStatus" id="settingStatus" required>
                                        <option value="Active" selected>Active</option>
                                        <option value="Inactive">Inactive</option>
                                    </select>
                                </div>

                            </form>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">Cancel</button>
                            <button type="submit" form="newSettingForm" class="btn btn-success">Save Setting</button>
                        </div>

                    </div>
                </div>
            </div>

            <!-- =============================================== -->
            <!-- MODAL: Edit System Setting (Updated)            -->
            <!-- =============================================== -->
            <div class="modal fade" id="editSettingModal" tabindex="-1" role="dialog" aria-labelledby="editSettingModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">

                        <div class="modal-header">
                            <div>
                                <h5 class="modal-title" id="editSettingModalLabel">Edit System Setting</h5>
                                <small class="text-muted" id="editingSettingIDSubtitle"></small>
                            </div>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>

                        <div class="modal-body">
                            <form id="editSettingForm" action="update-setting" method="POST">
                                <input type="hidden" id="editSettingId" name="settingId">

                                <div class="form-group">
                                    <label for="editSettingID">Setting ID</label>
                                    <input type="text" class="form-control" id="editSettingID" name="settingID" readonly>
                                </div>

                                <div class="form-group">
                                    <label for="editSettingType">Setting Type</label>
                                    <input type="text" class="form-control" id="editSettingType" name="settingType" readonly>
                                </div>

                                <div class="form-group">
                                    <label for="editSettingValue">Setting Value</label>
                                    <input type="text" class="form-control" id="editSettingValue" name="settingValue" required>
                                </div>

                                <div class="form-group">
                                    <label for="editSettingDescription">Description</label>
                                    <textarea class="form-control" id="editSettingDescription" name="settingDescription" rows="3"></textarea>
                                </div>

                                <div class="form-group">
                                    <label for="editSettingStatus">Status</label>
                                    <select class="form-control" id="editSettingStatus" name="settingStatus" required>
                                        <option value="Active">Active</option>
                                        <option value="Inactive">Inactive</option>
                                    </select>
                                </div>

                            </form>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">Cancel</button>
                            <button type="submit" form="editSettingForm" class="btn btn-primary">Apply Changes</button>
                        </div>

                    </div>
                </div>
            </div>
        </div>

        <script src="<%= request.getContextPath() %>/assets/plugins/jquery/jquery-3.4.1.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script>
            // Đoạn script này sẽ chạy mỗi khi modal "editSettingModal" được mở
            $('#editSettingModal').on('show.bs.modal', function (event) {
                // Lấy thông tin từ nút "edit" đã được nhấn
                var button = $(event.relatedTarget);

                // Trích xuất dữ liệu từ các thuộc tính data-* của nút
                var id = button.data('id');
                var type = button.data('type');
                var value = button.data('value');
                var description = button.data('description');
                var status = button.data('status');

                // Tìm các thành phần trong modal và điền dữ liệu vào
                var modal = $(this);
                modal.find('.modal-header .text-muted').text('Editing: ' + ID);
                modal.find('#editSettingId').val(id);
                modal.find('#editSettingType').val(type);
                modal.find('#editSettingValue').val(value);
                modal.find('#editSettingDescription').val(description);
                modal.find('#editSettingStatus').val(status);
            });
        </script>                                           

        <script src="<%= request.getContextPath() %>/assets/plugins/jquery/jquery-3.4.1.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/popper.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/plugins/jquery-slimscroll/jquery.slimscroll.min.js"></script>
        <script src="<%= request.getContextPath() %>/assets/js/connect.min.js"></script>
    </body>
</html>
