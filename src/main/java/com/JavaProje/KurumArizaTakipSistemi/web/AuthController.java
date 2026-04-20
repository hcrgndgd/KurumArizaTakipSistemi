package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        try {
            userService.registerUser(fullName, email, password);

            model.addAttribute("info",
                    "Doğrulama kodu e-posta adresinize gönderildi. Lütfen kodu girerek hesabınızı aktif edin.");
            model.addAttribute("email", email);

            return "auth/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "email", required = false) String email,
            Model model) {

        model.addAttribute("email", email);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        try {
            User user = userService.login(email, password);

            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", user.getRole().getRoleName());

            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}