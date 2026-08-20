package com.bialem.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials:}")
    private String credentialsPath;

    private boolean available;

    @PostConstruct
    public void init() {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            LOG.warn("Firebase FCM disabled: firebase.credentials / FIREBASE_CREDENTIALS is empty");
            return;
        }
        Path path = Path.of(credentialsPath);
        if (!Files.isRegularFile(path)) {
            LOG.warn("Firebase FCM disabled: credentials file not found at {}", path);
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) {
            available = true;
            return;
        }
        try (InputStream in = new FileInputStream(path.toFile())) {
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(in)).build();
            FirebaseApp.initializeApp(options);
            available = true;
            LOG.info("Firebase Admin SDK initialized for FCM");
        } catch (IOException ex) {
            LOG.error("Firebase FCM disabled: failed to initialize Admin SDK", ex);
        }
    }

    public boolean isAvailable() {
        return available;
    }
}
