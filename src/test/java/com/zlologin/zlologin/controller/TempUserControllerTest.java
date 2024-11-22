package com.zlologin.zlologin.controller;

import com.zlologin.zlologin.model.TempUser;
import com.zlologin.zlologin.repository.TempUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TempUserControllerTest {

    @InjectMocks
    private TempUserController tempUserController;

    @Mock
    private TempUserRepository tempUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTempUsers_ShouldReturnListOfTempUsers() {
        // Arrange
        List<TempUser> mockUsers = new ArrayList<>();
        mockUsers.add(new TempUser(1L, "user1@example.com", "555199999999", "token1",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), "ROLE_TEMPUSER"));
        mockUsers.add(new TempUser(2L, "user2@example.com", "555199999998", "token2",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), "ROLE_TEMPUSER"));

        when(tempUserRepository.findAll()).thenReturn(mockUsers);

        // Act
        ResponseEntity<List<TempUser>> response = tempUserController.getAllTempUsers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(tempUserRepository, times(1)).findAll();
    }

    @Test
    void getAllTempUsers_ShouldReturnEmptyList_WhenNoTempUsersExist() {
        // Arrange
        when(tempUserRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<TempUser>> response = tempUserController.getAllTempUsers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().size());
        verify(tempUserRepository, times(1)).findAll();
    }

    @Test
    void deleteAllTempUsers_ShouldDeleteAllUsers_WhenRoleIsAdmin() {
        // Act
        ResponseEntity<String> response = tempUserController.deleteAllTempUsers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Todos os registros de usuários temporários foram removidos com sucesso.", response.getBody());
        verify(tempUserRepository, times(1)).deleteAll();
    }
}
