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

        logger.info("POST /auth/register - KayÄ±t isteÄŸi | email={}", email);

        try {
            userService.registerUser(fullName, email, password);
            logger.info("KayÄ±t baÅŸarÄ±lÄ±, doÄŸrulama maili gÃ¶nderildi | email={}", email);
            return ResponseEntity.ok(Map.of("message", "KayÄ±t baÅŸarÄ±lÄ±! LÃ¼tfen e-posta adresinizi doÄŸrulayÄ±n."));
        } catch (IllegalArgumentException e) {
            logger.warn("KayÄ±t baÅŸarÄ±sÄ±z | email={} | sebep={}", email, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam("token") String token) {
        logger.info("GET /auth/verify - DoÄŸrulama isteÄŸi | token={}", token);

        boolean verified = userService.verifyEmail(token);

        if (verified) {
            logger.info("Mail doÄŸrulama baÅŸarÄ±lÄ± | token={}", token);
            return ResponseEntity.ok(Map.of(
                    "verified", true,
                    "message", "E-posta adresiniz baÅŸarÄ±yla doÄŸrulandÄ±. GiriÅŸ yapabilirsiniz."
            ));
        } else {
            logger.warn("Mail doÄŸrulama baÅŸarÄ±sÄ±z | token={}", token);
            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "message", "DoÄŸrulama baÄŸlantÄ±sÄ± geÃ§ersiz veya sÃ¼resi dolmuÅŸ."
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        String password = body.get("password");

        logger.info("POST /auth/login - GiriÅŸ isteÄŸi | email={}", email);

        try {
            User user = userService.login(email, password);
            String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", roleName);

            logger.info("GiriÅŸ baÅŸarÄ±lÄ± | email={} | rol={}", email, roleName);

            return ResponseEntity.ok(Map.of(
                    "message", "GiriÅŸ baÅŸarÄ±lÄ±",
                    "role", roleName,
                    "fullName", user.getFullName()
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("GiriÅŸ baÅŸarÄ±sÄ±z | email={} | sebep={}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        String email = session.getAttribute("currentUser") != null
                ? ((User) session.getAttribute("currentUser")).getEmail()
                : "bilinmiyor";

        session.invalidate();
        logger.info("POST /auth/logout - Ã‡Ä±kÄ±ÅŸ yapÄ±ldÄ± | email={}", email);
        return ResponseEntity.ok(Map.of("message", "Ã‡Ä±kÄ±ÅŸ baÅŸarÄ±lÄ±."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        logger.info("POST /auth/resend-verification - Yeniden doÄŸrulama isteÄŸi | email={}", email);

        try {
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok(Map.of("message", "DoÄŸrulama maili tekrar gÃ¶nderildi."));
        } catch (IllegalArgumentException e) {
            logger.warn("Yeniden doÄŸrulama baÅŸarÄ±sÄ±z | email={} | sebep={}", email, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
