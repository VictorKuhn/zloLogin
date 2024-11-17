package com.zlologin.zlologin.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {

    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
    }

    @Test
    void testSetAndGetEmail() {
        String email = "test@example.com";
        loginDTO.setEmail(email);
        assertEquals(email, loginDTO.getEmail(), "O e-mail deve ser configurado e recuperado corretamente.");
    }

    @Test
    void testSetAndGetPassword() {
        String password = "securePassword";
        loginDTO.setPassword(password);
        assertEquals(password, loginDTO.getPassword(), "A senha deve ser configurada e recuperada corretamente.");
    }

    @Test
    void testSetAndGetEmailWithNullValue() {
        loginDTO.setEmail(null);
        assertNull(loginDTO.getEmail(), "O e-mail deve ser nulo quando configurado como tal.");
    }

    @Test
    void testSetAndGetPasswordWithNullValue() {
        loginDTO.setPassword(null);
        assertNull(loginDTO.getPassword(), "A senha deve ser nula quando configurada como tal.");
    }
}
