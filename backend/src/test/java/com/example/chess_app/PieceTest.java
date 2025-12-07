package com.example.chess_app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for all chess piece classes.
 * 
 * NOTE: These tests are in the same package (com.example.chess_app) as the piece classes
 * because Pawn, Rook, Knight, Bishop, Queen, and King are package-private classes.
 */
@DisplayName("Piece Model Tests")
class PieceTest {

    // ==================== Pawn Tests ====================
    
    @Test
    @DisplayName("Should create white pawn with correct attributes")
    void testWhitePawnCreation() {
        Pawn pawn = new Pawn("white", "e2", "no");
        
        assertThat(pawn.getColor()).isEqualTo("white");
        assertThat(pawn.getSquare()).isEqualTo("e2");
        assertThat(pawn.getPieceType()).isEqualTo("pawn");
        assertThat(pawn.getIsAlive()).isTrue();
        assertThat(pawn.getIsPinned()).isFalse();
        assertThat(pawn.getFirstMove()).isEqualTo("no");
        assertThat(pawn.getEnPassant()).isEqualTo("no");
        assertThat(pawn.getGetEnPassant()).isEqualTo("no");
    }

    @Test
    @DisplayName("Should create black pawn with correct attributes")
    void testBlackPawnCreation() {
        Pawn pawn = new Pawn("black", "e7", "no");
        
        assertThat(pawn.getColor()).isEqualTo("black");
        assertThat(pawn.getSquare()).isEqualTo("e7");
        assertThat(pawn.getPieceType()).isEqualTo("pawn");
    }

    @Test
    @DisplayName("Should manage pawn first move status")
    void testPawnFirstMove() {
        Pawn pawn = new Pawn("white", "e2", "no");
        
        assertThat(pawn.getFirstMove()).isEqualTo("no");
        pawn.setFirstMove("yes");
        assertThat(pawn.getFirstMove()).isEqualTo("yes");
    }

    @Test
    @DisplayName("Should manage en passant eligibility")
    void testPawnEnPassant() {
        Pawn pawn = new Pawn("white", "e4", "e5");
        
        assertThat(pawn.getEnPassant()).isEqualTo("e5");
        pawn.setEnPassant("e6");
        assertThat(pawn.getEnPassant()).isEqualTo("e6");
        
        pawn.setGetEnPassant("yes");
        assertThat(pawn.getGetEnPassant()).isEqualTo("yes");
    }

    // ==================== Rook Tests ====================
    
    @Test
    @DisplayName("Should create rook with correct attributes")
    void testRookCreation() {
        Rook rook = new Rook("white", "a1");
        
        assertThat(rook.getColor()).isEqualTo("white");
        assertThat(rook.getSquare()).isEqualTo("a1");
        assertThat(rook.getPieceType()).isEqualTo("rook");
        assertThat(rook.getIsAlive()).isTrue();
        assertThat(rook.getFirstMove()).isEqualTo("no");
    }

    @Test
    @DisplayName("Should manage rook first move for castling")
    void testRookFirstMove() {
        Rook rook = new Rook("white", "a1");
        
        assertThat(rook.getFirstMove()).isEqualTo("no");
        rook.setFirstMove("yes");
        assertThat(rook.getFirstMove()).isEqualTo("yes");
    }

    @Test
    @DisplayName("Should manage castling destination")
    void testRookCastleTo() {
        Rook rook = new Rook("white", "h1");
        
        rook.setCastleTo("f1");
        assertThat(rook.getCastleTo()).isEqualTo("f1");
    }

    // ==================== Knight Tests ====================
    
    @ParameterizedTest
    @ValueSource(strings = {"white", "black"})
    @DisplayName("Should create knight with different colors")
    void testKnightCreation(String color) {
        Knight knight = new Knight(color, "b1");
        
        assertThat(knight.getColor()).isEqualTo(color);
        assertThat(knight.getPieceType()).isEqualTo("knight");
        assertThat(knight.getIsAlive()).isTrue();
    }

    // ==================== Bishop Tests ====================
    
    @Test
    @DisplayName("Should create bishop with correct attributes")
    void testBishopCreation() {
        Bishop bishop = new Bishop("white", "c1");
        
        assertThat(bishop.getColor()).isEqualTo("white");
        assertThat(bishop.getSquare()).isEqualTo("c1");
        assertThat(bishop.getPieceType()).isEqualTo("bishop");
        assertThat(bishop.getIsAlive()).isTrue();
    }

