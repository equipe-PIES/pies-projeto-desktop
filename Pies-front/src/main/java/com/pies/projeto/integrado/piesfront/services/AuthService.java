package com.pies.projeto.integrado.piesfront.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pies.projeto.integrado.piesfront.dto.LoginRequestDTO;
import com.pies.projeto.integrado.piesfront.dto.LoginResponseDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Serviço responsável por fazer chamadas HTTP para o backend Spring Boot
 * e gerenciar a autenticação do usuário.
 */
public class AuthService {
    
    private static final String BASE_URL = "http://localhost:8080"; // URL do seu backend
    private static final String LOGIN_ENDPOINT = "/auth/login";
    private static final String USER_INFO_ENDPOINT = "/auth/me";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String currentToken; // Armazena o token JWT atual
    
    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Faz login no sistema chamando o endpoint /auth/login do backend
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return String com a role do usuário ou "INVÁLIDO" se falhar
     */
    public String authenticate(String email, String password) {
        try {
            // Cria o objeto de requisição
            LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            
            // Monta a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + LOGIN_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            // Envia a requisição
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            // Verifica se o login foi bem-sucedido
            if (response.statusCode() == 200) {
                // Parse da resposta para obter o token
                LoginResponseDTO loginResponse = objectMapper.readValue(
                        response.body(), LoginResponseDTO.class);
                
                // Armazena o token para uso posterior
                this.currentToken = loginResponse.token();
                
                // Busca as informações do usuário para obter a role
                return getUserRole();
            } else {
                return "INVÁLIDO";
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao fazer login: " + e.getMessage());
            return "INVÁLIDO";
        }
    }
    
    /**
     * Busca as informações do usuário logado para obter a role
     * @return String com a role do usuário ou "INVÁLIDO" se falhar
     */
    private String getUserRole() {
        if (currentToken == null) {
            return "INVÁLIDO";
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + USER_INFO_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                UserInfoDTO userInfo = objectMapper.readValue(
                        response.body(), UserInfoDTO.class);
                return userInfo.role();
            } else {
                return "INVÁLIDO";
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar informações do usuário: " + e.getMessage());
            return "INVÁLIDO";
        }
    }
    
    /**
     * Retorna o token JWT atual (se houver)
     * @return Token JWT ou null se não estiver logado
     */
    public String getCurrentToken() {
        return currentToken;
    }
    
    /**
     * Limpa o token atual (logout)
     */
    public void logout() {
        this.currentToken = null;
    }
}
