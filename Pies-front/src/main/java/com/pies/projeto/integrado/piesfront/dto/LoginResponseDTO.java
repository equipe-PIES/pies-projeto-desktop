package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para resposta de login
 * Representa a resposta do endpoint /auth/login
 */
public record LoginResponseDTO(
    String token  // Token JWT retornado pelo backend
) {}
