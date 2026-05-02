package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.config.TestAppConfig;
import com.JavaProje.KurumArizaTakipSistemi.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfig.class, TestAppConfig.class})
@WebAppConfiguration
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    /**
     * Geçersiz email domain ile kayıt isteği gönderildiğinde
     * sistem register sayfasına geri dönmeli ve hata mesajı göstermeli.
     * Sadece okul maili (@duzce.edu.tr) kabul edilir.
     */
    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("fullName", "Test User")
                        .param("email", "gecersiz-email")  // ← format geçersiz
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("register"));
    }

    /**
     * Yanlış şifre ile giriş yapılmaya çalışıldığında
     * sistem login sayfasına geri dönmeli ve hata mesajı göstermeli.
     */
    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("email", "test@duzce.edu.tr")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("login"));
    }

    /**
     * Oturumu kapatma isteği gönderildiğinde
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Geçersiz token ile mail doğrulama isteği gönderildiğinde
     * sistem login sayfasına hata parametresiyle yönlendirmeli.
     */
    @Test
    public void testVerify() throws Exception {
        mockMvc.perform(get("/auth/verify")
                        .param("token", "gecersiz-token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}