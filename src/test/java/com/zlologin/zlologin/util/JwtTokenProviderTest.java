package com.zlologin.zlologin.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String secretKey;

    @BeforeEach
    void setUp() {
        // Gerar uma chave segura para o teste
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        jwtTokenProvider = new JwtTokenProvider(secretKey, 3600000); // 1 hora em milissegundos para expiração padrão
    }

    @Test
    void testGenerateToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtTokenProvider.generateToken(authentication);
        assertNotNull(token);

        String emailFromToken = jwtTokenProvider.getUserEmailFromToken(token);
        assertEquals("test@example.com", emailFromToken);

        String rolesFromToken = jwtTokenProvider.getRolesFromToken(token);
        assertEquals("ROLE_USER", rolesFromToken);
    }

    @Test
    void testGenerateTokenWithExpiry() {
        String email = "test@example.com";
        int expiryMinutes = 5;

        String token = jwtTokenProvider.generateTokenWithExpiry(email, expiryMinutes);
        assertNotNull(token);

        String emailFromToken = jwtTokenProvider.getUserEmailFromToken(token);
        assertEquals(email, emailFromToken);

        Date expirationDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
        long expectedExpiry = System.currentTimeMillis() + (expiryMinutes * 60 * 1000L);
        assertTrue(expirationDate.getTime() - expectedExpiry < 1000); // tolerância de 1 segundo para o tempo de execução
    }

    @Test
    void testValidateTokenValid() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtTokenProvider.generateToken(authentication);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateTokenInvalid() {
        String invalidToken = "invalidTokenValue";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void testGetUserEmailFromToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateTokenWithExpiry(email, 10);

        String emailFromToken = jwtTokenProvider.getUserEmailFromToken(token);
        assertEquals(email, emailFromToken);
    }

    @Test
    void testGetRolesFromToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token = jwtTokenProvider.generateToken(authentication);
        String rolesFromToken = jwtTokenProvider.getRolesFromToken(token);

        assertEquals("ROLE_ADMIN", rolesFromToken);
    }
}
