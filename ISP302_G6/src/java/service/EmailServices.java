//package service;
//
//import jakarta.mail.Message;
//import jakarta.mail.MessagingException;
//import jakarta.mail.Session;
//import jakarta.mail.Transport;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeBodyPart;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.mail.internet.MimeMultipart;
//import jakarta.mail.internet.MimeUtility;
//import java.net.PasswordAuthentication;
//import java.util.Properties;
//
//public class EmailServices {
//    public static boolean sendMailAsync(String email, String nameUser, String userId, String token) {
//        Thread thread = new Thread(() -> {
//            try {
//                EmailServices.guiMailVerify(email, nameUser, userId, token);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        thread.start();
//
//        return true;
//    }
//
//    public static boolean guiMailVerify(String email, String nameUser, String userId, String token) throws UnsupportedEncodingException, AddressException, MessagingException {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.host", ConfigService.HOST_NAME);
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.port", Gmail.TSL_PORT);
//
//        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(Gmail.APP_EMAIL, Gmail.APP_PASSWORD);
//            }
//        });
//
//        // Bật debug để kiểm tra chi tiết
//        session.setDebug(true);
//
//        try {
//            MimeMessage message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(Gmail.APP_EMAIL));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
//
//            String subject = "Xác thực tài khoản của bạn";
//
//            // Tạo token cho link xác thực bằng cách ghép userId với UUID
//            String verifyLink = "http://127.0.0.1:8080/PenguinShop/verify?token=" + token; // Thay "yourdomain.com" bằng domain của bạn
//
//            // Nội dung email mới
//            String emailContent = "<html><head>"
//                    + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
//                    + "<style>"
//                    + "  body { font-family: Arial, sans-serif; }"
//                    + "  .email-container { width: 100%; padding: 20px; background-color: #f4f4f4; text-align: center; }"
//                    + "  .email-content { background-color: #fff; padding: 20px; border-radius: 10px; width: 100%; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }"
//                    + "  h2 { color: #333; }"
//                    + "  a.verify-link { display: inline-block; padding: 10px 20px; background-color: #3498db; color: #fff; text-decoration: none; border-radius: 5px; font-weight: bold; }"
//                    + "  a.verify-link:hover { background-color: #2980b9; }"
//                    + "</style>"
//                    + "</head><body>"
//                    + "<div class='email-container'>"
//                    + "<div class='email-content'>"
//                    + "<h2>Chào " + nameUser + "!</h2>"
//                    + "<p>Để xác thực tài khoản của bạn, vui lòng nhấp vào nút bên dưới:</p>"
//                    + "<p><a href='" + verifyLink + "' class='verify-link'>Xác thực tài khoản</a></p>"
//                    + "</div></div>"
//                    + "</body></html>";
//
//            // Đặt tiêu đề với UTF-8
//            message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
//
//            // Tạo MimeMultipart với subtype "alternative"
//            MimeMultipart multipart = new MimeMultipart("alternative");
//
//            // Tạo phần nội dung HTML
//            MimeBodyPart htmlPart = new MimeBodyPart();
//            htmlPart.setContent(emailContent, "text/html; charset=UTF-8");
//            multipart.addBodyPart(htmlPart);
//
//            // Gán multipart vào message
//            message.setContent(multipart);
//
//            // Gửi email
//            Transport.send(message);
//            System.out.println("Mail đã được gửi thành công tại: " + System.currentTimeMillis());
//
//            return true;
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
