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
                                    <h4 class="mb-3">Nhập mã OTP</h4>
                                    <p class="text-muted">Mã xác thực đã được gửi tới email: <strong><%=request.getAttribute("email") != null ? request.getAttribute("email") : request.getParameter("email")%></strong></p>
                                    <form method="post" action="<%=request.getContextPath()%>/auth/forgot-password/verify-otp">
                                        <input type="hidden" name="email" value="<%=request.getAttribute("email") != null ? request.getAttribute("email") : request.getParameter("email")%>">
                                        <div class="form-group">
                                            <label for="otp">Mã OTP</label>
                                            <input name="otp" type="text" class="form-control" id="otp" placeholder="Nhập mã OTP" maxlength="6" required>
                                        </div>
                                        <button type="submit" class="btn btn-primary btn-block btn-submit">Xác nhận</button>
                                        <div class="auth-options mt-3 d-flex justify-content-between">
                                            <a href="<%=request.getContextPath()%>/auth/forgot-password/resend?email=<%=request.getAttribute("email") != null ? request.getAttribute("email") : request.getParameter("email")%>" class="forgot-link">Gửi lại mã</a>
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
