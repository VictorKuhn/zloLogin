// JwtTokenProviderTest.java
package com.zlologin.zlologin.util;

import com.zlologin.zlologin.model.Role;
import com.zlologin.zlologin.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest  // Carrega o contexto do Spring Boot
@ActiveProfiles("test")  // Perfil de teste
public class JwtTokenProviderTest {

    private static final String JWT_SECRET = "9a74b83a56c54a8e1a65b8ab6d5c3f35a2f9873a4c65723a98f74a5e5e983a47a39c456d8a4f53d9e8b56473b8a29d6a";
    private static final long JWT_EXPIRATION_MS = 86400000; // 1 dia em milissegundos

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Testa a geração de um token JWT e verifica se o token é criado corretamente.
     */
    @Test
    public void whenGenerateToken_thenTokenIsCreated() {
        // Arrange
        User user = new User();
        user.setEmail("cuidador@gmail.com");
        user.setRole(Role.CUIDADOR);  // Usando "CUIDADOR" diretamente do enum

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token);
        Claims claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();

        // Valida que o subject contém o User completo
        assertEquals(user.toString(), claims.getSubject());

        // Valida as roles no claim "roles"
        assertEquals("CUIDADOR", claims.get("roles"));
    }

    /**
     * Testa a validação de um token JWT válido.
     */
    @Test
    public void whenValidToken_thenValidationSucceeds() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("cuidador@gmail.com")
                .claim("roles", "CUIDADOR")  // Ajustando para "CUIDADOR" conforme no enum
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    /**
     * Testa a validação de um token JWT expirado.
     */
    @Test
    public void whenExpiredToken_thenValidationFails() {
        // Arrange
        String expiredToken = Jwts.builder()
                .setSubject("cuidador@gmail.com")
                .claim("roles", "CUIDADOR")
                .setIssuedAt(new Date(System.currentTimeMillis() - JWT_EXPIRATION_MS * 2)) // Emissão antiga
                .setExpiration(new Date(System.currentTimeMillis() - JWT_EXPIRATION_MS))  // Expiração no passado
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    /**
     * Testa a extração do e-mail do token JWT.
     */
    @Test
    public void whenGetUserEmailFromToken_thenCorrectEmailIsReturned() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("cuidador@gmail.com")
                .claim("roles", "CUIDADOR")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

        // Act
        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // Assert
        assertEquals("cuidador@gmail.com", email);
    }
}
