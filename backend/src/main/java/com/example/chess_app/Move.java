package com.example.chess_app;

public class Move {
    private int moveNumber=0;
    private String from_sq;
    private String to_sq;
    private String pieceCaptured;
    private String pieceMoved;
    private boolean whiteKingInCheck=false;
    private boolean blackKingInCheck=false;
    private boolean enPassant=false;
    private boolean castling=false;
    private boolean promotion=false;
    private String promotionPiece;
    public Move(String from_sq, String to_sq) {
        this.from_sq = from_sq;
        this.to_sq = to_sq;
        this.pieceCaptured = "no";
    }
    public String getFrom_sq() {
        return from_sq;
    }
    public void setMoveNumber(int moveNo){
        moveNumber=moveNo;
    }
    public int getMoveNumber(){
        return moveNumber;
    }
    public void setFrom_sq(String from_sq) {
        this.from_sq = from_sq;
    }
    public String getTo_sq() {
        return to_sq;
    }
    public void setTo_sq(String to_sq) {
        this.to_sq = to_sq;
    }
    public String getPieceCaptured() {
        return pieceCaptured;
    }
    public void setPieceCaptured(String pieceCaptured) {
        this.pieceCaptured = pieceCaptured;
    }
    public String getPieceMoved() {
        return pieceMoved;
    }
    public void setPieceMoved(String pieceMoved) {
        this.pieceMoved = pieceMoved;
    }
    public boolean getWhiteKingInCheck() {
        return whiteKingInCheck;
    }
    public void setWhiteKingInCheck(boolean whiteKingInCheck) {
        this.whiteKingInCheck = whiteKingInCheck;
    }
    public boolean getBlackKingInCheck() {
        return blackKingInCheck;
    }
    public void setBlackKingInCheck(boolean blackKingInCheck) {
        this.blackKingInCheck = blackKingInCheck;
    }
    public boolean getEnPassant() {
        return enPassant;
    }
    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }
    public boolean getCastling() {
        return castling;
    }
    public void setCastling(boolean castling) {
        this.castling = castling;
    }
    public boolean getPromotion() {
        return promotion;
    }
    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }
    public String getPromotionPiece() {
        return promotionPiece;
    }
    public void setPromotionPiece(String promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

}
