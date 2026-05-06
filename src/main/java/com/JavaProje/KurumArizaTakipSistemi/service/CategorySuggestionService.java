package com.JavaProje.KurumArizaTakipSistemi.service;

import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CategorySuggestionService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final Logger logger = LoggerFactory.getLogger(CategorySuggestionService.class);

    private final CategoryCatalogService categoryCatalogService;
    private final Environment env;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public CategorySuggestionService(CategoryCatalogService categoryCatalogService,
                                     Environment env) {
        this.categoryCatalogService = categoryCatalogService;
        this.env = env;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Resolve category by id or suggest using Gemini.
     */
    public Optional<TicketCategory> resolveCategory(Integer categoryId, String title, String description) {
        if (categoryId != null) {
            return categoryCatalogService.ensureDefaultsAndList().stream()
                    .filter(c -> c.getCategoryId().equals(categoryId))
                    .findFirst();
        }
        return suggestCategory(title, description);
    }

    /**
     * Suggest a category using Gemini generateContent API.
     */
    public Optional<TicketCategory> suggestCategory(String title, String description) {
        List<TicketCategory> categories = categoryCatalogService.ensureDefaultsAndList();

        if (categories.isEmpty()) return Optional.empty();

        String apiKey = env.getProperty("gemini.api.key", "").trim();
        String model = normalizeModelName(env.getProperty("gemini.model", "gemini-2.1"));
        String baseUrl = env.getProperty("gemini.url", "https://generativelanguage.googleapis.com/v1beta");

        if (apiKey.isEmpty()) {
            logger.warn("gemini.api.key not set — category suggestion disabled");
            return Optional.empty();
        }

        String prompt = buildPrompt(categories, title, description);
        logger.debug("Prompt:\n{}", prompt);

        String raw;
        try {
            raw = callGenerateContent(baseUrl, model, apiKey, prompt);
            logger.debug("Raw Gemini response: {}", raw);
        } catch (Exception e) {
            logger.warn("Gemini call failed — category suggestion disabled", e);
            return Optional.empty();
        }

        String normalized = normalize(raw);

        // 1) exact match
        for (TicketCategory c : categories) {
            if (normalize(c.getCategoryName()).equals(normalized)) return Optional.of(c);
        }
        // 2) output contains category
        for (TicketCategory c : categories) {
            if (normalized.contains(normalize(c.getCategoryName()))) return Optional.of(c);
        }
        // 3) try Diğer/Other
        for (TicketCategory c : categories) {
            String n = c.getCategoryName();
            if (n == null) continue;
            if (n.equalsIgnoreCase("Diğer") || n.equalsIgnoreCase("Diger") || n.equalsIgnoreCase("Other")) return Optional.of(c);
        }

        return Optional.empty();
    }

    private String callGenerateContent(String baseUrl, String model, String apiKey, String prompt) throws Exception {
        String fullUrl = baseUrl + "/models/" + model + ":generateContent";

        java.util.Map<String, Object> part = new java.util.HashMap<>();
        part.put("text", prompt);
        java.util.Map<String, Object> contentObj = new java.util.HashMap<>();
        contentObj.put("parts", List.of(part));
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("contents", List.of(contentObj));

        String json = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(REQUEST_TIMEOUT)
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            String detail = resp.body();
            if (resp.statusCode() == 404) {
                throw new IllegalStateException("Gemini model/endpoint not found. Model=" + model + ", response=" + detail);
            }
            throw new IllegalStateException("Gemini request failed. HTTP " + resp.statusCode() + ", body=" + detail);
        }

        JsonNode root = mapper.readTree(resp.body());
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode first = candidates.get(0);
            JsonNode content = first.path("content");
            JsonNode parts = content.path("parts");
            if (parts.isArray() && parts.size() > 0) {
                return parts.get(0).path("text").asText();
            }
        }
        JsonNode output = root.path("output");
        if (output.isArray() && output.size() > 0) {
            JsonNode first = output.get(0);
            JsonNode content = first.path("content");
            if (content.isTextual()) return content.asText();
            JsonNode parts = content.path("parts");
            if (parts.isArray() && parts.size() > 0) return parts.get(0).path("text").asText();
        }

        return resp.body();
    }

    private String buildPrompt(List<TicketCategory> categories, String title, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("Aşağıdaki destek talebini verilen kategorilerden birine atayın.\n");
        sb.append("Sadece kategori adını döndürün, başka hiçbir şey yazmayın.\n\n");
        sb.append("Kategoriler:\n");
        for (TicketCategory c : categories) {
            sb.append("- ").append(c.getCategoryName()).append("\n");
        }
        sb.append("\nDestek Talebi:\n");
        sb.append("Başlık: ").append(safe(title)).append("\n");
        sb.append("Açıklama: ").append(safe(description)).append("\n");
        return sb.toString();
    }

    private String safe(String val) { return val == null ? "" : val.trim(); }

    private String normalizeModelName(String modelName) {
        if (modelName == null || modelName.isBlank()) return "gemini-2.1";
        String normalized = modelName.trim();
        if (normalized.startsWith("models/")) normalized = normalized.substring("models/".length());
        return normalized;
    }

    private String normalize(String value) {
        if (value == null) return "";
        String normalized = value.toLowerCase(Locale.ROOT)
                .replace('ı', 'i')
                .replace('ğ', 'g')
                .replace('ü', 'u')
                .replace('ş', 's')
                .replace('ö', 'o')
                .replace('ç', 'c');
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return normalized.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }
}