package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para informações do usuário
 * Representa a resposta do endpoint /auth/me
 */
public record UserInfoDTO(
    String id,      // ID do usuário (String para corresponder ao backend)
    String name,    // Nome do usuário
    String email,   // Email do usuário
    String role     // Role/papel do usuário (PROFESSOR, COORDENADOR, etc.)
) {}
