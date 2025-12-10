package com.pies.projeto.integrado.piesfront.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LocalCache {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path baseDir;

    public LocalCache() {
        String home = System.getProperty("user.home");
        this.baseDir = Paths.get(home, ".pies", "cache");
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            Files.createDirectories(baseDir);
        } catch (IOException ignored) {
        }
    }

    public boolean isFresh(String key, long ttlMillis) {
        Path p = pathFor(key);
        try {
            if (!Files.exists(p)) return false;
            long lm = Files.getLastModifiedTime(p).toMillis();
            long now = System.currentTimeMillis();
            return now - lm <= ttlMillis;
        } catch (IOException e) {
            return false;
        }
    }

    public <T> T readObject(String key, Class<T> clazz) {
        Path p = pathFor(key);
        try {
            if (!Files.exists(p)) return null;
            byte[] b = Files.readAllBytes(p);
            return objectMapper.readValue(b, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public <T> List<T> readList(String key, TypeReference<List<T>> typeRef) {
        Path p = pathFor(key);
        try {
            if (!Files.exists(p)) return java.util.Collections.emptyList();
            byte[] b = Files.readAllBytes(p);
            return objectMapper.readValue(b, typeRef);
        } catch (IOException e) {
            return java.util.Collections.emptyList();
        }
    }

    public java.util.Map<String, Object> readMap(String key, TypeReference<java.util.Map<String, Object>> typeRef) {
        Path p = pathFor(key);
        try {
            if (!Files.exists(p)) return java.util.Collections.emptyMap();
            byte[] b = Files.readAllBytes(p);
            return objectMapper.readValue(b, typeRef);
        } catch (IOException e) {
            return java.util.Collections.emptyMap();
        }
    }

    public void write(String key, Object data) {
        Path p = pathFor(key);
        try {
            byte[] b = objectMapper.writeValueAsBytes(data);
            Files.write(p, b, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    private Path pathFor(String key) {
        return baseDir.resolve(key + ".json");
    }

    public void delete(String key) {
        Path p = pathFor(key);
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }
}
