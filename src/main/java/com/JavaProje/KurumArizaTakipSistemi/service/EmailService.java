package com.JavaProje.KurumArizaTakipSistemi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    public void sendVerificationEmail(String toEmail, String fullName, String token) {
        logger.info("EmailService.sendVerificationEmail() - to={}", toEmail);

        String baseUrl = env.getProperty("mail.base-url");
        String fromEmail = env.getProperty("mail.username");

        if (baseUrl == null || baseUrl.isBlank() || "null".equalsIgnoreCase(baseUrl.trim())) {
            throw new IllegalStateException("mail.base-url property is missing. Please set it in hibernate.properties.");
        }

        String htmlContent = getString(fullName, token, baseUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Arıza Takip Sistemi - E-posta Doğrulama");
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Doğrulama maili gönderildi | to={}", toEmail);
        } catch (Exception e) {
            logger.error("Mail gönderilemedi | to={} | hata={}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Mail gönderilemedi: " + e.getMessage());
        }
    }

    @NonNull
    private static String getString(String fullName, String token, String baseUrl) {
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String verifyLink = normalizedBaseUrl + "/auth/verify?token=" + token;

        String htmlContent = """
                <html><body style="font-family: Arial, sans-serif; color: #333;">
                  <h2>Arıza Takip Sistemi - E-posta Doğrulama</h2>
                  <p>Merhaba <strong>%s</strong>,</p>
                  <p>Hesabınızı aktifleştirmek için aşağıdaki bağlantıya tıklayın:</p>
                  <p>
                    <a href="%s" style="background:#4CAF50;color:white;padding:12px 24px;
                       text-decoration:none;border-radius:4px;display:inline-block;">
                      E-postamı Doğrula
                    </a>
                  </p>
                  <p style="color:#888;font-size:12px;">Bu bağlantı 24 saat geçerlidir.</p>
                  <p style="color:#888;font-size:12px;">
                    Eğer bu isteği siz yapmadıysanız bu maili görmezden gelebilirsiniz.
                  </p>
                </body></html>
                """.formatted(fullName, verifyLink);
        return htmlContent;
    }

    public void sendPasswordResetEmail(String toEmail, String fullName, String token) {
        logger.info("EmailService.sendPasswordResetEmail() - to={}", toEmail);

        String baseUrl = env.getProperty("mail.base-url");
        String fromEmail = env.getProperty("mail.username");
        String resetLink = baseUrl + "/auth/reset-password?token=" + token;

        String htmlContent = """
            <html><body style="font-family: Arial, sans-serif; color: #333;">
              <h2>Arıza Takip Sistemi - Şifre Sıfırlama</h2>
              <p>Merhaba <strong>%s</strong>,</p>
              <p>Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:</p>
              <p>
                <a href="%s" style="background:#0f766e;color:white;padding:12px 24px;
                   text-decoration:none;border-radius:4px;display:inline-block;">
                  Şifremi Sıfırla
                </a>
              </p>
              <p style="color:#888;font-size:12px;">Bu bağlantı 1 saat geçerlidir.</p>
              <p style="color:#888;font-size:12px;">
                Eğer bu isteği siz yapmadıysanız bu maili görmezden gelebilirsiniz.
              </p>
            </body></html>
            """.formatted(fullName, resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Arıza Takip Sistemi - Şifre Sıfırlama");
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Şifre sıfırlama maili gönderildi | to={}", toEmail);
        } catch (Exception e) {
            logger.error("Mail gönderilemedi | to={} | hata={}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Mail gönderilemedi: " + e.getMessage());
        }
    }
}
