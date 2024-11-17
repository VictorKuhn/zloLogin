package com.zlologin.zlologin.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    @InjectMocks
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException accessDeniedException;

    @Test
    void testHandleAccessDenied() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Simula um StringWriter para capturar a saída de resposta
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        // Configura o comportamento do response
        when(response.getWriter()).thenReturn(printWriter);

        // Executa o método handle
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Verifica se o status foi definido como 403
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Verifica se o content type foi definido como "application/json"
        verify(response).setContentType("application/json");

        // Verifica se a mensagem de erro foi escrita corretamente
        printWriter.flush();
        assertEquals("{\"error\": \"Acesso não autorizado\"}", stringWriter.toString());
    }
}
