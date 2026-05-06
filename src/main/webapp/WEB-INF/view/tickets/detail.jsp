<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket #${ticket.ticketId}</title>
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

        .container { max-width: 900px; margin: 0 auto; }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            background: var(--panel);
            padding: 24px 32px;
            border-radius: 16px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            box-shadow: var(--shadow);
        }

        .header h1 { margin: 0; font-size: 1.8rem; }
        .header-actions { display: flex; gap: 12px; align-items: center; }

        .lang-switcher { font-size: 0.85rem; }
        .lang-switcher a { color: var(--accent-strong); text-decoration: none; font-weight: 600; margin-left: 8px; }
        .lang-switcher a:hover { text-decoration: underline; }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            border: 0;
            border-radius: 10px;
            font: inherit;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: transform 0.2s ease;
        }

        .btn-secondary { color: var(--text); background: var(--line); }
        .btn-secondary:hover { transform: translateY(-1px); }

        .detail-card {
            background: var(--panel);
            padding: 32px;
            border: 1px solid rgba(214, 199, 178, 0.85);
            border-radius: 16px;
            box-shadow: var(--shadow);
            margin-bottom: 24px;
        }

        .detail-section { margin-bottom: 24px; }
        .detail-section:last-child { margin-bottom: 0; }

        .section-title {
            font-size: 0.85rem;
            color: var(--accent-strong);
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 8px;
        }

        .section-content { font-size: 1.1rem; color: var(--text); line-height: 1.6; }

        .meta-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-top: 24px;
            padding-top: 24px;
            border-top: 1px solid var(--line);
        }

        .meta-item {
            background: rgba(15, 118, 110, 0.05);
            padding: 16px;
            border-radius: 10px;
            border: 1px solid rgba(15, 118, 110, 0.1);
        }

        .meta-label { font-size: 0.8rem; color: var(--muted); font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px; }
        .meta-value { font-size: 1rem; color: var(--text); font-weight: 600; }

        .status-badge { display: inline-block; padding: 8px 16px; border-radius: 8px; font-size: 0.9rem; font-weight: 700; }
        .status-open { background: #fef3c7; color: #92400e; }
        .status-closed { background: #dcfce7; color: #15803d; }
        .status-in-progress { background: #bfdbfe; color: #1e40af; }

        .back-link { display: inline-block; margin-top: 24px; color: var(--accent); text-decoration: none; font-weight: 600; }
        .back-link:hover { text-decoration: underline; }

        @media (max-width: 640px) {
            body { padding: 12px; }
            .header { flex-direction: column; gap: 16px; padding: 16px; }
            .header h1 { font-size: 1.3rem; }
            .detail-card { padding: 16px; }
            .meta-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>🎫 Ticket #${ticket.ticketId}</h1>
        <div class="header-actions">
            <div class="lang-switcher">
                <a href="?lang=tr">🇹🇷 Türkçe</a>
                <a href="?lang=en">🇬🇧 English</a>
            </div>
            <a href="${pageContext.request.contextPath}/user/tickets" class="btn btn-secondary">
                ← <spring:message code="ticket.detail.back"/>
            </a>
        </div>
    </div>

    <div class="detail-card">
        <div class="detail-section">
            <div class="section-title"><spring:message code="ticket.title"/></div>
            <div class="section-content">${ticket.title}</div>
        </div>

        <div class="detail-section">
            <div class="section-title"><spring:message code="ticket.description"/></div>
            <div class="section-content" style="white-space: pre-wrap;">${ticket.description}</div>
        </div>

        <div class="meta-item">
            <div class="meta-label"><spring:message code="ticket.status"/></div>
            <div class="meta-value">
                <c:choose>
                    <c:when test="${not empty ticket.status}">
                <span class="status-badge status-${fn:toLowerCase(ticket.status.statusName)}">
                        ${locale.language == 'tr' ? ticket.status.statusName : (not empty ticket.status.statusNameEn ? ticket.status.statusNameEn : ticket.status.statusName)}
                </span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge status-open"><spring:message code="ticket.status.open"/></span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="meta-item">
            <div class="meta-label"><spring:message code="ticket.category"/></div>
            <div class="meta-value">
                <c:choose>
                    <c:when test="${not empty ticket.category}">
                        ${locale.language == 'tr' ? ticket.category.categoryName : (not empty ticket.category.categoryNameEn ? ticket.category.categoryNameEn : ticket.category.categoryName)}
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </div>
        </div>

            <div class="meta-item">
                <div class="meta-label"><spring:message code="ticket.created.at"/></div>
                <div class="meta-value">${ticket.formattedCreatedAt}</div>
            </div>

            <div class="meta-item">
                <div class="meta-label"><spring:message code="ticket.updated.at"/></div>
                <div class="meta-value">
                    <c:choose>
                        <c:when test="${not empty ticket.updatedAt}">${ticket.formattedUpdatedAt}</c:when>
                        <c:otherwise><spring:message code="ticket.not.updated"/></c:otherwise>
                    </c:choose>
                </div>
            </div>

            <c:if test="${not empty ticket.assignedTechnician}">
                <div class="meta-item">
                    <div class="meta-label"><spring:message code="ticket.assigned.technician"/></div>
                    <div class="meta-value">${ticket.assignedTechnician.fullName}</div>
                </div>
            </c:if>
        </div>
    </div>

    <a href="${pageContext.request.contextPath}/user/tickets" class="back-link">
        ← <spring:message code="ticket.detail.back.list"/>
    </a>
</div>
</body>
</html>