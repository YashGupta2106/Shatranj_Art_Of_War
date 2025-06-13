package com.example.chess_app;


public class ShowMove {
    private String messageType;
    private int moveNumber;
    private String from_sq;
    private String to_sq;
    private String pieceCaptured="no";
    private String pieceMoved;
    private boolean enPassant=false;
    private boolean castling=false;
    private boolean promotion=false;
    private String promotionPiece;
    private int moveStatus=0;
    private String squareCaptured="no";

    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getMoveNumber() {
        return moveNumber;
    }
    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }
    public String getFrom_sq() {
        return from_sq;
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
    public boolean isEnPassant() {
        return enPassant;
    }
    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }
    public boolean isCastling() {
        return castling;
    }
    public void setCastling(boolean castling) {
        this.castling = castling;
    }
    public boolean isPromotion() {
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
    public int getMoveStatus() {
        return moveStatus;
    }
    public void setMoveStatus(int moveStatus) {
        this.moveStatus = moveStatus;
    }
    public String getSquareCaptured() {
        return squareCaptured;
    }
    public void setSquareCaptured(String squareCaptured) {
        this.squareCaptured = squareCaptured;
    }
    

}
