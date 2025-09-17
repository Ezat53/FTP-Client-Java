<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="tr" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'Xfer'}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="<c:url value='/css/style.css'/>" rel="stylesheet">
</head>
<body class="dark d-flex flex-column min-vh-100">
    <nav class="navbar navbar-expand-lg navbar-dark glass">
        <div class="container">
            <a class="navbar-brand" href="<c:url value='/'/>">
                <i class="fas fa-cloud-upload-alt me-2"></i>Xfer
            </a>
            
            <c:if test="${pageContext.request.userPrincipal != null}">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/dashboard'/>">
                            <i class="fas fa-tachometer-alt me-1"></i>Dashboard
                        </a>
                    </li>
                    <c:if test="${pageContext.request.userPrincipal.principal.role == 'admin'}">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/admin'/>">
                            <i class="fas fa-cogs me-1"></i>Admin Panel
                        </a>
                    </li>
                    </c:if>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user me-1"></i><c:out value="${pageContext.request.userPrincipal.principal.username}"/>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="<c:url value='/logout'/>">
                                <i class="fas fa-sign-out-alt me-1"></i>Çıkış Yap
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            </c:if>
        </div>
    </nav>

    <main class="container mt-4 fade-in flex-grow-1">
        <c:if test="${success != null}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <c:out value="${success}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <c:out value="${error}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <jsp:include page="${content}"/>
    </main>

    <footer class="text-center py-4 mt-auto">
        <div class="container">
            <p class="text-muted mb-0">
                <i class="fas fa-cloud-upload-alt me-2"></i>
                &copy; 2024 Xfer. Tüm hakları saklıdır.
            </p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/js/main.js'/>"></script>
    <jsp:include page="${scripts}"/>
</body>
</html>
