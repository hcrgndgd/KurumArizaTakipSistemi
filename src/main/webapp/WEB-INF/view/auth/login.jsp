<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Giriş Yap</title>
</head>
<body>

<h2>Giriş Yap</h2>

<c:if test="${not empty info}">
    <p style="color:green">${info}</p>
</c:if>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/auth/login">
    <div>
        <label>Email</label>
        <input name="email" type="email" value="${email}" required/>
    </div>

    <div>
        <label>Şifre</label>
        <input name="password" type="password" required/>
    </div>

    <button type="submit">Giriş</button>
</form>

<p>
    <a href="${pageContext.request.contextPath}/auth/register">Kayıt Ol</a>
</p>

</body>
</html>