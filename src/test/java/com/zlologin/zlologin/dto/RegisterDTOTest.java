package com.zlologin.zlologin.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterDTOTest {

    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
    }

    @Test
    void testSetAndGetEmail() {
        String email = "test@example.com";
        registerDTO.setEmail(email);
        assertEquals(email, registerDTO.getEmail(), "O e-mail deve ser configurado e recuperado corretamente.");
    }

    @Test
    void testSetAndGetPassword() {
        String password = "securePassword";
        registerDTO.setPassword(password);
        assertEquals(password, registerDTO.getPassword(), "A senha deve ser configurada e recuperada corretamente.");
    }

    @Test
    void testSetAndGetRole() {
        String role = "USER";
        registerDTO.setRole(role);
        assertEquals(role, registerDTO.getRole(), "O papel deve ser configurado e recuperado corretamente.");
    }

    @Test
    void testSetAndGetEmailWithNullValue() {
        registerDTO.setEmail(null);
        assertNull(registerDTO.getEmail(), "O e-mail deve ser nulo quando configurado como tal.");
    }

    @Test
    void testSetAndGetPasswordWithNullValue() {
        registerDTO.setPassword(null);
        assertNull(registerDTO.getPassword(), "A senha deve ser nula quando configurada como tal.");
    }

    @Test
    void testSetAndGetRoleWithNullValue() {
        registerDTO.setRole(null);
        assertNull(registerDTO.getRole(), "O papel deve ser nulo quando configurado como tal.");
    }
}
