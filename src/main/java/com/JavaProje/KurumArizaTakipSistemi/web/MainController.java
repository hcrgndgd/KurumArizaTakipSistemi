package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

}