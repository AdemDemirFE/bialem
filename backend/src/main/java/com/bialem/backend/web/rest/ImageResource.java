package com.bialem.backend.web.rest;

import com.bialem.backend.domain.Image;
import com.bialem.backend.service.ImageService;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Merkezi görsel kayıtları: dosya yükleme ya da URL ile kayıt açma,
 * meta okuma, gösterim adresine yönlendirme ve silme.
 */
@RestController
@RequestMapping("/api/app/images")
public class ImageResource {

    private final ImageService imageService;

    public ImageResource(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Yeni görsel kaydı: ya {@code file} (multipart) ya da {@code url} verilir.
     * Dönen {@code displayUrl} gösterimde doğrudan kullanılır.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> create(
        @RequestParam(value = "bucket", defaultValue = "general") String bucket,
        @RequestParam(value = "path", required = false) String path,
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "url", required = false) String url
    ) {
        boolean hasFile = file != null && !file.isEmpty();
        boolean hasUrl = url != null && !url.isBlank();
        if (hasFile == hasUrl) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya ya da adres bilgisinden biri verilmelidir");
        }
        Image image = hasFile
            ? imageService.registerUpload(bucket, path == null || path.isBlank() ? defaultPath(file) : path, file)
            : imageService.registerUrl(url);
        return toDto(image);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return toDto(find(id));
    }

    /**
     * Gösterim adresine yönlendirir; istemciler <img> kaynağı olarak
     * bu adresi doğrudan kullanabilir.
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<Void> content(@PathVariable Long id) {
        Image image = find(id);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(imageService.displayUrl(image))).build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        imageService.deleteOwned(id);
    }

    private Image find(Long id) {
        return imageService.findOne(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Görsel bulunamadı"));
    }

    private Map<String, Object> toDto(Image image) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", image.getId());
        dto.put("sourceType", image.getSourceType() == null ? null : image.getSourceType().name());
        dto.put("displayUrl", imageService.displayUrl(image));
        dto.put("bucket", image.getBucket());
        dto.put("contentType", image.getContentType());
        dto.put("fileSize", image.getFileSize());
        dto.put("width", image.getWidth());
        dto.put("height", image.getHeight());
        dto.put("createdAt", image.getCreatedAt() == null ? null : image.getCreatedAt().toString());
        return dto;
    }

    private String defaultPath(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            name = "upload";
        }
        String safe = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        return System.currentTimeMillis() + "-" + safe;
    }
}
