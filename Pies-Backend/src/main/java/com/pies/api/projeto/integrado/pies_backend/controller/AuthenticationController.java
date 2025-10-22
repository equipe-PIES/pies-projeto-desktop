package com.pies.api.projeto.integrado.pies_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.AuthenticationDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.LoginResponseDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.RegisterDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.UserInfoDTO;
import com.pies.api.projeto.integrado.pies_backend.infra.security.TokenService;
import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;

import jakarta.validation.Valid;

@RestController // Indica que é um controller REST que retorna JSON
@RequestMapping("auth") // Mapeia todas as rotas deste controller para /auth

public class AuthenticationController { // Controller responsável pela autenticação (login e registro)
    
    @Autowired
    private AuthenticationManager authenticationManager; // Gerenciador de autenticação do Spring Security

    @Autowired
    private UserRepository repository; // Repositório para operações com usuários no banco

    @Autowired
    private TokenService tokenService; // Serviço para gerar tokens JWT

    // Endpoint para fazer login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data){
        
        // Cria um token de autenticação com email e senha
        var usernamepassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        
        // Autentica o usuário (verifica se email e senha estão corretos)
        var auth = this.authenticationManager.authenticate(usernamepassword);
        
        // Gera um token JWT para o usuário autenticado
        var token = tokenService.gererateToken((User) auth.getPrincipal());
        
        // Retorna o token JWT para o cliente
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // Endpoint para registrar novos usuários
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data){
        
        // Verifica se o email já existe no banco de dados
        if(this.repository.findByEmail(data.login()) != null) {
            return ResponseEntity.badRequest().build(); // Retorna erro 400 se email já existe
        }

        // Criptografa a senha usando BCrypt antes de salvar no banco
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password()); 

        // Cria um novo usuário com os dados fornecidos
        User newUser = new User(data.login(), encryptedPassword, data.role());

        // Salva o novo usuário no banco de dados
        this.repository.save(newUser);

        // Retorna sucesso (status 200) se o registro foi bem-sucedido
        return ResponseEntity.ok().build();
    }

    // Endpoint para obter informações do usuário logado
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser() {
        // Obtém o usuário autenticado do contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        // Retorna as informações do usuário
        return ResponseEntity.ok(new UserInfoDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }

}
