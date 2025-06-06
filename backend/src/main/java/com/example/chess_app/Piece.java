package com.example.chess_app;
import java.util.ArrayList;

public abstract class Piece {
    private Boolean isAlive=true;
    private String Color;
    private boolean isPinned=false;
    private String Square;
    private String pinnedBy="no"; //sqaure it got pinned by
    private String pins="no";    //sqaure it pinned

    public Piece( String Color, String Square) {
        this.Color = Color;
        this.Square = Square;
    }

    public boolean getIsAlive() {
        return isAlive;
    }
    public void setIsAlive() {
        this.isAlive = !isAlive;
    }
    public String getColor() {
        return Color;
    }
    public void setColor(String Color) {
        this.Color = Color;
    }
    public boolean getIsPinned() {
        return isPinned;
    }
    public void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }
    public String getSquare() {
        return Square;
    }
    public void setSquare(String Square) {
        this.Square = Square;
    }
    public String getPinnedBy() {
        return pinnedBy;
    }
    public void setPinnedBy(String pinnedBy) {
        this.pinnedBy = pinnedBy;
    }
    public String getPins() {
        return pins;
    }
    public void setPins(String pins) {
        this.pins = pins;
    }
}


class Pawn extends Piece {

    private String firstMove;
    private String enPassant; // can get enpassant if opp pawn comes at this move
    private int firstMoveCount;
    private String getEnPassant; // can it be enpassanted
    public Pawn(String Color, String Square,String enPassant) {
        super(Color, Square);
        firstMove="no";
        this.enPassant=enPassant;
        getEnPassant="no";

    }
    public String getFirstMove() {
        return firstMove;
    }
    public void setFirstMove(String firstMove) {
        this.firstMove = firstMove;
    }
    public String getEnPassant() {
        return enPassant;
    }
    public void setEnPassant(String enPassant) {
        this.enPassant = enPassant;
    }
    public String getGetEnPassant() {
        return getEnPassant;
    }
    public void setGetEnPassant(String getEnPassant) {
        this.getEnPassant = getEnPassant;
    }
    public int getFirstMoveCount() {
        return firstMoveCount;
    }
    public void setFirstMoveCount(int firstMoveCount) {
        this.firstMoveCount = firstMoveCount;
    }

}

class Knight extends Piece {
    public Knight(String Color, String Square) {
        super(Color, Square);
    }

    
}

class Bishop extends Piece {
    public Bishop(String Color, String Square) {
        super(Color, Square);
    }

    
}

class Rook extends Piece{
    private String firstMove;
    private String castleTo;    // square it will castle to
    public Rook(String Color, String Square) {
        super(Color, Square);
        firstMove="no";
        // this.castleTo=castleTo;
    }
    public String getFirstMove() {
        return firstMove;
    }
    public void setFirstMove(String firstMove) {
        this.firstMove = firstMove;
    }
    public String getCastleTo() {
        return castleTo;
    }
    public void setCastleTo(String castleTo) {
        this.castleTo = castleTo;
    }
}

class Queen extends Piece{
    public Queen(String Color, String Square) {
        super(Color, Square);
    }
}

class King extends Piece{
    private String firstMove;
    private Boolean underCheck=false; 
    private ArrayList<String> checkByWhom=new ArrayList<String>();
    public King(String Color, String Square) {
        super(Color, Square);
        firstMove="no";
        // checkByWhom="no";
    }
    public String getFirstMove() {
        return firstMove;
    }
    public void setFirstMove(String firstMove) {
        this.firstMove = firstMove;
    }
    public Boolean getUnderCheck() {
        return underCheck;
    }
    public void setUnderCheck(boolean underCheck) {
        this.underCheck = underCheck;
    }
    public ArrayList<String> getCheckByWhom() {
        return checkByWhom;
    }
    public void setCheckByWhom(String checkByWhom) {
        this.checkByWhom.add(checkByWhom);
    }
}