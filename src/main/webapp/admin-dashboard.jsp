<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
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

        * {
            box-sizing: border-box;
        }

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

        .page {
            width: min(1280px, 100%);
            margin: 0 auto;
        }

        .hero,
        .stats-card,
        .panel {
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

        .hero h1 {
            margin: 0;
            font-size: clamp(2rem, 4vw, 3rem);
            line-height: 0.98;
        }

        .stats-card {
            padding: 24px;
            display: grid;
            gap: 16px;
            align-content: start;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 14px;
        }

        .stat {
            background: var(--panel-strong);
            border: 1px solid rgba(212, 192, 161, 0.68);
            border-radius: 20px;
            padding: 18px;
        }

        .stat .label {
            display: block;
            font-size: 0.82rem;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            color: var(--muted);
            margin-bottom: 8px;
        }

        .stat .value {
            font-size: 1.8rem;
            font-weight: 700;
        }

        .content {
            display: grid;
            grid-template-columns: 1.8fr 1fr;
            gap: 20px;
        }

        .panel {
            padding: 24px;
        }

        .panel-head {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 18px;
        }

        .panel-head h2 {
            margin: 0;
            font-size: 1.45rem;
        }

        .panel-head p {
            margin: 6px 0 0;
            color: var(--muted);
        }

        .toolbar {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .table-wrap {
            overflow: auto;
            border: 1px solid rgba(212, 192, 161, 0.7);
            border-radius: 22px;
            background: rgba(255, 255, 255, 0.58);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 760px;
        }

        th,
        td {
            text-align: left;
            padding: 16px 18px;
            border-bottom: 1px solid rgba(212, 192, 161, 0.55);
            vertical-align: middle;
        }

        th {
            font-size: 0.82rem;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            color: var(--muted);
            background: rgba(255, 248, 236, 0.85);
        }

        tbody tr:hover {
            background: rgba(139, 94, 52, 0.05);
        }

        .user-main {
            font-weight: 700;
            margin-bottom: 4px;
        }

        .user-sub {
            color: var(--muted);
            font-size: 0.92rem;
        }

        .pill {
            display: inline-flex;
            align-items: center;
            border-radius: 999px;
            padding: 7px 12px;
            font-size: 0.83rem;
            font-weight: 700;
            letter-spacing: 0.03em;
            border: 1px solid transparent;
        }

        .pill.success {
            color: var(--success);
            background: rgba(29, 122, 70, 0.12);
            border-color: rgba(29, 122, 70, 0.18);
        }

        .pill.warn {
            color: var(--danger);
            background: rgba(163, 49, 31, 0.12);
            border-color: rgba(163, 49, 31, 0.18);
        }

        .role-cell {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .stack {
            display: grid;
            gap: 18px;
        }

        .role-list {
            display: grid;
            gap: 12px;
        }

        .role-card {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 14px;
            padding: 16px 18px;
            border-radius: 20px;
            background: var(--panel-strong);
            border: 1px solid rgba(212, 192, 161, 0.68);
        }

        .role-name {
            font-weight: 700;
            margin-bottom: 4px;
        }

        .role-meta {
            color: var(--muted);
            font-size: 0.92rem;
        }

        .field-group {
            display: grid;
            gap: 12px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-size: 0.92rem;
            font-weight: 700;
        }

        input,
        select,
        button {
            font: inherit;
        }

        input,
        select {
            width: 100%;
            padding: 13px 14px;
            border-radius: 14px;
            border: 1px solid var(--line);
            background: rgba(255, 255, 255, 0.9);
            color: var(--text);
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 4px var(--accent-soft);
        }

        button {
            border: 0;
            border-radius: 14px;
            padding: 12px 16px;
            font-weight: 700;
            cursor: pointer;
            transition: transform 0.2s ease, opacity 0.2s ease, box-shadow 0.2s ease;
        }

        button:hover {
            transform: translateY(-1px);
        }

        button:disabled {
            opacity: 0.68;
            cursor: wait;
            transform: none;
        }

        .primary-btn {
            color: #fffaf3;
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            box-shadow: 0 14px 26px rgba(109, 71, 36, 0.24);
        }

        .secondary-btn {
            color: var(--accent-strong);
            background: rgba(139, 94, 52, 0.1);
        }

        .danger-btn {
            color: var(--danger);
            background: var(--danger-soft);
        }

        .mini-btn {
            padding: 10px 12px;
            border-radius: 12px;
            white-space: nowrap;
        }

        .feedback {
            min-height: 24px;
            font-weight: 700;
        }

        .feedback.success {
            color: var(--success);
        }

        .feedback.error {
            color: var(--danger);
        }

        .empty {
            padding: 22px;
            border: 1px dashed rgba(212, 192, 161, 0.9);
            border-radius: 18px;
            color: var(--muted);
            text-align: center;
            background: rgba(255, 250, 243, 0.7);
        }

        @media (max-width: 1080px) {
            .content {
                grid-template-columns: 1fr;
            }

            .hero {
                display: grid;
            }
        }

        @media (max-width: 720px) {
            body {
                padding: 16px;
            }

            .hero,
            .stats-card,
            .panel {
                border-radius: 22px;
            }

            .toolbar {
                width: 100%;
                justify-content: stretch;
            }

            .toolbar button {
                width: 100%;
            }

            .role-card {
                align-items: flex-start;
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<div class="page">
    <section class="hero">
        <h1>Admin control panel</h1>
    </section>

    <section class="stats-card" style="margin-bottom: 22px;">
        <div class="panel-head">
            <div>
                <h2>Snapshot</h2>
                <p>Current totals from the live admin API.</p>
            </div>
        </div>
        <div class="stats-grid">
            <div class="stat">
                <span class="label">Users</span>
                <span class="value" id="userCount">0</span>
            </div>
            <div class="stat">
                <span class="label">Verified</span>
                <span class="value" id="verifiedCount">0</span>
            </div>
            <div class="stat">
                <span class="label">Roles</span>
                <span class="value" id="roleCount">0</span>
            </div>
            <div class="stat">
                <span class="label">Unassigned</span>
                <span class="value" id="unassignedCount">0</span>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2>User directory</h2>
                    <p>Update a user's role inline or remove the account.</p>
                </div>
                <div class="toolbar">
                    <button id="refreshUsersButton" class="secondary-btn" type="button">Refresh list</button>
                </div>
            </div>

            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>User</th>
                        <th>Status</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody id="usersTableBody">
                    <tr>
                        <td colspan="4">
                            <div class="empty">Loading users...</div>
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
                        <h2>Role library</h2>
                        <p>See active roles and remove roles that are no longer used.</p>
                    </div>
                </div>
                <div id="rolesList" class="role-list">
                    <div class="empty">Loading roles...</div>
                </div>
                <p id="rolesFeedback" class="feedback" aria-live="polite"></p>
            </div>

            <div class="panel">
                <div class="panel-head">
                    <div>
                        <h2>Create role</h2>
                        <p>Add a new system role and make it available immediately.</p>
                    </div>
                </div>
                <form id="addRoleForm" class="field-group">
                    <div>
                        <label for="newRoleName">Role name</label>
                        <input id="newRoleName" name="role" type="text" maxlength="50" placeholder="Example: SUPPORT_LEAD" required>
                    </div>
                    <button id="addRoleButton" class="primary-btn" type="submit">Add role</button>
                    <p id="addRoleFeedback" class="feedback" aria-live="polite"></p>
                </form>
            </div>
        </div>
    </section>
</div>

<script>
    const contextPath = "<%= request.getContextPath() %>";

    const usersTableBody = document.getElementById("usersTableBody");
    const rolesList = document.getElementById("rolesList");
    const usersFeedback = document.getElementById("usersFeedback");
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

    function setFeedback(element, message, type) {
        element.textContent = message || "";
        element.className = "feedback" + (type ? " " + type : "");
    }

    async function readJson(response) {
        const text = await response.text();
        const contentType = response.headers.get("Content-Type") || "";

        if (!text) {
            return null;
        }

        if (contentType.includes("application/json")) {
            return JSON.parse(text);
        }

        return {
            rawText: text
        };
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
        const verifiedUsers = users.filter(function (user) {
            return user.verified;
        }).length;

        const usersWithoutRole = users.filter(function (user) {
            return !user.roleName;
        }).length;

        userCount.textContent = String(users.length);
        verifiedCount.textContent = String(verifiedUsers);
        roleCount.textContent = String(roles.length);
        unassignedCount.textContent = String(usersWithoutRole);
    }

    function buildRoleOptions(selectedRoleId) {
        return roles.map(function (role) {
            const selected = String(role.roleId) === String(selectedRoleId) ? " selected" : "";
            return '<option value="' + role.roleId + '"' + selected + ">" + role.roleName + "</option>";
        }).join("");
    }

    function renderUsers() {
        if (!users.length) {
            usersTableBody.innerHTML = '<tr><td colspan="4"><div class="empty">No users found.</div></td></tr>';
            updateStats();
            return;
        }

        usersTableBody.innerHTML = users.map(function (user) {
            const verifiedMarkup = user.verified
                ? '<span class="pill success">Verified</span>'
                : '<span class="pill warn">Pending</span>';

            return '' +
                '<tr data-user-id="' + user.userId + '">' +
                '  <td>' +
                '    <div class="user-main">' + escapeHtml(user.fullName || "Unnamed user") + '</div>' +
                '    <div class="user-sub">' + escapeHtml(user.email || "-") + ' | ID ' + escapeHtml(String(user.userId)) + '</div>' +
                '  </td>' +
                '  <td>' + verifiedMarkup + '</td>' +
                '  <td>' +
                '    <div class="role-cell">' +
                '      <select class="role-select" aria-label="Select role for user">' +
                buildRoleOptions(user.roleId) +
                '      </select>' +
                '      <button class="mini-btn secondary-btn save-role-btn" type="button">Save</button>' +
                '    </div>' +
                '  </td>' +
                '  <td>' +
                '    <button class="mini-btn danger-btn delete-user-btn" type="button">Delete user</button>' +
                '  </td>' +
                '</tr>';
        }).join("");

        updateStats();
    }

    function renderRoles() {
        if (!roles.length) {
            rolesList.innerHTML = '<div class="empty">No roles found.</div>';
            updateStats();
            return;
        }

        rolesList.innerHTML = roles.map(function (role) {
            const deleteDisabled = role.userCount > 0 ? " disabled" : "";
            const deleteTitle = role.userCount > 0 ? "Cannot delete a role that is assigned to users." : "Delete role";

            return '' +
                '<div class="role-card">' +
                '  <div>' +
                '    <div class="role-name">' + escapeHtml(role.roleName) + '</div>' +
                '    <div class="role-meta">' + escapeHtml(String(role.userCount)) + ' assigned users</div>' +
                '  </div>' +
                '  <button class="mini-btn danger-btn delete-role-btn" type="button" data-role-name="' + escapeHtml(role.roleName) + '" title="' + escapeHtml(deleteTitle) + '"' + deleteDisabled + '>Delete</button>' +
                '</div>';
        }).join("");

        updateStats();
    }

    async function loadUsers() {
        setFeedback(usersFeedback, "Loading users...", "success");
        try {
            users = await request(contextPath + "/admin/users");
            renderUsers();
            setFeedback(usersFeedback, "Users loaded.", "success");
        } catch (error) {
            usersTableBody.innerHTML = '<tr><td colspan="4"><div class="empty">Failed to load users.</div></td></tr>';
            setFeedback(usersFeedback, error.message, "error");
        }
    }

    async function loadRoles() {
        setFeedback(rolesFeedback, "Loading roles...", "success");
        try {
            roles = await request(contextPath + "/admin/roles");
            renderRoles();
            renderUsers();
            setFeedback(rolesFeedback, "Roles loaded.", "success");
        } catch (error) {
            rolesList.innerHTML = '<div class="empty">Failed to load roles.</div>';
            setFeedback(rolesFeedback, error.message, "error");
        }
    }

    async function refreshDashboard() {
        await Promise.all([loadRoles(), loadUsers()]);
    }

    async function saveUserRole(userId, roleId, button) {
        button.disabled = true;
        setFeedback(usersFeedback, "Updating role...", "success");

        try {
            await request(contextPath + "/admin/users/" + userId + "/role/" + roleId, {
                method: "PUT"
            });

            setFeedback(usersFeedback, "Role updated successfully.", "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(usersFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    async function deleteUser(userId, button) {
        if (!window.confirm("Delete this user permanently?")) {
            return;
        }

        button.disabled = true;
        setFeedback(usersFeedback, "Deleting user...", "success");

        try {
            await request(contextPath + "/admin/users/" + userId, {
                method: "DELETE"
            });

            setFeedback(usersFeedback, "User deleted successfully.", "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(usersFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    async function deleteRole(roleName, button) {
        if (!window.confirm("Delete role " + roleName + "?")) {
            return;
        }

        button.disabled = true;
        setFeedback(rolesFeedback, "Deleting role...", "success");

        try {
            await request(contextPath + "/admin/roles", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ roleName: roleName })
            });

            setFeedback(rolesFeedback, "Role deleted successfully.", "success");
            await refreshDashboard();
        } catch (error) {
            setFeedback(rolesFeedback, error.message, "error");
        } finally {
            button.disabled = false;
        }
    }

    function escapeHtml(value) {
        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    refreshUsersButton.addEventListener("click", function () {
        refreshDashboard();
    });

    addRoleForm.addEventListener("submit", async function (event) {
        event.preventDefault();
        const formData = new FormData(addRoleForm);
        const roleName = String(formData.get("role") || "").trim();

        if (!roleName) {
            setFeedback(addRoleFeedback, "Role name is required.", "error");
            return;
        }

        addRoleButton.disabled = true;
        setFeedback(addRoleFeedback, "Adding role...", "success");

        try {
            await request(contextPath + "/admin/roles", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ role: roleName })
            });

            addRoleForm.reset();
            setFeedback(addRoleFeedback, "Role added successfully.", "success");
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
            const userId = row.getAttribute("data-user-id");
            const roleSelect = row.querySelector(".role-select");
            saveUserRole(userId, roleSelect.value, saveButton);
            return;
        }

        const deleteButton = event.target.closest(".delete-user-btn");
        if (deleteButton) {
            const row = deleteButton.closest("tr");
            const userId = row.getAttribute("data-user-id");
            deleteUser(userId, deleteButton);
        }
    });

    rolesList.addEventListener("click", function (event) {
        const button = event.target.closest(".delete-role-btn");
        if (!button) {
            return;
        }

        const roleName = button.getAttribute("data-role-name");
        deleteRole(roleName, button);
    });

    refreshDashboard();
</script>
</body>
</html>
