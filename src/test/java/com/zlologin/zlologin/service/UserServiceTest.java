package com.zlologin.zlologin.service;

import com.zlologin.zlologin.dto.RegisterDTO;
import com.zlologin.zlologin.model.User;
import com.zlologin.zlologin.model.Role;
import com.zlologin.zlologin.repository.UserRepository;
import com.zlologin.zlologin.util.JwtTokenProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserSuccess() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPassword("password");
        registerDTO.setRole("CUIDADOR");

        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(registerDTO);

        assertNotNull(result);
        assertEquals(registerDTO.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.CUIDADOR, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserEmailAlreadyRegistered() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("existinguser@example.com");
        registerDTO.setPassword("password");
        registerDTO.setRole("CUIDADOR");

        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerDTO));
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.CUIDADOR);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_CUIDADOR")));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void testSendPasswordResetTokenSuccess() throws MessagingException {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateTokenWithExpiry(email, 15)).thenReturn("mockToken");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        userService.sendPasswordResetToken(email);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendPasswordResetTokenUserNotFound() {
        String email = "unknown@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.sendPasswordResetToken(email));
    }

    @Test
    void testResetPasswordSuccess() {
        String token = "validToken";
        String email = "user@example.com";
        String newPassword = "newPassword";
        User user = new User();
        user.setEmail(email);

        when(jwtTokenProvider.getUserEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        userService.resetPassword(token, newPassword);

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testResetPasswordInvalidToken() {
        String token = "invalidToken";
        when(jwtTokenProvider.getUserEmailFromToken(token)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword(token, "newPassword"));
    }
}
