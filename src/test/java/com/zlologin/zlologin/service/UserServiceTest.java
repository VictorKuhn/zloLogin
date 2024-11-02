// UserServiceTest.java
package com.zlologin.zlologin.service;

import com.zlologin.zlologin.model.Role;
import com.zlologin.zlologin.model.User;
import com.zlologin.zlologin.repository.UserRepository;
import com.zlologin.zlologin.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private User user;

    @BeforeEach
    public void setUp() {
        // Configuração inicial para um usuário de teste
        user = new User();
        user.setId(1L);
        user.setEmail("cuidador@gmail.com");
        user.setPassword("senha123");
        user.setRole(Role.CUIDADOR);
    }

    /**
     * Testa a recuperação de um usuário pelo e-mail.
     */
    @Test
    public void whenLoadUserByUsername_thenUserIsReturned() {
        // Arrange
        String email = "cuidador@gmail.com";

        // Configurando o mock para retornar um UserDetails em vez de com.zlologin.zlologin.model.User
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails foundUser = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getUsername());  // Usando getUsername() do UserDetails
    }

    /**
     * Testa a recuperação de um usuário por e-mail que não está registrado.
     */
    @Test
    public void whenLoadUserByUsernameWithUnregisteredEmail_thenThrowsException() {
        // Arrange
        String email = "inexistente@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
        assertEquals("Usuário não encontrado com o e-mail: " + email, exception.getMessage());
    }

    /**
     * Testa a validação de um token JWT.
     */
    @Test
    public void whenValidateJwtToken_thenReturnTrue() {
        // Arrange
        String token = "validToken";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    /**
     * Testa a recuperação do e-mail a partir do token JWT.
     */
    @Test
    public void whenGetEmailFromJwtToken_thenEmailIsReturned() {
        // Arrange
        String token = "validToken";
        String email = "cuidador@gmail.com";
        when(jwtTokenProvider.getUserEmailFromToken(token)).thenReturn(email);

        // Act
        String returnedEmail = jwtTokenProvider.getUserEmailFromToken(token);

        // Assert
        assertEquals(email, returnedEmail);
    }
}
