package com.bialem.backend.service;

import com.bialem.backend.domain.Image;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.enumeration.ImageSourceType;
import com.bialem.backend.repository.ImageRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import com.bialem.backend.security.SecurityUtils;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Merkezi görsel kayıt servisi.
 *
 * Dosya yükleme AppMediaService üzerinden diske yazılır; URL kayıtları
 * doğrulanıp olduğu gibi saklanır. Gösterim adresi her zaman
 * {@link #displayUrl(Image)} ile çözümlenir.
 */
@Service
@Transactional
public class ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AppMediaService mediaService;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository, AppMediaService mediaService) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.mediaService = mediaService;
    }

    /**
     * Dosya yükleyerek görsel kaydı açar. Aynı içerik daha önce yüklenmişse
     * mevcut kaydı döndürür (checksum ile tekilleme).
     */
    public Image registerUpload(String bucket, String path, MultipartFile file) {
        byte[] bytes = readBytes(file);
        String checksum = sha256Hex(bytes);
        Optional<Image> existing = imageRepository.findFirstByChecksum(checksum);
        if (existing.isPresent()) {
            return existing.get();
        }
        Map<String, String> saved = mediaService.save(bucket, path, file);
        Image image = new Image()
            .sourceType(ImageSourceType.UPLOAD)
            .bucket(bucket)
            .storagePath(saved.get("path"))
            .contentType(file.getContentType())
            .fileSize(file.getSize())
            .checksum(checksum)
            .createdAt(Instant.now());
        probeDimensions(bytes, image);
        image.setCreatedBy(currentUser().orElse(null));
        return imageRepository.save(image);
    }

    /**
     * Dış URL ile görsel kaydı açar. Adres doğrulanır, içerik indirilmez;
     * gösterimde orijinal adres kullanılır.
     */
    public Image registerUrl(String rawUrl) {
        String url = normalizeHttpsUrl(rawUrl);
        Image image = new Image()
            .sourceType(ImageSourceType.URL)
            .originalUrl(url)
            .createdAt(Instant.now());
        image.setCreatedBy(currentUser().orElse(null));
        return imageRepository.save(image);
    }

    @Transactional(readOnly = true)
    public Optional<Image> findOne(Long id) {
        return imageRepository.findById(id);
    }

    /**
     * Görüntülenecek adresi çözer: yüklenen dosyalar uygulama medya
     * yoluna, URL kayıtları orijinal adrese işaret eder.
     */
    public String displayUrl(Image image) {
        if (image.getSourceType() == ImageSourceType.URL) {
            return image.getOriginalUrl();
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/app/media/")
            .path(image.getBucket())
            .path("/")
            .path(image.getStoragePath())
            .toUriString();
    }

    /**
     * Kaydı siler (sahibi ya da admin). Yüklenen dosya diskten de kaldırılır.
     */
    public void deleteOwned(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Görsel bulunamadı"));
        boolean admin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            || SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SUPER_ADMIN);
        Optional<User> me = currentUser();
        boolean owner = me.isPresent() && image.getCreatedBy() != null && image.getCreatedBy().getId() != null
            && image.getCreatedBy().getId().equals(me.get().getId());
        if (!admin && !owner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu görseli silme yetkiniz yok");
        }
        if (image.getSourceType() == ImageSourceType.UPLOAD && image.getBucket() != null && image.getStoragePath() != null) {
            try {
                mediaService.delete(image.getBucket(), image.getStoragePath());
            } catch (ResponseStatusException ex) {
                LOG.warn("Görsel dosyası silinemedi, kayıt siliniyor: {}", image.getId());
            }
        }
        imageRepository.delete(image);
    }

    private Optional<User> currentUser() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya okunamadı");
        }
    }

    private String sha256Hex(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dosya işlenemedi");
        }
    }

    private void probeDimensions(byte[] bytes, Image image) {
        try (var stream = new ByteArrayInputStream(bytes)) {
            var buffered = ImageIO.read(stream);
            if (buffered != null) {
                image.width(buffered.getWidth()).height(buffered.getHeight());
            }
        } catch (Exception ex) {
            LOG.debug("Görsel boyutu okunamadı, atlanıyor");
        }
    }

    private String normalizeHttpsUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Görsel adresi zorunludur");
        }
        String value = rawUrl.trim();
        if (value.length() > 2048) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Görsel adresi çok uzun");
        }
        if (value.regionMatches(true, 0, "http:", 0, 5)) {
            value = "https:" + value.substring(5);
        }
        URI uri;
        try {
            uri = URI.create(value);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz görsel adresi");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || uri.getHost().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Görsel adresi https olmalıdır");
        }
        return uri.toASCIIString();
    }
}
