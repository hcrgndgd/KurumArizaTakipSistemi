<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="ticket.new.title"/></title>
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
            padding: 24px;
        }

        .container { max-width: 600px; margin: 0 auto; }

        .card {
            background: var(--panel);
            padding: 40px 36px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            border-radius: 16px;
            box-shadow: var(--shadow);
        }

        .header { margin-bottom: 24px; }
        .header h1 { margin: 0 0 8px; font-size: 2rem; }
        .header p { margin: 0; color: var(--muted); font-size: 0.95rem; }

        .lang-switcher {
            text-align: right;
            margin-bottom: 16px;
            font-size: 0.85rem;
        }

        .lang-switcher a {
            color: var(--accent-strong);
            text-decoration: none;
            font-weight: 600;
            margin-left: 8px;
        }

        .lang-switcher a:hover { text-decoration: underline; }

        .form-group { margin-bottom: 24px; }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 700;
            color: var(--text);
        }

        input, select, textarea {
            width: 100%;
            padding: 12px 16px;
            border: 1px solid var(--line);
            border-radius: 10px;
            font: inherit;
            color: var(--text);
            background: rgba(255, 255, 255, 0.92);
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 4px rgba(15, 118, 110, 0.14);
        }

        textarea { resize: vertical; min-height: 120px; }

        .form-actions { display: flex; gap: 12px; margin-top: 32px; }

        button, .btn {
            flex: 1;
            padding: 14px 16px;
            border: 0;
            border-radius: 10px;
            font: inherit;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        button[type="submit"] {
            color: #f8fafc;
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            box-shadow: 0 16px 30px rgba(15, 118, 110, 0.22);
        }

        button[type="submit"]:hover { transform: translateY(-1px); }
        button:disabled { opacity: 0.7; cursor: wait; transform: none; }

        .btn-secondary { color: var(--text); background: var(--line); }
        .btn-secondary:hover { transform: translateY(-1px); }

        .error {
            background: #fee;
            color: var(--danger);
            padding: 12px 16px;
            border-radius: 8px;
            border-left: 4px solid var(--danger);
            margin-bottom: 24px;
        }

        .info {
            background: #dbeafe;
            color: #1e40af;
            padding: 12px 16px;
            border-radius: 8px;
            border-left: 4px solid #1e40af;
            margin-bottom: 24px;
            font-size: 0.95rem;
        }

        @media (max-width: 640px) {
            body { padding: 12px; }
            .card { padding: 24px 20px; }
            .header h1 { font-size: 1.5rem; }
            .form-actions { flex-direction: column; }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="card">

        <!-- Dil Değiştirme -->
        <div class="lang-switcher">
            <a href="?lang=tr">🇹🇷 Türkçe</a>
            <a href="?lang=en">🇬🇧 English</a>
        </div>

        <div class="header">
            <h1>📝 <spring:message code="ticket.new.title"/></h1>
            <p><spring:message code="ticket.new.subtitle"/></p>
        </div>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <div class="info">
            ℹ️ <spring:message code="ticket.new.ai.info"/>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/user/tickets">
            <div class="form-group">
                <label for="title"><spring:message code="ticket.title"/> *</label>
                <input type="text" id="title" name="title" maxlength="200" required
                       placeholder="<spring:message code='ticket.new.title.placeholder'/>"
                       value="${title}"/>
            </div>

            <div class="form-group">
                <label for="description"><spring:message code="ticket.description"/> *</label>
                <textarea id="description" name="description" maxlength="2000" required
                          placeholder="<spring:message code='ticket.new.description.placeholder'/>">${description}</textarea>
            </div>

            <div class="form-actions">
                <button type="submit">✓ <spring:message code="ticket.new.submit"/></button>
                <a href="${pageContext.request.contextPath}/user/tickets" class="btn btn-secondary">
                    ← <spring:message code="ticket.new.cancel"/>
                </a>
            </div>
        </form>
    </div>
</div>
</body>
</html>