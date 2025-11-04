package com.pies.api.projeto.integrado.pies_backend.controller.dto;

/**
 * DTO para resposta de login
 * Retorna o token JWT após autenticação bem-sucedida
 */
public record LoginResponseDTO(
    String token  // Token JWT gerado
) {}
