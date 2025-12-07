package com.example.chess_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Game entity class.
 * Tests game initialization, state management, and data integrity.
 */
@DisplayName("Game Model Tests")
class GameTest {

    private Game game;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;

    @BeforeEach
    void setUp() {
        // Initialize pieces for testing
        whitePieces = createWhitePieces();
        blackPieces = createBlackPieces();
        
        // Create a game instance
        game = new Game("white@example.com", "black@example.com", "Test Game", whitePieces, blackPieces);
    }

    @Test
    @DisplayName("Should create game with valid players and pieces")
    void testGameCreation() {
        assertThat(game).isNotNull();
        assertThat(game.getWhitePlayer()).isEqualTo("white@example.com");
        assertThat(game.getBlackPlayer()).isEqualTo("black@example.com");
        assertThat(game.getGameName()).isEqualTo("Test Game");
        assertThat(game.getWhitePieces()).hasSize(16);
        assertThat(game.getBlackPieces()).hasSize(16);
        assertThat(game.getMoves()).isEmpty();
        assertThat(game.isActive()).isFalse(); // Default state
        assertThat(game.getStartTime()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize game with default values")
    void testDefaultValues() {
        assertThat(game.isActive()).isFalse();
        assertThat(game.getMoveNumber()).isEqualTo(0);
        assertThat(game.getLastSquareClicked()).isEqualTo("no");
        assertThat(game.getWhiteDrawOffer()).isEqualTo("no");
        assertThat(game.getBlackDrawOffer()).isEqualTo("no");
        assertThat(game.getWinner()).isNull();
        assertThat(game.getGameEndReason()).isNull();
        assertThat(game.getEndTime()).isNull();
    }

    @Test
    @DisplayName("Should add moves to game")
    void testAddMove() {
        Move move1 = new Move("e2", "e4");
        Move move2 = new Move("e7", "e5");
        
        game.addMove(move1);
        game.addMove(move2);
        
        assertThat(game.getMoves()).hasSize(2);
        assertThat(game.getMoves()).containsExactly(move1, move2);
    }

    @Test
    @DisplayName("Should increment move number")
    void testMoveNumberIncrement() {
        assertThat(game.getMoveNumber()).isEqualTo(0);
        
        game.setMoveNumber();
        assertThat(game.getMoveNumber()).isEqualTo(1);
        
        game.setMoveNumber();
        assertThat(game.getMoveNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should set game as active")
    void testActivateGame() {
        assertThat(game.isActive()).isFalse();
        
        game.setActive(true);
        
        assertThat(game.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should set winner and game end reason")
    void testGameEnd() {
        game.setWinner("white");
        game.setGameEndReason("Checkmate");
        game.setActive(false);
        game.setEndTime(LocalDateTime.now());
        
        assertThat(game.getWinner()).isEqualTo("white");
        assertThat(game.getGameEndReason()).isEqualTo("Checkmate");
        assertThat(game.isActive()).isFalse();
        assertThat(game.getEndTime()).isNotNull();
    }

    @Test
    @DisplayName("Should manage draw offers")
    void testDrawOffers() {
        assertThat(game.getWhiteDrawOffer()).isEqualTo("no");
        assertThat(game.getBlackDrawOffer()).isEqualTo("no");
        
        game.setWhiteDrawOffer("yes");
        assertThat(game.getWhiteDrawOffer()).isEqualTo("yes");
        assertThat(game.getBlackDrawOffer()).isEqualTo("no");
        
        game.setBlackDrawOffer("yes");
        assertThat(game.getBlackDrawOffer()).isEqualTo("yes");
    }

    @Test
    @DisplayName("Should set and get player names")
    void testPlayerNames() {
        game.setWhitePlayerName("Alice");
        game.setBlackPlayerName("Bob");
        
        assertThat(game.getWhitePlayerName()).isEqualTo("Alice");
        assertThat(game.getBlackPlayerName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Should set and get last square clicked")
    void testLastSquareClicked() {
        game.setLastSquareClicked("e4");
        assertThat(game.getLastSquareClicked()).isEqualTo("e4");
    }

    @Test
    @DisplayName("Should manage game ID")
    void testGameId() {
        game.setId("game123");
        assertThat(game.getId()).isEqualTo("game123");
    }

    @Test
    @DisplayName("Should track start and end times")
    void testTimestamps() {
        LocalDateTime startTime = game.getStartTime();
        assertThat(startTime).isNotNull();
        assertThat(startTime).isBeforeOrEqualTo(LocalDateTime.now());
        
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(30);
        game.setEndTime(endTime);
        assertThat(game.getEndTime()).isEqualTo(endTime);
        assertThat(game.getEndTime()).isAfter(game.getStartTime());
    }

    @Test
    @DisplayName("Should maintain separate piece lists for white and black")
    void testPieceSeparation() {
        assertThat(game.getWhitePieces()).isNotSameAs(game.getBlackPieces());
        assertThat(game.getWhitePieces()).hasSize(16);
        assertThat(game.getBlackPieces()).hasSize(16);
        
        // Verify white pieces are white
        game.getWhitePieces().forEach(piece -> 
            assertThat(piece.getColor()).isEqualTo("white")
        );
        
        // Verify black pieces are black
        game.getBlackPieces().forEach(piece -> 
            assertThat(piece.getColor()).isEqualTo("black")
        );
    }

    // Helper methods to create pieces
    private List<Piece> createWhitePieces() {
        List<Piece> pieces = new ArrayList<>();
        
        // Pawns
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn("white", "a" + (i + 1), "no"));
        }
        
        // Other pieces
        pieces.add(new Rook("white", "a1"));
        pieces.add(new Knight("white", "b1"));
        pieces.add(new Bishop("white", "c1"));
        pieces.add(new Queen("white", "d1"));
        pieces.add(new King("white", "e1"));
        pieces.add(new Bishop("white", "f1"));
        pieces.add(new Knight("white", "g1"));
        pieces.add(new Rook("white", "h1"));
        
        return pieces;
    }

    private List<Piece> createBlackPieces() {
        List<Piece> pieces = new ArrayList<>();
        
        // Pawns
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn("black", "a" + (i + 1), "no"));
        }
        
        // Other pieces
        pieces.add(new Rook("black", "a8"));
        pieces.add(new Knight("black", "b8"));
        pieces.add(new Bishop("black", "c8"));
        pieces.add(new Queen("black", "d8"));
        pieces.add(new King("black", "e8"));
        pieces.add(new Bishop("black", "f8"));
        pieces.add(new Knight("black", "g8"));
        pieces.add(new Rook("black", "h8"));
        
        return pieces;
    }
}
