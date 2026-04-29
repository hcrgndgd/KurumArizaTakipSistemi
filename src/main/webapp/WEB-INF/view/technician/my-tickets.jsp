<%--
  Created by IntelliJ IDEA.
  User: HacerGndgd
  Date: 28.04.2026
  Time: 22:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.JavaProje.KurumArizaTakipSistemi.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="technician.my.tickets"/></title>
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

    .header-title { margin: 0; font-size: 1.8rem; }
    .nav-links { display: flex; gap: 12px; align-items: center; }

    .lang-switcher { font-size: 0.85rem; }
    .lang-switcher a {
      color: var(--accent-strong);
      text-decoration: none;
      font-weight: 600;
      margin-left: 8px;
    }
    .lang-switcher a:hover { text-decoration: underline; }

    .ticket-table-wrapper {
      background: var(--panel);
      border-radius: 16px;
      border: 1px solid rgba(214, 199, 178, 0.85);
      box-shadow: var(--shadow);
      overflow: hidden;
      margin-bottom: 32px;
    }

    .ticket-table-header {
      padding: 20px 24px;
      border-bottom: 1px solid var(--line);
      font-size: 1.1rem;
      font-weight: 700;
      color: var(--accent-strong);
    }

    table { width: 100%; border-collapse: collapse; }

    th {
      background: rgba(15, 118, 110, 0.08);
      padding: 12px 16px;
      text-align: left;
      font-weight: 700;
      color: var(--accent-strong);
      font-size: 0.9rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    td {
      padding: 12px 16px;
      border-bottom: 1px solid rgba(214, 199, 178, 0.5);
      font-size: 0.95rem;
    }

    tr:last-child td { border-bottom: none; }
    tr:hover td { background: rgba(15, 118, 110, 0.04); }

    .badge { padding: 4px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: 700; }
    .badge-waiting { background: #fef3c7; color: #92400e; }
    .badge-started { background: #dbeafe; color: #1e40af; }
    .badge-progress { background: #d1fae5; color: #065f46; }
    .badge-done { background: #e0e7ff; color: #3730a3; }

    .btn {
      display: inline-block;
      padding: 8px 16px;
      border: 0;
      border-radius: 10px;
      font: inherit;
      font-size: 0.85rem;
      font-weight: 700;
      cursor: pointer;
      transition: transform 0.2s ease;
      text-decoration: none;
    }

    .btn-primary {
      color: #f8fafc;
      background: linear-gradient(135deg, var(--accent), var(--accent-strong));
      box-shadow: 0 8px 20px rgba(15, 118, 110, 0.22);
    }

    .btn-danger {
      color: #f8fafc;
      background: linear-gradient(135deg, var(--danger), #8b1a12);
      box-shadow: 0 8px 20px rgba(180, 35, 24, 0.22);
    }

    .btn-success {
      color: #f8fafc;
      background: linear-gradient(135deg, #059669, #047857);
      box-shadow: 0 8px 20px rgba(5, 150, 105, 0.22);
    }

    .btn:hover { transform: translateY(-1px); }

    select {
      padding: 7px 10px;
      border-radius: 8px;
      border: 1px solid var(--line);
      font: inherit;
      font-size: 0.85rem;
      background: white;
    }

    .actions-row { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }

    .empty-msg { text-align: center; padding: 40px; color: var(--muted); }

    .error-msg {
      background: #fee2e2;
      color: var(--danger);
      padding: 12px 16px;
      border-radius: 10px;
      margin-bottom: 16px;
      font-weight: 600;
    }
  </style>
</head>
<body>
<div class="container">

  <!-- Header -->
  <div class="header">
    <h1 class="header-title">📋 <spring:message code="technician.my.tickets"/></h1>
    <div class="nav-links">
      <div class="lang-switcher">
        <a href="?lang=tr">🇹🇷 Türkçe</a>
        <a href="?lang=en">🇬🇧 English</a>
      </div>
      <a href="${pageContext.request.contextPath}/technician/tickets" class="btn btn-primary">
        <spring:message code="technician.pending"/>
      </a>
      <button class="btn btn-danger" onclick="logout()">
        <spring:message code="common.logout"/>
      </button>
    </div>
  </div>

  <!-- Hata mesajları -->
  <c:if test="${param.error == 'not_yours'}">
    <div class="error-msg">⚠️ <spring:message code="technician.error.not_yours"/></div>
  </c:if>
  <c:if test="${param.error == 'closed'}">
    <div class="error-msg">âš ï¸ <spring:message code="technician.error.closed"/></div>
  </c:if>
  <c:if test="${param.error == 'true'}">
    <div class="error-msg">⚠️ <spring:message code="common.error"/></div>
  </c:if>

  <!-- Ticketlarım -->
  <div class="ticket-table-wrapper">
    <div class="ticket-table-header">🔧 <spring:message code="technician.my.tickets.header"/></div>
    <c:choose>
      <c:when test="${empty tickets}">
        <div class="empty-msg">
          📋 <spring:message code="technician.no.tickets"/>
          <br><br>
          <a href="${pageContext.request.contextPath}/technician/tickets" class="btn btn-primary">
            <spring:message code="technician.pending"/>
          </a>
        </div>
      </c:when>
      <c:otherwise>
        <table>
          <thead>
          <tr>
            <th><spring:message code="ticket.id"/></th>
            <th><spring:message code="ticket.title"/></th>
            <th><spring:message code="ticket.category"/></th>
            <th><spring:message code="ticket.status"/></th>
            <th><spring:message code="ticket.date"/></th>
            <th><spring:message code="ticket.action"/></th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="ticket" items="${tickets}">
            <tr>
              <td>${ticket.ticketId}</td>
              <td>${ticket.title}</td>
              <td>${ticket.category.categoryName}</td>
              <td>
                <span class="badge badge-started">${ticket.status.statusName}</span>
              </td>
              <td>${ticket.createdAt}</td>
              <td>
                <div class="actions-row">
                  <!-- Finish -->
                  <form method="post"
                        action="${pageContext.request.contextPath}/technician/tickets/${ticket.ticketId}/finish"
                        onsubmit="return confirm('<spring:message code="technician.finish.confirm"/>')">
                    <button type="submit" class="btn btn-success">
                      <spring:message code="technician.finish"/>
                    </button>
                  </form>

                  <!-- Bırak -->
                  <form method="post"
                        action="${pageContext.request.contextPath}/technician/tickets/${ticket.ticketId}/unassign"
                        onsubmit="return confirm('<spring:message code="technician.unassign.confirm"/>')">
                    <button type="submit" class="btn btn-danger">
                      <spring:message code="technician.unassign"/>
                    </button>
                  </form>
                </div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </div>

  <!-- Completed tickets -->
  <div class="ticket-table-wrapper">
    <div class="ticket-table-header">✓ <spring:message code="technician.completed.tickets.header"/></div>
    <c:choose>
      <c:when test="${empty completedTickets}">
        <div class="empty-msg">
          <spring:message code="technician.no.completed.tickets"/>
        </div>
      </c:when>
      <c:otherwise>
        <table>
          <thead>
          <tr>
            <th><spring:message code="ticket.id"/></th>
            <th><spring:message code="ticket.title"/></th>
            <th><spring:message code="ticket.category"/></th>
            <th><spring:message code="ticket.status"/></th>
            <th><spring:message code="ticket.date"/></th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="ticket" items="${completedTickets}">
            <tr>
              <td>${ticket.ticketId}</td>
              <td>${ticket.title}</td>
              <td>${ticket.category.categoryName}</td>
              <td>
                <span class="badge badge-done">${ticket.status.statusName}</span>
              </td>
              <td>${ticket.createdAt}</td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<script>
  async function logout() {
    if (!confirm("<spring:message code='profile.logout.confirm'/>")) return;
    try {
      const response = await fetch("<%= request.getContextPath() %>/auth/logout", {
        method: "POST",
        headers: { "Content-Type": "application/json" }
      });
      if (response.ok) {
        setTimeout(() => {
          window.location.href = "<%= request.getContextPath() %>/login";
        }, 500);
      }
    } catch (error) {
      console.error("Logout error:", error);
    }
  }
</script>
</body>
</html>
