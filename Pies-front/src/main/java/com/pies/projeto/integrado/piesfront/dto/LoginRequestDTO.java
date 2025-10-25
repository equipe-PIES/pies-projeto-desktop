package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para requisição de login
 * Representa os dados enviados para o endpoint /auth/login
 */
public record LoginRequestDTO(
    String login,    // Email do usuário
    String password  // Senha do usuário
) {}
