package controller.auth;

import service.LoginServices;
import model.User;
import service.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý đăng nhập
 */
@WebServlet(name = "LoginController", urlPatterns = { "/auth/login" })
public class LoginController extends HttpServlet {

    private final String signInJSP = "/view/auth/sign-in.jsp";
    private LoginServices loginServices;
    private service.AuthServices authServices;

    @Override
    public void init() throws ServletException {
        super.init();
        this.loginServices = new LoginServices();
        this.authServices = new service.AuthServices();
    }

    /**
     * GET method - Hiển thị form login
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đã đăng nhập chưa
        if (isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.setAttribute("pageTitle", "Đăng nhập");
        request.getRequestDispatcher(signInJSP).forward(request, response);
    }

    /**
     * POST method - Xử lý submit form login
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        // ============= HARDCODE LOGIN FOR BARISTA =============
        // Tự động login với role Barista
        // HttpSession session = request.getSession();
        // session.setAttribute("userId", "3"); // Barista user ID
        // session.setAttribute("username", "barista1");
        // session.setAttribute("role", "Barista");
        // session.setAttribute("token", "hardcoded-barista-token");
        // session.setAttribute("isLoggedIn", true);

        // // Load permissions for Barista
        // try {
        // java.util.List<model.Permission> perms =
        // authServices.getPermissionsForUser("3");
        // session.setAttribute("permissions", perms);
        // System.out.println("✅ HARDCODE: Logged in as Barista with " + perms.size() +
        // " permissions");
        // } catch (Exception e) {
        // System.err.println("Failed to load permissions: " + e.getMessage());
        // session.setAttribute("permissions", new java.util.ArrayList<>());
        // }

        // request.getSession().setAttribute("ms", "🎉 Chào mừng Barista! Đăng nhập
        // thành công!");

        // // Redirect dựa trên role từ session
        // String userRole = (String) session.getAttribute("role");
        // if ("Barista".equalsIgnoreCase(userRole)) {
        // response.sendRedirect(request.getContextPath() + "/dashboard/barista");
        // } else {
        // response.sendRedirect(request.getContextPath() + "/dashboard");
        // }
        // return;

        // Validate input
        if (StringUtils.isNullOrEmpty(email) || StringUtils.isNullOrEmpty(password)) {
            request.setAttribute("error", "Email và mật khẩu không được để trống");
            request.setAttribute("email", email);
            doGet(request, response);
            return;
        }

        // Thực hiện đăng nhập
        List<String> result = loginServices.login(email, password);

        if ("success".equals(result.get(0))) {
            // Đăng nhập thành công
            // result format: [success, message, token, userId, role, shopId]
            String token = result.get(2);
            String userId = result.get(3);
            String role = result.get(4);
            Integer shopId = Integer.valueOf(result.get(5));

            // Lấy thông tin user từ database
            List<String> validateResult = loginServices.validateToken(token);
            if ("success".equals(validateResult.get(0))) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", validateResult.get(2));
                session.setAttribute("role", role);
                session.setAttribute("shopId", shopId);
                session.setAttribute("token", token);
                session.setAttribute("isLoggedIn", true);
                // Tải quyền và lưu vào session
                try {
                    java.util.List<model.Permission> perms = authServices.getPermissionsForUser(userId);
                    session.setAttribute("permissions", perms);
                    System.out.println("Check quyền" + perms.size());
                } catch (Exception e) {
                    System.err.println("Failed to load permissions for user " + userId + ": " + e.getMessage());
                    session.setAttribute("permissions", new java.util.ArrayList<>());
                }

                // Set cookie nếu user chọn "Remember me"
                if ("on".equals(remember)) {
                    setRememberMeCookie(response, token);
                }
                request.getSession().setAttribute("ms", "Đăng nhập thành công");
                // Redirect về trang trước đó hoặc dashboard
                String returnUrl = (String) session.getAttribute("returnUrl");
                if (StringUtils.isNotNullAndNotEmpty(returnUrl)) {
                    session.removeAttribute("returnUrl");
                    response.sendRedirect(returnUrl);
                } else {
                    // THAY THẾ LOGIC CHUYỂN HƯỚNG MẶC ĐỊNH BẰNG LOGIC PHÂN QUYỀN
                    String redirectUrl = request.getContextPath();

                    // ============================Phân Quyền==============================
                    switch (role.trim()) {
                        case "Admin":
                            System.out.println("Redirecting to Admin dashboard");
                            redirectUrl += "/dashboard/admin"; // fix
                            break;

                        case "Inventory Staff":
                            System.out.println("Redirecting to Inventory dashboard");
                            redirectUrl += "/dashboard/inventory";
                            break;

                        case "Barista":
                            System.out.println("Redirecting to Orders");
                            redirectUrl += "/dashboard/barista"; // fix
                            break;

                        case "HR Staff":
                            System.out.println("Redirecting to HR");
                            redirectUrl += "/hr/users"; // fix
                            break;

                        case "Manager":
                            System.out.println("Redirecting to Manager reports");
                            redirectUrl += "/manager/reports"; // fix
                            break;

                        default:
                            System.out.println("Redirecting to default dashboard - Role: [" + role + "]");
                            redirectUrl += "/dashboard";
                            break;
                    }

                    System.out.println("Final redirect URL: " + redirectUrl);
                    response.sendRedirect(redirectUrl);
                }
            } else {
                request.setAttribute("error", "Lỗi xác thực token");
                request.setAttribute("email", email);
                doGet(request, response);
            }

        } else {
            // Đăng nhập thất bại
            request.setAttribute("error", result.get(1)); // message
            request.setAttribute("email", email);
            doGet(request, response);
        }
    }

    /**
     * Kiểm tra user đã đăng nhập chưa
     */
    private boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("isLoggedIn") != null) {
            return (Boolean) session.getAttribute("isLoggedIn");
        }

        // Kiểm tra Remember Me cookie
        return checkRememberMeCookie(request);
    }

    /**
     * Set Remember Me cookie
     */
    private void setRememberMeCookie(HttpServletResponse response, String token) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("rememberMe", token);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * Kiểm tra Remember Me cookie
     */
    private boolean checkRememberMeCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("rememberMe".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    List<String> result = loginServices.validateToken(token);
                    System.out.println("Check token" + result.size());
                    if ("success".equals(result.get(0))) {
                        // Tự động đăng nhập
                        // result format: [success, userId, username, role]
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", result.get(1));
                        session.setAttribute("username", result.get(2));
                        session.setAttribute("role", result.get(3));
                        session.setAttribute("token", token);
                        session.setAttribute("isLoggedIn", true);
                        // Load permissions for user and store in session
                        try {
                            java.util.List<model.Permission> perms = authServices.getPermissionsForUser(result.get(1));
                            session.setAttribute("permissions", perms);
                            System.out.println("Check quyền" + perms.size());
                        } catch (Exception e) {
                            System.err.println(
                                    "Failed to load permissions for user " + result.get(1) + ": " + e.getMessage());
                            session.setAttribute("permissions", new java.util.ArrayList<>());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
