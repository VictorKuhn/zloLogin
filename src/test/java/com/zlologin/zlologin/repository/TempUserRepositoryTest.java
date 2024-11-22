package com.zlologin.zlologin.repository;

import com.zlologin.zlologin.model.TempUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TempUserRepositoryTest {

    @Mock
    private TempUserRepository tempUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        String email = "tempuser@example.com";
        TempUser tempUser = new TempUser();
        tempUser.setId(1L);
        tempUser.setEmail(email);
        tempUser.setPhoneNumber("555199999999");
        tempUser.setJwtToken("mock-token");
        tempUser.setCreatedAt(LocalDateTime.now());
        tempUser.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tempUser.setRole("ROLE_TEMPUSER");

        when(tempUserRepository.findByEmail(email)).thenReturn(Optional.of(tempUser));

        // Act
        Optional<TempUser> result = tempUserRepository.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(tempUserRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        when(tempUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<TempUser> result = tempUserRepository.findByEmail(email);

        // Assert
        assertTrue(result.isEmpty());
        verify(tempUserRepository, times(1)).findByEmail(email);
    }

    @Test
    void save_ShouldPersistTempUser() {
        // Arrange
        TempUser tempUser = new TempUser();
        tempUser.setId(1L);
        tempUser.setEmail("tempuser@example.com");
        tempUser.setPhoneNumber("555199999999");
        tempUser.setJwtToken("mock-token");
        tempUser.setCreatedAt(LocalDateTime.now());
        tempUser.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tempUser.setRole("ROLE_TEMPUSER");

        when(tempUserRepository.save(tempUser)).thenReturn(tempUser);

        // Act
        TempUser result = tempUserRepository.save(tempUser);

        // Assert
        assertNotNull(result);
        assertEquals("tempuser@example.com", result.getEmail());
        verify(tempUserRepository, times(1)).save(tempUser);
    }
}
