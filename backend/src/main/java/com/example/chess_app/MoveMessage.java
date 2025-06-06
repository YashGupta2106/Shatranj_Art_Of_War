package com.example.chess_app;

public class MoveMessage {
    private String GameId;
    private String Color;
    private String squareClicked;
    private int whiteTime;
    private int blackTime;
    private String hasCircle="no";  // will help telling if move is possible on this move or not
    private String promoteTo="no"; // takes value as queen/rook/bishop/knight
    public String buttonClicked="no"; // takes values as draw,resign,50 moves,3 fold
    public String drawOffer="NA"; // this is used to send draw offer to both players
    public void setGameId(String gameId) {
        this.GameId = gameId;
    }
    public String getGameId() {
        return GameId;
    }
    public void setColor(String color) {
        this.Color = color;
    }
    public String getColor() {
        return Color;
    }
    public void setSquareClicked(String squareClicked) {
        this.squareClicked = squareClicked;
    }
    public String getSquareClicked() {
        return squareClicked;
    }
    public void setWhiteTime(int whiteTime) {
        this.whiteTime = whiteTime;
    }
    public int getWhiteTime() {
        return whiteTime;
    }
    public void setBlackTime(int blackTime) {
        this.blackTime = blackTime;
    }
    public int getBlackTime() {
        return blackTime;
    }
    public void setHasCircle(String hasCircle) {
        this.hasCircle = hasCircle;
    }
    public String getHasCircle() {
        return hasCircle;
    }
    public void setPromoteTo(String promoteTo) {
        this.promoteTo = promoteTo;
    }
    public String getPromoteTo() {
        return promoteTo;
    }
    public void setButtonClicked(String buttonClicked) {
        this.buttonClicked = buttonClicked;
    }
    public String getButtonClicked() {
        return buttonClicked;

    }
    public void setDrawOffer(String drawOffer) {
        this.drawOffer = drawOffer;
    }
    public String getDrawOffer() {
        return drawOffer;
    }

}
