package com.example.chess_app;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Document(collection = "games")
public class Game {
    @Id
    private String gameId; // Unique identifier for the game
    private boolean isActive=false; // tells if game is going or over
    private String whitePlayerName;
    private String blackPlayerName;
    private String whitePlayer;
    private String blackPlayer;
    private String gameName;
    private List<Piece> whitePieces; // All white pieces
    private List<Piece> blackPieces; // All black pieces
    private String winner;
    private String gameEndReason; // Reason for the game ending (e.g., checkmate, stalemate)
    private List<Move> moves; // List of moves made during the game
    private LocalDateTime startTime;
    private String lastSquareClicked="no"; // last square clicked by the player
    private int moveNumber=0;
    private String whiteDrawOffer="no"; // to check if draw offer is accepted by both players
    private String blackDrawOffer="no"; // to check if draw offer is accepted by both players

    public Game(String whitePlayer, String blackPlayer, String gameName, List<Piece> whitePieces, List<Piece> blackPieces) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameName = gameName;
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.moves =new ArrayList<>() ;
        this.startTime = LocalDateTime.now();
    }
    // Getters and setters
    public String getWhitePlayer() {
        return whitePlayer;
    }
    public String getBlackPlayer() {
        return blackPlayer;
    }
    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }
    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }


    public String getId() {
        return gameId;
    }

    public void setId(String id) {
        this.gameId = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public void setWhitePieces(List<Piece> whitePieces) {
        this.whitePieces = whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }

    public void setBlackPieces(List<Piece> blackPieces) {
        this.blackPieces = blackPieces;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void addMove(Move move) {
        this.moves.add(move);
    }
    public String getWinner() {
        return winner;
    }
    public void setWinner(String winner) {
        this.winner = winner;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getLastSquareClicked() {
        return lastSquareClicked;
    }

    public void setLastSquareClicked(String lastSquareClicked) {
        this.lastSquareClicked = lastSquareClicked;
    }

    public int getMoveNumber() {
        return moveNumber;
    }
    public void setMoveNumber() {
        this.moveNumber +=1;
    }
    public String getGameEndReason() {
        return gameEndReason;
    }
    public void setGameEndReason(String gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    public String getWhiteDrawOffer() {
        return whiteDrawOffer;
    }
    public void setWhiteDrawOffer(String whiteDrawOffer) {
        this.whiteDrawOffer = whiteDrawOffer;
    }
    public String getBlackDrawOffer() {
        return blackDrawOffer;
    }
    public void setBlackDrawOffer(String blackDrawOffer) {
        this.blackDrawOffer = blackDrawOffer;
    }
    public String getWhitePlayerName() {
        return whitePlayerName;
    }
    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName = whitePlayerName;
    }
    public String getBlackPlayerName() {
        return blackPlayerName;
    }
    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
    }
    
}
