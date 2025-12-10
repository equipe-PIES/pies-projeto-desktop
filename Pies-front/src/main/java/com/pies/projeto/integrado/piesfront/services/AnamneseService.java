package com.pies.projeto.integrado.piesfront.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pies.projeto.integrado.piesfront.dto.AnamneseDTO;
import com.pies.projeto.integrado.piesfront.dto.AnamneseRequestDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AnamneseService {
    private static final String BASE_URL = "http://localhost:8080";
    private static final String ANAMNESE_ENDPOINT = "/api/anamneses";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LocalCache localCache = new LocalCache();

    public AnamneseDTO getByEducando(String educandoId, String token) {
        String key = "anamnese_" + educandoId;
        if (localCache.isFresh(key, 120_000)) {
            AnamneseDTO dto = localCache.readObject(key, AnamneseDTO.class);
            if (dto != null) {
                return dto;
            }
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESE_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                AnamneseDTO dto = objectMapper.readValue(response.body(), AnamneseDTO.class);
                localCache.write(key, dto);
                return dto;
            } else {
                return localCache.readObject(key, AnamneseDTO.class);
            }
        } catch (Exception e) {
            return localCache.readObject(key, AnamneseDTO.class);
        }
    }

    public boolean submit(String educandoId, AnamneseRequestDTO dto, String token) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESE_ENDPOINT + "/educando/" + educandoId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                try {
                    AnamneseDTO salvo = objectMapper.readValue(response.body(), AnamneseDTO.class);
                    localCache.write("anamnese_" + educandoId, salvo);
                } catch (Exception ignored) {}
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao enviar anamnese: " + e.getMessage());
            return false;
        }
    }

    public boolean update(String educandoId, AnamneseRequestDTO dto, String token) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESE_ENDPOINT + "/educando/" + educandoId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                try {
                    AnamneseDTO salvo = objectMapper.readValue(response.body(), AnamneseDTO.class);
                    localCache.write("anamnese_" + educandoId, salvo);
                } catch (Exception ignored) {}
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteByEducando(String educandoId, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESE_ENDPOINT + "/educando/" + educandoId))
                    .header("Authorization", "Bearer " + token)
                    .DELETE()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204 || response.statusCode() == 200) {
                localCache.delete("anamnese_" + educandoId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}

