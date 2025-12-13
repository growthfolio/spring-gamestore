package com.energygames.lojadegames.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Customiza a resposta quando um usuário não autenticado tenta acessar um recurso protegido.
 * Retorna JSON com mensagem explicativa ao invés do erro padrão do Spring Security.
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("error", "Não Autorizado");
        errorDetails.put("message", "Token JWT inválido, expirado ou ausente. Use o endpoint /usuarios/logar para obter um novo token.");
        errorDetails.put("path", request.getServletPath());
        
        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
}
