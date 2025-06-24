package com.example.chess_app;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    @Value("${FIREBASE_CONFIG:}")
    private String firebaseConfigJson;

    @Value("${app.debug.enabled:false}")
    private boolean debugEnabled;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setProjectId(projectId);

                GoogleCredentials credentials = null;

                // First, try to use environment variable (for production/Render)
                if (!firebaseConfigJson.isEmpty()) {
                    if (debugEnabled) {
                        logger.info("Initializing Firebase with environment variable credentials");
                    }
                    try {
                        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(
                            firebaseConfigJson.getBytes(StandardCharsets.UTF_8)
                        );
                        credentials = GoogleCredentials.fromStream(credentialsStream);
                    } catch (Exception e) {
                        logger.error("Failed to parse Firebase config from environment variable: {}", e.getMessage());
                    }
                }
                
                // If env variable failed or not available, try service account file (for local development)
                if (credentials == null && !serviceAccountPath.isEmpty()) {
                    if (debugEnabled) {
                        logger.info("Initializing Firebase with service account file");
                    }
                    try {
                        FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
                        credentials = GoogleCredentials.fromStream(serviceAccount);
                    } catch (Exception e) {
                        logger.error("Failed to load service account file: {}", e.getMessage());
                    }
                }

                // If both above failed, try Application Default Credentials
                if (credentials == null) {
                    if (debugEnabled) {
                        logger.warn("No service account path or env config provided - trying default credentials");
                    }
                    try {
                        credentials = GoogleCredentials.getApplicationDefault();
                    } catch (IOException e) {
                        logger.warn("Could not load default credentials, Firebase features may not work: {}", e.getMessage());
                        return; // Don't initialize Firebase if no credentials available
                    }
                }

                if (credentials != null) {
                    optionsBuilder.setCredentials(credentials);
                    FirebaseApp.initializeApp(optionsBuilder.build());
                    
                    if (debugEnabled) {
                        logger.info("Firebase initialized successfully for project: {}", projectId);
                    }
                } else {
                    logger.error("No valid Firebase credentials found");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage());
            if (debugEnabled) {
                e.printStackTrace();
            }
        }
    }
}
