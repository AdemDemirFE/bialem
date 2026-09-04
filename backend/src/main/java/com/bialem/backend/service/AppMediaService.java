package com.bialem.backend.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class AppMediaService {

    private static final Logger LOG = LoggerFactory.getLogger(AppMediaService.class);
    private static final int MAX_PROXY_BYTES = 5 * 1024 * 1024;
    private static final int MAX_UPLOAD_BYTES = 5 * 1024 * 1024;
    private static final Set<String> UPLOAD_BUCKETS = Set.of(
        "community-covers",
        "event-covers",
        "profile-avatars",
        "post-media",
        "stories",
        "general",
        "store-products",
        "partner-venues"
    );
    private static final Set<String> UPLOAD_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final int MAX_REDIRECTS = 5;
    private static final Set<String> PROXY_HOSTS = Set.of(
        "ticketm.net",
        "ticketmaster.com",
        "ticketmaster.com.tr",
        "ticketmaster.eu",
        "tmol.io"
    );
    private static final String BROWSER_UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NEVER)
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    @Value("${bialem.upload-dir:uploads}")
    private String uploadDir;

    public Map<String, String> save(String bucket, String path, MultipartFile file) {
        if (!UPLOAD_BUCKETS.contains(bucket)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz medya alanı");
        }
        if (file == null || file.isEmpty() || file.getSize() > MAX_UPLOAD_BYTES || !UPLOAD_IMAGE_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Görsel JPEG, PNG veya WebP ve en fazla 5 MB olmalıdır");
        }
        try {
            Path bucketRoot = Path.of(uploadDir, bucket).toAbsolutePath().normalize();
            Path target = bucketRoot.resolve(path).normalize();
            if (!target.startsWith(bucketRoot)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz yol");
            }
            Files.createDirectories(target.getParent());
            Files.write(target, file.getBytes());
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/app/media/")
                .path(bucket)
                .path("/")
                .path(path)
                .toUriString();
            return Map.of("publicUrl", publicUrl, "path", path);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dosya kaydedilemedi");
        }
    }

    public Path resolve(String bucket, String path) {
        Path target = Path.of(uploadDir, bucket, path).normalize();
        Path root = Path.of(uploadDir).toAbsolutePath().normalize();
        if (!target.toAbsolutePath().normalize().startsWith(root)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz yol");
        }
        return target;
    }

    public void delete(String bucket, String path) {
        try {
            Files.deleteIfExists(resolve(bucket, path));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dosya silinemedi");
        }
    }

    public ResponseEntity<byte[]> proxyRemote(String rawUrl) {
        URI uri = parseAllowedUri(rawUrl);
        try {
            HttpResponse<byte[]> response = fetchWithRedirects(uri);
            byte[] body = response.body();
            if (response.statusCode() >= 400 || body == null || body.length == 0) {
                LOG.warn("Image proxy upstream {} for {}", response.statusCode(), uri);
                return ResponseEntity.notFound().build();
            }
            if (body.length > MAX_PROXY_BYTES) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }
            return ResponseEntity.ok()
                .contentType(imageMediaType(response.headers().firstValue("content-type").orElse("")))
                .header("Cache-Control", "public, max-age=86400")
                .body(body);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOG.warn("Image proxy interrupted for {}", uri);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception ex) {
            LOG.warn("Image proxy failed for {}: {}", uri, ex.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private HttpResponse<byte[]> fetchWithRedirects(URI start) throws IOException, InterruptedException {
        URI current = start;
        HttpResponse<byte[]> response = null;
        for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
            HttpRequest request = HttpRequest.newBuilder(current)
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", BROWSER_UA)
                .header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
                .header("Accept-Language", "tr-TR,tr;q=0.9,en;q=0.8")
                .header("Accept-Encoding", "identity")
                .header("Referer", refererFor(current))
                .GET()
                .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int status = response.statusCode();
            if (status < 300 || status >= 400) {
                return response;
            }
            String location = response.headers().firstValue("location").orElse(null);
            if (location == null || location.isBlank()) {
                return response;
            }
            current = parsePublicHttps(current.resolve(location), false);
        }
        throw new IOException("Too many redirects");
    }

    private String refererFor(URI uri) {
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        if (host.endsWith("ticketmaster.com.tr") || host.endsWith("tmol.io")) {
            return "https://www.ticketmaster.com.tr/";
        }
        if (host.endsWith("ticketmaster.eu")) {
            return "https://www.ticketmaster.de/";
        }
        return "https://www.ticketmaster.com/";
    }

    private MediaType imageMediaType(String raw) {
        String value = raw == null ? "" : raw.split(";")[0].trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "image/png" -> MediaType.IMAGE_PNG;
            case "image/gif" -> MediaType.IMAGE_GIF;
            case "image/webp" -> MediaType.parseMediaType("image/webp");
            case "image/avif" -> MediaType.parseMediaType("image/avif");
            case "image/bmp", "image/x-ms-bmp" -> MediaType.parseMediaType("image/bmp");
            default -> MediaType.IMAGE_JPEG;
        };
    }

    private URI parseAllowedUri(String rawUrl) {
        try {
            return parsePublicHttps(URI.create(rawUrl.trim().replaceFirst("(?i)^http:", "https:")), true);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
        }
    }

    private URI parsePublicHttps(URI uri, boolean requireAllowlist) {
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
        }
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        if (requireAllowlist) {
            boolean allowed = PROXY_HOSTS.stream().anyMatch(allowedHost -> host.equals(allowedHost) || host.endsWith("." + allowedHost));
            if (!allowed) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu görsel kaynağı desteklenmiyor");
            }
        }
        try {
            InetAddress address = InetAddress.getByName(host);
            if (address.isLoopbackAddress() || address.isSiteLocalAddress() || address.isAnyLocalAddress() || address.isLinkLocalAddress()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
        }
        return uri;
    }
}
