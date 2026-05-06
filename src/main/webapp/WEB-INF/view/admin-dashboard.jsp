<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="admin.title"/></title>
    <style>
        :root {
            --bg-top: #f7f3eb;
            --bg-bottom: #e6dcc7;
            --panel: rgba(255, 251, 243, 0.9);
            --panel-strong: rgba(255, 248, 236, 0.96);
            --line: #d4c0a1;
            --text: #1f2933;
            --muted: #5f6c7b;
            --accent: #8b5e34;
            --accent-strong: #6d4724;
            --accent-soft: rgba(139, 94, 52, 0.14);
            --success: #1d7a46;
            --danger: #a3311f;
            --danger-soft: rgba(163, 49, 31, 0.14);
            --shadow: 0 30px 80px rgba(34, 31, 27, 0.16);
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text);
            background:
                    radial-gradient(circle at top left, rgba(12, 74, 110, 0.14), transparent 28%),
                    radial-gradient(circle at top right, rgba(139, 94, 52, 0.18), transparent 30%),
                    linear-gradient(155deg, var(--bg-top) 0%, var(--bg-bottom) 100%);
            padding: 28px;
        }

        .page { width: min(1280px, 100%); margin: 0 auto; }

        .hero, .stats-card, .panel {
            background: var(--panel);
            border: 1px solid rgba(212, 192, 161, 0.75);
            border-radius: 28px;
            box-shadow: var(--shadow);
            backdrop-filter: blur(10px);
        }

        .hero {
            margin-bottom: 22px;
            padding: 28px 30px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 20px;
        }

        .hero h1 { margin: 0; font-size: clamp(2rem, 4vw, 3rem); line-height: 0.98; }

        .lang-switcher { font-size: 0.85rem; }
        .lang-switcher a { color: var(--accent-strong); text-decoration: none; font-weight: 600; margin-left: 8px; }
        .lang-switcher a:hover { text-decoration: underline; }

        .stats-card { padding: 24px; display: grid; gap: 16px; align-content: start; }

        .stats-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }

        .stat { background: var(--panel-strong); border: 1px solid rgba(212, 192, 161, 0.68); border-radius: 20px; padding: 18px; }
        .stat .label { display: block; font-size: 0.82rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--muted); margin-bottom: 8px; }
        .stat .value { font-size: 1.8rem; font-weight: 700; }

        .content { display: grid; grid-template-columns: 1.8fr 1fr; gap: 20px; }
        .panel { padding: 24px; }

        .panel-head { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
        .panel-head h2 { margin: 0; font-size: 1.45rem; }
        .panel-head p { margin: 6px 0 0; color: var(--muted); }

        .toolbar { display: flex; gap: 10px; align-items: center; }

        .table-wrap { overflow: auto; border: 1px solid rgba(212, 192, 161, 0.7); border-radius: 22px; background: rgba(255, 255, 255, 0.58); }

        table { width: 100%; border-collapse: collapse; min-width: 760px; }

        th, td { text-align: left; padding: 16px 18px; border-bottom: 1px solid rgba(212, 192, 161, 0.55); vertical-align: middle; }

        th { font-size: 0.82rem; text-transform: uppercase; letter-spacing: 0.08em; color: var(--muted); background: rgba(255, 248, 236, 0.85); }

        tbody tr:hover { background: rgba(139, 94, 52, 0.05); }

        .user-main { font-weight: 700; margin-bottom: 4px; }
        .user-sub { color: var(--muted); font-size: 0.92rem; }

        .pill { display: inline-flex; align-items: center; border-radius: 999px; padding: 7px 12px; font-size: 0.83rem; font-weight: 700; letter-spacing: 0.03em; border: 1px solid transparent; }
        .pill.success { color: var(--success); background: rgba(29, 122, 70, 0.12); border-color: rgba(29, 122, 70, 0.18); }
        .pill.warn { color: var(--danger); background: rgba(163, 49, 31, 0.12); border-color: rgba(163, 49, 31, 0.18); }

        .role-cell { display: flex; gap: 10px; align-items: center; }
        .stack { display: grid; gap: 18px; }
        .role-list { display: grid; gap: 12px; }
        .wide-panel { margin-bottom: 22px; }

        .role-card { display: flex; justify-content: space-between; align-items: center; gap: 14px; padding: 16px 18px; border-radius: 20px; background: var(--panel-strong); border: 1px solid rgba(212, 192, 161, 0.68); }

        .role-name { font-weight: 700; margin-bottom: 4px; }
        .role-meta { color: var(--muted); font-size: 0.92rem; }
        .ticket-main { font-weight: 700; margin-bottom: 4px; }
        .ticket-sub { color: var(--muted); font-size: 0.92rem; }
        .assignment-cell { display: grid; grid-template-columns: minmax(180px, 1fr) auto auto; gap: 10px; align-items: center; min-width: 420px; }

        .field-group { display: grid; gap: 12px; }

        label { display: block; margin-bottom: 8px; font-size: 0.92rem; font-weight: 700; }

        input, select, button { font: inherit; }

        input, select { width: 100%; padding: 13px 14px; border-radius: 14px; border: 1px solid var(--line); background: rgba(255, 255, 255, 0.9); color: var(--text); transition: border-color 0.2s ease, box-shadow 0.2s ease; }

        input:focus, select:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 4px var(--accent-soft); }

        button { border: 0; border-radius: 14px; padding: 12px 16px; font-weight: 700; cursor: pointer; transition: transform 0.2s ease, opacity 0.2s ease, box-shadow 0.2s ease; }
        button:hover { transform: translateY(-1px); }
        button:disabled { opacity: 0.68; cursor: wait; transform: none; }

        .primary-btn { color: #fffaf3; background: linear-gradient(135deg, var(--accent), var(--accent-strong)); box-shadow: 0 14px 26px rgba(109, 71, 36, 0.24); }
        .secondary-btn { color: var(--accent-strong); background: rgba(139, 94, 52, 0.1); }
        .danger-btn { color: var(--danger); background: var(--danger-soft); }
        .mini-btn { padding: 10px 12px; border-radius: 12px; white-space: nowrap; }

        .feedback { min-height: 24px; font-weight: 700; }
        .feedback.success { color: var(--success); }
        .feedback.error { color: var(--danger); }

        .empty { padding: 22px; border: 1px dashed rgba(212, 192, 161, 0.9); border-radius: 18px; color: var(--muted); text-align: center; background: rgba(255, 250, 243, 0.7); }

        @media (max-width: 1080px) { .content { grid-template-columns: 1fr; } .hero { display: grid; } }
        @media (max-width: 720px) {
            body { padding: 16px; }
            .hero, .stats-card, .panel { border-radius: 22px; }
            .toolbar { width: 100%; justify-content: stretch; }
            .toolbar button { width: 100%; }
            .role-card { align-items: flex-start; flex-direction: column; }
            .assignment-cell { grid-template-columns: 1fr; min-width: 260px; }
        }
    </style>
</head>
<body>
<div class="page">

    <section class="hero">
        <h1><spring:message code="admin.title"/></h1>
        <div style="display:flex; gap:12px; align-items:center;">
            <div class="lang-switcher">
                <a href="?lang=tr">🇹🇷 Türkçe</a>
                <a href="?lang=en">🇬🇧 English</a>
            </div>
            <a href="${pageContext.request.contextPath}/user/profile"
               class="secondary-btn" style="padding: 10px 20px; text-decoration: none; border-radius: 14px; font-weight: 700;">
                 <spring:message code="profile.title"/>
            </a>
            <form method="post" action="${pageContext.request.contextPath}/auth/logout">
                <button type="submit" class="danger-btn" style="padding: 10px 20px;">
                    <spring:message code="common.logout"/>
                </button>
            </form>
        </div>
    </section>

    <section class="stats-card" style="margin-bottom: 22px;">
        <div class="panel-head">
            <div>
                <h2><spring:message code="admin.snapshot"/></h2>
                <p><spring:message code="admin.snapshot.desc"/></p>
            </div>
        </div>
        <div class="stats-grid">
            <div class="stat">
                <span class="label"><spring:message code="admin.users"/></span>
                <span class="value" id="userCount">0</span>
            </div>
            <div class="stat">
                <span class="label"><spring:message code="admin.verified"/></span>
                <span class="value" id="verifiedCount">0</span>
            </div>
            <div class="stat">
                <span class="label"><spring:message code="admin.roles"/></span>
                <span class="value" id="roleCount">0</span>
            </div>
            <div class="stat">
                <span class="label"><spring:message code="admin.unassigned"/></span>
                <span class="value" id="unassignedCount">0</span>
            </div>
        </div>
    </section>

    <section class="panel wide-panel">
        <div class="panel-head">
            <div>
                <h2><spring:message code="admin.active.tickets"/></h2>
                <p><spring:message code="admin.active.tickets.desc"/></p>
            </div>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th><spring:message code="admin.ticket"/></th>
                    <th><spring:message code="admin.status"/></th>
                    <th><spring:message code="admin.requester"/></th>
                    <th><spring:message code="admin.assignee"/></th>
                </tr>
                </thead>
                <tbody id="ticketsTableBody">
                <tr>
                    <td colspan="4">
                        <div class="empty"><spring:message code="admin.loading.tickets"/></div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <p id="ticketsFeedback" class="feedback" aria-live="polite"></p>
    </section>

    <section class="content">
        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2><spring:message code="admin.user.directory"/></h2>
                    <p><spring:message code="admin.user.directory.desc"/></p>
                </div>
                <div class="toolbar">
                    <button id="refreshUsersButton" class="secondary-btn" type="button">
                        <spring:message code="admin.refresh"/>
                    </button>
                </div>
            </div>

            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th><spring:message code="admin.user"/></th>
                        <th><spring:message code="admin.status"/></th>
                        <th><spring:message code="admin.role"/></th>
                        <th><spring:message code="admin.actions"/></th>
                    </tr>
                    </thead>
                    <tbody id="usersTableBody">
                    <tr>
                        <td colspan="4">
                            <div class="empty"><spring:message code="admin.loading.users"/></div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <p id="usersFeedback" class="feedback" aria-live="polite"></p>
        </div>

        <div class="stack">
            <div class="panel">
                <div class="panel-head">
                    <div>
                        <h2><spring:message code="admin.role.library"/></h2>
                        <p><spring:message code="admin.role.library.desc"/></p>
                    </div>
                </div>
                <div id="rolesList" class="role-list">
                    <div class="empty"><spring:message code="admin.loading.roles"/></div>
                </div>
                <p id="rolesFeedback" class="feedback" aria-live="polite"></p>
            </div>

            <div class="panel">
                <div class="panel-head">
                    <div>
                        <h2><spring:message code="admin.create.role"/></h2>
                        <p><spring:message code="admin.create.role.desc"/></p>
                    </div>
                </div>
                <form id="addRoleForm" class="field-group">
                    <div>
                        <label for="newRoleName"><spring:message code="admin.role.name"/></label>
                        <input id="newRoleName" name="role" type="text" maxlength="50"
                               placeholder="Example: SUPPORT_LEAD" required>
                    </div>
                    <button id="addRoleButton" class="primary-btn" type="submit">
                        <spring:message code="admin.add.role"/>
                    </button>
                    <p id="addRoleFeedback" class="feedback" aria-live="polite"></p>
                </form>
            </div>
        </div>
    </section>
</div>

<script>
    const contextPath = "<%= request.getContextPath() %>";

    // ← Dil metinleri JSP'den JS'e aktarılıyor
    const i18n = {
        verified: "<spring:message code='admin.status.verified'/>",
        pending: "<spring:message code='admin.status.pending'/>",
        save: "<spring:message code='admin.save'/>",
        delete: "<spring:message code='admin.delete'/>",
        assignedUsers: "<spring:message code='admin.assigned.users'/>",
        noUsers: "<spring:message code='admin.no.users'/>",
        noRoles: "<spring:message code='admin.no.roles'/>",
        roleRequired: "<spring:message code='admin.role.required'/>",
        roleAdded: "<spring:message code='admin.role.added'/>",
        roleDeleted: "<spring:message code='admin.role.deleted'/>",
        userDeleted: "<spring:message code='admin.user.deleted'/>",
        roleUpdated: "<spring:message code='admin.role.updated'/>",
        confirmDelete: "<spring:message code='admin.confirm.delete'/>",
        confirmDeleteRole: "<spring:message code='admin.confirm.delete.role'/>",
        noTickets: "<spring:message code='admin.no.tickets'/>",
        assign: "<spring:message code='admin.assign'/>",
        unassign: "<spring:message code='admin.unassign'/>",
        unassignedTicket: "<spring:message code='admin.unassigned.ticket'/>",
        ticketAssigned: "<spring:message code='admin.ticket.assigned'/>",
        ticketUnassigned: "<spring:message code='admin.ticket.unassigned'/>"
    };

    const usersTableBody = document.getElementById("usersTableBody");
    const ticketsTableBody = document.getElementById("ticketsTableBody");
    const rolesList = document.getElementById("rolesList");
    const usersFeedback = document.getElementById("usersFeedback");
    const ticketsFeedback = document.getElementById("ticketsFeedback");
    const rolesFeedback = document.getElementById("rolesFeedback");
    const addRoleFeedback = document.getElementById("addRoleFeedback");
    const addRoleForm = document.getElementById("addRoleForm");
    const addRoleButton = document.getElementById("addRoleButton");
    const refreshUsersButton = document.getElementById("refreshUsersButton");
    const userCount = document.getElementById("userCount");
    const verifiedCount = document.getElementById("verifiedCount");
    const roleCount = document.getElementById("roleCount");
    const unassignedCount = document.getElementById("unassignedCount");

    let users = [];
    let roles = [];
    let tickets = [];

    function getTechnicians() {
        return users.filter(user => String(user.roleName || "").toUpperCase() === "TECHNICIAN");
    }

    function setFeedback(element, message, type) {
        element.textContent = message || "";
        element.className = "feedback" + (type ? " " + type : "");
    }

    async function readJson(response) {
        const text = await response.text();
        const contentType = response.headers.get("Content-Type") || "";
        if (!text) return null;
        if (contentType.includes("application/json")) return JSON.parse(text);
        return { rawText: text };
    }

    async function request(url, options) {
        const response = await fetch(url, options);
        const data = await readJson(response);
        if (!response.ok) {
            const message = data && (data.error || data.message)
                ? (data.error || data.message)
                : "Request failed with status " + response.status + ".";
            throw new Error(message);
        }
        return data;
    }

    function updateStats() {
        const verifiedUsers = users.filter(u => u.verified).length;
        const unassignedTickets = tickets.filter(t => !t.assignedUser).length;
        userCount.textContent = String(users.length);
        verifiedCount.textContent = String(verifiedUsers);
        roleCount.textContent = String(roles.length);
        unassignedCount.textContent = String(unassignedTickets);
    }

    function buildRoleOptions(selectedRoleId) {
        return roles.map(role => {
            const selected = String(role.roleId) === String(selectedRoleId) ? " selected" : "";
            return '<option value="' + role.roleId + '"' + selected + ">" + role.roleName + "</option>";
        }).join("");
    }

    function buildUserOptions(selectedUserId) {
        return getTechnicians().map(user => {
            const selected = selectedUserId && String(user.userId) === String(selectedUserId) ? " selected" : "";
            return '<option value="' + user.userId + '"' + selected + ">" +
                escapeHtml(user.fullName || user.email || "User " + user.userId) +
                "</option>";
        }).join("");
    }

    function renderUsers() {
        if (!users.length) {
            usersTableBody.innerHTML = '<tr><td colspan="4"><div class="empty">' + i18n.noUsers + '</div></td></tr>';
            updateStats();
            return;
        }

        usersTableBody.innerHTML = users.map(user => {
            const verifiedMarkup = user.verified
                ? '<span class="pill success">' + i18n.verified + '</span>'
                : '<span class="pill warn">' + i18n.pending + '</span>';

            return '<tr data-user-id="' + user.userId + '">' +
                '<td><div class="user-main">' + escapeHtml(user.fullName || "Unnamed user") + '</div>' +
                '<div class="user-sub">' + escapeHtml(user.email || "-") + ' | ID ' + user.userId + '</div></td>' +
                '<td>' + verifiedMarkup + '</td>' +
                '<td><div class="role-cell"><select class="role-select">' + buildRoleOptions(user.roleId) + '</select>' +
                '<button class="mini-btn secondary-btn save-role-btn" type="button">' + i18n.save + '</button></div></td>' +
                '<td><button class="mini-btn danger-btn delete-user-btn" type="button">' + i18n.delete + '</button></td>' +
                '</tr>';
        }).join("");

        updateStats();
    }

    function renderTickets() {
        if (!tickets.length) {
            ticketsTableBody.innerHTML = '<tr><td colspan="4"><div class="empty">' + i18n.noTickets + '</div></td></tr>';
            updateStats();
            return;
        }

        ticketsTableBody.innerHTML = tickets.map(ticket => {
            const assignedUserId = ticket.assignedUser ? ticket.assignedUser.userId : "";
            const assignedName = ticket.assignedUser
                ? escapeHtml(ticket.assignedUser.fullName || ticket.assignedUser.email || ("ID " + ticket.assignedUser.userId))
                : i18n.unassignedTicket;
            const unassignDisabled = ticket.assignedUser ? "" : " disabled";
            const assignDisabled = getTechnicians().length ? "" : " disabled";

            return '<tr data-ticket-id="' + ticket.ticketId + '">' +
                '<td><div class="ticket-main">' + escapeHtml(ticket.title || "Untitled ticket") + '</div>' +
                '<div class="ticket-sub">ID ' + ticket.ticketId + ' | ' + escapeHtml(ticket.categoryName || "-") + '</div></td>' +
                '<td><span class="pill success">' + escapeHtml(ticket.statusName || "-") + '</span></td>' +
                '<td><div class="user-main">' + escapeHtml(ticket.requester ? (ticket.requester.fullName || "-") : "-") + '</div>' +
                '<div class="user-sub">' + escapeHtml(ticket.requester ? (ticket.requester.email || "-") : "-") + '</div></td>' +
                '<td><div class="assignment-cell">' +
                '<select class="ticket-user-select" aria-label="Assignee">' + buildUserOptions(assignedUserId) + '</select>' +
                '<button class="mini-btn secondary-btn assign-ticket-btn" type="button"' + assignDisabled + '>' + i18n.assign + '</button>' +
                '<button class="mini-btn danger-btn unassign-ticket-btn" type="button"' + unassignDisabled + '>' + i18n.unassign + '</button>' +
                '</div><div class="ticket-sub">' + assignedName + '</div></td>' +
                '</tr>';
        }).join("");

        updateStats();
    }

    function renderRoles() {
        if (!roles.length) {
            rolesList.innerHTML = '<div class="empty">' + i18n.noRoles + '</div>';
            updateStats();
            return;
        }

        rolesList.innerHTML = roles.map(role => {
            const deleteDisabled = role.userCount > 0 ? " disabled" : "";
            return '<div class="role-card"><div>' +
                '<div class="role-name">' + escapeHtml(role.roleName) + '</div>' +
                '<div class="role-meta">' + role.userCount + ' ' + i18n.assignedUsers + '</div></div>' +
                '<button class="mini-btn danger-btn delete-role-btn" type="button" data-role-name="' +
                escapeHtml(role.roleName) + '"' + deleteDisabled + '>' + i18n.delete + '</button></div>';
        }).join("");

        updateStats();
    }

    async function loadUsers() {
        try {
            users = await request(contextPath + "/admin/users");
            renderUsers();
            renderTickets();
        } catch (error) {
            usersTableBody.innerHTML = '<tr><td colspan="4"><div class="empty">Failed to load users.</div></td></tr>';
            setFeedback(usersFeedback, error.message, "error");
        }
    }

    async function loadActiveTickets() {
        try {
            tickets = await request(contextPath + "/admin/tickets/active");
            renderTickets();
        } catch (error) {
            ticketsTableBody.innerHTML = '<tr><td colspan="4"><div class="empty">Failed to load active tickets.</div></td></tr>';
            setFeedback(ticketsFeedback, error.message, "error");
        }
    }

    async function loadRoles() {
        try {
            roles = await request(contextPath + "/admin/roles");
            renderRoles();
            renderUsers();
        } catch (error) {
            rolesList.innerHTML = '<div class="empty">Failed to load roles.</div>';
            setFeedback(rolesFeedback, error.message, "error");
        }
    }

    async function refreshDashboard() {
        await Promise.all([loadRoles(), loadUsers(), loadActiveTickets()]);
    }

    async function saveUserRole(userId, roleId, button) {
        button.disabled = true;
        try {
            await request(contextPath + "/admin/users/" + userId + "/role/" + roleId, { method: "PUT" });
            setFeedback(usersFeedback, i18n.roleUpdated, "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(usersFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    async function deleteUser(userId, button) {
        if (!window.confirm(i18n.confirmDelete)) return;
        button.disabled = true;
        try {
            await request(contextPath + "/admin/users/" + userId, { method: "DELETE" });
            setFeedback(usersFeedback, i18n.userDeleted, "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(usersFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    async function deleteRole(roleName, button) {
        if (!window.confirm(i18n.confirmDeleteRole + " " + roleName + "?")) return;
        button.disabled = true;
        try {
            await request(contextPath + "/admin/roles", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ roleName: roleName })
            });
            setFeedback(rolesFeedback, i18n.roleDeleted, "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(rolesFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    async function assignTicket(ticketId, userId, button) {
        button.disabled = true;
        try {
            await request(contextPath + "/admin/tickets/" + ticketId + "/assignee/" + userId, { method: "PUT" });
            setFeedback(ticketsFeedback, i18n.ticketAssigned, "success");
            await loadActiveTickets();
        } catch (error) {
            setFeedback(ticketsFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    async function unassignTicket(ticketId, button) {
        button.disabled = true;
        try {
            await request(contextPath + "/admin/tickets/" + ticketId + "/assignee", { method: "DELETE" });
            setFeedback(ticketsFeedback, i18n.ticketUnassigned, "success");
            await loadActiveTickets();
        } catch (error) {
            setFeedback(ticketsFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    function escapeHtml(value) {
        return String(value)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;").replace(/'/g, "&#39;");
    }

    refreshUsersButton.addEventListener("click", () => refreshDashboard());

    addRoleForm.addEventListener("submit", async function (event) {
        event.preventDefault();
        const roleName = String(new FormData(addRoleForm).get("role") || "").trim();
        if (!roleName) { setFeedback(addRoleFeedback, i18n.roleRequired, "error"); return; }
        addRoleButton.disabled = true;
        try {
            await request(contextPath + "/admin/roles", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ role: roleName })
            });
            addRoleForm.reset();
            setFeedback(addRoleFeedback, i18n.roleAdded, "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(addRoleFeedback, error.message, "error");
        } finally {
            addRoleButton.disabled = false;
        }
    });

    usersTableBody.addEventListener("click", function (event) {
        const saveButton = event.target.closest(".save-role-btn");
        if (saveButton) {
            const row = saveButton.closest("tr");
            saveUserRole(row.getAttribute("data-user-id"), row.querySelector(".role-select").value, saveButton);
            return;
        }
        const deleteButton = event.target.closest(".delete-user-btn");
        if (deleteButton) {
            deleteUser(deleteButton.closest("tr").getAttribute("data-user-id"), deleteButton);
        }
    });

    ticketsTableBody.addEventListener("click", function (event) {
        const assignButton = event.target.closest(".assign-ticket-btn");
        if (assignButton) {
            const row = assignButton.closest("tr");
            const userSelect = row.querySelector(".ticket-user-select");
            assignTicket(row.getAttribute("data-ticket-id"), userSelect.value, assignButton);
            return;
        }

        const unassignButton = event.target.closest(".unassign-ticket-btn");
        if (unassignButton) {
            unassignTicket(unassignButton.closest("tr").getAttribute("data-ticket-id"), unassignButton);
        }
    });

    rolesList.addEventListener("click", function (event) {
        const button = event.target.closest(".delete-role-btn");
        if (button) deleteRole(button.getAttribute("data-role-name"), button);
    });

    refreshDashboard();
</script>
</body>
</html>
