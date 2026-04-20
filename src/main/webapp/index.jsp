<!--<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>-->
<html>
<head>
    <title>Kurum Arıza Takip</title>
</head>
<body>
<h2>Kurum Arıza Takip Sistemi</h2>

<ul>
    <li><a href="${pageContext.request.contextPath}/auth/register">Kayıt Ol</a></li>
    <li><a href="${pageContext.request.contextPath}/auth/login">Giriş Yap</a></li>
    <li><a href="${pageContext.request.contextPath}/tickets">Ticket Listesi</a></li>
    <li><a href="${pageContext.request.contextPath}/tickets/new">Ticket Aç</a></li>
</ul>

<form method="post" action="${pageContext.request.contextPath}/auth/logout">
    <button type="submit">Çıkış</button>
</form>
</body>
</html>
