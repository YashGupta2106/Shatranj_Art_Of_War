package com.example.chess_app;

import java.util.ArrayList;
import java.util.List;

/**
 * Test fixture helper for creating chess pieces in tests.
 * This is in the main package to access package-private piece classes.
 */
public class TestPieceFactory {

    public static List<Piece> createWhitePieces() {
        List<Piece> pieces = new ArrayList<>();
        // Add 8 pawns
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn("white", "a" + (i + 1), "no"));
        }
        // Add other pieces
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

    public static List<Piece> createBlackPieces() {
        List<Piece> pieces = new ArrayList<>();
        // Add 8 pawns
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn("black", "a" + (i + 1), "no"));
        }
        // Add other pieces
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

    public static List<Piece> createPiecesByColor(String color) {
        return "white".equals(color) ? createWhitePieces() : createBlackPieces();
    }
}
