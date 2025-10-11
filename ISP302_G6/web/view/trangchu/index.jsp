<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="../common/css.jsp"/>
    </head>
    <body>
<!--        <div class='loader'>
            <div class='spinner-grow text-primary' role='status'>
                <span class='sr-only'>Loading...</span>
            </div>
        </div>-->
        <div class="connect-container align-content-stretch d-flex flex-wrap">
            <jsp:include page="../common/left-sidebar.jsp"/>
            <div class="page-container">
                <jsp:include page="../common/header-bar.jsp"/>
                <div class="page-content">
                    <div class="page-info">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="#">Dashboard</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Blank Dashboard</li>
                            </ol>
                        </nav>
                        
                    </div>
                </div>
                
            </div>
        </div>
        
        <jsp:include page="../common/js.jsp"/>
        <jsp:include page="../common/message.jsp"/>
    </body>
</html>
