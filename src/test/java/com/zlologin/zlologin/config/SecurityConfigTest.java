package com.zlologin.zlologin.config;

import com.zlologin.zlologin.util.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void testAuthEndpointsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/register")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUIDADOR")
    void testCuidadorEndpointAccessibleByCuidadorRole() throws Exception {
        mockMvc.perform(get("/api/cuidador/dashboard")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RESPONSAVEL")
    void testResponsavelEndpointAccessibleByResponsavelRole() throws Exception {
        mockMvc.perform(get("/api/responsavel/dashboard")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminEndpointAccessibleByAdminRole() throws Exception {
        mockMvc.perform(get("/admin/dashboard")).andExpect(status().isOk());
    }
}
