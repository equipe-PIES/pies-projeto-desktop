package com.pies.projeto.integrado.piesfront.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public boolean submit(String educandoId, AnamneseRequestDTO dto, String token) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ANAMNESE_ENDPOINT + "/" + educandoId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("Erro ao enviar anamnese: " + e.getMessage());
            return false;
        }
    }
}

