package com.example.chess_app;
import java.util.ArrayList;

public class MoveResponse {
    private boolean timeUp=false;
    private String gameId;
    private String squareClicked;
    private String color; // tells who this is white or black player
    // private int whiteTime;
    // private int blackTime;
    private boolean valid=false; // tells if move made by whom it should have i.e time to make a change to board i.e hightlight/move piece
    private String clickStatus;  // takes value of highlight/move : this is used after knowing value of isValid()
    private ArrayList<String> possibleMoves=new ArrayList<>(); // this is used to highlight the possible moves
    private String moveFrom="no"; // this is used to move the piece
    private String moveTo="no"; // this is used to move the piece
    private String removePiece="no"; // this is used to remove the piece from the board
    private String isCastle="no";
    private String isPromotion="no";
    private String promoteTo="no"; // takes value as queen/rook/bishop/knight
    private boolean gameEnded=false;
    private String gameEndReason="no"; // tells reason to win such as insufficient material, checkmate etc
    private String winner="no"; // tells white/black/draw
    private String drawOffer="no";
    private boolean gameEndCheck=false; // this is used to check for draw,timeout, resign things


    private String errorMessage;
    
    // Getters and setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public String getSquareClicked() { return squareClicked; }
    public void setSquareClicked(String squareClicked) { this.squareClicked = squareClicked; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    // public int getWhiteTime() { return whiteTime; }
    // public void setWhiteTime(int whiteTime) { this.whiteTime = whiteTime; }
    
    // public int getBlackTime() { return blackTime; }
    // public void setBlackTime(int blackTime) { this.blackTime = blackTime; }
    
    public boolean getIsValid() { return valid; }
    public void setIsValid(boolean valid) { this.valid = valid; }

    public String getClickStatus() { return clickStatus; }
    public void setClickStatus(String clickStatus) { this.clickStatus = clickStatus; }

    public ArrayList<String> getPossibleMoves() { return possibleMoves; }
    public void setPossibleMoves(ArrayList<String> possibleMoves) { this.possibleMoves = possibleMoves; }
    
    public String getMoveFrom() { return moveFrom; }
    public void setMoveFrom(String moveFrom) { this.moveFrom = moveFrom; }

    public String getMoveTo() { return moveTo; }
    public void setMoveTo(String moveTo) { this.moveTo = moveTo; }

    public boolean isGameEnded() { return gameEnded; }
    public void setGameEnded(boolean gameEnded) { this.gameEnded = gameEnded; }

    public String getGameEndReason() { return gameEndReason; }
    public void setGameEndReason(String gameEndReason) { this.gameEndReason = gameEndReason; }

    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }

    public String getRemovePiece() { return removePiece; }
    public void setRemovePiece(String removePiece) { this.removePiece = removePiece; }

    public String getIsCastle() { return isCastle; }
    public void setIsCastle(String isCastle) { this.isCastle = isCastle; }

    public String getIsPromotion() { return isPromotion; }
    public void setIsPromotion(String isPromotion) { this.isPromotion = isPromotion; }

    public String getPromoteTo() { return promoteTo; }
    public void setPromoteTo(String promoteTo) { this.promoteTo = promoteTo; }
    public boolean getIsTimeUp() { return timeUp; }
    public void setTimeUp(boolean timeUp) { this.timeUp = timeUp; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getDrawOffer() { return drawOffer; }
    public void setDrawOffer(String drawOffer) { this.drawOffer = drawOffer; }

    public boolean isGameEndCheck() { return gameEndCheck; }
    public void setGameEndCheck(boolean gameEndCheck) { this.gameEndCheck = gameEndCheck; }
}
