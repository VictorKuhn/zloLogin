// AuthControllerTest.java
package com.zlologin.zlologin.controller;

import com.zlologin.zlologin.dto.LoginDTO;
import com.zlologin.zlologin.util.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Testa o sucesso do login quando as credenciais são válidas.
     * Verifica se o sistema retorna um token JWT esperado.
     */
    @Test
    public void whenLoginWithValidCredentials_thenReturnsJwtToken() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("cuidador@gmail.com");
        loginDTO.setPassword("cuidador123");

        Authentication authentication = mock(Authentication.class);

        // Configura mocks para simular a autenticação e geração de token bem-sucedidas
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        )).thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mockedJwtToken");

        // Act
        ResponseEntity<?> response = authController.loginUser(loginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mockedJwtToken", response.getBody());
    }

    /**
     * Testa a falha no login quando as credenciais são inválidas.
     * Verifica se o sistema retorna uma mensagem de "Credenciais inválidas!" com status 401.
     */
    @Test
    public void whenLoginWithInvalidCredentials_thenReturnsUnauthorizedWithMessage() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("cuidador@gmail.com");
        loginDTO.setPassword("wrongPassword");

        // Configura o mock para simular uma exceção de credenciais inválidas
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        )).thenThrow(new BadCredentialsException("Credenciais inválidas!"));

        // Act
        ResponseEntity<?> response = authController.loginUser(loginDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas!", response.getBody());
    }

    /**
     * Testa o tratamento de exceção genérica de autenticação.
     * Verifica se o sistema retorna uma mensagem de "Erro de autenticação!" com status 401.
     */
    @Test
    public void whenAuthenticationExceptionOccurs_thenReturnsUnauthorizedWithGenericMessage() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("cuidador@gmail.com");
        loginDTO.setPassword("cuidador123");

        // Configura o mock para simular uma exceção de autenticação genérica
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        )).thenThrow(new AuthenticationException("Erro de autenticação!") {});

        // Act
        ResponseEntity<?> response = authController.loginUser(loginDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Erro de autenticação!", response.getBody());
    }
}
