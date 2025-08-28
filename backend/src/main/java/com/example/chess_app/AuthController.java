package com.example.chess_app;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import java.security.SecureRandom;
import java.util.Base64;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"${app.cors.allowed-origins}", "http://localhost:3000"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RedisService redisService;

    @Value("${app.debug.enabled:false}")
    private boolean debugEnabled;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        if (debugEnabled) {
            logger.info("Test endpoint accessed");
        }
        return ResponseEntity.ok("Backend is working!");
    }
    

    // this endpoint is used to verify the session of the user that is already logged in
    @GetMapping("/sessionVerify")
    public ResponseEntity<?> verifySession(HttpServletRequest request, HttpServletResponse response){
        Cookie cookies[] = request.getCookies();
        String sessionId=null;
        if(cookies==null || cookies.length==0){
            
        }
        else{
            for(Cookie cookie:cookies){
                if("sessionId".equals(cookie.getName())){
                    sessionId=cookie.getValue();
                    break;
                }
            }
        }
        
        if(sessionId!=null){
            System.out.println("i found the cookie so the session is valid");
            System.out.println("now lets see if the session lasts for how long");
            Long ttl = redisService.getSessionTTL("session"+sessionId);
            if(ttl<=0){
                System.out.println("session has expired so moving back to login page");
                ResponseCookie clearedCookie = ResponseCookie.from("sessionId", "")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(0)
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, clearedCookie.toString());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Session expired. Please log in again."));
            }
            else if(ttl<=300){
                
                redisService.refreshSessionTTL("session"+sessionId);
                ResponseCookie cookie = ResponseCookie.from("sessionId", sessionId)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(3600)
                    .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                System.out.println("session is valid and refreshed, sending new cookie with sessionId "+sessionId);
                return ResponseEntity.ok(Map.of("message", "Session is valid"));
                
            }
            else{
                return ResponseEntity.ok(Map.of("message", "Session is valid and has sufficient time left"));
            }
        }
        else{
            System.out.println("i did not find cookie so the session has expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Session expired. Please log in again."));
        }
    }


    // this is used when user opens the app to check if they have a valid session
    @PostMapping("/login")
    public ResponseEntity<?>verifyCookie(HttpServletRequest request, HttpServletResponse response){
        System.out.println("this is the time when i am starting the session");
        System.out.println("lets see if user was here in past 1 hr");
        String userMail=null;
        String sessionId=null;
        Cookie cookies[]=request.getCookies();
        if(cookies==null || cookies.length==0){
            System.out.println("clearly the user has not been here for a long time");
            System.out.println("the user has to login/register");
            Map<String,String> reply=new HashMap<>();
            reply.put("error","Session not found, Kindly login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reply);
        }

        for(Cookie cookie:cookies){
            if("sessionId".equals(cookie.getName())){
                // i found the cookie required
                sessionId=cookie.getValue();
                break;
            }
        }
        if(sessionId==null){
            System.out.println("i did not find cookie so the session has expired");
            System.out.println("moving the user back to login page");
            Map<String, String> reply = new HashMap<>();
            reply.put("error", "Session expired. Please log in again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reply);
        }
        else{
            System.out.println("sessionId found now lets find it in the redis");
            userMail=redisService.getUserIdBySession("session"+sessionId);
            if(userMail==null){
                System.out.println("redis did not find session so back to login page");
                Map<String,String> reply=new HashMap<>();
                reply.put("error","Session not found, Kindly login");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reply);
            }
            else{
                System.out.println("yayy user is found... so now lets send back the user mail ");
                redisService.refreshSessionTTL("session"+sessionId);
                Map<String, String> reply = new HashMap<>();
                reply.put("userMail", userMail);
                ResponseCookie cookie = ResponseCookie.from("sessionId", sessionId)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(3600)
                    .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                System.out.println("sending new cookie with sessionId "+sessionId);
                return ResponseEntity.ok(reply);
            }

        }



    }

    //  this endpoint is used when user logs in/ registers
    @PostMapping("/verify")
    public ResponseEntity<Map<String,String>> login(HttpServletResponse response,@RequestHeader("Authorization") String authHeader) {
        if (debugEnabled) {
            logger.info("Token verification request received");
        }
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid authorization header format");
                return ResponseEntity.status(401).body(Map.of("message","Invalid authorization header"));
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
            } 
            else {
                if (debugEnabled) {
                    logger.info("Found existing player: {}", player.getEmail());
                }
            }
            System.out.println("time to make a new cookie for the user");
            String sessionId = SessionUtil.generateSessionId();
            ResponseCookie cookie = ResponseCookie.from("sessionId", sessionId)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(3600)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            redisService.saveSession("session"+sessionId, email);
            System.out.println("sending new cookie with sessionId "+sessionId);
            System.out.println("now lets check if i have that cookie in redis");
            String redisEmail = redisService.getUserIdBySession("session"+sessionId);
            System.out.println("the player mail is "+email+" and the mail i got in redis is: "+redisEmail);
            if(debugEnabled){
                String ans="success+: "+sessionId;
                return ResponseEntity.ok(Map.of("message",ans));
            }
            return ResponseEntity.ok(Map.of("message", "success"));
            
        } 
        catch (Exception e) {
            logger.error("Token verification failed: {}", e.getMessage());
            
            if (debugEnabled) {
                return ResponseEntity.status(401).body(Map.of("message", "Token verification failed: " + e.getMessage()));
            } 
            else {
                return ResponseEntity.status(401).body(Map.of("message", "Authentication failed"));
            }
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request,HttpServletResponse response) {
        Cookie cookies[]=request.getCookies();
        String sessionId=null;
        if(cookies!=null && cookies.length!=0){
            for(Cookie cookie:cookies){
                if("sessionId".equals(cookie.getName())){
                    sessionId=cookie.getValue();
                    redisService.deleteSession("session"+sessionId);  
                    ResponseCookie clearedCookie = ResponseCookie.from("sessionId", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(0)
                    .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, clearedCookie.toString());
                    break;
                }
            }
        }
        
    }
}

class SessionUtil {
    private static final SecureRandom secureRandom = new SecureRandom(); // thread-safe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateSessionId() {
        byte[] randomBytes = new byte[24]; // 24 bytes = 192 bits = strong enough
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}