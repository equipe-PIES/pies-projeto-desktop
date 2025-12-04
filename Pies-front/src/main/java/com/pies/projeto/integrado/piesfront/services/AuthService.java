package com.pies.projeto.integrado.piesfront.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pies.projeto.integrado.piesfront.dto.AnamneseDTO;
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
    private static final String EDUCANDOS_SIMPLIFICADOS_ENDPOINT = "/api/educandos/simplificados";
    private static final String ANAMNESES_ENDPOINT = "/api/anamneses";
    private static final String RELATORIOS_INDIVIDUAIS_ENDPOINT = "/api/relatorios-individuais";
    private static final String PAEES_ENDPOINT = "/api/paees";
    private static final String PDIS_ENDPOINT = "/api/pdis";
    private static final String DIAGNOSTICOS_INICIAIS_ENDPOINT = "/api/diagnosticos-iniciais";
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String currentToken; // Armazena o token JWT atual
    private UserInfoDTO cachedUserInfo;
    private java.util.List<TurmaDTO> cachedTurmas;
    private long turmasCacheTs;
    private final java.util.Map<String, java.util.List<EducandoDTO>> cachedEducandosPorTurma = new java.util.HashMap<>();
    private final java.util.Map<String, Long> educandosPorTurmaCacheTs = new java.util.HashMap<>();
    
    private java.util.List<EducandoDTO> cachedEducandos;
    private long educandosCacheTs;
    private java.util.List<com.pies.projeto.integrado.piesfront.dto.ProfessorDTO> cachedProfessores;
    private long professoresCacheTs;
    
    /**
     * Construtor privado para implementar o padrão singleton
     */
    private AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
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
        if (cachedUserInfo != null) {
            return cachedUserInfo;
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
                cachedUserInfo = objectMapper.readValue(
                        response.body(), UserInfoDTO.class);
                return cachedUserInfo;
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
        this.cachedUserInfo = null;
        this.cachedTurmas = null;
        this.cachedEducandos = null;
        this.cachedEducandosPorTurma.clear();
        this.educandosPorTurmaCacheTs.clear();
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
        long now = System.currentTimeMillis();
        if (cachedTurmas != null && (now - turmasCacheTs) < 10_000) {
            return cachedTurmas;
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
                cachedTurmas = objectMapper.readValue(response.body(), typeRef);
                turmasCacheTs = now;
                return cachedTurmas;
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

    public java.util.List<com.pies.projeto.integrado.piesfront.dto.ProfessorDTO> getProfessores() {
        if (currentToken == null) {
            return new java.util.ArrayList<>();
        }
        long now = System.currentTimeMillis();
        if (cachedProfessores != null && (now - professoresCacheTs) < 10000) {
            return cachedProfessores;
        }
        try {
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/professores"))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();
            java.net.http.HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.pies.projeto.integrado.piesfront.dto.ProfessorDTO>> typeRef =
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.pies.projeto.integrado.piesfront.dto.ProfessorDTO>>() {};
                cachedProfessores = objectMapper.readValue(response.body(), typeRef);
                professoresCacheTs = now;
                return cachedProfessores;
            } else {
                return new java.util.ArrayList<>();
            }
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            return new java.util.ArrayList<>();
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
        long now = System.currentTimeMillis();
        if (cachedEducandos != null && (now - educandosCacheTs) < 60000) {
            return cachedEducandos;
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
                cachedEducandos = objectMapper.readValue(response.body(), typeRef);
                educandosCacheTs = now;
                return cachedEducandos;
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

    public List<EducandoDTO> getEducandosSimplificados() {
        if (currentToken == null) {
            System.err.println("getEducandosSimplificados: Token é NULL!");
            return new ArrayList<>();
        }
        long now = System.currentTimeMillis();
        if (cachedEducandos != null && (now - educandosCacheTs) < 60000) {
            return cachedEducandos;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + EDUCANDOS_SIMPLIFICADOS_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                TypeReference<List<EducandoDTO>> typeRef = new TypeReference<List<EducandoDTO>>() {};
                cachedEducandos = objectMapper.readValue(response.body(), typeRef);
                educandosCacheTs = now;
                return cachedEducandos;
            } else {
                System.err.println("Erro ao buscar educandos simplificados. Status: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar educandos simplificados: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public java.util.List<EducandoDTO> getEducandosPorTurma(String turmaId) {
        if (currentToken == null || turmaId == null || turmaId.isEmpty()) {
            return new ArrayList<>();
        }
        long now = System.currentTimeMillis();
        java.util.List<EducandoDTO> cache = cachedEducandosPorTurma.get(turmaId);
        Long ts = educandosPorTurmaCacheTs.get(turmaId);
        if (cache != null && ts != null && (now - ts) < 60_000) {
            return cache;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + EDUCANDOS_ENDPOINT + "/turma/" + turmaId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                TypeReference<List<EducandoDTO>> typeRef = new TypeReference<List<EducandoDTO>>() {};
                java.util.List<EducandoDTO> lista = objectMapper.readValue(response.body(), typeRef);
                cachedEducandosPorTurma.put(turmaId, lista);
                educandosPorTurmaCacheTs.put(turmaId, now);
                return lista;
            } else {
                System.err.println("Erro ao buscar educandos por turma. Status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar educandos por turma: " + e.getMessage());
        }
        return new ArrayList<>();
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

    public String getProfessorId() {
        if (currentToken == null) {
            System.err.println("getProfessorId: Token é NULL!");
            return null;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/professores/me"))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                var professor = objectMapper.readValue(response.body(),
                        new TypeReference<java.util.Map<String, Object>>() {});
                return (String) professor.get("id");
            } else {
                System.err.println("Erro ao buscar professor logado. Status: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar professor: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Cria uma anamnese para um educando
     * @param educandoId ID do educando
     * @param anamneseDTO Dados da anamnese
     * @return AnamneseDTO criada ou null se falhar
     */
    public AnamneseDTO criarAnamnese(String educandoId, AnamneseDTO anamneseDTO) {
        if (currentToken == null || educandoId == null || anamneseDTO == null) {
            System.err.println("criarAnamnese: Token, educandoId ou anamneseDTO é NULL!");
            return null;
        }
        
        try {
            String requestBody = objectMapper.writeValueAsString(anamneseDTO);
            System.out.println("=== DEBUG CREATE ANAMNESE ===");
            System.out.println("URL: " + BASE_URL + ANAMNESES_ENDPOINT + "/educando/" + educandoId);
            System.out.println("Request Body: " + requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESES_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), AnamneseDTO.class);
            } else {
                System.err.println("Erro ao criar anamnese. Status: " + response.statusCode());
                return null;
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao criar anamnese: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Busca a anamnese de um educando
     * @param educandoId ID do educando
     * @return AnamneseDTO ou null se não encontrada
     */
    public AnamneseDTO getAnamnesePorEducando(String educandoId) {
        if (currentToken == null || educandoId == null) {
            System.err.println("getAnamnesePorEducando: Token ou educandoId é NULL!");
            return null;
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESES_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), AnamneseDTO.class);
            } else if (response.statusCode() == 404) {
                System.out.println("Anamnese não encontrada para o educando: " + educandoId);
                return null;
            } else {
                System.err.println("Erro ao buscar anamnese. Status: " + response.statusCode());
                return null;
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao buscar anamnese: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Atualiza a anamnese de um educando
     * @param educandoId ID do educando
     * @param anamneseDTO Dados atualizados da anamnese
     * @return AnamneseDTO atualizada ou null se falhar
     */
    public AnamneseDTO atualizarAnamnese(String educandoId, AnamneseDTO anamneseDTO) {
        if (currentToken == null || educandoId == null || anamneseDTO == null) {
            System.err.println("atualizarAnamnese: Token, educandoId ou anamneseDTO é NULL!");
            return null;
        }
        
        try {
            String requestBody = objectMapper.writeValueAsString(anamneseDTO);
            System.out.println("=== DEBUG UPDATE ANAMNESE ===");
            System.out.println("URL: " + BASE_URL + ANAMNESES_ENDPOINT + "/educando/" + educandoId);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESES_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status Code: " + response.statusCode());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), AnamneseDTO.class);
            } else {
                System.err.println("Erro ao atualizar anamnese. Status: " + response.statusCode());
                return null;
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao atualizar anamnese: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean deletarAnamnesePorEducando(String educandoId) {
        if (currentToken == null || educandoId == null) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESES_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .DELETE()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 || response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO criarRelatorioIndividual(
            com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO dto) {
        if (currentToken == null || dto == null || dto.educandoId() == null) {
            System.err.println("criarRelatorioIndividual: Token ou DTO inválido");
            return null;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + RELATORIOS_INDIVIDUAIS_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                return objectMapper.readValue(response.body(),
                        com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO.class);
            } else {
                System.err.println("Erro ao criar relatório individual. Status: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao criar relatório individual: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public java.util.List<com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO> getRelatoriosIndividuaisPorEducando(String educandoId) {
        if (currentToken == null || educandoId == null) {
            return new java.util.ArrayList<>();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + RELATORIOS_INDIVIDUAIS_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO>> typeRef =
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO>>() {};
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                return new java.util.ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            return new java.util.ArrayList<>();
        }
    }

    public boolean criarPAEE(
            com.pies.projeto.integrado.piesfront.controllers.PAEEController.CreatePAEEDTO dto) {
        System.out.println("=== AuthService.criarPAEE ===");
        if (currentToken == null || dto == null || dto.educandoId == null) {
            System.err.println("criarPAEE: Token ou DTO inválido");
            System.err.println("Token null? " + (currentToken == null));
            System.err.println("DTO null? " + (dto == null));
            System.err.println("EducandoId null? " + (dto != null && dto.educandoId == null));
            return false;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            System.out.println("Request Body:");
            System.out.println(requestBody);
            String url = BASE_URL + PAEES_ENDPOINT;
            System.out.println("URL: " + url);
            System.out.println("Token (primeiros 20 chars): " + currentToken.substring(0, Math.min(20, currentToken.length())) + "...");
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            System.out.println("Enviando requisição...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("PAEE criado com sucesso!");
                return true;
            } else {
                System.err.println("Erro ao criar PAEE. Status: " + response.statusCode());
                System.err.println("Response body: " + response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao criar PAEE: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<java.util.Map<String, Object>> getPdisPorEducandoRaw(String educandoId) {
        if (currentToken == null || educandoId == null) {
            return new java.util.ArrayList<>();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PDIS_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>> typeRef =
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {};
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                return new java.util.ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            return new java.util.ArrayList<>();
        }
    }

    public java.util.List<java.util.Map<String, Object>> getPaeesPorEducandoRaw(String educandoId) {
        if (currentToken == null || educandoId == null) {
            return new java.util.ArrayList<>();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PAEES_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>> typeRef =
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {};
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                return new java.util.ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            return new java.util.ArrayList<>();
        }
    }


    public boolean criarPDI(com.pies.projeto.integrado.piesfront.dto.CreatePDIDTO dto) {
        if (currentToken == null || dto == null || dto.educandoId() == null) {
            System.err.println("criarPDI: Token ou DTO inválido");
            return false;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PDIS_ENDPOINT))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                return true;
            } else {
                System.err.println("Erro ao criar PDI. Status: " + response.statusCode());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao criar PDI: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean criarDiagnosticoInicial(com.pies.projeto.integrado.piesfront.controllers.DIController.CreateDiagnosticoInicialDTO dto, String educandoId) {
        if (currentToken == null || dto == null || educandoId == null) {
            System.err.println("criarDiagnosticoInicial: Token ou DTO inválido");
            System.err.println("Token: " + (currentToken != null ? "OK" : "NULL"));
            System.err.println("DTO: " + (dto != null ? "OK" : "NULL"));
            System.err.println("EducandoId: " + (educandoId != null ? educandoId : "NULL"));
            return false;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            System.out.println("=== Enviando Diagnóstico Inicial ===");
            System.out.println("URL: " + BASE_URL + DIAGNOSTICOS_INICIAIS_ENDPOINT + "/educando/" + educandoId);
            System.out.println("Body size: " + requestBody.length() + " chars");
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + DIAGNOSTICOS_INICIAIS_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            
            boolean success = response.statusCode() == 201 || response.statusCode() == 200;
            if (!success) {
                System.err.println("ERRO: Status code " + response.statusCode());
            }
            return success;
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao criar Diagnóstico Inicial: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.Map<String, Object> getDiagnosticoInicialPorEducandoRaw(String educandoId) {
        if (currentToken == null || educandoId == null) {
            return null;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + DIAGNOSTICOS_INICIAIS_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public boolean deletarDiagnosticoInicial(String id) {
        if (currentToken == null || id == null) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + DIAGNOSTICOS_INICIAIS_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .DELETE()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 || response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean deletarPDI(String id) {
        if (currentToken == null || id == null) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PDIS_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .DELETE()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 || response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean deletarPAEE(String id) {
        if (currentToken == null || id == null) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PAEES_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .DELETE()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 || response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean deletarRelatorioIndividual(String id) {
        if (currentToken == null || id == null) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + RELATORIOS_INDIVIDUAIS_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .DELETE()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 || response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean atualizarDiagnosticoInicial(String id, String educandoId, com.pies.projeto.integrado.piesfront.controllers.DIController.CreateDiagnosticoInicialDTO dto) {
        if (currentToken == null || id == null || educandoId == null || dto == null) {
            return false;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + DIAGNOSTICOS_INICIAIS_ENDPOINT + "/" + id + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean atualizarPDI(String id, com.pies.projeto.integrado.piesfront.dto.CreatePDIDTO dto) {
        if (currentToken == null || id == null || dto == null) {
            return false;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PDIS_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean atualizarPAEE(String id, com.pies.projeto.integrado.piesfront.controllers.PAEEController.CreatePAEEDTO dto) {
        if (currentToken == null || id == null || dto == null) {
            return false;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + PAEES_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO atualizarRelatorioIndividual(String id, com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO dto) {
        if (currentToken == null || id == null || dto == null) {
            return null;
        }
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + RELATORIOS_INDIVIDUAIS_ENDPOINT + "/" + id))
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO.class);
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}
