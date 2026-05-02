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

    @PostMapping("/register")
    public String register(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        logger.info("POST /auth/register - Kayıt isteği | email={}", email);

        try {
            userService.registerUser(fullName, email, password);
            logger.info("Kayıt başarılı, doğrulama maili gönderildi | email={}", email);
            model.addAttribute("success", "Kayıt başarılı! Lütfen e-posta adresinizi doğrulayın.");
            return "register";
        } catch (IllegalArgumentException e) {
            logger.warn("Kayıt başarısız | email={} | sebep={}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        logger.info("GET /auth/verify - Doğrulama isteği | token={}", token);

        boolean verified = userService.verifyEmail(token);

        if (verified) {
            logger.info("Mail doğrulama başarılı | token={}", token);
            return "redirect:/login?verified=true";
        } else {
            logger.warn("Mail doğrulama başarısız | token={}", token);
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        logger.info("POST /auth/login - Giriş isteği | email={}", email);
        logger.error("TEST ERROR LOG");

        try {
            User user = userService.login(email, password);
            String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", roleName);

            logger.info("Giriş başarılı | email={} | rol={}", email, roleName);

            if ("TECHNICIAN".equals(roleName)) {
                return "redirect:/technician/tickets";
            } else if ("ADMIN".equals(roleName)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/profile";
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Giriş başarısız | email={} | sebep={}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        String email = session.getAttribute("currentUser") != null
                ? ((User) session.getAttribute("currentUser")).getEmail()
                : "bilinmiyor";

        session.invalidate();
        logger.info("POST /auth/logout - Çıkış yapıldı | email={}", email);
        return "redirect:/login";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(
            @RequestParam("email") String email,
            Model model) {

        logger.info("POST /auth/resend-verification - Yeniden doğrulama isteği | email={}", email);

        try {
            userService.resendVerificationEmail(email);
            model.addAttribute("success", "Doğrulama maili tekrar gönderildi.");
            return "login";
        } catch (IllegalArgumentException e) {
            logger.warn("Yeniden doğrulama başarısız | email={} | sebep={}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam("email") String email,
            Model model) {

        logger.info("POST /auth/forgot-password - email={}", email);

        try {
            userService.sendPasswordResetEmail(email);
            model.addAttribute("success", "Şifre sıfırlama maili gönderildi.");
            return "forgot-password";
        } catch (IllegalArgumentException e) {
            logger.warn("Şifre sıfırlama başarısız | email={} | sebep={}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        logger.info("POST /auth/reset-password - token={}", token);

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Şifreler eşleşmiyor.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        try {
            userService.resetPassword(token, password);
            return "redirect:/login?reset=true";
        } catch (IllegalArgumentException e) {
            logger.warn("Şifre sıfırlama başarısız | sebep={}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}