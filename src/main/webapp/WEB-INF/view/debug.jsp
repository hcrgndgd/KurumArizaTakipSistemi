<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Debug Page - Admin Login Test</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f5f5f5;
        }
        .container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .success {
            color: #28a745;
            font-weight: bold;
        }
        .error {
            color: #dc3545;
            font-weight: bold;
        }
        .info {
            color: #007bff;
        }
        .warning {
            color: #ffc107;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔍 Sistem Debug Sayfası</h1>

        <h3>Veritabanı Bağlantısı:</h3>
        <p class="${message.contains('successful') ? 'success' : 'error'}">${message}</p>

        <c:if test="${not empty error}">
            <p class="error">Hata Detayı: ${error}</p>
        </c:if>

        <h3>Veritabanı İstatistikleri:</h3>
        <ul>
            <li><strong>Kullanıcı Sayısı:</strong> ${userCount}</li>
            <li><strong>Rol Sayısı:</strong> ${roleCount}</li>
            <li><strong>Timestamp:</strong> ${timestamp}</li>
        </ul>

        <h3>Admin Giriş Testi:</h3>
        <p class="${adminLogin.contains('SUCCESS') ? 'success' : 'error'}">
            Admin Login: ${adminLogin}
        </p>
        <c:if test="${not empty adminRole}">
            <p class="info">Admin Rolü: ${adminRole}</p>
        </c:if>

        <h3>Test Bilgileri:</h3>
        <ul>
            <li><strong>Server:</strong> <%= request.getServerName() %>:<%= request.getServerPort() %></li>
            <li><strong>Context Path:</strong> <%= request.getContextPath() %></li>
            <li><strong>Servlet Path:</strong> <%= request.getServletPath() %></li>
        </ul>

        <h3>Test Linkleri:</h3>
        <ul>
            <li><a href="<%= request.getContextPath() %>/">Ana Sayfa (Login)</a></li>
            <li><a href="<%= request.getContextPath() %>/register">Kayıt Sayfası</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard">Admin Dashboard</a></li>
        </ul>

        <h3>Admin Giriş Bilgileri:</h3>
        <div style="background: #f8f9fa; padding: 10px; border-radius: 4px;">
            <strong>Email:</strong> admin@gmail.com<br>
            <strong>Şifre:</strong> Admin123!
        </div>

        <h3>Message Test:</h3>
        <p><spring:message code="auth.login.title" text="Default: Giriş Yap"/></p>
        <p><spring:message code="admin.title" text="Default: Admin Panel"/></p>
    </div>
</body>
</html>