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
 * Bước 2: Nhập OTP được gửi qua email.
 */
@WebServlet(name = "ForgotPasswordOtpController", urlPatterns = {"/auth/forgot-password/otp"})
public class ForgotPasswordOtpController extends HttpServlet {

    private static final String OTP_JSP = "/view/auth/forgot-password-otp.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String email = request.getParameter("email");
        if (StringUtils.isNullOrEmpty(email) && session != null) {
            email = (String) session.getAttribute("forgotEmail");
        }

        if (StringUtils.isNullOrEmpty(email)) {
            response.sendRedirect(request.getContextPath() + "/auth/forgot-password");
            return;
        }

        request.setAttribute("email", email);
        request.getRequestDispatcher(OTP_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");

        if (StringUtils.isNullOrEmpty(email)) {
            response.sendRedirect(request.getContextPath() + "/auth/forgot-password");
            return;
        }

        if (StringUtils.isNullOrEmpty(otp)) {
            request.setAttribute("error", "Vui lòng nhập mã OTP");
            request.setAttribute("email", email);
            request.getRequestDispatcher(OTP_JSP).forward(request, response);
            return;
        }

        // TODO: Xác thực OTP bằng service khi đã cài đặt

        HttpSession session = request.getSession();
        session.setAttribute("forgotOtpVerified", Boolean.TRUE);
        session.setAttribute("forgotEmail", email);

        response.sendRedirect(request.getContextPath() + "/auth/forgot-password/reset");
    }
}
