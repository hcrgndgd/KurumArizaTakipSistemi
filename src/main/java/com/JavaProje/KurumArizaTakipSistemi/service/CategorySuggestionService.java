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
     * Suggest a category using Gemini generateContent API and robust mapping logic.
     */
    public Optional<TicketCategory> suggestCategory(String title, String description) {
        List<TicketCategory> categories = categoryCatalogService.ensureDefaultsAndList();

        if (categories.isEmpty()) return Optional.empty();

        String apiKey = env.getProperty("gemini.api.key", "").trim();
        String model = normalizeModelName(env.getProperty("gemini.model", "gemini-2.1"));
        String baseUrl = env.getProperty("gemini.url", "https://generativelanguage.googleapis.com/v1beta");

        // If key not set, fall back to deterministic matching
        if (apiKey.isEmpty()) {
            logger.warn("gemini.api.key not set — using deterministic matching fallback");
            return Optional.of(deterministicMatch(categories, title, description));
        }

        String prompt = buildPrompt(categories, title, description);
        logger.debug("Prompt:\n{}", prompt);

        String raw;
        try {
            raw = callGenerateContent(baseUrl, model, apiKey, prompt);
            logger.debug("Raw Gemini response: {}", raw);
        } catch (Exception e) {
            logger.warn("Gemini call failed, using deterministic fallback", e);
            return Optional.of(deterministicMatch(categories, title, description));
        }

        // Normalize model output and attempt mapping
        String normalized = normalize(raw);

        // 1) exact
        for (TicketCategory c : categories) {
            if (normalize(c.getCategoryName()).equals(normalized)) return Optional.of(c);
        }
        // 2) output contains category
        for (TicketCategory c : categories) {
            if (normalized.contains(normalize(c.getCategoryName()))) return Optional.of(c);
        }
        // 3) deterministic match from text
        TicketCategory det = deterministicMatchScore(categories, title, description);
        if (det != null) return Optional.of(det);

        // 4) try Diğer/Other
        for (TicketCategory c : categories) {
            String n = c.getCategoryName();
            if (n == null) continue;
            if (n.equalsIgnoreCase("Diğer") || n.equalsIgnoreCase("Diger") || n.equalsIgnoreCase("Other")) return Optional.of(c);
        }

        // final fallback
        return Optional.of(categories.get(0));
    }

    private String callGenerateContent(String baseUrl, String model, String apiKey, String prompt) throws Exception {
        String fullUrl = baseUrl + "/models/" + model + ":generateContent";

        // build payload { "contents": [ { "parts": [ { "text": prompt } ] } ] }
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

        // parse response
        JsonNode root = mapper.readTree(resp.body());
        // try candidates -> content -> parts -> text
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode first = candidates.get(0);
            JsonNode content = first.path("content");
            JsonNode parts = content.path("parts");
            if (parts.isArray() && parts.size() > 0) {
                return parts.get(0).path("text").asText();
            }
        }
        // fallback: output
        JsonNode output = root.path("output");
        if (output.isArray() && output.size() > 0) {
            JsonNode first = output.get(0);
            JsonNode content = first.path("content");
            if (content.isTextual()) return content.asText();
            JsonNode parts = content.path("parts");
            if (parts.isArray() && parts.size() > 0) return parts.get(0).path("text").asText();
        }

        // last: raw body
        return resp.body();
    }

    private TicketCategory deterministicMatch(List<TicketCategory> categories, String title, String description) {
        // simple keyword scoring across title+description
        String text = ((title == null ? "" : title) + " " + (description == null ? "" : description)).toLowerCase();
        TicketCategory best = null;
        int bestScore = 0;
        for (TicketCategory c : categories) {
            if (c.getCategoryName() == null) continue;
            String name = c.getCategoryName().toLowerCase();
            int score = 0;
            if (text.contains(name)) score += 5;
            String[] parts = name.split("[\\s,/_-]+");
            for (String p : parts) {
                if (p.length() > 2 && text.contains(p)) score += 1;
            }
            if (score > bestScore) {
                bestScore = score;
                best = c;
            }
        }
        if (best != null && bestScore > 0) return best;
        return categories.get(0);
    }

    private TicketCategory deterministicMatchScore(List<TicketCategory> categories, String title, String description) {
        // token-overlap based fallback
        String full = ((title == null ? "" : title) + " " + (description == null ? "" : description));
        String normFull = normalize(full);
        String[] tokens = normFull.split("\\s+");
        int bestScore = 0; TicketCategory best = null;
        for (TicketCategory c : categories) {
            String norm = normalize(c.getCategoryName());
            String[] catTokens = norm.split("\\s+");
            int score = 0;
            for (String t1 : tokens) for (String t2 : catTokens) if (t1.equals(t2)) score++;
            if (score > bestScore) { bestScore = score; best = c; }
        }
        return bestScore > 0 ? best : null;
    }

    private String buildPrompt(List<TicketCategory> categories, String title, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("Kurum destek taleplerini sınıflandırıyorsunuz.\n");
        sb.append("Aşağıdaki listeden TAM olarak sadece BİR kategori seçin ve TAM adıyla, hiçbir ek metin olmadan sadece kategori adını döndürün.\n");
        sb.append("Eğer uygulanabilir bir kategori yoksa 'Diğer' yazın.\n\n");
        sb.append("Kategoriler (tam adlarıyla):\n");
        StringBuilder allowed = new StringBuilder();
        for (TicketCategory c : categories) {
            sb.append("- ").append(c.getCategoryName()).append("\n");
            if (allowed.length() > 0) allowed.append(", ");
            allowed.append(c.getCategoryName());
        }
        sb.append("\nAllowed: ").append(allowed.toString()).append("\n\n");
        sb.append("Örnekler (başlık -> kategori):\n");
        sb.append("- Başlık: İnternet gidip geliyor. Açıklama: Ofiste internete bağlanamıyoruz. -> Ag\n");
        sb.append("- Başlık: Bilgisayar açılmıyor. Açıklama: Güç düğmesine basınca hiç tepki yok. -> Donanim\n");
        sb.append("- Başlık: Program hata veriyor. Açıklama: Uygulama açılırken istisna fırlatıyor. -> Yazilim\n");
        sb.append("- Başlık: Lavabo sızdırıyor. Açıklama: Kat koridorunda su akıyor. -> Tesisat\n");
        sb.append("- Başlık: Priz çalışmıyor. Açıklama: Bir odada elektrik yok. -> Elektrik\n");
        sb.append("- Başlık: Kapı kolu. Açıklama: Bir odada kapı kolu yok. -> Elektrik\n");
        sb.append("- Başlık: Klavye eksik. Açıklama: Yeni gelen personel için malzeme gerekiyor. -> Malzeme eksikligi\n\n");
        sb.append("Başlık: ").append(safe(title)).append("\n");
        sb.append("Açıklama: ").append(safe(description)).append("\n\n");
        sb.append("Lütfen CEVABI yalnızca ve tam olarak bir kategori adı olarak yazın; başka hiçbir kelime, nokta veya açıklama eklemeyin.\n");
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
