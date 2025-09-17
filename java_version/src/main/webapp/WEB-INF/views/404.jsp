<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Sayfa Bulunamadı - Xfer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
</head>
<body class="dark d-flex align-items-center min-vh-100">
    <div class="container text-center">
        <div class="row justify-content-center">
            <div class="col-lg-6">
                <div class="gradient-bg rounded-circle d-inline-flex align-items-center justify-content-center mb-4" style="width: 120px; height: 120px;">
                    <i class="fas fa-exclamation-triangle fa-3x text-white"></i>
                </div>
                <h1 class="display-1 fw-bold text-primary mb-3">404</h1>
                <h2 class="h3 mb-4">Sayfa Bulunamadı</h2>
                <p class="lead text-muted mb-4">Aradığınız sayfa mevcut değil veya taşınmış olabilir.</p>
                <a href="<c:url value='/'/>" class="btn btn-primary btn-lg">
                    <i class="fas fa-home me-2"></i>Ana Sayfaya Dön
                </a>
            </div>
        </div>
    </div>
</body>
</html>
