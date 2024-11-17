package com.zlologin.zlologin.controller;

import com.zlologin.zlologin.util.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CuidadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mocks para beans adicionais exigidos pelo contexto de segurança
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void testCuidadorDashboardWithCuidadorRole() throws Exception {
        mockMvc.perform(get("/api/cuidador/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCuidadorDashboardWithUserRole() throws Exception {
        mockMvc.perform(get("/api/cuidador/dashboard"))
                .andExpect(status().isForbidden());  // Espera um status 403 Forbidden
    }
}
