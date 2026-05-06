<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="auth.login.title"/></title>
    <style>
        :root {
            --bg: #f4f1ea;
            --panel: rgba(255, 252, 246, 0.94);
            --line: #d6c7b2;
            --text: #1f2933;
            --muted: #6b7280;
            --accent: #0f766e;
            --accent-strong: #115e59;
            --danger: #b42318;
            --success: #027a48;
            --shadow: 0 24px 60px rgba(31, 41, 51, 0.16);
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text);
            background:
                    radial-gradient(circle at top left, rgba(15, 118, 110, 0.22), transparent 34%),
                    radial-gradient(circle at bottom right, rgba(180, 83, 9, 0.18), transparent 28%),
                    linear-gradient(135deg, #f7f3eb 0%, #ebe5d8 100%);
            display: grid;
            place-items: center;
            padding: 24px;
        }

        .shell { width: 100%; display: flex; justify-content: center; }

        .card {
            width: min(420px, 100%);
            padding: 40px 36px;
            background: var(--panel);
            border: 1px solid rgba(214, 199, 178, 0.85);
            border-radius: 28px;
            box-shadow: var(--shadow);
        }

        .card h2 { margin: 0 0 10px; font-size: 2rem; }
        .card p { margin: 0 0 24px; color: var(--muted); line-height: 1.6; }
        .field { margin-bottom: 16px; }

        label { display: block; margin-bottom: 8px; font-weight: 600; }

        input {
            width: 100%;
            padding: 14px 16px;
            border: 1px solid var(--line);
            border-radius: 14px;
            font: inherit;
            color: var(--text);
            background: rgba(255, 255, 255, 0.92);
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        input:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 4px rgba(15, 118, 110, 0.14);
        }

        button {
            width: 100%;
            border: 0;
            border-radius: 14px;
            padding: 14px 16px;
            font: inherit;
            font-weight: 700;
            color: #f8fafc;
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            box-shadow: 0 16px 30px rgba(15, 118, 110, 0.22);
        }

        button:hover { transform: translateY(-1px); }

        .alert {
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 16px;
            font-weight: 600;
            font-size: 0.95rem;
        }

        .alert-error { background: #fee2e2; color: var(--danger); }
        .alert-success { background: #d1fae5; color: var(--success); }

        .meta {
            margin-top: 20px;
            color: var(--muted);
            line-height: 1.7;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .meta a { color: var(--accent-strong); font-weight: 700; text-decoration: none; }
        .meta a:hover { text-decoration: underline; }

        .forgot-link { font-size: 0.9rem; color: var(--muted); }
        .forgot-link a { color: var(--muted) !important; font-weight: 600 !important; }
        .forgot-link a:hover { text-decoration: underline; }

        .lang-switcher { text-align: right; margin-bottom: 16px; font-size: 0.85rem; }
        .lang-switcher a { color: var(--accent-strong); text-decoration: none; font-weight: 600; margin-left: 8px; }
        .lang-switcher a:hover { text-decoration: underline; }

        @media (max-width: 640px) {
            body { padding: 16px; }
            .card { padding: 28px 22px; border-radius: 22px; }
        }
    </style>
</head>
<body>
<main class="shell">
    <div class="card">

        <div class="lang-switcher">
            <a href="?lang=tr">🇹🇷 Türkçe</a>
            <a href="?lang=en">🇬🇧 English</a>
        </div>

        <h2><spring:message code="auth.login.title"/></h2>
        <p><spring:message code="auth.login.subtitle"/></p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <c:if test="${param.verified == 'true'}">
            <div class="alert alert-success">
                <spring:message code="auth.login.verified"/>
            </div>
        </c:if>

        <c:if test="${param.error == 'true'}">
            <div class="alert alert-error">
                <spring:message code="auth.login.token.expired"/>
            </div>
        </c:if>

        <c:if test="${param.reset == 'true'}">
            <div class="alert alert-success">
                <spring:message code="auth.reset.success"/>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/auth/login" autocomplete="off">
            <div class="field">
                <label for="email"><spring:message code="auth.login.email"/></label>
                <input id="email" name="email" type="email"
                       autocomplete="off"
                       placeholder="name@ogr.duzce.edu.tr" required>
            </div>

            <div class="field">
                <label for="password"><spring:message code="auth.login.password"/></label>
                <input id="password" name="password" type="password"
                       autocomplete="new-password"
                       placeholder="••••••••" required>
            </div>

            <button type="submit">
                <spring:message code="auth.login.button"/>
            </button>
        </form>

        <div class="meta">
            <span>
                <spring:message code="auth.login.register"/>
                <a href="${pageContext.request.contextPath}/register">
                    <spring:message code="auth.register.title"/>
                </a>
            </span>
            <span class="forgot-link">
                 <a href="${pageContext.request.contextPath}/auth/forgot-password">
                    <spring:message code="auth.forgot.title"/>
                </a>
            </span>
        </div>
    </div>
</main>
<script>
    // Sayfa her gösterildiğinde formu temizle (geri tuşu dahil)
    window.addEventListener("pageshow", function(event) {
        document.querySelector("form").reset();
    });
</script>
</body>
</html>