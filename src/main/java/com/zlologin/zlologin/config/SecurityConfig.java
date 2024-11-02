package com.zlologin.zlologin.config;

import com.zlologin.zlologin.util.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desabilita CSRF para APIs REST
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticação estão liberados para todos
                        .requestMatchers("/auth/**").permitAll()

                        // Apenas usuários com a role "CUIDADOR" podem acessar os endpoints /api/cuidador/**
                        .requestMatchers("/api/cuidador/**").hasRole("CUIDADOR")

                        // Apenas usuários com a role "RESPONSAVEL" podem acessar os endpoints /api/responsavel/**
                        .requestMatchers("/api/responsavel/**").hasRole("RESPONSAVEL")

                        // ADMIN pode acessar todos os endpoints
                        .requestMatchers("/admin/**", "/api/**").hasRole("ADMIN")

                        // Qualquer outro endpoint precisa de autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Adiciona o filtro JWT antes da autenticação padrão

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}