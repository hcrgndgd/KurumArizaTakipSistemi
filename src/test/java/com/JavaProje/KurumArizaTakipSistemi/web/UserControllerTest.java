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
public class UserControllerTest {

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
     * Giriş yapmamış kullanıcı profil sayfasına erişmeye çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testProfile() throws Exception {
        mockMvc.perform(get("/user/profile")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı ticket listesine erişmeye çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testListTickets() throws Exception {
        mockMvc.perform(get("/user/tickets")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı yeni ticket formuna erişmeye çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testShowNewTicketForm() throws Exception {
        mockMvc.perform(get("/user/tickets/new")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı ticket oluşturmaya çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testCreateTicket() throws Exception {
        mockMvc.perform(post("/user/tickets")
                        .header("Accept", "text/html")
                        .param("title", "Test Ticket")
                        .param("description", "Test Description"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı ticket detayına erişmeye çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testViewTicketDetail() throws Exception {
        mockMvc.perform(get("/user/tickets/1")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }
}