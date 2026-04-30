package com.JavaProje.KurumArizaTakipSistemi.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        String requestId = UUID.randomUUID().toString();
        req.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        MDC.put("requestId", requestId);

        logger.info(
                "REQUEST_START | endpoint={} {} | handler={} | clientIp={} | sessionId={}",
                req.getMethod(),
                getRequestPath(req),
                getHandlerName(handler),
                req.getRemoteAddr(),
                getSessionId(req)
        );

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        long durationMs = getDurationMs(req);

        if (ex != null) {
            logger.error(
                    "REQUEST_ERROR | endpoint={} {} | handler={} | status={} | durationMs={} | error={}",
                    req.getMethod(),
                    getRequestPath(req),
                    getHandlerName(handler),
                    res.getStatus(),
                    durationMs,
                    ex.getMessage(),
                    ex
            );
        } else {
            logger.info(
                    "REQUEST_END | endpoint={} {} | handler={} | status={} | durationMs={}",
                    req.getMethod(),
                    getRequestPath(req),
                    getHandlerName(handler),
                    res.getStatus(),
                    durationMs
            );
        }

        MDC.clear();
    }

    private String getRequestPath(HttpServletRequest req) {
        return req.getRequestURI();
    }

    private String getHandlerName(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getBeanType().getSimpleName() + "." + handlerMethod.getMethod().getName();
        }

        return handler != null ? handler.getClass().getSimpleName() : "unknown";
    }

    private String getSessionId(HttpServletRequest req) {
        return req.getSession(false) != null ? req.getSession(false).getId() : "none";
    }

    private long getDurationMs(HttpServletRequest req) {
        Object startTime = req.getAttribute(START_TIME_ATTRIBUTE);
        if (!(startTime instanceof Long startTimeMs)) {
            return -1L;
        }

        return System.currentTimeMillis() - startTimeMs;
    }
}
