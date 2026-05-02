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
public class TechnicianControllerTest {

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
     * Giriş yapmamış kullanıcı bekleyen ticketları listelemeye çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testListAvailableTickets() throws Exception {
        mockMvc.perform(get("/technician/tickets")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı kendi ticketlarını listelemeye çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testListMyTickets() throws Exception {
        mockMvc.perform(get("/technician/my-tickets")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı ticket almaya çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testAssignTicket() throws Exception {
        mockMvc.perform(post("/technician/tickets/1/assign")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı ticket bırakmaya çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testUnassignTicket() throws Exception {
        mockMvc.perform(post("/technician/tickets/1/unassign")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Giriş yapmamış kullanıcı ticket kapatmaya çalıştığında
     * sistem login sayfasına yönlendirmeli (3xx redirect).
     */
    @Test
    public void testFinishTicket() throws Exception {
        mockMvc.perform(post("/technician/tickets/1/finish")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection());
    }
}