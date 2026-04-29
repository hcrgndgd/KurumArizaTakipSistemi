<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error</title>

    <style>
        :root {
            --bg: #0f172a;
            --card: rgba(255, 255, 255, 0.06);
            --border: rgba(255, 255, 255, 0.1);
            --text: #e5e7eb;
            --muted: #9ca3af;
            --accent: #6366f1;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Inter", sans-serif;
            background: radial-gradient(circle at top, #1e293b, #020617);
            color: var(--text);
            display: grid;
            place-items: center;
        }

        .card {
            width: min(480px, 90%);
            padding: 40px;
            border-radius: 24px;
            background: var(--card);
            backdrop-filter: blur(20px);
            border: 1px solid var(--border);
            text-align: center;
        }

        .icon {
            font-size: 64px;
            margin-bottom: 16px;
        }

        h1 {
            font-size: 2.2rem;
            margin-bottom: 10px;
        }

        p {
            color: var(--muted);
            margin-bottom: 28px;
            line-height: 1.6;
        }

        .btn {
            display: inline-block;
            padding: 12px 18px;
            border-radius: 12px;
            text-decoration: none;
            background: linear-gradient(135deg, #6366f1, #4f46e5);
            color: white;
            font-weight: 600;
            transition: 0.2s ease;
        }

        .btn:hover {
            transform: translateY(-2px);
            opacity: 0.9;
        }
    </style>
</head>
<body>

<%
    Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    if (statusCode == null) {
        statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    }
    if (statusCode == null) {
        statusCode = response.getStatus();
    }
%>

<div class="card">
    <c:choose>
        <c:when test="<%= statusCode == 404 %>">
            <div class="icon">🔍</div>
            <h1>Something is missing</h1>
            <p>The page you’re looking for doesn’t exist or has been moved.</p>
        </c:when>

        <c:when test="<%= statusCode == 403 %>">
            <div class="icon">⛔</div>
            <h1>Access denied</h1>
            <p>You don’t have permission to view this page.</p>
        </c:when>

        <c:otherwise>
            <div class="icon">⚠️</div>
            <h1>Something went wrong</h1>
            <p>An unexpected error occurred.</p>
        </c:otherwise>
    </c:choose>

    <a href="<%= request.getContextPath() %>/" class="btn">
        Go Home
    </a>
</div>

</body>
</html>