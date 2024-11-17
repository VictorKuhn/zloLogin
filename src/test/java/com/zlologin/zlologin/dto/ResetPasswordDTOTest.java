package com.zlologin.zlologin.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordDTOTest {

    private ResetPasswordDTO resetPasswordDTO;

    @BeforeEach
    void setUp() {
        resetPasswordDTO = new ResetPasswordDTO();
    }

    @Test
    void testSetAndGetToken() {
        String token = "sampleToken";
        resetPasswordDTO.setToken(token);
        assertEquals(token, resetPasswordDTO.getToken(), "O token deve ser configurado e recuperado corretamente.");
    }

    @Test
    void testSetAndGetNewPassword() {
        String newPassword = "newSecurePassword";
        resetPasswordDTO.setNewPassword(newPassword);
        assertEquals(newPassword, resetPasswordDTO.getNewPassword(), "A nova senha deve ser configurada e recuperada corretamente.");
    }

    @Test
    void testSetAndGetTokenWithNullValue() {
        resetPasswordDTO.setToken(null);
        assertNull(resetPasswordDTO.getToken(), "O token deve ser nulo quando configurado como tal.");
    }

    @Test
    void testSetAndGetNewPasswordWithNullValue() {
        resetPasswordDTO.setNewPassword(null);
        assertNull(resetPasswordDTO.getNewPassword(), "A nova senha deve ser nula quando configurada como tal.");
    }
}
