package com.zlologin.zlologin.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        String token = "validToken";
        String email = "user@example.com";
        String roles = "ROLE_USER";

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertEquals(email, authentication.getPrincipal());
        assertEquals(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), authentication.getAuthorities());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithExpiredToken() throws ServletException, IOException {
        String token = "expiredToken";

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("{\"error\": \"Sessão expirada\"}", response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithoutToken() throws ServletException, IOException {
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithGenericException() throws ServletException, IOException {
        String token = "validToken";

        // Adiciona o token de autenticação no cabeçalho
        request.addHeader("Authorization", "Bearer " + token);

        // Simula o comportamento de validação do token como válido
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserEmailFromToken(token)).thenThrow(new RuntimeException("Erro genérico de teste"));

        // Executa o filtro
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Verifica se a resposta é 403 com a mensagem de erro apropriada
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("{\"error\": \"Acesso não autorizado\"}", response.getContentAsString());

        // Garante que o filterChain não foi executado
        verify(filterChain, never()).doFilter(request, response);
    }
}
