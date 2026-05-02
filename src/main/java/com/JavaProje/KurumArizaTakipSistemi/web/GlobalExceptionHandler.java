    package com.JavaProje.KurumArizaTakipSistemi.web;

    import jakarta.servlet.http.HttpServletRequest;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.ControllerAdvice;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.ResponseStatus;
    import org.springframework.web.servlet.NoHandlerFoundException;

    @ControllerAdvice
    public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(value = NoHandlerFoundException.class)
        @ResponseStatus(value = HttpStatus.NOT_FOUND)
        public String handle404(HttpServletRequest request, Exception e) {
            logger.warn("404 - Sayfa bulunamadı | url={} | hata={}", request.getRequestURL(), e.getMessage());
            return "errors/404";
        }

        @ExceptionHandler(value = Exception.class)
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        public String handle500(HttpServletRequest request, Exception e) {
            logger.error("500 - Sunucu hatası | url={}", request.getRequestURL(), e);
            return "errors/500";
        }
    }