    // ==================== Queen Tests ====================
    
    @Test
    @DisplayName("Should create queen with correct attributes")
    void testQueenCreation() {
        Queen queen = new Queen("black", "d8");
        
        assertThat(queen.getColor()).isEqualTo("black");
        assertThat(queen.getSquare()).isEqualTo("d8");
        assertThat(queen.getPieceType()).isEqualTo("queen");
        assertThat(queen.getIsAlive()).isTrue();
    }

    // ==================== King Tests ====================
    
    @Test
    @DisplayName("Should create king with correct attributes")
    void testKingCreation() {
        King king = new King("white", "e1");
        
        assertThat(king.getColor()).isEqualTo("white");
        assertThat(king.getSquare()).isEqualTo("e1");
        assertThat(king.getPieceType()).isEqualTo("king");
        assertThat(king.getIsAlive()).isTrue();
        assertThat(king.getFirstMove()).isEqualTo("no");
        assertThat(king.getUnderCheck()).isFalse();
        assertThat(king.getCheckByWhom()).isEmpty();
    }

    @Test
    @DisplayName("Should manage king check status")
    void testKingCheckStatus() {
        King king = new King("white", "e1");
        
        assertThat(king.getUnderCheck()).isFalse();
        king.setUnderCheck(true);
        assertThat(king.getUnderCheck()).isTrue();
    }

    @Test
    @DisplayName("Should track pieces giving check to king")
    void testKingCheckByWhom() {
        King king = new King("white", "e1");
        
        assertThat(king.getCheckByWhom()).isEmpty();
        
        king.setCheckByWhom("d2");
        assertThat(king.getCheckByWhom()).contains("d2");
        
        king.setCheckByWhom("f3");
        assertThat(king.getCheckByWhom()).containsExactly("d2", "f3");
    }

    @Test
    @DisplayName("Should manage king first move for castling")
    void testKingFirstMove() {
        King king = new King("white", "e1");
        
        assertThat(king.getFirstMove()).isEqualTo("no");
        king.setFirstMove("yes");
        assertThat(king.getFirstMove()).isEqualTo("yes");
    }

    // ==================== Common Piece Tests ====================
    
    @Test
    @DisplayName("Should toggle piece alive status")
    void testPieceAliveToggle() {
        Piece knight = new Knight("white", "b1");
        
        assertThat(knight.getIsAlive()).isTrue();
        knight.setIsAlive(); // Toggles
        assertThat(knight.getIsAlive()).isFalse();
        knight.setIsAlive(); // Toggles back
        assertThat(knight.getIsAlive()).isTrue();
    }

    @Test
    @DisplayName("Should manage piece square position")
    void testPieceSquareUpdate() {
        Piece bishop = new Bishop("white", "c1");
        
        assertThat(bishop.getSquare()).isEqualTo("c1");
        bishop.setSquare("e3");
        assertThat(bishop.getSquare()).isEqualTo("e3");
    }

    @Test
    @DisplayName("Should manage pin status")
    void testPinStatus() {
        Piece rook = new Rook("white", "a1");
        
        assertThat(rook.getIsPinned()).isFalse();
        assertThat(rook.getPinnedBy()).isEqualTo("no");
        assertThat(rook.getPins()).isEqualTo("no");
        
        rook.setIsPinned(true);
        rook.setPinnedBy("e1");
        assertThat(rook.getIsPinned()).isTrue();
        assertThat(rook.getPinnedBy()).isEqualTo("e1");
    }

    @Test
    @DisplayName("Should track pieces this piece is pinning")
    void testPinsProperty() {
        Piece bishop = new Bishop("white", "c1");
        
        bishop.setPins("e3");
        assertThat(bishop.getPins()).isEqualTo("e3");
    }

    @Test
    @DisplayName("Should set and get piece color")
    void testPieceColor() {
        Piece queen = new Queen("white", "d1");
        
        assertThat(queen.getColor()).isEqualTo("white");
        queen.setColor("black");
        assertThat(queen.getColor()).isEqualTo("black");
    }

    @Test
    @DisplayName("Should set and get piece type")
    void testPieceType() {
        Piece pawn = new Pawn("white", "e2", "no");
        
        assertThat(pawn.getPieceType()).isEqualTo("pawn");
        pawn.setPieceType("queen"); // Represents promotion
        assertThat(pawn.getPieceType()).isEqualTo("queen");
    }
}
