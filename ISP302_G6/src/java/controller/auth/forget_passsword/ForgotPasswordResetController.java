package controller.auth.forget_passsword;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import service.StringUtils;

/**
 * Bước 3: Đặt lại mật khẩu mới.
 */
@WebServlet(name = "ForgotPasswordResetController", urlPatterns = {"/auth/forgot-password/reset"})
public class ForgotPasswordResetController extends HttpServlet {

    private static final String RESET_JSP = "/view/auth/forgot-password-reset.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Boolean verified = session != null ? (Boolean) session.getAttribute("forgotOtpVerified") : Boolean.FALSE;
        if (session == null || verified == null || !verified) {
            response.sendRedirect(request.getContextPath() + "/auth/forgot-password");
            return;
        }

        request.setAttribute("email", session.getAttribute("forgotEmail"));
        request.getRequestDispatcher(RESET_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("forgotOtpVerified") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/forgot-password");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!StringUtils.areAllNotNullAndNotEmpty(newPassword, confirmPassword)) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin");
            request.setAttribute("email", session.getAttribute("forgotEmail"));
            request.getRequestDispatcher(RESET_JSP).forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.setAttribute("email", session.getAttribute("forgotEmail"));
            request.getRequestDispatcher(RESET_JSP).forward(request, response);
            return;
        }

        // TODO: Gọi service cập nhật mật khẩu mới trong database

        session.removeAttribute("forgotOtpVerified");
        session.removeAttribute("forgotEmail");
        session.setAttribute("success", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");

        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
}
