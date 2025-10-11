<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Toastr Notification</title>

    <!-- Toastr CSS & JS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>

    <style>
        #toast-container > .toast {
            font-size: 16px;
            padding: 15px 20px 15px 50px; /* ⬅ thêm padding-left để tránh đè lên icon */
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
            position: relative;
            min-width: 300px;
        }

        .toast-success {
            background-color: #51A351 !important;
        }

        .toast-error {
            background-color: #BD362F !important;
        }

        /* ✅ Nút đóng (×) bên phải, to hơn */
        #toast-container > .toast .toast-close-button {
            font-size: 24px;
            position: absolute;
            top: 8px;
            right: 12px;
            color: white;
            opacity: 1;
        }
    </style>
</head>

<body>
    <script type="text/javascript">
        $(document).ready(function () {
            toastr.options = {
                "closeButton": true,
                "progressBar": true,
                "positionClass": "toast-top-right",
                "timeOut": "5000",
                "extendedTimeOut": "1000"
            };

            <c:if test="${not empty ms}">
                toastr.success("${ms}");
            </c:if>
          
            <c:if test="${not empty error}">
                toastr.error("${error}");
            </c:if>
        });
    </script>

    <% 
        session.removeAttribute("ms");
        session.removeAttribute("error");
    %>
</body>
</html>
