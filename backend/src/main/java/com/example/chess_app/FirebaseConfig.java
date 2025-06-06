package com.example.chess_app;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    @Value("${app.debug.enabled:false}")
    private boolean debugEnabled;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setProjectId(projectId);

                // Use service account file if provided (production)
                if (!serviceAccountPath.isEmpty()) {
                    if (debugEnabled) {
                        logger.info("Initializing Firebase with service account file");
                    }
                    FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
                    optionsBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
                } else {
                    // Development mode - use Application Default Credentials or mock
                    if (debugEnabled) {
                        logger.warn("No service account path provided - using default credentials");
                    }
                    try {
                        optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault());
                    } catch (IOException e) {
                        logger.warn("Could not load default credentials, Firebase features may not work: {}", e.getMessage());
                        return; // Don't initialize Firebase if no credentials available
                    }
                }

                FirebaseApp.initializeApp(optionsBuilder.build());
                
                if (debugEnabled) {
                    logger.info("Firebase initialized successfully for project: {}", projectId);
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
