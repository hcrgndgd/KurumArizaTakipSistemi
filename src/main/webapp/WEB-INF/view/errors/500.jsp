<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error</title>

    <style>
        :root {
            --bg: #020617;
            --card: rgba(255, 255, 255, 0.05);
            --border: rgba(255, 255, 255, 0.08);
            --text: #e5e7eb;
            --muted: #9ca3af;
            --accent: #ef4444;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Inter", sans-serif;
            background: radial-gradient(circle at top, #1e293b, #020617);
            color: var(--text);
            display: grid;
            place-items: center;
        }

        .card {
            width: min(480px, 90%);
            padding: 40px;
            border-radius: 24px;
            background: var(--card);
            backdrop-filter: blur(20px);
            border: 1px solid var(--border);
            text-align: center;
        }

        .icon {
            font-size: 64px;
            margin-bottom: 16px;
            color: var(--accent);
        }

        h1 {
            font-size: 2.2rem;
            margin-bottom: 10px;
        }

        p {
            color: var(--muted);
            margin-bottom: 28px;
            line-height: 1.6;
        }

        .btn {
            display: inline-block;
            padding: 12px 18px;
            border-radius: 12px;
            text-decoration: none;
            background: linear-gradient(135deg, #ef4444, #dc2626);
            color: white;
            font-weight: 600;
            transition: 0.2s ease;
        }

        .btn:hover {
            transform: translateY(-2px);
            opacity: 0.9;
        }
    </style>
</head>
<body>

<div class="card">
    <div class="icon">💥</div>
    <h1>Oops, something went wrong</h1>
    <p>
        We're having some trouble on our end.
        Please try again later.
    </p>

    <a href="<%= request.getContextPath() %>/" class="btn">
        Go Home
    </a>
</div>

</body>
</html>