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
 * Bước 1: Nhập email để nhận OTP.
 */
@WebServlet(name = "ForgotPasswordEmailController", urlPatterns = {"/auth/forgot-password", "/auth/forgot-password/email"})
public class ForgotPasswordEmailController extends HttpServlet {

    private static final String EMAIL_JSP = "/view/auth/forgot-password-email.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(EMAIL_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        if (StringUtils.isNullOrEmpty(email)) {
            request.setAttribute("error", "Vui lòng nhập email");
            request.getRequestDispatcher(EMAIL_JSP).forward(request, response);
            return;
        }

        // Lưu email vào session để các bước tiếp theo sử dụng
        HttpSession session = request.getSession();
        session.setAttribute("forgotEmail", email);

        // TODO: Gửi email OTP thông qua EmailServices khi hàm đã sẵn sàng

        response.sendRedirect(request.getContextPath() + "/auth/forgot-password/otp");
    }
}
