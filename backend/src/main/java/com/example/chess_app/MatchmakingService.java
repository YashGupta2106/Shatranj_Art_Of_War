package com.example.chess_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchmakingService {
    
    private final Queue<Player> waitingPlayers = new ConcurrentLinkedQueue<>();
    
    // Track players currently in games
    private final Set<String> playersInGame = ConcurrentHashMap.newKeySet();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private GameRepository gameRepository;
    
    public void findPlayerToPlay(Player player) {
        System.out.println("🔍 Finding match for: " + player.getEmail());
        
        if (playersInGame.contains(player.getEmail())) {
            System.out.println("⚠️ Player already in active game: " + player.getEmail());
            return;
        }

        // Check if player is already in queue (prevent duplicates)
        if (isPlayerInQueue(player)) {
            System.out.println("⚠️ Player already in queue: " + player.getEmail());
            return;
        }
        
        if (waitingPlayers.size() > 0) {
            // Match found!
            Player player1 = player;
            Player player2 = waitingPlayers.poll();
            playersInGame.add(player1.getEmail());
            playersInGame.add(player2.getEmail());
            
            System.out.println("🎯 Match found: " + player1.getEmail() + " vs " + player2.getEmail());
            
            // Randomly assign colors
            boolean player1IsWhite = Math.random() < 0.5;
            String whitePlayerEmail = player1IsWhite ? player1.getEmail() : player2.getEmail();
            String blackPlayerEmail = player1IsWhite ? player2.getEmail() : player1.getEmail();
            String gameId = UUID.randomUUID().toString();
            
            // Create game
            Game game = createOnlineGame(player1, player2, "Online", whitePlayerEmail, blackPlayerEmail, player1IsWhite);
            game.setActive(false); // Not active until both players ready
            // game.setGameStatus("waiting_for_players");
            
            if(player1IsWhite){
                game.setWhitePlayer(whitePlayerEmail);
                game.setBlackPlayer(blackPlayerEmail);
            } else {
                game.setWhitePlayer(blackPlayerEmail);
                game.setBlackPlayer(whitePlayerEmail);
            }
            game.setId(gameId);
            gameRepository.save(game);
            System.out.println("📝 Game created with ID: " + gameId);
            System.out.println("game saved to reposiory ");            
            // Send MATCH_FOUND to both players (keep your existing approach!)
            String dest1 = "/topic/" + player1.getEmail();
            MatchNotification notification1 = createMatchNotification(gameId, player1IsWhite, player2.getEmail());
            messagingTemplate.convertAndSend(dest1, notification1);
            
            String dest2 = "/topic/" + player2.getEmail();
            MatchNotification notification2 = createMatchNotification(gameId, !player1IsWhite, player1.getEmail());
            messagingTemplate.convertAndSend(dest2, notification2);
            
            System.out.println("📤 Match notifications sent to both players");
            
        } else {
            // Add to queue
            System.out.println("⏳ Adding player to queue: " + player.getEmail());
            waitingPlayers.add(player);
            
            // Send queue confirmation
            QueueNotification queueNotification = new QueueNotification();
            queueNotification.setMessageType("QUEUE_JOINED");
            queueNotification.setMessage("Looking for opponent...");
            queueNotification.setQueuePosition(waitingPlayers.size());
            
            messagingTemplate.convertAndSend("/topic/" + player.getEmail(), queueNotification);
        }
    }
    
    public void removePlayerFromQueue(Player player) {
        boolean removed = waitingPlayers.removeIf(p -> p.getEmail().equals(player.getEmail()));
        
        if (removed) {
            System.out.println("❌ Removed player from queue: " + player.getEmail());
            
            // Send cancellation confirmation
            CancelNotification cancelNotification = new CancelNotification();
            cancelNotification.setMessageType("MATCH_CANCELLED");
            cancelNotification.setMessage("Matchmaking cancelled");
            
            messagingTemplate.convertAndSend("/topic/" + player.getEmail(), cancelNotification);
        }
    }

    public void removePlayerFromActiveGame(String playerEmail) {
        playersInGame.remove(playerEmail);
        System.out.println("🏁 Removed player from active game: " + playerEmail);
    }
    
    // Helper method to check if player already in queue
    private boolean isPlayerInQueue(Player player) {
        return waitingPlayers.stream()
                .anyMatch(p -> p.getEmail().equals(player.getEmail()));
    }
    
    // Get queue status (useful for debugging)
    public int getQueueSize() {
        return waitingPlayers.size();
    }

    // Updated createMatchNotification with messageType
    private MatchNotification createMatchNotification(String gameId, boolean isWhite, String oppName) {
        MatchNotification notification = new MatchNotification();
        notification.setMessageType("MATCH_FOUND"); // ADD THIS LINE
        notification.setGameId(gameId);
        notification.setOppName(oppName);
        notification.setColor(isWhite ? "white" : "black");
        return notification;
    }
    
    // Updated MatchNotification class
    public static class MatchNotification {
        private String messageType; // ADD THIS FIELD
        private String gameId;
        private String color;
        private String oppName;
        
        // Getters and setters
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getOppName() { return oppName; }
        public void setOppName(String oppName) { this.oppName = oppName; }
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    // New notification classes
    public static class QueueNotification {
        private String messageType;
        private String message;
        private int queuePosition;
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getQueuePosition() { return queuePosition; }
        public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }
    }
    
    public static class CancelNotification {
        private String messageType;
        private String message;
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // Keep your existing createOnlineGame method (it's perfect as-is)
    private Game createOnlineGame(Player player1, Player player2, String gameType, String whitePlayerEmail, String blackPlayerEmail, boolean player1IsWhite) {
        // ... your existing implementation
        List<Piece> whitePieces = new ArrayList<>();
        List<Piece> blackPieces = new ArrayList<>();
        String[] col={"a","b","c","d","e","f","g","h"};
        String[] row={"1","2","3","4","5","6","7","8"};
        
        // Initialize pawns
        for (int i = 0; i < 8; i++) {
            Pawn whitePawn = new Pawn("white", col[i]+row[1],col[i]+"3");
            Pawn blackPawn = new Pawn("black", col[i]+row[6],col[i]+"6");
            whitePieces.add(whitePawn);
            blackPieces.add(blackPawn);
        }
    
        // Initialize other pieces
        Rook rook1=new Rook("white", col[0]+row[0]);
        rook1.setCastleTo("d1");
        Rook rook2=new Rook("white", col[7]+row[0]);
        rook2.setCastleTo("f1");
        Rook rook3=new Rook("black", col[0]+row[7]);
        rook3.setCastleTo("d8");
        Rook rook4=new Rook("black", col[7]+row[7]);
        rook4.setCastleTo("f8");
        
        whitePieces.add(rook1);
        blackPieces.add(rook3);
        whitePieces.add(rook2);
        blackPieces.add(rook4);

        Knight knight1=new Knight("white", col[1]+row[0]);
        Knight knight2=new Knight("white", col[6]+row[0]);
        Knight knight3=new Knight("black", col[1]+row[7]);
        Knight knight4=new Knight("black", col[6]+row[7]);
        
        whitePieces.add(knight1);
        blackPieces.add(knight3);
        whitePieces.add(knight2);
        blackPieces.add(knight4);

        Bishop bishop1=new Bishop("white", col[2]+row[0]);
        Bishop bishop2=new Bishop("white", col[5]+row[0]);
        Bishop bishop3=new Bishop("black", col[2]+row[7]);
        Bishop bishop4=new Bishop("black", col[5]+row[7]);
        
        whitePieces.add(bishop1);
        blackPieces.add(bishop3);
        whitePieces.add(bishop2);
        blackPieces.add(bishop4);

        Queen queen1=new Queen("white", col[3]+row[0]);
        Queen queen2=new Queen("black", col[3]+row[7]);
        
        whitePieces.add(queen1);
        blackPieces.add(queen2);

        King king1=new King("white", col[4]+row[0]);
        King king2=new King("black", col[4]+row[7]);
        
        whitePieces.add(king1);
        blackPieces.add(king2);
    
        return new Game(player1.getEmail(), player2.getEmail(), gameType, whitePieces, blackPieces);
    }
}
