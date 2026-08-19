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

    private static final int MAX_PROXY_BYTES = 5 * 1024 * 1024;
    private static final Set<String> PROXY_HOSTS = Set.of("ticketm.net", "ticketmaster.com", "ticketmaster.com.tr");

    private final HttpClient httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(8))
        .build();

    @Value("${bialem.upload-dir:uploads}")
    private String uploadDir;

    public Map<String, String> save(String bucket, String path, MultipartFile file) {
        try {
            Path target = Path.of(uploadDir, bucket, path).normalize();
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
            HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(12))
                .header("User-Agent", "Mozilla/5.0 (compatible; Bialem/1.0)")
                .header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
                .GET()
                .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 400 || response.body() == null || response.body().length == 0) {
                return ResponseEntity.notFound().build();
            }
            if (response.body().length > MAX_PROXY_BYTES) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Görsel çok büyük");
            }
            String contentType = response.headers().firstValue("content-type").orElse(MediaType.IMAGE_JPEG_VALUE);
            if (!contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
                contentType = MediaType.IMAGE_JPEG_VALUE;
            }
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType.split(";")[0].trim()))
                .header("Cache-Control", "public, max-age=86400")
                .body(response.body());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Görsel alınamadı");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Görsel alınamadı");
        }
    }

    private URI parseAllowedUri(String rawUrl) {
        URI uri;
        try {
            uri = URI.create(rawUrl.trim().replaceFirst("(?i)^http:", "https:"));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
        }
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        boolean allowed = PROXY_HOSTS.stream().anyMatch(allowedHost -> host.equals(allowedHost) || host.endsWith("." + allowedHost));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu görsel kaynağı desteklenmiyor");
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
