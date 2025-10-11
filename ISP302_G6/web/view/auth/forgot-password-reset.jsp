<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../common/css.jsp"/>
    </head>
    <body class="auth-page sign-in">
        <div class="connect-container align-content-stretch d-flex flex-wrap">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-5">
                        <div class="auth-form">
                            <div class="row">
                                <div class="col">
                                    <div class="logo-box"><a href="<%=request.getContextPath()%>/" class="logo-text">Connect</a></div>
                                    <h4 class="mb-3">Đặt lại mật khẩu</h4>
                                    <p class="text-muted">Nhập mật khẩu mới cho tài khoản của bạn.</p>
                                    <form method="post" action="<%=request.getContextPath()%>/auth/forgot-password/reset">
                                        <input type="hidden" name="email" value="<%=request.getAttribute("email") != null ? request.getAttribute("email") : request.getParameter("email")%>">
                                        <div class="form-group">
                                            <label for="newPassword">Mật khẩu mới</label>
                                            <input name="newPassword" type="password" class="form-control" id="newPassword" placeholder="Nhập mật khẩu mới" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                                            <input name="confirmPassword" type="password" class="form-control" id="confirmPassword" placeholder="Nhập lại mật khẩu" required>
                                        </div>
                                        <button type="submit" class="btn btn-primary btn-block btn-submit">Cập nhật mật khẩu</button>
                                        <div class="auth-options mt-3 d-flex justify-content-between">
                                            <a href="<%=request.getContextPath()%>/auth/login" class="forgot-link">Quay lại đăng nhập</a>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 d-none d-lg-block d-xl-block">
                        <div class="auth-image"></div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/js.jsp"/>
        <jsp:include page="../common/message.jsp"/>
    </body>
</html>
