package com.example.chess_app;

import com.example.chess_app.Player;
import com.example.chess_app.PlayerRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"${app.cors.allowed-origins}", "http://localhost:3000"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Value("${app.debug.enabled:false}")
    private boolean debugEnabled;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        if (debugEnabled) {
            logger.info("Test endpoint accessed");
        }
        return ResponseEntity.ok("Backend is working!");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        if (debugEnabled) {
            logger.info("Token verification request received");
        }
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid authorization header format");
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String idToken = authHeader.replace("Bearer ", "");
            
            if (debugEnabled) {
                logger.info("Processing token of length: {}", idToken.length());
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            if (debugEnabled) {
                logger.info("Firebase verification successful for user: {}", email);
            }

            Player player = playerRepository.findByUid(uid);
            if (player == null) {
                if (debugEnabled) {
                    logger.info("Creating new player for email: {}", email);
                }
                player = new Player();
                player.setUid(uid);
                player.setEmail(email);
                playerRepository.save(player);
            } else {
                if (debugEnabled) {
                    logger.info("Found existing player: {}", player.getEmail());
                }
            }

            return ResponseEntity.ok(player);
            
        } catch (Exception e) {
            logger.error("Token verification failed: {}", e.getMessage());
            
            if (debugEnabled) {
                return ResponseEntity.status(401).body("Token verification failed: " + e.getMessage());
            } else {
                return ResponseEntity.status(401).body("Authentication failed");
            }
        }
    }
}
