package com.bialem.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
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

    @Value("${firebase.enabled:false}")
    private boolean enabled;

    @Value("${firebase.project-id:}")
    private String projectId;

    private boolean available;

    @PostConstruct
    public void init() {
        if (!enabled) {
            LOG.info("Firebase Admin SDK is disabled by configuration");
            available = false;
            return;
        }
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                initialize();
            } catch (Exception ex) {
                LOG.warn("Firebase FCM disabled: failed to initialize Admin SDK", ex);
                available = false;
            }
        } else {
            available = true;
        }
    }

    private void initialize() throws IOException {
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            Path path = Path.of(credentialsPath);
            if (Files.isRegularFile(path)) {
                try (InputStream in = Files.newInputStream(path)) {
                    FirebaseOptions options = options(GoogleCredentials.fromStream(in));
                    FirebaseApp.initializeApp(options);
                    available = true;
                    LOG.info("Firebase Admin SDK initialized from configured credentials");
                    return;
                }
            } else {
                LOG.warn("Firebase credentials file not found at {}, trying Application Default Credentials", path);
            }
        }

        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirebaseOptions options = options(credentials);
            FirebaseApp.initializeApp(options);
            available = true;
            LOG.info("Firebase Admin SDK initialized from Application Default Credentials");
        } catch (IOException ex) {
            LOG.warn("Firebase FCM disabled: no credentials configured and Application Default Credentials unavailable");
            available = false;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getProjectId() {
        return projectId == null || projectId.isBlank() ? null : projectId;
    }

    private FirebaseOptions options(GoogleCredentials credentials) {
        FirebaseOptions.Builder builder = FirebaseOptions.builder().setCredentials(credentials);
        if (projectId != null && !projectId.isBlank()) builder.setProjectId(projectId.trim());
        return builder.build();
    }
}
