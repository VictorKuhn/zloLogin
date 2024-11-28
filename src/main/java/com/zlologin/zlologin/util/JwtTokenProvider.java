package com.zlologin.zlologin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private static final String CLAIM_ROLES = "roles";

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") long jwtExpirationMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // Gera o token JWT padrão para autenticação com expiração padrão
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(email)
                .claim(CLAIM_ROLES, roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Método existente para gerar um token JWT com expiração personalizada (intacto)
    public String generateTokenWithExpiry(String email, int expiryMinutes) {
        long expiryInMillis = expiryMinutes * 60 * 1000L;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryInMillis))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Novo método para gerar um token JWT a um usuário temporário
    public String generateTempUserToken(String email, int expiryMinutes) {
        long expiryInMillis = expiryMinutes * 60 * 1000L;
        return Jwts.builder()
                .setSubject(email) // Email do usuário
                .claim(CLAIM_ROLES, "ROLE_TEMPUSER") // Role fixa para usuários temporários
                .setIssuedAt(new Date()) // Data de emissão
                .setExpiration(new Date(System.currentTimeMillis() + expiryInMillis)) // Data de expiração
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Assina o token
                .compact();
    }

    // Valida o token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // Extrai o e-mail do token
    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // Extrai as roles do token
    public String getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.get(CLAIM_ROLES, String.class);
    }
}
