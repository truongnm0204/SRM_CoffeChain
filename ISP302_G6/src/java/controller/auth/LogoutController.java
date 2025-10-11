package controller.auth;

import service.LoginServices;
import service.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý đăng xuất
 */
@WebServlet(name = "LogoutController", urlPatterns = {"/auth/logout"})
public class LogoutController extends HttpServlet {
    
    private LoginServices loginServices;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.loginServices = new LoginServices();
    }
    
    /**
     * GET method - Xử lý đăng xuất
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }
    
    /**
     * POST method - Xử lý đăng xuất
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }
    
    /**
     * Xử lý đăng xuất
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            String token = (String) session.getAttribute("token");
            
            
            // Xóa session
            session.invalidate();
        }
        
        // Xóa remember me cookie
        removeRememberMeCookie(response);
        
        // Redirect về trang login với thông báo
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
    
    /**
     * Remove Remember Me cookie
     */
    private void removeRememberMeCookie(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("rememberMe", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}