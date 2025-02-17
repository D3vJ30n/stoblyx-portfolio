package com.j30n.stoblyx.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenAccessPublicEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"name\":\"Test User\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessProtectedEndpoint_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/protected-resource"))
                .andExpect(status().isUnauthorized());
    }
} 