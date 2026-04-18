package com.JavaProje.KurumArizaTakipSistemi.web;


import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        String email = body.get("email");
        String password = body.get("password");

        logger.info("POST /auth/register - Kayıt isteği | email={}", email);

        try {
            userService.registerUser(fullName, email, password);
            logger.info("Kayıt başarılı, doğrulama maili gönderildi | email={}", email);
            return ResponseEntity.ok(Map.of("message", "Kayıt başarılı! Lütfen e-posta adresinizi doğrulayın."));
        } catch (IllegalArgumentException e) {
            logger.warn("Kayıt başarısız | email={} | sebep={}", email, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam("token") String token) {
        logger.info("GET /auth/verify - Doğrulama isteği | token={}", token);

        boolean verified = userService.verifyEmail(token);

        if (verified) {
            logger.info("Mail doğrulama başarılı | token={}", token);
            return ResponseEntity.ok(Map.of(
                    "verified", true,
                    "message", "E-posta adresiniz başarıyla doğrulandı. Giriş yapabilirsiniz."
            ));
        } else {
            logger.warn("Mail doğrulama başarısız | token={}", token);
            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "message", "Doğrulama bağlantısı geçersiz veya süresi dolmuş."
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");

        logger.info("POST /auth/login - Giriş isteği | email={}", email);

        try {
            User user = userService.login(email, password);
            String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", roleName);

            logger.info("Giriş başarılı | email={} | rol={}", email, roleName);

            return ResponseEntity.ok(Map.of(
                    "message", "Giriş başarılı",
                    "role", roleName,
                    "fullName", user.getFullName()
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Giriş başarısız | email={} | sebep={}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        String email = session.getAttribute("currentUser") != null
                ? ((User) session.getAttribute("currentUser")).getEmail()
                : "bilinmiyor";

        session.invalidate();
        logger.info("POST /auth/logout - Çıkış yapıldı | email={}", email);
        return ResponseEntity.ok(Map.of("message", "Çıkış başarılı."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        logger.info("POST /auth/resend-verification - Yeniden doğrulama isteği | email={}", email);

        try {
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok(Map.of("message", "Doğrulama maili tekrar gönderildi."));
        } catch (IllegalArgumentException e) {
            logger.warn("Yeniden doğrulama başarısız | email={} | sebep={}", email, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}