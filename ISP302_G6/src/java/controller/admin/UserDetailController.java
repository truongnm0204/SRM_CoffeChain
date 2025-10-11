/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

/**
 *
 * @author Minh's PC
 */
@WebServlet(urlPatterns = {"/users/update"})
public class UserDetailController extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("id");
        User user = userDAO.findByUserId(userId);
        if (user != null) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("/view/admin/userdetail.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/users/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = Integer.parseInt(request.getParameter("userID"));
        String fullName = request.getParameter("fullName");
        String role = request.getParameter("role");
        String phone = request.getParameter("phoneNumber");
        String status = request.getParameter("status");

        User user = new User();
        user.setUserID(userId);
        user.setFullName(fullName);
        user.setPhoneNumber(phone);
        user.setUserName(role);
        user.setStatus(status);

        boolean success = userDAO.updateUser(user);

        if (success) {
            request.setAttribute("message", "Cập nhật thành công!");
        }

        // Load lại dữ liệu mới
        User updatedUser = userDAO.findByUserId(userId);
        request.setAttribute("user", updatedUser);
        request.getRequestDispatcher("/view/admin/userdetail.jsp").forward(request, response);
    }

}
