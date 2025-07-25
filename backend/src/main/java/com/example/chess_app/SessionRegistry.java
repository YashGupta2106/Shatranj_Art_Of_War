package com.example.chess_app;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionRegistry {
    private final ConcurrentHashMap<String, String> sessionIdToEmail = new ConcurrentHashMap<>();

    public void register(String sessionId, String email) {
        sessionIdToEmail.put(sessionId, email);
        System.out.println("Player email stored in session: ");
    }

    public String getEmail(String sessionId) {
        return sessionIdToEmail.get(sessionId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        System.out.println("time to remove this guy:");
        String sessionId = event.getSessionId();
        sessionIdToEmail.remove(sessionId);  // clean up
    }
}