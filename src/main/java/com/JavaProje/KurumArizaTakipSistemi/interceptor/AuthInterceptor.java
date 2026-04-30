package com.JavaProje.KurumArizaTakipSistemi.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            logger.warn(
                    "SECURITY_DENIED | class=AuthInterceptor | method=preHandle | endpoint={} {} | reason=AUTHENTICATION_REQUIRED | status={}",
                    req.getMethod(),
                    req.getRequestURI(),
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            if (acceptsHtml(req)) {
                res.sendRedirect(req.getContextPath() + "/login");
            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication is required.");
            }
            return false;
        }

        logger.info(
                "SECURITY_ALLOWED | class=AuthInterceptor | method=preHandle | endpoint={} {} | reason=AUTHENTICATED",
                req.getMethod(),
                req.getRequestURI()
        );
        return true;
    }

    private boolean acceptsHtml(HttpServletRequest req) {
        String acceptHeader = req.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("text/html");
    }
}
