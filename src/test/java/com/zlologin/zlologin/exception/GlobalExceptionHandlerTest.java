package com.zlologin.zlologin.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleIllegalArgumentException() {
        // Simula a exceção com uma mensagem específica
        IllegalArgumentException exception = new IllegalArgumentException("E-mail já registrado!");

        // Chama o método de tratamento de exceção
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Verifica se o status e o corpo da resposta estão corretos
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-mail já registrado!", response.getBody());
    }
}
