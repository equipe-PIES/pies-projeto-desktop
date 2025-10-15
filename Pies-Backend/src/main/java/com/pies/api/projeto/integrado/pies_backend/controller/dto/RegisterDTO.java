package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import com.pies.api.projeto.integrado.pies_backend.model.UserRole;

public record RegisterDTO(String login, String password, UserRole role){

}
