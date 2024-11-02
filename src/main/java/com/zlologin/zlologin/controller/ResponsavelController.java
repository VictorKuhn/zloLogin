package com.zlologin.zlologin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/responsavel")
public class ResponsavelController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> responsavelDashboard() {
        return ResponseEntity.ok("Seja bem-vindo ao dashboard do responsável!");
    }
}
