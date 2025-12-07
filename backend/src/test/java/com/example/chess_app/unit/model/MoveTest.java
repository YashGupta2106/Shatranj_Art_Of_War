package com.example.chess_app.unit.model;

import com.example.chess_app.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Move class.
 * Tests move creation, validation, and special move flags.
 */
@DisplayName("Move Model Tests")
class MoveTest {

    private Move move;

    @BeforeEach
    void setUp() {
        move = new Move("e2", "e4");
    }

    @Test
    @DisplayName("Should create move with from and to squares")
    void testMoveCreation() {
        assertThat(move).isNotNull();
        assertThat(move.getFrom_sq()).isEqualTo("e2");
        assertThat(move.getTo_sq()).isEqualTo("e4");
    }

    @Test
    @DisplayName("Should initialize with default values")
    void testDefaultValues() {
        assertThat(move.getPieceCaptured()).isEqualTo("no");
        assertThat(move.getMoveNumber()).isEqualTo(0);
        assertThat(move.getWhiteKingInCheck()).isFalse();
        assertThat(move.getBlackKingInCheck()).isFalse();
        assertThat(move.getEnPassant()).isFalse();
        assertThat(move.getCastling()).isFalse();
        assertThat(move.getPromotion()).isFalse();
        assertThat(move.getPromotionPiece()).isEqualTo("no");
        assertThat(move.getSquareCaptured()).isEqualTo("no");
    }

    @Test
    @DisplayName("Should set and get move number")
    void testMoveNumber() {
        move.setMoveNumber(1);
        assertThat(move.getMoveNumber()).isEqualTo(1);
        
        move.setMoveNumber(42);
        assertThat(move.getMoveNumber()).isEqualTo(42);
    }

    @Test
    @DisplayName("Should set and get piece moved")
    void testPieceMoved() {
        move.setPieceMoved("pawn");
        assertThat(move.getPieceMoved()).isEqualTo("pawn");
        
        move.setPieceMoved("knight");
        assertThat(move.getPieceMoved()).isEqualTo("knight");
    }

    @Test
    @DisplayName("Should handle piece capture")
    void testPieceCapture() {
        assertThat(move.getPieceCaptured()).isEqualTo("no");
        
        move.setPieceCaptured("pawn");
        assertThat(move.getPieceCaptured()).isEqualTo("pawn");
        
        move.setSquareCaptured("e5");
        assertThat(move.getSquareCaptured()).isEqualTo("e5");
    }

    @Test
    @DisplayName("Should handle check flags")
    void testCheckFlags() {
        move.setWhiteKingInCheck(true);
        assertThat(move.getWhiteKingInCheck()).isTrue();
        assertThat(move.getBlackKingInCheck()).isFalse();
        
        move.setBlackKingInCheck(true);
        assertThat(move.getBlackKingInCheck()).isTrue();
        
        move.setWhiteKingInCheck(false);
        assertThat(move.getWhiteKingInCheck()).isFalse();
    }

    @Test
    @DisplayName("Should handle en passant flag")
    void testEnPassant() {
        assertThat(move.getEnPassant()).isFalse();
        
        move.setEnPassant(true);
        assertThat(move.getEnPassant()).isTrue();
    }

    @Test
    @DisplayName("Should handle castling flag")
    void testCastling() {
        assertThat(move.getCastling()).isFalse();
        
        move.setCastling(true);
        assertThat(move.getCastling()).isTrue();
    }

    @Test
    @DisplayName("Should handle promotion")
    void testPromotion() {
        assertThat(move.getPromotion()).isFalse();
        assertThat(move.getPromotionPiece()).isEqualTo("no");
        
        move.setPromotion(true);
        move.setPromotionPiece("queen");
        
        assertThat(move.getPromotion()).isTrue();
        assertThat(move.getPromotionPiece()).isEqualTo("queen");
    }

    @Test
    @DisplayName("Should set from and to squares correctly")
    void testSquareSetters() {
        move.setFrom_sq("a2");
        move.setTo_sq("a4");
        
        assertThat(move.getFrom_sq()).isEqualTo("a2");
        assertThat(move.getTo_sq()).isEqualTo("a4");
    }

    @Test
    @DisplayName("Should handle multiple move attributes together")
    void testComplexMove() {
        // Simulate a pawn capture with check
        move.setPieceMoved("pawn");
        move.setPieceCaptured("knight");
        move.setSquareCaptured("e5");
        move.setWhiteKingInCheck(true);
        move.setMoveNumber(15);
        
        assertThat(move.getPieceMoved()).isEqualTo("pawn");
        assertThat(move.getPieceCaptured()).isEqualTo("knight");
        assertThat(move.getSquareCaptured()).isEqualTo("e5");
        assertThat(move.getWhiteKingInCheck()).isTrue();
        assertThat(move.getMoveNumber()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should handle pawn promotion to different pieces")
    void testPromotionToDifferentPieces() {
        String[] pieces = {"queen", "rook", "bishop", "knight"};
        
        for (String piece : pieces) {
            move.setPromotion(true);
            move.setPromotionPiece(piece);
            
            assertThat(move.getPromotion()).isTrue();
            assertThat(move.getPromotionPiece()).isEqualTo(piece);
        }
    }
}
