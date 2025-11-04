package com.pies.api.projeto.integrado.pies_backend.controller.dto;

/**
 * DTO para requisição de autenticação (login)
 * Recebe as credenciais do usuário
 */
public record AuthenticationDTO(
    String login,    // Email do usuário
    String password  // Senha do usuário
) {}
