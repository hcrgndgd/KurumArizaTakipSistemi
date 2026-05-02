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
public class MainControllerTest {

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
     * Ana sayfa isteği gönderildiğinde
     * sistem 200 OK döndürmeli.
     */
    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    /**
     * Login sayfası isteği gönderildiğinde
     * sistem 200 OK döndürmeli.
     */
    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    /**
     * Register sayfası isteği gönderildiğinde
     * sistem 200 OK döndürmeli.
     */
    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    /**
     * Giriş yapmamış kullanıcı admin dashboard'a erişmeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Var olmayan bir URL'e istek gönderildiğinde
     * GlobalExceptionHandler devreye girerek
     * sistem 404 Not Found döndürmeli.
     */
    @Test
    public void testError404() throws Exception {
        mockMvc.perform(get("/yokboylesayfa"))
                .andExpect(status().isNotFound());
    }

    /**
     * Şifremi unuttum sayfası /auth/forgot-password altında tanımlıdır.
     * Bu endpoint'e istek gönderildiğinde sistem 200 OK döndürmeli.
     */
    @Test
    public void testForgotPassword() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk());
    }
}