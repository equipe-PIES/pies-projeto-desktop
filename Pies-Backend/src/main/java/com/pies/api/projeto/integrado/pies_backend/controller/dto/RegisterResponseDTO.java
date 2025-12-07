package com.pies.api.projeto.integrado.pies_backend.controller.dto;

/**
 * DTO para resposta de registro de usuário
 * Retorna o ID do usuário criado após registro bem-sucedido
 */
public record RegisterResponseDTO(
    String userId  // ID do usuário criado
) {}

