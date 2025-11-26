package com.pies.projeto.integrado.piesfront.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.LoginRequestDTO;
import com.pies.projeto.integrado.piesfront.dto.LoginResponseDTO;
import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por fazer chamadas HTTP para o backend Spring Boot
 * e gerenciar a autenticação do usuário.
 * Implementado como singleton para compartilhar o estado de autenticação entre controllers.
 */
public class AuthService {
    
    private static AuthService instance; // Instância singleton para compartilhar o estado de autenticação entre controllers.
    
    private static final String BASE_URL = "http://localhost:8080"; // URL do seu backend
    private static final String LOGIN_ENDPOINT = "/auth/login";
    private static final String USER_INFO_ENDPOINT = "/auth/me";
    private static final String TURMAS_ENDPOINT = "/turmas";
    private static final String EDUCANDOS_ENDPOINT = "/api/educandos";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String currentToken; // Armazena o token JWT atual
    
    /**
     * Construtor privado para implementar o padrão singleton
     */
    private AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Obtém a instância singleton do AuthService
     * @return Instância única do AuthService
     */
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Faz login no sistema chamando o endpoint /auth/login do backend
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return String com a role do usuário, "INVÁLIDO" se credenciais incorretas, ou "ERRO_CONEXAO" se houver problema de conexão
     */
    public String authenticate(String email, String password) {
        try {
            System.out.println("=== DEBUG AUTH SERVICE ===");
            System.out.println("Email: " + email);
            System.out.println("URL: " + BASE_URL + LOGIN_ENDPOINT);
            
            // Cria o objeto de requisição
            LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            System.out.println("Request Body: " + requestBody);
            
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
            
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            
            // Verifica se o login foi bem-sucedido
            if (response.statusCode() == 200) {
                // Parse da resposta para obter o token
                LoginResponseDTO loginResponse = objectMapper.readValue(
                        response.body(), LoginResponseDTO.class);
                
                // Armazena o token para uso posterior
                this.currentToken = loginResponse.token();
                System.out.println("Token armazenado: " + currentToken.substring(0, 30) + "...");
                
                // Busca as informações do usuário para obter a role
                String role = getUserRole();
                System.out.println("Role retornada: " + role);
                return role;
            } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                System.err.println("Login falhou - credenciais inválidas");
                return "INVÁLIDO";
            } else {
                System.err.println("Login falhou com status: " + response.statusCode());
                return "ERRO_CONEXAO";
            }
            
        } catch (java.net.ConnectException e) {
            System.err.println("ERRO DE CONEXÃO: Não foi possível conectar ao servidor backend.");
            System.err.println("Verifique se o backend está rodando em " + BASE_URL);
            e.printStackTrace();
            return "ERRO_CONEXAO";
        } catch (IOException | InterruptedException e) {
            System.err.println("EXCEÇÃO ao fazer login: " + e.getMessage());
            e.printStackTrace();
            return "ERRO_CONEXAO";
        }
    }
    
    /**
     * Busca as informações do usuário logado para obter a role
     * @return String com a role do usuário ou "INVÁLIDO" se falhar
     */
    private String getUserRole() {
        if (currentToken == null) {
            System.err.println("getUserRole: Token é NULL!");
            return "INVÁLIDO";
        }
        
        try {
            System.out.println("=== DEBUG GET USER ROLE ===");
            System.out.println("URL: " + BASE_URL + USER_INFO_ENDPOINT);
            System.out.println("Token: " + currentToken.substring(0, 30) + "...");
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + USER_INFO_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            
            if (response.statusCode() == 200) {
                UserInfoDTO userInfo = objectMapper.readValue(
                        response.body(), UserInfoDTO.class);
                System.out.println("UserInfo parsed - role: " + userInfo.role());
                return userInfo.role();
            } else {
                System.err.println("getUserRole falhou com status: " + response.statusCode());
                return "INVÁLIDO";
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("EXCEÇÃO ao buscar informações do usuário: " + e.getMessage());
            e.printStackTrace();
            return "INVÁLIDO";
        }
    }
    
    /**
     * Busca as informações completas do usuário logado
     * @return UserInfoDTO com as informações do usuário ou null se falhar
     */
    public UserInfoDTO getUserInfo() {
        if (currentToken == null) {
            return null;
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
                return objectMapper.readValue(
                        response.body(), UserInfoDTO.class);
            } else {
                System.err.println("Erro ao buscar informações do usuário. Status: " + response.statusCode());
                return null;
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar informações do usuário: " + e.getMessage());
            return null;
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
    
    /**
     * Busca todas as turmas do backend
     * @return Lista de turmas ou lista vazia se falhar
     */
    public List<TurmaDTO> getTurmas() {
        if (currentToken == null) {
            System.err.println("getTurmas: Token é NULL!");
            return new ArrayList<>();
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + TURMAS_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                TypeReference<List<TurmaDTO>> typeRef = new TypeReference<List<TurmaDTO>>() {};
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                System.err.println("Erro ao buscar turmas. Status: " + response.statusCode());
                return new ArrayList<>();
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar turmas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca uma turma específica pelo ID
     * @param turmaId ID da turma
     * @return TurmaDTO ou null se não encontrada
     */
    public TurmaDTO getTurmaById(String turmaId) {
        if (currentToken == null || turmaId == null) {
            System.err.println("getTurmaById: Token ou ID é NULL!");
            return null;
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + TURMAS_ENDPOINT + "/" + turmaId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), TurmaDTO.class);
            } else {
                System.err.println("Erro ao buscar turma. Status: " + response.statusCode());
                return null;
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar turma: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Busca todos os educandos do backend
     * @return Lista de educandos ou lista vazia se falhar
     */
    public List<EducandoDTO> getEducandos() {
        if (currentToken == null) {
            System.err.println("getEducandos: Token é NULL!");
            return new ArrayList<>();
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + EDUCANDOS_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                TypeReference<List<EducandoDTO>> typeRef = new TypeReference<List<EducandoDTO>>() {};
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                System.err.println("Erro ao buscar educandos. Status: " + response.statusCode());
                return new ArrayList<>();
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar educandos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca o ID do professor pelo nome do usuário logado
     * @param nomeUsuario Nome do usuário logado
     * @return ID do professor ou null se não encontrado
     */
    public String getProfessorIdByNome(String nomeUsuario) {
        if (currentToken == null || nomeUsuario == null) {
            System.err.println("getProfessorIdByNome: Token ou nome é NULL!");
            return null;
        }
        
        try {
            // Usa o novo endpoint /professores/me que retorna os dados do professor logado
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/professores/me"))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse para um Map genérico
                var professor = objectMapper.readValue(response.body(), 
                    new TypeReference<java.util.Map<String, Object>>() {});
                
                return (String) professor.get("id");
            } else {
                System.err.println("Erro ao buscar professor logado. Status: " + response.statusCode());
                return null;
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar professor por nome: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
