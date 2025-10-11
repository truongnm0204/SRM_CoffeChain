package controller.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import service.ConfigService;
import dao.UserDAO;
import model.User;
import service.StringUtils;
import service.JWTUtils;
import service.AuthServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import model.GoogleAccount;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import service.LoginServices;

/**
 * Servlet để khởi tạo Google OAuth flow và xử lý callback.
 *
 * Note: This is a minimal skeleton. It redirects the user to Google's
 * authorization endpoint. The callback handler receives the `code` parameter
 * and currently only displays a simple message. Implement token exchange and
 * user creation/login as needed.
 */
@WebServlet(name = "GoogleAuthController", urlPatterns = {"/auth/google", "/auth/google/callback"})
public class GoogleAuthController extends HttpServlet {

    private ConfigService config = ConfigService.getInstance();
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/auth/google".equals(path)) {
            startOAuth(request, response);
        } else if ("/auth/google/callback".equals(path)) {
            handleCallback(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void startOAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clientId = config.getGoogleClientId();
        String redirectUri = config.getGoogleRedirectUri();
        String scope = "openid%20email%20profile";
        String state = "st"; // TODO: generate and validate state per session

        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + scope +
                "&access_type=offline" +
                "&prompt=consent" +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

        response.sendRedirect(authUrl);
    }

    private void handleCallback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        LoginServices l = new LoginServices();
        if (code == null || code.isEmpty()) {
            response.getWriter().write("Google OAuth failed or was cancelled.");
            return;
        }

        try {
            String accessToken = getToken(code);
            GoogleAccount account = getUserInfo(accessToken);

            if (account == null || StringUtils.isNullOrEmpty(account.getEmail())) {
                request.setAttribute("error", "Không lấy được email từ Google");
                request.getRequestDispatcher("/view/auth/sign-in.jsp").forward(request, response);
                return;
            }

            UserDAO userDAO = new UserDAO();
            AuthServices authServices = new AuthServices();

            // If email exists in system, use that user. Otherwise create a new user.
            User user = userDAO.findByEmail(account.getEmail());
            if (user == null) {
                // If there's no local user with that email, deny login and show error
                request.getSession().setAttribute("error", "Tài khoản Google này chưa được cấp quyền. Vui lòng liên hệ quản trị viên.");
                response.sendRedirect("auth/login");
                return;
            }

            // Create JWT token and set session attributes (mirror LoginController)
            String userId = user.getUserId() != null ? user.getUserId() : (user.getUserID() != null ? user.getUserID().toString() : null);
            if (userId == null) userId = user.getUserID() != null ? user.getUserID().toString() : "";

            String role = user.getRoleSettingName() != null ? user.getRoleSettingName() : "user";
            String token = JWTUtils.createToken(userId, user.getUserName(), role);

            HttpSession session = request.getSession();
            session.setAttribute("userId", userId);
            session.setAttribute("username", user.getUserName());
            session.setAttribute("role", role);
            session.setAttribute("token", token);
            session.setAttribute("isLoggedIn", true);

            // Load permissions and store in session
            try {
                java.util.List<model.Permission> perms = authServices.getPermissionsForUser(userId);
                session.setAttribute("permissions", perms);
            } catch (Exception e) {
                session.setAttribute("permissions", new java.util.ArrayList<>());
            }

            // Redirect to returnUrl or dashboard
            String returnUrl = (String) session.getAttribute("returnUrl");
            if (StringUtils.isNotNullAndNotEmpty(returnUrl)) {
                session.removeAttribute("returnUrl");
                response.sendRedirect(returnUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }

        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("Error while exchanging token or fetching user info: " + e.getMessage());
        }
    }
    
    private String getToken(String code) throws IOException {
        HttpClient client = HttpClients.createDefault();
        String tokenEndpoint = config.getGoogleTokenEndpoint();
        HttpPost post = new HttpPost(tokenEndpoint);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", config.getGoogleClientId()));
        params.add(new BasicNameValuePair("client_secret", config.getGoogleClientSecret()));
        params.add(new BasicNameValuePair("redirect_uri", config.getGoogleRedirectUri()));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("grant_type", config.getGoogleGrantType()));

        post.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post)) {
            byte[] bytes = response.getEntity().getContent().readAllBytes();
            String json = new String(bytes, StandardCharsets.UTF_8); // âœ… Ã©p UTF-8
            JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
            return jobj.get("access_token").getAsString();
        }

    }

    private GoogleAccount getUserInfo(String accessToken) throws IOException {
        HttpClient client = HttpClients.createDefault();
        String infoEndpoint = config.getGoogleUserInfoEndpoint() + accessToken;
        HttpGet get = new HttpGet(infoEndpoint);

        try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(get)) {
            byte[] bytes = response.getEntity().getContent().readAllBytes();
            String json = new String(bytes, StandardCharsets.UTF_8); // âœ… Ã©p UTF-8
            return new Gson().fromJson(json, GoogleAccount.class);
        }

    }

}
