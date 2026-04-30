package com.JavaProje.KurumArizaTakipSistemi.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String index() {
        logger.info("PAGE_REQUEST | class=MainController | method=index | endpoint=GET / | view=login");
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        logger.info("PAGE_REQUEST | class=MainController | method=login | endpoint=GET /login | view=login");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        logger.info("PAGE_REQUEST | class=MainController | method=register | endpoint=GET /register | view=register");
        return "register";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        logger.info("PAGE_REQUEST | class=MainController | method=adminDashboard | endpoint=GET /admin/dashboard | view=admin-dashboard");
        return "admin-dashboard";
    }

    @GetMapping("/404")
    public String error404() {
        logger.warn("ERROR_PAGE_REQUEST | class=MainController | method=error404 | endpoint=GET /404 | view=errors/404");
        return "errors/404";
    }

    @GetMapping("/500")
    public String error505()
    {
        logger.error("ERROR_PAGE_REQUEST | class=MainController | method=error505 | endpoint=GET /500 | view=errors/500");
        return "errors/500";
    }
}
