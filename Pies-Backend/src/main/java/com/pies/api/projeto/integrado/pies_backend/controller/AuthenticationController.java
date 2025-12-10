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
import com.pies.api.projeto.integrado.pies_backend.model.Enums.UserRole;
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
        var token = tokenService.generateToken((User) auth.getPrincipal());
        
        // Retorna o token JWT para o cliente
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // Endpoint para registrar novos usuários
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data){
        
        try {
            System.out.println("=== DEBUG REGISTER ===");
            System.out.println("Login recebido: " + data.login());
            System.out.println("Role recebida: " + data.role());
            
            // Verifica se o email já existe no banco de dados
            User existingUser = this.repository.findByEmail(data.login());
            if(existingUser != null) {
                System.out.println("Usuario ja existe!");
                return ResponseEntity.badRequest().build(); // Retorna erro 400 se email já existe
            }

            // Criptografa a senha usando BCrypt antes de salvar no banco
            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password()); 
            System.out.println("Senha criptografada");

            // Define role padrão se vier null
            UserRole userRole = (data.role() != null) ? data.role() : UserRole.USER;
            System.out.println("Role a ser usada: " + userRole.getRole());

            // Cria um novo usuário com os dados fornecidos
            User newUser = new User();
            newUser.setEmail(data.login());
            newUser.setPassword(encryptedPassword);
            newUser.setRole(userRole);
            newUser.setName(data.login().split("@")[0]); // Define o nome como parte do email antes do @
            
            System.out.println("Usuario criado: " + newUser.getEmail() + " | Name: " + newUser.getName() + " | Role: " + userRole.getRole());

            // Salva o novo usuário no banco de dados
            this.repository.save(newUser);
            System.out.println("Usuario salvo com sucesso!");

            // Retorna sucesso (status 200) se o registro foi bem-sucedido
            return ResponseEntity.ok().build();
            
        } catch(Exception e) {
            System.err.println("ERRO NO REGISTRO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Endpoint para obter informações do usuário autenticado
     * Retorna id, email, nome e role do usuário logado
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getAuthenticatedUser() {
        try {
            // Obtém a autenticação do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                System.err.println("/auth/me: Usuario nao autenticado");
                return ResponseEntity.status(401).build();
            }
            
            // Obtém o usuário autenticado
            User user = (User) authentication.getPrincipal();
            
            System.out.println("=== /auth/me DEBUG ===");
            System.out.println("User: " + user.getEmail());
            System.out.println("Role enum name: " + user.getRole().name());
            System.out.println("Role value: " + user.getRole().getRole());
            
            // Cria o DTO com as informações do usuário
            // Usa o nome do enum (COORDENADOR) em vez do valor (coordenador)
            UserInfoDTO userInfo = new UserInfoDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name() // Usa name() em vez de getRole()
            );
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            System.err.println("ERRO NO /auth/me: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
