<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="auth.register.title"/></title>
    <style>
        :root {
            --bg: #f7f3eb;
            --panel: rgba(255, 252, 246, 0.95);
            --line: #d8ccb7;
            --text: #1f2933;
            --muted: #667085;
            --accent: #9a3412;
            --accent-strong: #7c2d12;
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
                    radial-gradient(circle at top right, rgba(154, 52, 18, 0.20), transparent 32%),
                    radial-gradient(circle at bottom left, rgba(14, 116, 144, 0.14), transparent 28%),
                    linear-gradient(135deg, #fcfaf6 0%, #ece3d4 100%);
            display: grid;
            place-items: center;
            padding: 24px;
        }

        .shell { width: 100%; display: flex; justify-content: center; }

        .card {
            width: min(440px, 100%);
            padding: 40px 36px;
            background: var(--panel);
            border: 1px solid rgba(216, 204, 183, 0.9);
            border-radius: 28px;
            box-shadow: var(--shadow);
        }

        h2 { margin: 0 0 12px; line-height: 1.1; font-size: 2rem; }

        .card p {
            margin: 0 0 24px;
            line-height: 1.6;
            color: var(--muted);
        }

        .field { margin-bottom: 16px; }

        label { display: block; margin-bottom: 8px; font-weight: 600; }

        input {
            width: 100%;
            padding: 14px 16px;
            border: 1px solid var(--line);
            border-radius: 14px;
            font: inherit;
            color: var(--text);
            background: rgba(255, 255, 255, 0.94);
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        input:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 4px rgba(154, 52, 18, 0.14);
        }

        button {
            width: 100%;
            border: 0;
            border-radius: 14px;
            padding: 14px 16px;
            font: inherit;
            font-weight: 700;
            color: #fff7ed;
            background: linear-gradient(135deg, var(--accent), var(--accent-strong));
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
            box-shadow: 0 16px 30px rgba(154, 52, 18, 0.22);
        }

        button:hover { transform: translateY(-1px); }
        button:disabled { opacity: 0.75; cursor: wait; transform: none; }

        .feedback {
            min-height: 24px;
            margin-top: 16px;
            font-size: 0.95rem;
            font-weight: 600;
        }

        .feedback.error { color: var(--danger); }
        .feedback.success { color: var(--success); }

        .meta {
            margin-top: 18px;
            color: var(--muted);
            line-height: 1.7;
        }

        .meta a {
            color: var(--accent-strong);
            font-weight: 700;
            text-decoration: none;
        }

        .meta a:hover { text-decoration: underline; }

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

        @media (max-width: 640px) {
            body { padding: 16px; }
            .card { padding: 28px 22px; border-radius: 22px; }
        }
    </style>
</head>
<body>
<main class="shell">
    <div class="card">

        <!-- Dil Değiştirme -->
        <div class="lang-switcher">
            <a href="?lang=tr">🇹🇷 Türkçe</a>
            <a href="?lang=en">🇬🇧 English</a>
        </div>

        <h2><spring:message code="auth.register.title"/></h2>
        <p><spring:message code="auth.register.subtitle"/></p>

        <form id="registerForm">
            <div class="field">
                <label for="fullName"><spring:message code="auth.register.fullname"/></label>
                <input id="fullName" name="fullName" type="text" autocomplete="name"
                       placeholder="<spring:message code='auth.register.fullname'/>" required>
            </div>

            <div class="field">
                <label for="email"><spring:message code="auth.register.email"/></label>
                <input id="email" name="email" type="email" autocomplete="email"
                       placeholder="name@ogr.duzce.edu.tr" required>
            </div>

            <div class="field">
                <label for="password"><spring:message code="auth.register.password"/></label>
                <input id="password" name="password" type="password" autocomplete="new-password"
                       placeholder="••••••••" required>
            </div>

            <button id="submitButton" type="submit">
                <spring:message code="auth.register.button"/>
            </button>
            <div id="feedback" class="feedback" aria-live="polite"></div>
        </form>

        <div class="meta">
            <spring:message code="auth.register.login"/>
            <a href="login"><spring:message code="auth.login.title"/></a>
        </div>
    </div>
</main>

<script>
    const form = document.getElementById("registerForm");
    const feedback = document.getElementById("feedback");
    const submitButton = document.getElementById("submitButton");

    function setFeedback(message, type) {
        feedback.textContent = message;
        feedback.className = "feedback " + type;
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();
        submitButton.disabled = true;
        setFeedback("<spring:message code='auth.register.submitting'/>", "success");

        const payload = {
            fullName: form.fullName.value.trim(),
            email: form.email.value.trim(),
            password: form.password.value
        };

        try {
            const response = await fetch("<%= request.getContextPath() %>/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (!response.ok) {
                setFeedback(data.error || "<spring:message code='auth.register.failed'/>", "error");
                return;
            }

            setFeedback(data.message || "<spring:message code='auth.register.success'/>", "success");
            form.reset();
        } catch (error) {
            setFeedback("<spring:message code='common.error'/>", "error");
        } finally {
            submitButton.disabled = false;
        }
    });
</script>
</body>
</html>