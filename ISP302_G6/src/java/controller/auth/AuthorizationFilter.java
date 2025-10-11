///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package controller.auth;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import model.Permission; // Model Permission của bạn
//import java.io.IOException;
//import java.util.List;
//
///**
// * Filter này chịu trách nhiệm kiểm tra quyền truy cập của người dùng cho mỗi
// * request.
// */
//@WebFilter("/*") // Áp dụng filter này cho TẤT CẢ các request đến server
//public class AuthorizationFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//        HttpSession session = request.getSession(false);
//
//        String contextPath = request.getContextPath();
//        String path = request.getRequestURI().substring(contextPath.length());
//
//        // 1. Bỏ qua kiểm tra cho các trang/tài nguyên công khai

//        if (path.equals("/auth/login") || path.startsWith("/assets/") || path.equals("/logout") || path.equals("/view/common/access-denied.jsp")) {
//        if (path.equals("/auth/login") 
//                ||path.equals("/auth/google")
//                ||path.equals("/auth/forgot-password")
//                ||path.equals("/auth/forgot-password/otp")
//                ||path.equals("/auth/forgot-password/reset")
//                || path.startsWith("/assets/") 
//                || path.equals("/logout") 
//                || path.equals("/view/common/access-denied.jsp")) {
//            chain.doFilter(request, response); // Cho phép đi tiếp
//            return;
//        }
//
//        // 2. Kiểm tra xem người dùng đã đăng nhập chưa
//        if (session == null || session.getAttribute("isLoggedIn") == null || !(Boolean) session.getAttribute("isLoggedIn")) {
//            response.sendRedirect(contextPath + "/auth/login"); // Chưa đăng nhập, chuyển về trang login
//            return;
//        }
//
//        // 3. Lấy danh sách quyền của người dùng từ session
//        @SuppressWarnings("unchecked")
//        List<Permission> permissions = (List<Permission>) session.getAttribute("permissions");
//
//        // 4. Kiểm tra xem người dùng có quyền truy cập vào đường dẫn này không
//        boolean isAuthorized = false;
//
//        // Dashboard là trang chung sau khi đăng nhập, mặc định cho phép
//        if (path.equals("/") || path.equals("/dashboard")) {
//            isAuthorized = true;
//        } else if (permissions != null) {
//            // Vòng lặp để so sánh path hiện tại với danh sách permission_path của người dùng
//            for (Permission p : permissions) {
//                if (p.getPermissionPath() != null && path.equals(p.getPermissionPath())) {
//                    isAuthorized = true;
//                    break;
//                }
//            }
//        }
//
//        // 5. Xử lý kết quả kiểm tra
//        if (isAuthorized) {
//            // Nếu có quyền, cho phép request đi tiếp đến Servlet/JSP tương ứng
//            chain.doFilter(request, response);
//        } else {
//            // Nếu không có quyền, chuyển hướng đến trang báo lỗi 403 (Access Denied)
//            response.sendRedirect(contextPath + "/view/common/access-denied.jsp");
//        }
//    }
//
//    // Các phương thức khác có thể để trống
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    @Override
//    public void destroy() {
//    }
//}
