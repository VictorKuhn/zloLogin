package com.zlologin.zlologin.controller;

import com.zlologin.zlologin.model.TempUser;
import com.zlologin.zlologin.repository.TempUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TempUserController {

    private final TempUserRepository tempUserRepository;

    public TempUserController(TempUserRepository tempUserRepository) {
        this.tempUserRepository = tempUserRepository;
    }

    // Endpoint 1: Retorna todos os registros da tabela temp_users
    @GetMapping("/temp-users")
    public ResponseEntity<List<TempUser>> getAllTempUsers() {
        List<TempUser> tempUsers = tempUserRepository.findAll();
        return ResponseEntity.ok(tempUsers);
    }

    // Endpoint 2: Limpa todos os registros da tabela temp_users
    @DeleteMapping("/temp-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllTempUsers() {
        tempUserRepository.deleteAll();
        return ResponseEntity.ok("Todos os registros de usuários temporários foram removidos com sucesso.");
    }
}
