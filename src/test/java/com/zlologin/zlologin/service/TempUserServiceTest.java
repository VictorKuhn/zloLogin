package com.zlologin.zlologin.service;

import com.zlologin.zlologin.model.TempUser;
import com.zlologin.zlologin.repository.TempUserRepository;
import com.zlologin.zlologin.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TempUserServiceTest {

    private TempUserRepository tempUserRepository;
    private JwtTokenProvider jwtTokenProvider;
    private TempUserService tempUserService;

    @BeforeEach
    void setUp() {
        tempUserRepository = mock(TempUserRepository.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        tempUserService = new TempUserService(tempUserRepository, jwtTokenProvider);
    }

    @Test
    void createTempUser_ShouldCreateNewUser_WhenNoExistingUser() {
        // Arrange
        String email = "tempuser@example.com";
        String phoneNumber = "555199999999";
        String token = "mock-token";
        when(tempUserRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtTokenProvider.generateTempUserToken(email, 15)).thenReturn(token);

        // Act
        String result = tempUserService.createTempUser(email, phoneNumber);

        // Assert
        assertEquals(token, result);
        verify(tempUserRepository, times(1)).save(any(TempUser.class));
    }

    @Test
    void createTempUser_ShouldUpdateExistingUser_WhenTokenIsValid() {
        // Arrange
        String email = "tempuser@example.com";
        String phoneNumber = "555199999999";
        String token = "new-mock-token";
        TempUser existingUser = new TempUser(1L, email, phoneNumber, "old-token", LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(5), "ROLE_TEMPUSER");

        when(tempUserRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.generateTempUserToken(email, 15)).thenReturn(token);

        // Act
        String result = tempUserService.createTempUser(email, phoneNumber);

        // Assert
        assertEquals(token, result);
        assertEquals("ROLE_TEMPUSER", existingUser.getRole());
        verify(tempUserRepository, times(1)).save(existingUser);
    }

    @Test
    void createTempUser_ShouldCreateNewRecord_WhenTokenIsExpired() {
        // Arrange
        String email = "tempuser@example.com";
        String phoneNumber = "555199999999";
        String token = "new-mock-token";
        TempUser expiredUser = new TempUser(1L, email, phoneNumber, "old-token", LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30), "ROLE_TEMPUSER");

        when(tempUserRepository.findByEmail(email)).thenReturn(Optional.of(expiredUser));
        when(jwtTokenProvider.generateTempUserToken(email, 15)).thenReturn(token);

        // Act
        String result = tempUserService.createTempUser(email, phoneNumber);

        // Assert
        assertEquals(token, result);
        verify(tempUserRepository, times(1)).save(any(TempUser.class));
    }
}
