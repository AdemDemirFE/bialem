package com.bialem.backend.service;

import com.bialem.backend.domain.AiUsageLog;
import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.enumeration.CityEventStatus;
import com.bialem.backend.domain.enumeration.CommunityVisibility;
import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.repository.AiUsageLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppAiChatService {

    private static final Logger LOG = LoggerFactory.getLogger(AppAiChatService.class);
    private static final ZoneId ISTANBUL = ZoneId.of("Europe/Istanbul");
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("EEE d MMM HH:mm", Locale.forLanguageTag("tr"));
    private static final int HOURLY_LIMIT = 20;
    private static final int MAX_MESSAGES = 16;
    private static final int MAX_CONTENT = 2000;

    private final AppSupport support;
    private final AiUsageLogRepository usageLogs;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(15))
        .build();

    @PersistenceContext
    private EntityManager em;

    @Value("${bialem.openai.api-key:}")
    private String openaiApiKey;

    @Value("${bialem.openai.model:gpt-4o-mini}")
    private String openaiModel;

    public AppAiChatService(AppSupport support, AiUsageLogRepository usageLogs, ObjectMapper objectMapper) {
        this.support = support;
        this.usageLogs = usageLogs;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Map<String, Object> chat(Map<String, Object> body) {
        Profile me = support.currentProfile();
        Instant hourAgo = Instant.now().minus(Duration.ofHours(1));
        long recent = usageLogs.countByUser_IdAndCreatedAtAfter(me.getId(), hourAgo);
        if (recent >= HOURLY_LIMIT) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Saatlik asistan limiti doldu. Biraz sonra tekrar dene.");
        }

        List<Map<String, String>> messages = parseMessages(body);
        if (messages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mesaj gerekli");
        }

        String catalog = buildCatalog();
        String answer;
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            LOG.warn("OPENAI_API_KEY yok; yerel etkinlik özeti dönülüyor");
            answer = localAnswer(messages, catalog);
        } else {
            try {
                answer = callOpenAi(messages, catalog);
            } catch (Exception ex) {
                LOG.warn("OpenAI yanıtı alınamadı, yerel özete düşülüyor: {}", ex.getMessage());
                answer = localAnswer(messages, catalog);
            }
        }

        AiUsageLog log = new AiUsageLog();
        log.setUser(me);
        log.setCreatedAt(Instant.now());
        usageLogs.save(log);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("answer", answer);
        out.put("reply", answer);
        return out;
    }

    private List<Map<String, String>> parseMessages(Map<String, Object> body) {
        Object raw = body == null ? null : body.get("messages");
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, String>> out = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            String role = String.valueOf(map.get("role") == null ? "user" : map.get("role")).trim().toLowerCase(Locale.ROOT);
            if (!role.equals("user") && !role.equals("assistant")) {
                continue;
            }
            String content = String.valueOf(map.get("content") == null ? "" : map.get("content")).trim();
            if (content.isEmpty()) {
                continue;
            }
            if (content.length() > MAX_CONTENT) {
                content = content.substring(0, MAX_CONTENT);
            }
            out.add(Map.of("role", role, "content", content));
            if (out.size() >= MAX_MESSAGES) {
                break;
            }
        }
        return out;
    }

    private String buildCatalog() {
        Instant now = Instant.now();
        Instant until = now.plus(Duration.ofDays(10));
        List<CityEvent> cityEvents = em
            .createQuery(
                "select e from CityEvent e where e.status = :status and e.startsAt >= :from and e.startsAt < :to order by e.startsAt",
                CityEvent.class
            )
            .setParameter("status", CityEventStatus.PUBLISHED)
            .setParameter("from", now)
            .setParameter("to", until)
            .setMaxResults(12)
            .getResultList();
        List<Event> groupEvents = em
            .createQuery(
                "select e from Event e where e.status = :status and e.startsAt >= :from and e.startsAt < :to order by e.startsAt",
                Event.class
            )
            .setParameter("status", EventStatus.PUBLISHED)
            .setParameter("from", now)
            .setParameter("to", until)
            .setMaxResults(8)
            .getResultList();
        List<Community> communities = em
            .createQuery(
                "select c from Community c where c.visibility = :vis and c.parent is null order by c.name",
                Community.class
            )
            .setParameter("vis", CommunityVisibility.PUBLIC)
            .setMaxResults(10)
            .getResultList();

        StringBuilder sb = new StringBuilder();
        sb.append("Şehir etkinlikleri:\n");
        if (cityEvents.isEmpty()) {
            sb.append("- (yakın tarihli kayıt yok)\n");
        } else {
            for (CityEvent event : cityEvents) {
                sb
                    .append("- ")
                    .append(event.getTitle())
                    .append(" | ")
                    .append(formatWhen(event.getStartsAt()))
                    .append(" | ")
                    .append(nz(event.getCity()))
                    .append(" | ")
                    .append(nz(event.getVenueName()))
                    .append(" | ")
                    .append(nz(event.getCategory()))
                    .append(" | ")
                    .append(nz(event.getPriceLabel()))
                    .append('\n');
            }
        }
        sb.append("Topluluk etkinlikleri:\n");
        if (groupEvents.isEmpty()) {
            sb.append("- (yakın tarihli kayıt yok)\n");
        } else {
            for (Event event : groupEvents) {
                sb
                    .append("- ")
                    .append(event.getTitle())
                    .append(" | ")
                    .append(formatWhen(event.getStartsAt()))
                    .append(" | ")
                    .append(nz(event.getLocationName()))
                    .append('\n');
            }
        }
        sb.append("Açık topluluklar:\n");
        if (communities.isEmpty()) {
            sb.append("- (yok)\n");
        } else {
            for (Community community : communities) {
                sb.append("- ").append(community.getName()).append('\n');
            }
        }
        return sb.toString();
    }

    private String callOpenAi(List<Map<String, String>> messages, String catalog) throws Exception {
        List<Map<String, String>> payloadMessages = new ArrayList<>();
        payloadMessages.add(Map.of("role", "system", "content", systemPrompt(catalog)));
        payloadMessages.addAll(messages);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", openaiModel);
        payload.put("temperature", 0.7);
        payload.put("messages", payloadMessages);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .timeout(Duration.ofSeconds(45))
            .header("Authorization", "Bearer " + openaiApiKey.trim())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("OpenAI HTTP " + response.statusCode());
        }
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        String text = content.asText("").trim();
        if (text.isEmpty()) {
            throw new IllegalStateException("OpenAI boş yanıt");
        }
        return text;
    }

    private String systemPrompt(String catalog) {
        String now = ZonedDateTime.now(ISTANBUL).format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy HH:mm", Locale.forLanguageTag("tr")));
        return """
            Sen Bialem Asistanısın. Türkiye'de sosyalleşme, topluluk ve etkinlik keşfi için kısa, samimi ve net Türkçe yanıt ver.
            Bugünün tarihi (İstanbul): %s
            Sadece aşağıdaki güncel katalogdaki gerçek etkinlik ve toplulukları öner. Uydurma. Katalog boşsa genel fikir ver ve uygulamadaki Keşfet / Şehir / Topluluk sekmelerini işaret et.
            Cevap 2-6 kısa cümle veya madde olsun.

            KATALOG:
            %s
            """.formatted(now, catalog);
    }

    private String localAnswer(List<Map<String, String>> messages, String catalog) {
        String last = messages.get(messages.size() - 1).get("content");
        String lower = last.toLowerCase(Locale.ROOT);
        boolean weekend = lower.contains("hafta son") || lower.contains("haftason") || lower.contains("cumartesi") || lower.contains("pazar");
        boolean community = lower.contains("topluluk");
        StringBuilder sb = new StringBuilder();
        if (weekend) {
            sb.append("Bu hafta sonu için katalogdaki yakın etkinliklere bakarak şunları önerebilirim:\n\n");
        } else if (community) {
            sb.append("Sana uygun toplulukları Keşfet ve Topluluklar sekmelerinden de tarayabilirsin. Açık olanlardan birkaçı:\n\n");
        } else {
            sb.append("İşte yakındaki öneriler:\n\n");
        }
        String[] lines = catalog.split("\n");
        int shown = 0;
        for (String line : lines) {
            if (!line.startsWith("- ") || line.contains("(yakın") || line.contains("(yok)")) {
                continue;
            }
            sb.append(line).append('\n');
            shown++;
            if (shown >= 6) {
                break;
            }
        }
        if (shown == 0) {
            sb.append("Şu an yakın tarihli kayıtlı etkinlik yok. Şehir etkinlikleri ve topluluk sekmelerinden güncel listeyi kontrol edebilirsin.");
        } else {
            sb.append("\nDetay ve bilet için uygulamadaki Şehir etkinlikleri ve Topluluklar ekranına bak.");
        }
        return sb.toString().trim();
    }

    private static String formatWhen(Instant instant) {
        if (instant == null) {
            return "";
        }
        return WHEN.format(instant.atZone(ISTANBUL));
    }

    private static String nz(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
