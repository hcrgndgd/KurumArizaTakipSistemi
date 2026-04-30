package com.JavaProje.KurumArizaTakipSistemi.interceptor;

import com.JavaProje.KurumArizaTakipSistemi.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);
    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            logger.warn(
                    "SECURITY_DENIED | class=AdminInterceptor | method=preHandle | endpoint={} {} | reason=AUTHENTICATION_REQUIRED | status={}",
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

        String roleName = (String) session.getAttribute("userRole");
        if (roleName == null) {
            roleName = getRoleNameFromCurrentUser(session);
        }

        if (!ADMIN_ROLE.equalsIgnoreCase(roleName != null ? roleName.trim() : null)) {
            logger.warn(
                    "SECURITY_DENIED | class=AdminInterceptor | method=preHandle | endpoint={} {} | reason=ADMIN_ROLE_REQUIRED | actualRole={} | status={}",
                    req.getMethod(),
                    req.getRequestURI(),
                    roleName,
                    HttpServletResponse.SC_FORBIDDEN
            );
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin authority is required.");
            return false;
        }

        logger.info(
                "SECURITY_ALLOWED | class=AdminInterceptor | method=preHandle | endpoint={} {} | role={}",
                req.getMethod(),
                req.getRequestURI(),
                roleName
        );
        return true;
    }

    private String getRoleNameFromCurrentUser(HttpSession session) {
        Object currentUser = session.getAttribute("currentUser");
        if (!(currentUser instanceof User user) || user.getRole() == null) {
            return null;
        }

        return user.getRole().getRoleName();
    }

    private boolean acceptsHtml(HttpServletRequest req) {
        String acceptHeader = req.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("text/html");
    }
}
