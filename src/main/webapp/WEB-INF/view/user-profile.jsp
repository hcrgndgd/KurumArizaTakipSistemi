<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.JavaProje.KurumArizaTakipSistemi.model.User" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="profile.title"/></title>
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

        .container { max-width: 800px; margin: 0 auto; }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
            background: var(--panel);
            padding: 24px 32px;
            border-radius: 16px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            box-shadow: var(--shadow);
        }

        .header-title { margin: 0; font-size: 1.8rem; }
        .header-actions { display: flex; gap: 12px; align-items: center; }

        .user-info {
            background: var(--panel);
            padding: 32px;
            border-radius: 16px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            box-shadow: var(--shadow);
            margin-bottom: 32px;
        }

        .info-group { margin-bottom: 24px; }

        .info-group label {
            display: block;
            font-weight: 700;
            color: var(--accent-strong);
            margin-bottom: 8px;
            font-size: 0.95rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .info-group p {
            margin: 0;
            font-size: 1.2rem;
            color: var(--text);
            background: rgba(255, 255, 255, 0.5);
            padding: 12px 16px;
            border-radius: 10px;
            border: 1px solid var(--line);
        }

        .actions-section {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }

        .action-card {
            background: var(--panel);
            padding: 24px;
            border-radius: 16px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            box-shadow: var(--shadow);
            text-align: center;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .action-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 28px 70px rgba(31, 41, 51, 0.18);
        }

        .action-card h3 { margin: 0 0 16px; font-size: 1.1rem; color: var(--text); }
        .action-card p { margin: 0 0 16px; color: var(--muted); font-size: 0.95rem; line-height: 1.5; }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            border: 0;
            border-radius: 12px;
            font: inherit;
            font-weight: 700;
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
            text-decoration: none;
            text-align: center;
            width: 100%;
        }

        .btn-primary {
            color: #f8fafc;
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            box-shadow: 0 16px 30px rgba(15, 118, 110, 0.22);
        }

        .btn-primary:hover { transform: translateY(-1px); }

        .btn-danger {
            color: #f8fafc;
            background: linear-gradient(135deg, var(--danger), #8b1a12);
            box-shadow: 0 16px 30px rgba(180, 35, 24, 0.22);
        }

        .btn-danger:hover { transform: translateY(-1px); }
        .btn:disabled { opacity: 0.7; cursor: wait; transform: none; }

        .feedback {
            min-height: 24px;
            margin: 16px 0 0;
            font-size: 0.95rem;
            font-weight: 600;
        }

        .feedback.error { color: var(--danger); }
        .feedback.success { color: var(--success); }

        .lang-switcher {
            font-size: 0.85rem;
        }

        .lang-switcher a {
            color: var(--accent-strong);
            text-decoration: none;
            font-weight: 600;
            margin-left: 8px;
        }

        .lang-switcher a:hover { text-decoration: underline; }

        @media (max-width: 640px) {
            body { padding: 12px; }
            .container { padding: 0; }
            .header { flex-direction: column; gap: 16px; align-items: flex-start; padding: 16px; }
            .header-title { font-size: 1.5rem; }
            .header-actions { width: 100%; flex-direction: column; }
            .user-info { padding: 16px; }
            .actions-section { grid-template-columns: 1fr; }
            .action-card { padding: 16px; }
        }
    </style>
</head>
<body>
<div class="container">

    <!-- Header -->
    <div class="header">
        <h1 class="header-title"><spring:message code="profile.title"/></h1>
        <div class="header-actions">
            <div class="lang-switcher">
                <a href="?lang=tr">🇹🇷 Türkçe</a>
                <a href="?lang=en">🇬🇧 English</a>
            </div>
            <button class="btn btn-danger" id="logoutBtn" onclick="logout()">
                <spring:message code="profile.logout"/>
            </button>
        </div>
    </div>

    <!-- Kullanıcı Bilgileri -->
    <div class="user-info">
        <div class="info-group">
            <label><spring:message code="profile.fullname"/></label>
            <p id="fullName">${user.fullName}</p>
        </div>
        <div class="info-group">
            <label><spring:message code="profile.email"/></label>
            <p id="email">${user.email}</p>
        </div>
        <% if (session.getAttribute("userRole") != null) { %>
        <div class="info-group">
            <label><spring:message code="profile.role"/></label>
            <p id="role"><%= session.getAttribute("userRole") %></p>
        </div>
        <% } %>
    </div>

    <!-- Aksiyon Kartları -->
    <div class="actions-section">
        <div class="action-card">
            <h3>📝 <spring:message code="profile.create.ticket"/></h3>
            <p><spring:message code="profile.create.ticket.desc"/></p>
            <a href="<%= request.getContextPath() %>/user/tickets/new" class="btn btn-primary">
                <spring:message code="profile.create.ticket"/>
            </a>
        </div>
        <div class="action-card">
            <h3>📋 <spring:message code="profile.view.tickets"/></h3>
            <p><spring:message code="profile.view.tickets.desc"/></p>
            <a href="<%= request.getContextPath() %>/user/tickets" class="btn btn-primary">
                <spring:message code="profile.view.tickets"/>
            </a>
        </div>
    </div>

    <div id="feedback" class="feedback" aria-live="polite"></div>
</div>

<script>
    function setFeedback(message, type) {
        const feedback = document.getElementById("feedback");
        feedback.textContent = message;
        feedback.className = "feedback " + type;
    }

    async function logout() {
        if (!confirm("<spring:message code='profile.logout.confirm'/>")) return;

        const logoutBtn = document.getElementById("logoutBtn");
        logoutBtn.disabled = true;
        setFeedback("<spring:message code='profile.logout.loading'/>", "success");

        try {
            const response = await fetch("<%= request.getContextPath() %>/auth/logout", {
                method: "POST",
                headers: { "Content-Type": "application/json" }
            });

            const data = await response.json();

            if (!response.ok) {
                setFeedback(data.error || "<spring:message code='common.error'/>", "error");
                logoutBtn.disabled = false;
                return;
            }

            setFeedback(data.message || "<spring:message code='profile.logout.success'/>", "success");

            setTimeout(() => {
                window.location.href = "<%= request.getContextPath() %>/login";
            }, 1000);
        } catch (error) {
            setFeedback("<spring:message code='common.error'/>", "error");
            logoutBtn.disabled = false;
        }
    }

    window.addEventListener("load", () => {
        const fullName = document.getElementById("fullName").textContent.trim();
        if (!fullName) {
            window.location.href = "<%= request.getContextPath() %>/login";
        }
    });
</script>
</body>
</html>