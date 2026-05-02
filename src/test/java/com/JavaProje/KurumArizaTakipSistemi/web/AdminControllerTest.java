package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.config.TestAppConfig;
import com.JavaProje.KurumArizaTakipSistemi.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
public class AdminControllerTest {

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
     * Giriş yapmamış kullanıcı kullanıcı listesine erişmeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testLoadUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Giriş yapmamış kullanıcı rol listesine erişmeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testLoadRoles() throws Exception {
        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Giriş yapmamış kullanıcı kullanıcı silmeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/admin/users/99999"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Giriş yapmamış kullanıcı rol güncellemeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testSetRole() throws Exception {
        mockMvc.perform(put("/admin/users/99999/role/99999"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Giriş yapmamış kullanıcı rol eklemeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testAddRole() throws Exception {
        String json = """
                {
                    "role": "TEST_ROLE"
                }
                """;

        mockMvc.perform(post("/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Giriş yapmamış kullanıcı aktif ticketlara erişmeye çalıştığında
     * sistem 401 Unauthorized döndürmeli.
     */
    @Test
    public void testLoadActiveTickets() throws Exception {
        mockMvc.perform(get("/admin/tickets/active"))
                .andExpect(status().isUnauthorized());
    }
}