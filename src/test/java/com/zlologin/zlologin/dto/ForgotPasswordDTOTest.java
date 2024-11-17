package com.zlologin.zlologin.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ForgotPasswordDTOTest {

    private ForgotPasswordDTO forgotPasswordDTO;

    @BeforeEach
    void setUp() {
        forgotPasswordDTO = new ForgotPasswordDTO();
    }

    @Test
    void testSetAndGetEmail() {
        String email = "test@example.com";
        forgotPasswordDTO.setEmail(email);
        assertEquals(email, forgotPasswordDTO.getEmail(), "O e-mail deve ser configurado e recuperado corretamente.");
    }

    @Test
    void testSetAndGetEmailWithNullValue() {
        forgotPasswordDTO.setEmail(null);
        assertNull(forgotPasswordDTO.getEmail(), "O e-mail deve ser nulo quando configurado como tal.");
    }
}
