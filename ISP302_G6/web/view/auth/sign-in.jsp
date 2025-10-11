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
                                    <div class="logo-box"><a href="#" class="logo-text">Connect</a></div>
                                    <form method="post" action="login">
                                        <div class="form-group">
                                            <input name="email" type="email" class="form-control" id="email" aria-describedby="emailHelp" placeholder="Enter email">
                                        </div>
                                        <div class="form-group">
                                            <input name="password" type="password" class="form-control" id="password" placeholder="Password">
                                        </div>
                                        <button type="submit" class="btn btn-primary btn-block btn-submit">Sign In</button>

                                        <!-- Google OAuth login button -->
                                        <div class="text-center mt-3 mb-2">or</div>
                                        <div class="social-login">
                                            <a href="<%=request.getContextPath()%>/auth/google" class="btn btn-outline-danger btn-block d-flex align-items-center justify-content-center" role="button">
                                                <!-- Inline Google logo (SVG) -->
                                                <svg width="20" height="20" viewBox="0 0 533.5 544.3" xmlns="http://www.w3.org/2000/svg" style="margin-right:8px;">
                                                  <path fill="#4285F4" d="M533.5 278.4c0-17.4-1.6-34.1-4.7-50.3H272v95.1h146.9c-6.3 33.8-25.4 62.4-54.1 81.6v67.8h87.3c51-47 80.4-116.3 80.4-193.9z"/>
                                                  <path fill="#34A853" d="M272 544.3c73.5 0 135.3-24.3 180.4-66.1l-87.3-67.8c-24.3 16.3-55.3 25.9-93.1 25.9-71.6 0-132.3-48.3-154-113.2H30.5v70.9C75.6 487.7 168 544.3 272 544.3z"/>
                                                  <path fill="#FBBC05" d="M118 322.9c-10.8-32-10.8-66.6 0-98.6V153.4H30.5C11 197.8 0 238.8 0 272s11 74.2 30.5 118.6L118 322.9z"/>
                                                  <path fill="#EA4335" d="M272 107.7c39.9 0 75.7 13.7 103.9 40.6l77.9-77.9C407.8 24.7 345.9 0 272 0 168 0 75.6 56.6 30.5 153.4l87.5 70.9C139.7 156 200.4 107.7 272 107.7z"/>
                                                </svg>
                                                Sign in with Google
                                            </a>
                                        </div>

                                        <div class="auth-options">
                                            <div class="custom-control custom-checkbox form-group">
                                                <input name="remember" type="checkbox" class="custom-control-input" id="exampleCheck1">
                                                <label class="custom-control-label" for="exampleCheck1">Nhớ tôi?</label>
                                            </div>
                                            <a href="<%=request.getContextPath()%>/auth/forgot-password" class="forgot-link">Quên mật khẩu ?</a>
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
