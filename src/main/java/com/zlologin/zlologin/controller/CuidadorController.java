package com.zlologin.zlologin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuidador")
public class CuidadorController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> cuidadorDashboard() {
        return ResponseEntity.ok("Seja bem-vindo ao dashboard do cuidador!");
    }
}
