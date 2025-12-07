package com.example.chess_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.Charset;


// for stockfish response
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.*;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:3000")
public class GameHistoryController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RedisService redisService;

    // Endpoint 1: Get game history/results for a user
    @GetMapping("/results")
    public ResponseEntity<?> getUserGameHistory(HttpServletRequest request) {
        
        try {
            String sessionId=null;
            String email=null;
            Cookie cookies[]= request.getCookies();
            if(cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("sessionId".equals(cookie.getName())) {
                        sessionId= cookie.getValue();
                        System.out.println("🔑 Found sessionId cookie: " + sessionId);
                        break;
                    }
                }
            }
            else{
                System.out.println("❌ No cookies found in request");
                return ResponseEntity.status(401).body("Unauthorized: No session cookie found");
            }
            email= redisService.getUserIdBySession("session"+sessionId);
            if(email==null){
                System.out.println("invalid session id i cant find any email associated");
            }
            else{

                System.out.println("🎮 Fetching game history for: " + email);
            }
            
            // Find all completed games where user participated
            List<Game> userGames = gameRepository.findCompletedGamesByPlayerEmail(email);
            
            if (userGames.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>()); // Return empty list
            }
            
            List<GameHistoryResponse> gameHistory = new ArrayList<>();
            
            for (Game game : userGames) {
                GameHistoryResponse historyItem = new GameHistoryResponse();
                
                // Basic game info
                historyItem.setGameId(game.getId());
                historyItem.setDate(game.getEndTime() != null ? game.getEndTime() : game.getStartTime());
                historyItem.setTotalMoves(game.getMoveNumber());
                
                // Calculate duration (in seconds)
                if (game.getStartTime() != null && game.getEndTime() != null) {
                    long duration = java.time.Duration.between(
                        game.getStartTime(), game.getEndTime()
                    ).getSeconds();
                    historyItem.setDuration((int) duration);
                } else {
                    historyItem.setDuration(0);
                }
                
                // Determine user's color and opponent
                boolean isUserWhite = email.equals(game.getWhitePlayer());
                historyItem.setUserColor(isUserWhite ? "white" : "black");
                
                // Set opponent info
                Player opponent = isUserWhite ? playerRepository.findByEmail(game.getBlackPlayer()) : playerRepository.findByEmail(game.getWhitePlayer());
                GameHistoryResponse.OpponentInfo opponentInfo = new GameHistoryResponse.OpponentInfo();
                opponentInfo.setName(opponent.getUsername() != null ? opponent.getUsername() : opponent.getEmail());
                opponentInfo.setEmail(opponent.getEmail());
                historyItem.setOpponent(opponentInfo);
                
                // Determine result from user's perspective
                String gameResult = game.getWinner();
                if ("draw".equals(gameResult)) {
                    historyItem.setResult("draw");
                } else if ((isUserWhite && "white".equals(gameResult)) || 
                          (!isUserWhite && "black".equals(gameResult))) {
                    historyItem.setResult("win");
                } else {
                    historyItem.setResult("loss");
                }
                
                // Set end reason
                historyItem.setEndReason(game.getGameEndReason() != null ? 
                    game.getGameEndReason().toLowerCase() : "completed");
                
                gameHistory.add(historyItem);
            }
            
            // Sort by date (most recent first)
            gameHistory.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            
            System.out.println("✅ Found " + gameHistory.size() + " games for user: " + email);
            return ResponseEntity.ok(gameHistory);
            
        } 
        catch (Exception e) {
            System.err.println("❌ Error fetching game history: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching game history");
        }
    }

    // Endpoint 2: Get detailed game data for replay
    @GetMapping("/replay/{gameId}")
    public ResponseEntity<?> getGameForReplay(@PathVariable String gameId) {
        try {
            System.out.println("🎮 Fetching game replay data for: " + gameId);
            
            Game game = gameRepository.findByGameId(gameId);
            if (game == null) {
                return ResponseEntity.status(404).body("Game not found");
            }
            
            // Check if game is completed
            if (game.isActive()) {
                return ResponseEntity.status(400).body("Game is still active");
            }
            
            GameReplayResponse replayData = new GameReplayResponse();
            
            // Basic game info
            replayData.setGameId(game.getId());
            replayData.setStartTime(game.getStartTime());
            replayData.setEndTime(game.getEndTime());
            replayData.setResult(game.getWinner());
            replayData.setEndReason(game.getGameEndReason());
            
            // Player info
            GameReplayResponse.PlayerInfo whitePlayer = new GameReplayResponse.PlayerInfo();
            whitePlayer.setEmail(game.getWhitePlayer());
            whitePlayer.setName(game.getWhitePlayerName() != null ? 
                game.getWhitePlayerName() : game.getWhitePlayer());
            
            GameReplayResponse.PlayerInfo blackPlayer = new GameReplayResponse.PlayerInfo();
            blackPlayer.setEmail(game.getBlackPlayer());
            blackPlayer.setName(game.getBlackPlayerName()!= null ? 
                game.getBlackPlayerName() : game.getBlackPlayer());
            
            GameReplayResponse.Players players = new GameReplayResponse.Players();
            players.setWhite(whitePlayer);
            players.setBlack(blackPlayer);
            replayData.setPlayers(players);
            
            // Convert moves to replay format
            List<GameReplayResponse.MoveData> replayMoves = new ArrayList<>();
            for (Move move : game.getMoves()) {
                GameReplayResponse.MoveData moveData = new GameReplayResponse.MoveData();
                moveData.setMoveNumber(move.getMoveNumber());
                moveData.setFrom_sq(move.getFrom_sq());
                moveData.setTo_sq(move.getTo_sq());
                moveData.setPieceMoved(move.getPieceMoved());
                moveData.setPieceCaptured(move.getPieceCaptured());
                moveData.setCastling(move.getCastling());
                moveData.setEnPassant(move.getEnPassant());
                moveData.setPromotion(move.getPromotion());
                moveData.setPromotionPiece(move.getPromotionPiece());
                moveData.setSquareCaptured(move.getSquareCaptured());
                replayMoves.add(moveData);
            }
            replayData.setMoves(replayMoves);
            
            System.out.println("✅ Game replay data prepared for: " + gameId);
            return ResponseEntity.ok(replayData);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching game replay: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching game replay data");
        }
    }

    @PostMapping("/stockfish")
    public ResponseEntity<String> getStockfishResponse(@RequestBody Map<String, String> request) {
        try {
            String fen = request.get("fen");
            
            if (fen == null || fen.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"success\": false, \"error\": \"FEN is required\"}");
            }
            
            RestTemplate restTemplate = new RestTemplate();
            
            // ✅ Use GET request with query parameters as per documentation
            String url = "https://stockfish.online/api/s/v2.php?fen=" + 
                         java.net.URLEncoder.encode(fen, "UTF-8") + 
                         "&depth=15";
            
            System.out.println("📡 Calling Stockfish API: " + url);
            
            // ✅ Use GET request instead of POST
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            String responseBody = response.getBody();
            System.out.println("📊 Stockfish raw response: " + responseBody);
            
            if (responseBody != null && !responseBody.isEmpty()) {
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.ok("{\"success\": false, \"error\": \"No response from engine\"}");
            }
            
        } catch (Exception e) {
            System.err.println("Stockfish API error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"Analysis service unavailable: " + e.getMessage() + "\"}");
        }
    }




    // Response DTOs
    public static class GameHistoryResponse {
        private String gameId;
        private OpponentInfo opponent;
        private String userColor;
        private String result;
        private String endReason;
        private LocalDateTime date;
        private int duration;
        private int totalMoves;

        // Getters and setters
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public OpponentInfo getOpponent() { return opponent; }
        public void setOpponent(OpponentInfo opponent) { this.opponent = opponent; }
        
        public String getUserColor() { return userColor; }
        public void setUserColor(String userColor) { this.userColor = userColor; }
        
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        
        public String getEndReason() { return endReason; }
        public void setEndReason(String endReason) { this.endReason = endReason; }
        
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        
        public int getTotalMoves() { return totalMoves; }
        public void setTotalMoves(int totalMoves) { this.totalMoves = totalMoves; }

        public static class OpponentInfo {
            private String name;
            private String email;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
        }
    }

    public static class GameReplayResponse {
        private String gameId;
        private Players players;
        private String result;
        private String endReason;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private List<MoveData> moves;

        // Getters and setters
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public Players getPlayers() { return players; }
        public void setPlayers(Players players) { this.players = players; }
        
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        
        public String getEndReason() { return endReason; }
        public void setEndReason(String endReason) { this.endReason = endReason; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public List<MoveData> getMoves() { return moves; }
        public void setMoves(List<MoveData> moves) { this.moves = moves; }

        public static class Players {
            private PlayerInfo white;
            private PlayerInfo black;

            public PlayerInfo getWhite() { return white; }
            public void setWhite(PlayerInfo white) { this.white = white; }
            
            public PlayerInfo getBlack() { return black; }
            public void setBlack(PlayerInfo black) { this.black = black; }
        }

        public static class PlayerInfo {
            private String email;
            private String name;

            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
            
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }

        public static class MoveData {
            private int moveNumber;
            private String from_sq;
            private String to_sq;
            private String pieceMoved;
            private String pieceCaptured;
            private boolean castling;
            private boolean enPassant;
            private boolean promotion;
            private String promotionPiece;
            private String squareCaptured;

            // Getters and setters
            public int getMoveNumber() { return moveNumber; }
            public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }
            
            public String getFrom_sq() { return from_sq; }
            public void setFrom_sq(String from_sq) { this.from_sq = from_sq; }
            
            public String getTo_sq() { return to_sq; }
            public void setTo_sq(String to_sq) { this.to_sq = to_sq; }
            
            public String getPieceMoved() { return pieceMoved; }
            public void setPieceMoved(String pieceMoved) { this.pieceMoved = pieceMoved; }
            
            public String getPieceCaptured() { return pieceCaptured; }
            public void setPieceCaptured(String pieceCaptured) { this.pieceCaptured = pieceCaptured; }
            
            public boolean getCastling() { return castling; }
            public void setCastling(boolean castling) { this.castling = castling; }
            
            public boolean getEnPassant() { return enPassant; }
            public void setEnPassant(boolean enPassant) { this.enPassant = enPassant; }
            
            public boolean getPromotion() { return promotion; }
            public void setPromotion(boolean promotion) { this.promotion = promotion; }
            
            public String getPromotionPiece() { return promotionPiece; }
            public void setPromotionPiece(String promotionPiece) { this.promotionPiece = promotionPiece; }
            
            public String getSquareCaptured() { return squareCaptured; }
            public void setSquareCaptured(String squareCaptured) { this.squareCaptured = squareCaptured; }
        }
    }
}
