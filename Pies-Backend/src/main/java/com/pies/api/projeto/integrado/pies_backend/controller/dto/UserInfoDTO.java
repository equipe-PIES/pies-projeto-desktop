package com.pies.api.projeto.integrado.pies_backend.controller.dto;

/**
 * DTO (Data Transfer Object) para transferir informações do usuário logado.
 * Este DTO é usado no endpoint /auth/me para retornar dados do usuário autenticado
 * sem expor informações sensíveis como a senha.
 * 
 * @param id Identificador único do usuário
 * @param name Nome completo do usuário
 * @param email Email do usuário (usado como login)
 * @param role Role do usuário (professor, coordenador, admin, user)
 */
public record UserInfoDTO(String id, String name, String email, String role) {
}
