package com.example.chess_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Controller
public class OnlineGameController {

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private MatchmakingService matchmakingService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PlayerRepository playerRepository;
    
    // Track ready states per game
    private final Map<String, Integer> gameReadyCount = new ConcurrentHashMap<>();

    // Handle all WebSocket messages (main entry point)
    @MessageMapping("/chess-game")
    public void handleWebSocketMessage(WebSocketMessage message) {
        System.out.println("📥 Received STOMP message type: " + message.getMessageType());
        
        try {
            switch (message.getMessageType()) {
                case "FIND_MATCH":
                    handleFindMatch(message);
                    break;
                case "CANCEL_MATCH":
                    handleCancelMatch(message);
                    break;
                case "PLAYER_READY":
                    handlePlayerReady(message);
                    break;
                case "GAME_MOVE":
                    handleGameMove(message);
                    break;
                default:
                    System.err.println("❌ Unknown message type: " + message.getMessageType());
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing STOMP message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handle matchmaking requests
    private void handleFindMatch(WebSocketMessage message) {
        System.out.println("🔍 Processing FIND_MATCH for: " + message.getPlayerEmail());
        
        Player player = playerRepository.findByEmail(message.getPlayerEmail());
        // player.setUsername(message.getPlayerName());
        
        // Call matchmaking service (it will send MATCH_FOUND notifications)
        matchmakingService.findPlayerToPlay(player);
    }

    // Handle cancel matchmaking
    private void handleCancelMatch(WebSocketMessage message) {
        System.out.println("❌ Processing CANCEL_MATCH for: " + message.getPlayerEmail());
        
        Player player = new Player();
        player.setEmail(message.getPlayerEmail());
        player.setUsername(message.getPlayerName());
        
        matchmakingService.removePlayerFromQueue(player);
        
        // Send confirmation to player
        CancelResponse response = new CancelResponse();
        response.setMessageType("MATCH_CANCELLED");
        response.setMessage("Matchmaking cancelled");
        
        messagingTemplate.convertAndSend("/topic/" + message.getPlayerEmail(), response);
    }

    // Handle player ready signals
    private void handlePlayerReady(WebSocketMessage message) {
        System.out.println("👤 Processing PLAYER_READY for game: " + message.getGameId());
        
        String gameId = message.getGameId();
        
        // Increment ready count for this game
        int readyCount = gameReadyCount.merge(gameId, 1, Integer::sum);
        
        System.out.println("📊 Ready count for game " + gameId + ": " + readyCount);
        
        // Create ready response
        ReadyResponse response = new ReadyResponse();
        response.setMessageType("PLAYER_READY");
        response.setGameId(gameId);
        response.setPlayersReady(readyCount);
        
        // Send to all players in this game
        messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
        
        // If both players are ready, start the game
        if (readyCount >= 2) {
            startGame(gameId);
        }
    }

    // Start the game when both players are ready
    private void startGame(String gameId) {
        System.out.println("🚀 Starting game: " + gameId);
        
        // Get game from repository
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            System.err.println("❌ Game not found: " + gameId);
            return;
        }
        
        // Update game status
        game.setActive(true);
        // game.setGameStatus("active");
        gameRepository.save(game);
        
        // Create game start response
        GameStartResponse response = new GameStartResponse();
        response.setMessageType("GAME_START");
        response.setGameId(gameId);
        response.setCurrentPlayer("white"); // White always starts
        response.setMessage("Game started! White's turn.");
        
        // Send to all players in this game
        messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
        
        // Clean up ready count
        gameReadyCount.remove(gameId);
    }

    // Handle game moves (existing logic, but adapted)
    private void handleGameMove(WebSocketMessage message) {
        System.out.println("🎯 Processing GAME_MOVE for game: " + message.getGameId());
        
        // Convert WebSocketMessage to MoveMessage for existing logic
        MoveMessage moveMessage = new MoveMessage();
        moveMessage.setSquareClicked(message.getSquareClicked());
        moveMessage.setColor(message.getColor());
        moveMessage.setHasCircle(message.getHasCircle());
        moveMessage.setWhiteTime(message.getWhiteTime());
        moveMessage.setBlackTime(message.getBlackTime());
        moveMessage.setPromoteTo(message.getPromoteTo());
        moveMessage.setButtonClicked(message.getButtonClicked());
        moveMessage.setDrawOffer(message.getDrawOffer());
        
        // Call existing move processing logic
        MoveResponse response = processMove(message.getGameId(), moveMessage);

        if(response.getClickStatus().equals("highlight")){
            System.out.println("its a highlight move... send to respective player only");
            System.out.println("sending message to player: " + message.getPlayerEmail());
            messagingTemplate.convertAndSend("/topic/" + message.getPlayerEmail(),response);
        }
        else{
            System.out.println("sending this game response for the move played");
            // Send response to all players in this game
            messagingTemplate.convertAndSend("/topic/game/" + message.getGameId(), response);
        }
        
    }

    // Existing move processing logic (keeping your current implementation)
    public MoveResponse processMove(String gameId, MoveMessage moveMessage) {
        System.out.println("Received move: " + moveMessage.getSquareClicked() + " for game: " + gameId);
        
        // 1. Retrieve the game from the repository
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            return createErrorResponse("Game not found");
        }
        
        MoveResponse response = new MoveResponse();
        
        // 2. Validate the move (player's turn, valid move, etc.)
        boolean isWhiteTurn = (game.getMoves().size() % 2 == 0);
        boolean isPlayerWhite = moveMessage.getColor().equals("white");
        int whiteTime = moveMessage.getWhiteTime();
        int blackTime = moveMessage.getBlackTime();
        String squareClicked = moveMessage.getSquareClicked();
        String hasCircle = moveMessage.getHasCircle();
        String playerColor = moveMessage.getColor();

        response.setGameId(gameId);
        response.setSquareClicked(moveMessage.getSquareClicked());
        response.setColor(moveMessage.getColor());

        // Check for time-based game endings
        if(whiteTime == 0){
            response.setGameEnded(true);
            response.setGameEndReason("White ran out of Time");
            response.setWinner("black");
            response.setTimeUp(true);
            return response;
        }
        else if(blackTime == 0){
            response.setGameEnded(true);
            response.setGameEndReason("Black ran out of Time");
            response.setWinner("white");
            response.setTimeUp(true);
            return response;
        }

        // Handle draw offers
        if(isPlayerWhite){
            if(moveMessage.getDrawOffer().equals("yes")){
                response.setGameEndCheck(true);
                game.setWhiteDrawOffer("yes");
                gameRepository.save(game);
                if(game.getBlackDrawOffer().equals("yes") && game.getWhiteDrawOffer().equals("yes")){
                    game.setGameEndReason("Draw");
                    game.setWinner("draw");
                    game.setActive(false);
                    gameRepository.save(game);
                    response.setGameEnded(true);
                    response.setGameEndReason("Draw");
                    response.setWinner("draw");
                }
            }
            else if(moveMessage.getDrawOffer().equals("no")){
                response.setGameEndCheck(false);
                game.setBlackDrawOffer("no");
                game.setWhiteDrawOffer("no");
                gameRepository.save(game);
            }
        }
        else if(!isPlayerWhite){
            if(moveMessage.getDrawOffer().equals("yes")){
                response.setGameEndCheck(true);
                game.setBlackDrawOffer("yes");
                gameRepository.save(game);
                if(game.getBlackDrawOffer().equals("yes") && game.getWhiteDrawOffer().equals("yes")){
                    game.setGameEndReason("Draw");
                    game.setWinner("draw");
                    game.setActive(false);
                    gameRepository.save(game);
                    response.setGameEnded(true);
                    response.setGameEndReason("Draw");
                    response.setWinner("draw");
                }
            }
            else if(moveMessage.getDrawOffer().equals("no")){
                response.setGameEndCheck(false);
                game.setBlackDrawOffer("no");
                game.setWhiteDrawOffer("no");
                gameRepository.save(game);
            } 
        }

        // Validate turn and process move
        if(isWhiteTurn && isPlayerWhite){
            response.setIsValid(true);
            processPlayerMove(moveMessage, response, game, squareClicked, hasCircle, playerColor);
            // Save updated game state
            // gameRepository.save(game);    
        }
        else if(!isWhiteTurn && !isPlayerWhite){
            response.setIsValid(true);
            processPlayerMove(moveMessage, response, game, squareClicked, hasCircle, playerColor);
            // Save updated game state
            // gameRepository.save(game);
        }
        else{
            response.setIsValid(false);
            response.setErrorMessage("Not your turn");
        }
        
        // Save updated game state
        System.out.println("saving the game");
        gameRepository.save(game);
        System.out.println("game saved successfully");
        System.out.println("moving back to controller");
        return response;
    }

    // Helper method to process player moves
    private void processPlayerMove(MoveMessage moveMessage, MoveResponse response, Game game, 
                                 String squareClicked, String hasCircle, String playerColor) {
        
        if(moveMessage.getButtonClicked().equals("draw")){
            response.setGameEndCheck(true);
            response.setDrawOffer("yes");
        }
        else if(moveMessage.getButtonClicked().equals("resign")){
            response.setGameEndCheck(true);
            response.setGameEnded(true);
            response.setGameEndReason(playerColor.equals("white") ? "White Resigned" : "Black Resigned");
            response.setWinner(playerColor.equals("white") ? "black" : "white");
        }
        else if(moveMessage.getButtonClicked().equals("50 moves")){
            response.setGameEndCheck(true);
            response.setGameEnded(true);
            response.setGameEndReason("50 moves");
            response.setWinner("draw");
        }
        else if(moveMessage.getButtonClicked().equals("3 fold")){
            response.setGameEndCheck(true);
            response.setGameEnded(true);
            response.setGameEndReason("3 fold repetition");
            response.setWinner("draw");
        }
        else {
            if(!(moveMessage.getPromoteTo().equals("no"))){
                response.setPromoteTo(moveMessage.getPromoteTo());
            }
            // Call game handler for actual move processing
            OnlineGameHandler gameHandler = new OnlineGameHandler(game, squareClicked, hasCircle, playerColor, response);
            gameHandler.handleSquareClick();
            if(response.isGameEnded()==true){
                System.out.println("Game has ended, time to remove from playing list.");
                matchmakingService.removePlayerFromActiveGame(game.getWhitePlayer());
                matchmakingService.removePlayerFromActiveGame(game.getBlackPlayer());

            }
            
        }
    }

    // Helper method to create error responses
    private MoveResponse createErrorResponse(String errorMessage) {
        MoveResponse response = new MoveResponse();
        response.setIsValid(false);
        response.setErrorMessage(errorMessage);
        return response;
    }

    // Response classes (you'll need to create these)
    public static class WebSocketMessage {
        private String messageType;
        private String playerEmail;
        private String playerName;
        private String gameId;
        private String squareClicked;
        private String color;
        private String hasCircle;
        private int whiteTime;
        private int blackTime;
        private String promoteTo;
        private String buttonClicked;
        private String drawOffer;

        // Getters and setters
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getPlayerEmail() { return playerEmail; }
        public void setPlayerEmail(String playerEmail) { this.playerEmail = playerEmail; }
        
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public String getSquareClicked() { return squareClicked; }
        public void setSquareClicked(String squareClicked) { this.squareClicked = squareClicked; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public String getHasCircle() { return hasCircle; }
        public void setHasCircle(String hasCircle) { this.hasCircle = hasCircle; }
        
        public int getWhiteTime() { return whiteTime; }
        public void setWhiteTime(int whiteTime) { this.whiteTime = whiteTime; }
        
        public int getBlackTime() { return blackTime; }
        public void setBlackTime(int blackTime) { this.blackTime = blackTime; }
        
        public String getPromoteTo() { return promoteTo; }
        public void setPromoteTo(String promoteTo) { this.promoteTo = promoteTo; }
        
        public String getButtonClicked() { return buttonClicked; }
        public void setButtonClicked(String buttonClicked) { this.buttonClicked = buttonClicked; }
        
        public String getDrawOffer() { return drawOffer; }
        public void setDrawOffer(String drawOffer) { this.drawOffer = drawOffer; }
    }

    public static class ReadyResponse {
        private String messageType;
        private String gameId;
        private int playersReady;

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public int getPlayersReady() { return playersReady; }
        public void setPlayersReady(int playersReady) { this.playersReady = playersReady; }

    }

    public static class GameStartResponse {
        private String messageType;
        private String gameId;
        private String currentPlayer;
        private String message;
        private Object initialBoard; // You can define a proper Board class

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        
        public String getCurrentPlayer() { return currentPlayer; }
        public void setCurrentPlayer(String currentPlayer) { this.currentPlayer = currentPlayer; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getInitialBoard() { return initialBoard; }
        public void setInitialBoard(Object initialBoard) { this.initialBoard = initialBoard; }
    }

    public static class CancelResponse {
        private String messageType;
        private String message;

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

