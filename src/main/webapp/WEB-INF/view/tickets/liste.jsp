<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="ticket.list.title"/></title>
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

        .container { max-width: 1000px; margin: 0 auto; }

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

        .header h1 { margin: 0; font-size: 2rem; }
        .header-actions { display: flex; gap: 12px; align-items: center; }

        .lang-switcher { font-size: 0.85rem; }
        .lang-switcher a {
            color: var(--accent-strong);
            text-decoration: none;
            font-weight: 600;
            margin-left: 8px;
        }
        .lang-switcher a:hover { text-decoration: underline; }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            border: 0;
            border-radius: 12px;
            font: inherit;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .btn-primary {
            color: #f8fafc;
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            box-shadow: 0 16px 30px rgba(15, 118, 110, 0.22);
        }

        .btn-primary:hover { transform: translateY(-1px); }

        .btn-secondary { color: var(--text); background: var(--line); }
        .btn-secondary:hover { transform: translateY(-1px); }

        .error {
            background: #fee;
            color: var(--danger);
            padding: 16px;
            border-radius: 8px;
            border-left: 4px solid var(--danger);
            margin-bottom: 16px;
        }

        .empty-state {
            background: var(--panel);
            padding: 48px;
            border-radius: 16px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            text-align: center;
            box-shadow: var(--shadow);
        }

        .empty-state p { font-size: 1.1rem; color: var(--muted); margin-bottom: 24px; }

        table {
            width: 100%;
            background: var(--panel);
            border-collapse: collapse;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: var(--shadow);
        }

        thead { background: var(--accent); color: white; }

        th {
            padding: 16px;
            text-align: left;
            font-weight: 700;
            border-bottom: 2px solid rgba(214, 199, 178, 0.85);
        }

        td {
            padding: 12px 16px;
            border-bottom: 1px solid rgba(214, 199, 178, 0.5);
        }

        tbody tr:hover { background: rgba(15, 118, 110, 0.05); }
        tbody tr:last-child td { border-bottom: none; }

        .status-badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .status-open { background: #fef3c7; color: #92400e; }
        .status-closed { background: #dcfce7; color: #15803d; }
        .status-in-progress { background: #bfdbfe; color: #1e40af; }

        .ticket-link {
            color: var(--accent);
            text-decoration: none;
            font-weight: 600;
        }

        .ticket-link:hover { text-decoration: underline; }

        .footer { margin-top: 32px; text-align: center; }
        .footer a { color: var(--accent); text-decoration: none; font-weight: 600; }
        .footer a:hover { text-decoration: underline; }

        @media (max-width: 640px) {
            body { padding: 12px; }
            .header { flex-direction: column; gap: 16px; padding: 16px; }
            .header h1 { font-size: 1.5rem; }
            table { font-size: 0.85rem; }
            th, td { padding: 8px; }
        }
    </style>
</head>
<body>
<div class="container">

    <!-- Header -->
    <div class="header">
        <h1>🎫 <spring:message code="ticket.list.title"/></h1>
        <div class="header-actions">
            <div class="lang-switcher">
                <a href="?lang=tr">🇹🇷 Türkçe</a>
                <a href="?lang=en">🇬🇧 English</a>
            </div>
            <a href="${pageContext.request.contextPath}/user/tickets/new" class="btn btn-primary">
                + <spring:message code="ticket.list.new"/>
            </a>
        </div>
    </div>

    <!-- Hata -->
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <!-- Boş durum -->
    <c:if test="${empty tickets}">
        <div class="empty-state">
            <p><spring:message code="ticket.list.empty"/></p>
            <a href="${pageContext.request.contextPath}/user/tickets/new" class="btn btn-primary">
                <spring:message code="ticket.list.new"/>
            </a>
        </div>
    </c:if>

    <!-- Ticket tablosu -->
    <c:if test="${not empty tickets}">
        <table>
            <thead>
            <tr>
                <th style="width: 10%"><spring:message code="ticket.id"/></th>
                <th style="width: 30%"><spring:message code="ticket.title"/></th>
                <th style="width: 20%"><spring:message code="ticket.category"/></th>
                <th style="width: 15%"><spring:message code="ticket.status"/></th>
                <th style="width: 25%"><spring:message code="ticket.created.at"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${tickets}" var="t">
                <tr>
                    <td>
                        <a href="${pageContext.request.contextPath}/user/tickets/${t.ticketId}" class="ticket-link">
                            #${t.ticketId}
                        </a>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/user/tickets/${t.ticketId}" class="ticket-link">
                                ${t.title}
                        </a>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty t.category}">${t.category.categoryName}</c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty t.status}">
                <span class="status-badge status-${fn:toLowerCase(t.status.statusName)}">
                        ${t.status.statusName}
                </span>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${t.createdAt}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer">
        <a href="${pageContext.request.contextPath}/user/profile">
            ← <spring:message code="ticket.list.back.profile"/>
        </a>
    </div>
</div>
</body>
</html>