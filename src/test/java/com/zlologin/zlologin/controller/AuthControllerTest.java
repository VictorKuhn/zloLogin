package com.zlologin.zlologin.controller;

import com.zlologin.zlologin.dto.ForgotPasswordDTO;
import com.zlologin.zlologin.dto.LoginDTO;
import com.zlologin.zlologin.dto.RegisterDTO;
import com.zlologin.zlologin.dto.ResetPasswordDTO;
import com.zlologin.zlologin.service.UserService;
import com.zlologin.zlologin.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole("USER");

        ResponseEntity<?> response = authController.registerUser(registerDTO);

        verify(userService, times(1)).registerUser(registerDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário registrado com sucesso!", response.getBody());
    }

    @Test
    void testLoginUserSuccess() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mockedToken");

        ResponseEntity<?> response = authController.loginUser(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mockedToken", response.getBody());
    }

    @Test
    void testLoginUserBadCredentials() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas!"));

        ResponseEntity<?> response = authController.loginUser(loginDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas!", response.getBody());
    }

    @Test
    void testForgotPasswordSuccess() {
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setEmail("test@example.com");

        doNothing().when(userService).sendPasswordResetToken(forgotPasswordDTO.getEmail());

        ResponseEntity<String> response = authController.forgotPassword(forgotPasswordDTO);

        verify(userService, times(1)).sendPasswordResetToken(forgotPasswordDTO.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("E-mail para redefinição de senha enviado com sucesso.", response.getBody());
    }

    @Test
    void testForgotPasswordFailure() {
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setEmail("test@example.com");

        doThrow(new RuntimeException("Erro ao enviar e-mail")).when(userService).sendPasswordResetToken(forgotPasswordDTO.getEmail());

        ResponseEntity<String> response = authController.forgotPassword(forgotPasswordDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro ao enviar e-mail de redefinição de senha.", response.getBody());
    }

    @Test
    void testResetPasswordSuccess() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken("validToken");
        resetPasswordDTO.setNewPassword("newPassword123");

        doNothing().when(userService).resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());

        ResponseEntity<String> response = authController.resetPassword(resetPasswordDTO);

        verify(userService, times(1)).resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Senha redefinida com sucesso.", response.getBody());
    }

    @Test
    void testResetPasswordFailure() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken("invalidToken");
        resetPasswordDTO.setNewPassword("newPassword123");

        doThrow(new RuntimeException("Token inválido ou expirado")).when(userService).resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());

        ResponseEntity<String> response = authController.resetPassword(resetPasswordDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token inválido ou expirado.", response.getBody());
    }

    @Test
    void testLoginUserAuthenticationException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        // Simula uma exceção AuthenticationException genérica (não BadCredentialsException)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Erro de autenticação!") {});

        ResponseEntity<?> response = authController.loginUser(loginDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Erro de autenticação!", response.getBody());
    }
}