<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In</title>
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

        * {
            box-sizing: border-box;
        }

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

        .shell {
            width: 100%;
            display: flex;
            justify-content: center;
        }

        .card {
            width: min(420px, 100%);
            padding: 40px 36px;
            background: var(--panel);
            border: 1px solid rgba(214, 199, 178, 0.85);
            border-radius: 28px;
            box-shadow: var(--shadow);
        }

        .card h2 {
            margin: 0 0 10px;
            font-size: 2rem;
        }

        .card p {
            margin: 0 0 24px;
            color: var(--muted);
            line-height: 1.6;
        }

        .field {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
        }

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
            transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
            box-shadow: 0 16px 30px rgba(15, 118, 110, 0.22);
        }

        button:hover {
            transform: translateY(-1px);
        }

        button:disabled {
            opacity: 0.7;
            cursor: wait;
            transform: none;
        }

        .feedback {
            min-height: 24px;
            margin: 16px 0 0;
            font-size: 0.95rem;
            font-weight: 600;
        }

        .feedback.error {
            color: var(--danger);
        }

        .feedback.success {
            color: var(--success);
        }

        .meta {
            margin-top: 20px;
            color: var(--muted);
            line-height: 1.7;
        }

        .meta a {
            color: var(--accent-strong);
            font-weight: 700;
            text-decoration: none;
        }

        .meta a:hover {
            text-decoration: underline;
        }

        @media (max-width: 640px) {
            body {
                padding: 16px;
            }

            .card {
                padding: 28px 22px;
                border-radius: 22px;
            }
        }
    </style>
</head>
<body>
<main class="shell">
    <div class="card">
        <h2>Sign In</h2>
        <p>Enter your email address and password to continue.</p>

        <form id="loginForm">
            <div class="field">
                <label for="email">Email</label>
                <input id="email" name="email" type="email" autocomplete="email" placeholder="name@company.com" required>
            </div>

            <div class="field">
                <label for="password">Password</label>
                <input id="password" name="password" type="password" autocomplete="current-password" placeholder="Enter your password" required>
            </div>

            <button id="submitButton" type="submit">Sign In</button>
            <div id="feedback" class="feedback" aria-live="polite"></div>
        </form>

        <div class="meta">
            " Don't have an account? <a href="register">Register</a>
        </div>
    </div>
</main>

<script>
    const form = document.getElementById("loginForm");
    const feedback = document.getElementById("feedback");
    const submitButton = document.getElementById("submitButton");

    function setFeedback(message, type) {
        feedback.textContent = message;
        feedback.className = "feedback " + type;
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();
        submitButton.disabled = true;
        setFeedback("Signing in...", "success");

        const payload = {
            email: form.email.value.trim(),
            password: form.password.value
        };

        try {
            const response = await fetch("<%= request.getContextPath() %>/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (!response.ok) {
                setFeedback(data.error || "Sign in failed.", "error");
                return;
            }

            const name = data.fullName ? " Signed in as " + data.fullName + "." : "";
            const role = data.role ? " Role: " + data.role + "." : "";
            setFeedback((data.message || "Sign in successful.") + name + role, "success");
            
            // Redirect to profile after 1.5 seconds
            setTimeout(() => {
                window.location.href = "<%= request.getContextPath() %>/user/profile";
            }, 1500);
        } catch (error) {
            setFeedback("The server could not be reached. Please try again later.", "error");
        } finally {
            submitButton.disabled = false;
        }
    });
</script>
</body>
</html>
