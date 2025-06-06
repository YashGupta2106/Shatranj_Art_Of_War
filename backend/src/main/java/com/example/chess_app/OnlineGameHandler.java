package com.example.chess_app;

// import all stuff
import java.util.ArrayList;
import java.util.List;

// this function will search for the game from the gamerepository.
// it will find kiski baari hai to determine a valid click is made or not
// if valid is made then it will proceed with almost same logic of gameHandler
// in the end i will return a json object which has details like:
    //  1) is it valid click
    //  2) what are possible moves if it is higlight click
    //  3) updated board if it is a move
    //  4) updated time if it is a move
    //  5) did the game end for some reason

public class OnlineGameHandler {
    private Game game;
    private MoveResponse moveResponse;
    private String playerColor;
    private String hasCircle;
    private String squareClicked;
    public OnlineGameHandler(Game game,String squareClicked,String hasCircle,String playerColor,MoveResponse response){
        
        this.game=game;
        this.moveResponse=response;
        this.playerColor=playerColor;
        this.hasCircle=hasCircle;
        this.squareClicked=squareClicked;
        // i came here now i know that i will start computing shit right away 
        // first lets see for squareClicks
        
    }
    
    public void handleSquareClick(){
        // i came here now i know that i will start computing shit right away
        
        // lets extract some info from the game

        List<Piece> whitePieces=game.getWhitePieces();
        List<Piece> blackPieces=game.getBlackPieces();
        King whiteKing=null;
        King blackKing=null;
        for(Piece piece:whitePieces){
            if(piece instanceof King){
                whiteKing=((King)piece);
            }
        }
        for(Piece piece:blackPieces){
            if(piece instanceof King){
                blackKing=((King)piece);
            }
        }

        if(hasCircle.equals("yes")){
            // in this case i will make a move to that square

            // now i have to do a lot of changes
            game.setMoveNumber();
            Move newMove=new Move(game.getLastSquareClicked(),squareClicked);
            // at end remember to update lastSquare to this current one
            moveResponse.setMoveFrom(game.getLastSquareClicked());
            moveResponse.setMoveTo(squareClicked);
            moveResponse.setClickStatus("move");
            Piece toBeRemoved=null;
            if(playerColor.equals("white")){
                for(Piece p:whitePieces){
                    int pawnFlag=0;
                    int kingFlag=0;
                    if(p.getSquare().equals(game.getLastSquareClicked())){
                        // here write the thing to increase move number

                        // newMove.setMoveNumber();
                        
                        if(p instanceof Rook){
                            Rook piece=(Rook)p;
                            piece.setFirstMove("yes");
                            newMove.setPieceMoved("Rook");

                        }
                        if(p instanceof King){
                            newMove.setPieceMoved("King");
                            King piece=(King)p;
                            // lets see if this is a castling move:
                            if(piece.getFirstMove().equals("no")){
                                if(squareClicked.equals("c1")){
                                    for(Piece rookPiece:whitePieces){
                                        if(rookPiece instanceof Rook && rookPiece.getSquare().equals("a1")){
                                            // this rook now goes to its destined square
                                            Rook rook=(Rook)rookPiece;
                                            
                                            p.setSquare(squareClicked);
                                            rook.setSquare("d1");
                                            kingFlag=1;
                                            newMove.setCastling(true);
                                            moveResponse.setIsCastle("yes");
                                            break;
                                        }
                                        
                                    }
                                }

                                else if(squareClicked.equals("g1")){
                                    for(Piece rookPiece:whitePieces){
                                        if(rookPiece instanceof Rook && rookPiece.getSquare().equals("h1")){
                                            // this rook now goes to its destined square
                                            Rook rook=(Rook)rookPiece;
                                            kingFlag=1;
                                            p.setSquare(squareClicked);
                                            rook.setSquare("f1");
                                            newMove.setCastling(true);
                                            moveResponse.setIsCastle("yes");
                                            break;
                                        }
                                        
                                    }
                                }
                                
                            }
                            piece.setFirstMove("yes");
                        }

                        if(p instanceof Pawn){
                            newMove.setPieceMoved("Pawn");
                            Pawn piece=(Pawn)p;
                            // System.out.println("yes its a pawn");
                            int rank=Integer.parseInt(squareClicked.substring(1));
                            
                            if(piece.getFirstMove().equals("no")){
                                // System.out.println("its making its first move");
                                piece.setFirstMoveCount(game.getMoveNumber());
                                piece.setFirstMove("yes");
                                int num1 = Integer.parseInt(piece.getSquare().substring(1)); // Extract "4"
                                int num2 = Integer.parseInt(squareClicked.substring(1));
                                // System.out.println("num1 is: "+num1);
                                // System.out.println("num2 is: "+num2);
                                // System.out.println("square1 is: "+piece.getSquare());
                                // System.out.println("square2 is: "+square.getSquare());
                                if(piece.getSquare().substring(0,1).equals(squareClicked.substring(0,1))){
                                    int diff=Math.abs(num1-num2);
                                    if(diff==2){
                                        // System.out.println("it can get enpassanted");
                                        piece.setGetEnPassant("yes");
                                    }
                                    else{
                                        // System.out.println("enpassant not possible");
                                        piece.setGetEnPassant("no");
                                    }
                                }
                                else{
                                    // System.out.println("enpassant not possible");
                                    piece.setGetEnPassant("no");
                                }
                                
                                // piece.setEnPassant("no");
                                
                            }

                            else{   // here i write for enpassanting
                                // System.out.println("its not making its first move");
                                // System.out.println("lets see if it can enpassant");
                                for(Piece p1:blackPieces){
                                    if(p1 instanceof Pawn){
                                        piece=(Pawn)p1;
                                        if(piece.getGetEnPassant().equals("yes")){
                                            if(squareClicked.equals(piece.getEnPassant())){
                                                // it is going at enpassant piece only.
                                                // thus we remve it
                                                toBeRemoved=p1;
                                                // System.out.println("removing this piece");
                                                
                                                // removePiece(piece.getSquare(),squarePanes);
                                                blackPieces.remove(toBeRemoved);
                                                moveResponse.setRemovePiece(p1.getSquare());
                                                // System.out.println("now i will move the pawn to its enpassant space");
                                                // movePiece(p.getSquare(),square.getSquare(),squarePanes);
                                                p.setSquare(squareClicked);
                                                pawnFlag=1;
                                                newMove.setPieceCaptured("Pawn");
                                                newMove.setEnPassant(true);
                                                break;
                                            }
                                        }
                                    }
                                }

                            }
                            if(rank==8){
                                System.out.println("its promotion time baby");
                                // removePiece(square.getSquare(),squarePanes);
                                for(Piece bp: blackPieces){
                                    if(bp.getSquare().equals(squareClicked)){
                                        blackPieces.remove(bp);
                                        break;
                                    }
                                }
                                // movePiece(gameState.getSquareSelected(),square.getSquare(),squarePanes);
                                p.setSquare(squareClicked);
                                // String promotedPiece=handlePawnPromotion(squareClicked, "white", squarePanes);
                                pawnFlag=1;
                                newMove.setPromotion(true);
                                moveResponse.setIsPromotion("yes");
                                // here i make a call to ask for what to promote to
                                // newMove.setPromotionPiece(promotedPiece);
                                

                            }

                        }
                        if(pawnFlag==1){    // this ensure neeche ke thing doesnt run
                            break;
                        }
                        if(kingFlag==1){
                            break;
                        }
                        if(p instanceof Knight){
                            newMove.setPieceMoved("Knight");

                        }
                        else if(p instanceof Bishop){
                            newMove.setPieceMoved("Bishop");
                        }
                        else if(p instanceof Queen){
                            newMove.setPieceMoved("Queen");
                        }

                        // System.out.println("changing this piece ka position");
                        // first lets get position of piece on target square
                        for(Piece p1: blackPieces){
                            if(p1.getSquare().equals(squareClicked)){
                                p1.setIsAlive();
                                if(p1 instanceof Pawn ){
                                    newMove.setPieceCaptured("Pawn");
                                }
                                else if(p1 instanceof Knight ){
                                    newMove.setPieceCaptured("Knight");
                                }
                                else if(p1 instanceof Rook){
                                    newMove.setPieceCaptured("Rook");
                                }
                                else if(p1 instanceof Bishop){
                                    newMove.setPieceCaptured("Bishop");
                                }
                                else if(p1 instanceof Queen){
                                    newMove.setPieceCaptured("Queen");
                                }
                                // remove it from the array
                                toBeRemoved=p1;
                                blackPieces.remove(toBeRemoved);
                                moveResponse.setRemovePiece(p1.getSquare());
                                
                                // System.out.println("removing this piece");
                                
                                break;

                            }
                        }
                        // System.out.println("out of the loop...time to remove it");
                        
                        // System.out.println("removed the piece officially");
                        // removePiece(square.getSquare(),squarePanes);
                        // movePiece(gameState.getSquareSelected(),square.getSquare(),squarePanes);
                        p.setSquare(squareClicked);
                        System.out.println("done moving the piece and setting it to: "+p.getSquare());
                        // lets see if something exists on g8
                        break;


                    }

                }
            }
            else{
                // this is for when player is black

                for(Piece p:blackPieces){
                    int pawnFlag=0;
                    int kingFlag=0;
                    if(p.getSquare().equals(game.getLastSquareClicked())){
                        // here write the thing to increase move number

                        // newMove.setMoveNumber();
                        
                        if(p instanceof Rook){
                            Rook piece=(Rook)p;
                            piece.setFirstMove("yes");
                            newMove.setPieceMoved("Rook");

                        }
                        if(p instanceof King){
                            newMove.setPieceMoved("King");
                            King piece=(King)p;
                            // lets see if this is a castling move:
                            if(piece.getFirstMove().equals("no")){
                                if(squareClicked.equals("c8")){
                                    for(Piece rookPiece:blackPieces){
                                        if(rookPiece instanceof Rook && rookPiece.getSquare().equals("a8")){
                                            // this rook now goes to its destined square
                                            Rook rook=(Rook)rookPiece;
                                            
                                            p.setSquare(squareClicked);
                                            rook.setSquare("d8");
                                            kingFlag=1;
                                            newMove.setCastling(true);
                                            moveResponse.setIsCastle("yes");
                                            break;
                                        }
                                        
                                    }
                                }

                                else if(squareClicked.equals("g8")){
                                    for(Piece rookPiece:blackPieces){
                                        if(rookPiece instanceof Rook && rookPiece.getSquare().equals("h8")){
                                            // this rook now goes to its destined square
                                            Rook rook=(Rook)rookPiece;
                                            kingFlag=1;
                                            p.setSquare(squareClicked);
                                            rook.setSquare("f8");
                                            newMove.setCastling(true);
                                            moveResponse.setIsCastle("yes");
                                            break;
                                        }
                                        
                                    }
                                }
                                
                            }
                            piece.setFirstMove("yes");
                        }

                        if(p instanceof Pawn){
                            newMove.setPieceMoved("Pawn");
                            Pawn piece=(Pawn)p;
                            // System.out.println("yes its a pawn");
                            int rank=Integer.parseInt(squareClicked.substring(1));
                            
                            if(piece.getFirstMove().equals("no")){
                                // System.out.println("its making its first move");
                                piece.setFirstMoveCount(game.getMoveNumber());
                                piece.setFirstMove("yes");
                                int num1 = Integer.parseInt(piece.getSquare().substring(1)); // Extract "4"
                                int num2 = Integer.parseInt(squareClicked.substring(1));
                                // System.out.println("num1 is: "+num1);
                                // System.out.println("num2 is: "+num2);
                                // System.out.println("square1 is: "+piece.getSquare());
                                // System.out.println("square2 is: "+square.getSquare());
                                if(piece.getSquare().substring(0,1).equals(squareClicked.substring(0,1))){
                                    int diff=Math.abs(num1-num2);
                                    if(diff==2){
                                        // System.out.println("it can get enpassanted");
                                        piece.setGetEnPassant("yes");
                                    }
                                    else{
                                        // System.out.println("enpassant not possible");
                                        piece.setGetEnPassant("no");
                                    }
                                }
                                else{
                                    // System.out.println("enpassant not possible");
                                    piece.setGetEnPassant("no");
                                }
                                
                                // piece.setEnPassant("no");
                                
                            }

                            else{   // here i write for enpassanting
                                // System.out.println("its not making its first move");
                                // System.out.println("lets see if it can enpassant");
                                for(Piece p1:whitePieces){
                                    if(p1 instanceof Pawn){
                                        piece=(Pawn)p1;
                                        if(piece.getGetEnPassant().equals("yes")){
                                            if(squareClicked.equals(piece.getEnPassant())){
                                                // it is going at enpassant piece only.
                                                // thus we remve it
                                                toBeRemoved=p1;
                                                moveResponse.setRemovePiece(p1.getSquare());
                                                // System.out.println("removing this piece");
                                                
                                                // removePiece(piece.getSquare(),squarePanes);
                                                whitePieces.remove(toBeRemoved);
                                                // System.out.println("now i will move the pawn to its enpassant space");
                                                // movePiece(p.getSquare(),square.getSquare(),squarePanes);
                                                p.setSquare(squareClicked);
                                                pawnFlag=1;
                                                newMove.setPieceCaptured("Pawn");
                                                newMove.setEnPassant(true);
                                                break;
                                            }
                                        }
                                    }
                                }

                            }
                            if(rank==8){
                                System.out.println("its promotion time baby");
                                // removePiece(square.getSquare(),squarePanes);
                                for(Piece bp: whitePieces){
                                    if(bp.getSquare().equals(squareClicked)){
                                        whitePieces.remove(bp);
                                        break;
                                    }
                                }
                                // movePiece(gameState.getSquareSelected(),square.getSquare(),squarePanes);
                                p.setSquare(squareClicked);
                                // String promotedPiece=handlePawnPromotion(squareClicked, "white", squarePanes);
                                pawnFlag=1;
                                newMove.setPromotion(true);
                                moveResponse.setIsPromotion("yes");
                                // newMove.setPromotionPiece(promotedPiece);
                                

                            }

                        }
                        if(pawnFlag==1){    // this ensure neeche ke thing doesnt run
                            break;
                        }
                        if(kingFlag==1){
                            break;
                        }
                        if(p instanceof Knight){
                            newMove.setPieceMoved("Knight");

                        }
                        else if(p instanceof Bishop){
                            newMove.setPieceMoved("Bishop");
                        }
                        else if(p instanceof Queen){
                            newMove.setPieceMoved("Queen");
                        }

                        // System.out.println("changing this piece ka position");
                        // first lets get position of piece on target square
                        for(Piece p1: whitePieces){
                            if(p1.getSquare().equals(squareClicked)){
                                p1.setIsAlive();
                                if(p1 instanceof Pawn ){
                                    newMove.setPieceCaptured("Pawn");
                                }
                                else if(p1 instanceof Knight ){
                                    newMove.setPieceCaptured("Knight");
                                }
                                else if(p1 instanceof Rook){
                                    newMove.setPieceCaptured("Rook");
                                }
                                else if(p1 instanceof Bishop){
                                    newMove.setPieceCaptured("Bishop");
                                }
                                else if(p1 instanceof Queen){
                                    newMove.setPieceCaptured("Queen");
                                }
                                // remove it from the array
                                toBeRemoved=p1;
                                whitePieces.remove(toBeRemoved);
                                moveResponse.setRemovePiece(p1.getSquare());
                                
                                // System.out.println("removing this piece");
                                
                                break;

                            }
                        }
                        // System.out.println("out of the loop...time to remove it");
                        
                        // System.out.println("removed the piece officially");
                        // removePiece(square.getSquare(),squarePanes);
                        // movePiece(gameState.getSquareSelected(),square.getSquare(),squarePanes);
                        p.setSquare(squareClicked);
                        System.out.println("done moving the piece and setting it to: "+p.getSquare());
                        // lets see if something exists on g8
                        break;


                    }

                }

            }

            // now i do the pins and checks to update pieces
            PinsAndChecks(newMove);
            game.addMove(newMove);
            game.setLastSquareClicked(squareClicked);

            




        }

        else{
            // not highlighted then i remove the current highlighted and do new highlighting for current square
            moveResponse.setClickStatus("highlight");
            ArrayList<String>possibleMoves=new ArrayList<>();
            if(playerColor.equals("white")){
                for(Piece piece:whitePieces){
                    if(piece.getSquare().equals(squareClicked)){
                        if(piece instanceof Pawn){
                            // pawn logic
                            System.out.println("its a pawn");
                            Pawn pawn=(Pawn)piece;
                            possibleMoves=PawnMoves(pawn,whiteKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                        else if(piece instanceof Rook){
                            // rook logic
                            System.out.println("its a rook");
                            Rook rook=(Rook)piece;
                            possibleMoves=RookMoves(rook,whiteKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }

                        }
                        else if(piece instanceof Knight){
                            // knight logic
                            System.out.println("its a knight");
                            Knight knight=(Knight)piece;
                            possibleMoves=KnightMoves(knight,whiteKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                        else if(piece instanceof Bishop){
                            // bishop logic
                            System.out.println("its a bishop");
                            Bishop bishop=(Bishop)piece;
                            possibleMoves=BishopMoves(bishop,whiteKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                            
                        }
                        else if(piece instanceof Queen){
                            // queen logic
                            System.out.println("its a queen");
                            Queen queen=(Queen)piece;
                            possibleMoves=QueenMoves(queen,whiteKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                        else if(piece instanceof King){
                            // king logic
                            System.out.println("its a king");
                            King king=(King)piece;
                            possibleMoves=KingMoves(king);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                    }
                    
                }
                moveResponse.setPossibleMoves(possibleMoves);
            }
            else{
                for(Piece piece:blackPieces){
                    if(piece.getSquare().equals(squareClicked)){
                        if(piece instanceof Pawn){
                            // pawn logic
                            System.out.println("its a pawn");
                            Pawn pawn=(Pawn)piece;
                            possibleMoves=PawnMoves(pawn,blackKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                        else if(piece instanceof Rook){
                            // rook logic
                            System.out.println("its a rook");
                            Rook rook=(Rook)piece;
                            possibleMoves=RookMoves(rook,blackKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }

                        }
                        else if(piece instanceof Knight){
                            // knight logic
                            System.out.println("its a knight");
                            Knight knight=(Knight)piece;
                            possibleMoves=KnightMoves(knight,blackKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                        else if(piece instanceof Bishop){
                            // bishop logic
                            System.out.println("its a bishop");
                            Bishop bishop=(Bishop)piece;
                            possibleMoves=BishopMoves(bishop,blackKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                            
                        }
                        else if(piece instanceof Queen){
                            // queen logic
                            System.out.println("its a queen");
                            Queen queen=(Queen)piece;
                            possibleMoves=QueenMoves(queen,blackKing);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                        else if(piece instanceof King){
                            // king logic
                            System.out.println("its a king");
                            King king=(King)piece;
                            possibleMoves=KingMoves(king);
                            for(String move:possibleMoves){
                                System.out.println(move);
                            }
                        }
                    }
                }
                moveResponse.setPossibleMoves(possibleMoves);
            }
            game.setLastSquareClicked(squareClicked);

        }

        // return moveResponse;


    }






    public void PinsAndChecks(Move newMove){
        System.out.println("");
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println("i did enter pinsAndChecks");
        System.out.println("");
        System.out.println("");
        System.out.println("now lets do some imp shit");

        // here we first see if its checkmate/stalemate or not
        King whiteKing=null;
        for(Piece p:game.getWhitePieces()){
            if(p instanceof King){
                whiteKing=(King)p;
                break;
            }
        }
        King blackKing=null;
        for(Piece p:game.getBlackPieces()){
            if(p instanceof King){
                blackKing=(King)p;
                break;
            }
        }

        

        // whiteKing.setCheckByWhom("no");
        whiteKing.setUnderCheck(false);
        // blackKing.setCheckByWhom("no");
        blackKing.setUnderCheck(false);
        whiteKing.getCheckByWhom().clear();
        blackKing.getCheckByWhom().clear();


        // removing the pin status from every piece
        for(Piece p:game.getWhitePieces()){
            p.setPinnedBy("no");
            p.setPins("no");
            p.setIsPinned(false);
        }
        for(Piece p:game.getBlackPieces()){
            p.setPinnedBy("no");
            p.setPins("no");
            p.setIsPinned(false);
        }
        System.out.println("done setting the everything to no");
        // first we check for white pieces and then the same for black pieces
        // doing for white pieces
        
        System.out.println("set the king to no checks");
        System.out.println("time to look out for these pawns");
        System.out.println("");
        System.out.println("");
        // now i will remove the can get enpassanted from pawns
        for(Piece p:game.getWhitePieces()){
            if(p instanceof Pawn){
                Pawn piece=(Pawn)p;
                if(piece.getGetEnPassant().equals("yes")){
                    if(piece.getFirstMoveCount()!=game.getMoveNumber()){
                        System.out.println("mera move set is: "+piece.getFirstMoveCount());
                        System.out.println("current move ongoing is: "+game.getMoveNumber());
                        piece.setGetEnPassant("no");
                    }
                    else{
                        System.out.println(" i can get enpassanted at this move");
                    }
                }
            }
        }
        for(Piece p:game.getBlackPieces()){
            if(p instanceof Pawn){
                Pawn piece=(Pawn)p;

                if(piece.getGetEnPassant().equals("yes")){
                    if(piece.getFirstMoveCount()!=game.getMoveNumber()){
                        System.out.println("mera move set is: "+piece.getFirstMoveCount());
                        System.out.println("current move ongoing is: "+game.getMoveNumber());
                        piece.setGetEnPassant("no");
                    }
                    else{
                        System.out.println(" i can get enpassanted at this move");
                    }
                }
            }
        }
        System.out.println("done looking enpassant of pawns");
        
        ArrayList<Piece> opponentPieces=new ArrayList<Piece>();
        for(Piece p:game.getBlackPieces()){
            if(p instanceof Bishop || p instanceof Rook || p instanceof Queen){
                opponentPieces.add(p);
                // System.out.println("i will print the black opp squares");
                // System.out.println(p.getSquare());
            }
        }
        // System.out.println("outside the printing loop of opp pieces");
        // System.out.println("done3");
        // now we start checking for pins
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        for(Piece p:opponentPieces){
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("current opp piece is: "+p.getSquare());
            System.out.println("-------------------------------------------------------------------------------");
            String[]squares=p.getSquare().split("");
            int opp_letter=0;
            int opp_int=0;
            for(int i=0;i<8;i++){
                if(squares[0].equals(col[i])){
                    opp_letter=i;
                    break;
                }
            }
            for(int i=0;i<8;i++){
                if(squares[1].equals(row[i])){
                    opp_int=i;
                    break;
                }
            }
            // now i write all the squares for the piece
            ArrayList<ArrayList<String>> allDirections=new ArrayList<ArrayList<String>>();
            // doing northeast
            ArrayList<String> directionMoves=new ArrayList<String>();
            int j=opp_int+1;
            for(int i=opp_letter+1;i<8;i++){
                if(j<8){
                    String square=col[i]+row[j++];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();


            // doing southeast
            j=opp_int+1;
            for(int i=opp_letter-1;i>=0;i--){
                if(j<8){
                    String square=col[i]+row[j++];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing southwest
            j=opp_int-1;
            for(int i=opp_letter-1;i>=0;i--){
                if(j>=0){
                    String square=col[i]+row[j--];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();


            // doing northwest
            j=opp_int-1;
            for(int i=opp_letter+1;i<8;i++){
                if(j>=0){
                    String square=col[i]+row[j--];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();
//------------------------------------------ now rook moves

            // doing north
            for(int i=opp_int+1;i<=7;i++){
                
                String square=col[opp_letter]+row[i];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing east
            for(int i=opp_letter+1;i<=7;i++){
                
                String square=col[i]+row[opp_int];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing south
            for(int i=opp_int-1;i>=0;i--){
                
                String square=col[opp_letter]+row[i];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing west
            for(int i=opp_letter-1;i>=0;i--){
                
                String square=col[i]+row[opp_int];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            System.out.println("done with all the directions");
            System.out.println("now i will check for pins and checks");
            System.out.println("---------------------------------------------------");
            if(p instanceof Bishop || p instanceof Queen){
                System.out.println("doing for bishop or queen");
                // System.out.println("the square is: "+p.getSquare());
                for(int i=0;i<4;i++){
                    int flag_opp=0; // this is to check if opp(Black) piece found
                    int flag_friend=0; // find for friend (white is found)
                    int flag=0; // tells when to break out of loop after checking white pin
                    String friend_square="no";
                    System.out.println("starting new direction");
                    for(String square:allDirections.get(i)){
                        System.out.println("");
                        System.out.println("");
                        System.out.println("");
                        System.out.println("current square is: "+square);
                        if(flag_opp==0){
                            System.out.println("entering inside as flagopp=0");
                            for(Piece p1:game.getBlackPieces()){
                                if(p1.getSquare().equals(square)){
                                    // no pin or check from this piece
                                    System.out.println("no pin from this piece in this direction");
                                    flag_opp=1;
                                    break;
                                }
                            }
                            
                            for(Piece p1:game.getWhitePieces()){
                                if(p1.getSquare().equals(square) && flag_opp==0){
                                    if(flag_friend<=1){
                                        // now i have to see if i find king or not directly
                                        if(!(p1 instanceof King)){
                                            if(flag_friend==0){
                                                friend_square=p1.getSquare();
                                            }
                                            System.out.println("friend square is: "+friend_square);
                                            flag_friend++;
                                        }
                                        else{
                                            // king spotted
                                            if(flag_friend==1){
                                                // that square got pinned
                                                for(Piece pp:game.getWhitePieces()){
                                                    if(pp.getSquare().equals(friend_square)){
                                                        pp.setIsPinned(true);
                                                        pp.setPinnedBy(p.getSquare());
                                                        p.setPins(pp.getSquare());
                                                        flag=1;
                                                        System.out.println("setting the pin for this piece at square: "+pp.getSquare());
                                                        break;
                                                    }
                                                }
        
                                                break;
                                            }
                                            else{
                                                // king is in check
                                                System.out.println("king is in check");
                                                whiteKing.setUnderCheck(true);
                                                System.out.println("setting the check for white king by "+p.getSquare());
                                                whiteKing.setCheckByWhom(p.getSquare());
                                                System.out.println("king in check from :"+whiteKing.getCheckByWhom());
                                                newMove.setWhiteKingInCheck(true);

                                                flag=1;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        // no point checking anymore
                                        System.out.println("no check in this direction");
                                        friend_square="no";
                                        break;
        
                                    }
                                }
                                if(flag==1){
                                    break;
                                }
                                
                            }
                            
                        }
                        else{
                            System.out.println("end of search in this direction");
                            break;
                        }
                    }
                }
                    
                
            }
            if(p instanceof Rook || p instanceof Queen){
                for(int i=4;i<8;i++){
                    int flag_opp=0; // this is to check if opp piece found
                    int flag_friend=0;
                    int flag=0; // tells when to break out of loop after checking white pin
                    String friend_square="no";
                    for(String square:allDirections.get(i)){
                        if(flag_opp==0){
                            for(Piece p1:game.getBlackPieces()){
                                if(p1.getSquare().equals(square)){
                                    // no pin or check from this piece
                                    flag_opp=1;
                                    break;
                                }
                            }
                            
                            for(Piece p1:game.getWhitePieces()){
                                if(p1.getSquare().equals(square) && flag_opp==0){
                                    if(flag_friend<=1){
                                        // now i have to see if i find king or not directly
                                        if(!(p1 instanceof King)){
                                            if(flag_friend==0){
                                                friend_square=p1.getSquare();
                                            }
                                            flag_friend++;
                                        }
                                        else{
                                            // king spotted
                                            if(flag_friend==1){
                                                // that square got pinned
                                                for(Piece pp:game.getWhitePieces()){
                                                    if(pp.getSquare().equals(friend_square)){
                                                        pp.setIsPinned(true);
                                                        pp.setPinnedBy(p.getSquare());
                                                        p.setPins(pp.getSquare());
                                                        flag=1;
                                                        break;
                                                    }
                                                }
        
                                                break;
                                            }
                                            else{
                                                // king is in check
                                                whiteKing.setUnderCheck(true);
                                                whiteKing.setCheckByWhom(p.getSquare());
                                                newMove.setWhiteKingInCheck(true);
                                                flag=1;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        // no point checking anymore
                                        friend_square="no";
                                        break;
        
                                    }
                                }
                                if(flag==1){
                                    break;
                                }
                                
                            }
                            
                        }
                        else{
                            break;
                        }
                    }
                }
            }

        }
        System.out.println("came out doing for white pieces");
        // System.out.println("done4");
        opponentPieces.clear();
//-----------------------------------------------------
        // now checking for black pieces
        for(Piece p:game.getWhitePieces()){
            if(p instanceof Bishop || p instanceof Rook || p instanceof Queen){
                opponentPieces.add(p);
            }
        }
        // System.out.println("done5");

        // now we start checking for pins
        for(Piece p:opponentPieces){
            String[]squares=p.getSquare().split("");
            int opp_letter=0;
            int opp_int=0;
            for(int i=0;i<8;i++){
                if(squares[0].equals(col[i])){
                    opp_letter=i;
                    break;
                }
            }
            for(int i=0;i<8;i++){
                if(squares[1].equals(row[i])){
                    opp_int=i;
                    break;
                }
            }
            // now i write all the squares for the piece
            ArrayList<ArrayList<String>> allDirections=new ArrayList<ArrayList<String>>();
            // doing northeast
            ArrayList<String> directionMoves=new ArrayList<String>();
            int j=opp_int+1;
            for(int i=opp_letter+1;i<8;i++){
                if(j<8){
                    String square=col[i]+row[j++];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();


            // doing southeast
            j=opp_int+1;
            for(int i=opp_letter-1;i>=0;i--){
                if(j<8){
                    String square=col[i]+row[j++];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing southwest
            j=opp_int-1;
            for(int i=opp_letter-1;i>=0;i--){
                if(j>=0){
                    String square=col[i]+row[j--];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();


            // doing northwest
            j=opp_int-1;
            for(int i=opp_letter+1;i<8;i++){
                if(j>=0){
                    String square=col[i]+row[j--];
                    directionMoves.add(square);
                }
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();
//------------------------------------------ now rook moves

            // doing north
            for(int i=opp_int+1;i<=7;i++){
                
                String square=col[opp_letter]+row[i];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing east
            for(int i=opp_letter+1;i<=7;i++){
                
                String square=col[i]+row[opp_int];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing south
            for(int i=opp_int-1;i>=0;i--){
                
                String square=col[opp_letter]+row[i];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();

            // doing west
            for(int i=opp_letter-1;i>=0;i--){
                
                String square=col[i]+row[opp_int];
                directionMoves.add(square);
                
            }
            allDirections.add(new ArrayList<>(directionMoves));
            directionMoves.clear();


            if(p instanceof Bishop || p instanceof Queen){
                
                for(int i=0;i<4;i++){
                    int flag_opp=0; // this is to check if opp piece found
                    int flag_friend=0;
                    int flag=0; // tells when to break out of loop after checking white pin
                    String friend_square="no";
                    for(String square:allDirections.get(i)){
                        if(flag_opp==0){
                            for(Piece p1:game.getWhitePieces()){
                                if(p1.getSquare().equals(square)){
                                    // no pin or check from this piece
                                    flag_opp=1;
                                    break;
                                }
                            }
                            
                            for(Piece p1:game.getBlackPieces()){
                                if(p1.getSquare().equals(square) && flag_opp==0){
                                    if(flag_friend<=1){
                                        // now i have to see if i find king or not directly
                                        if(!(p1 instanceof King)){
                                            if(flag_friend==0){
                                                friend_square=p1.getSquare();
                                            }
                                            flag_friend++;
                                        }
                                        else{
                                            // king spotted
                                            if(flag_friend==1){
                                                // that square got pinned
                                                for(Piece pp:game.getBlackPieces()){
                                                    if(pp.getSquare().equals(friend_square)){
                                                        pp.setIsPinned(true);
                                                        pp.setPinnedBy(p.getSquare());
                                                        p.setPins(pp.getSquare());
                                                        flag=1;
                                                        break;
                                                    }
                                                }
        
                                                break;
                                            }
                                            else{
                                                // king is in check
                                                blackKing.setUnderCheck(true);
                                                blackKing.setCheckByWhom(p.getSquare());
                                                newMove.setBlackKingInCheck(true);
                                                flag=1;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        // no point checking anymore
                                        friend_square="no";
                                        break;
        
                                    }
                                }
                                if(flag==1){
                                    break;
                                }
                                
                            }
                            
                        }
                        else{
                            break;
                        }
                    }
                }
                    
                
            }
            if(p instanceof Rook || p instanceof Queen){
                for(int i=4;i<8;i++){
                    int flag_opp=0; // this is to check if opp piece found
                    int flag_friend=0;
                    int flag=0; // tells when to break out of loop after checking white pin
                    String friend_square="no";
                    for(String square:allDirections.get(i)){
                        if(flag_opp==0){
                            for(Piece p1:game.getWhitePieces()){
                                if(p1.getSquare().equals(square)){
                                    // no pin or check from this piece
                                    flag_opp=1;
                                    break;
                                }
                            }
                            
                            for(Piece p1:game.getBlackPieces()){
                                if(p1.getSquare().equals(square) && flag_opp==0){
                                    if(flag_friend<=1){
                                        // now i have to see if i find king or not directly
                                        if(!(p1 instanceof King)){
                                            if(flag_friend==0){
                                                friend_square=p1.getSquare();
                                            }
                                            flag_friend++;
                                        }
                                        else{
                                            // king spotted
                                            if(flag_friend==1){
                                                // that square got pinned
                                                for(Piece pp:game.getBlackPieces()){
                                                    if(pp.getSquare().equals(friend_square)){
                                                        pp.setIsPinned(true);
                                                        pp.setPinnedBy(p.getSquare());
                                                        p.setPins(pp.getSquare());
                                                        flag=1;
                                                        break;
                                                    }
                                                }
        
                                                break;
                                            }
                                            else{
                                                // king is in check
                                                blackKing.setUnderCheck(true);
                                                blackKing.setCheckByWhom(p.getSquare());
                                                newMove.setBlackKingInCheck(true);
                                                flag=1;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        // no point checking anymore
                                        friend_square="no";
                                        break;
        
                                    }
                                }
                                if(flag==1){
                                    break;
                                }
                                
                            }
                            
                        }
                        else{
                            break;
                        }
                    }
                }
            }

        }

        System.out.println("done doing for black pieces");

        System.out.println("now we write for pawns and knight checks"); 
        System.out.println("first for knight against white king");
        opponentPieces.clear();
        for(Piece p:game.getBlackPieces()){
            if(p instanceof Knight){
                opponentPieces.add(p);
            }
        }
        for(Piece p:opponentPieces){
            String[] squares=p.getSquare().split("");
            int file=0;
            int pos=0;
            ArrayList<String> directionalmoves=new ArrayList<String>();
            for(int i=0;i<8;i++){
                if(squares[0].equals(col[i])){
                    file=i;
                    break;
                }
            }
            pos=Integer.parseInt(squares[1]);
            
            if(file-1>=0 && pos-2>=1){
                directionalmoves.add(col[file-1]+(pos-2));
            }
            if(file+1<=7 && pos-2>=1){
                directionalmoves.add(col[file+1]+(pos-2));
            }
            if(file-2>=0 && pos-1>=1){
                directionalmoves.add(col[file-2]+(pos-1));
            }
            if(file+2<=7 && pos-1>=1){
                directionalmoves.add(col[file+2]+(pos-1));
            }
            if(file-1>=0 && pos+2<=8){
                directionalmoves.add(col[file-1]+(pos+2));
            }
            if(file+1<=7 && pos+2<=8){
                directionalmoves.add(col[file+1]+(pos+2));
            }
            if(file-2>=0 && pos+1<=8){
                directionalmoves.add(col[file-2]+(pos+1));
            }
            if(file+2<=7 && pos+1<=8){
                directionalmoves.add(col[file+2]+(pos+1));
            }
            for(String s:directionalmoves){
                if(s.equals(whiteKing.getSquare())){
                    System.out.println("yes this knight is checking the king");
                    whiteKing.setUnderCheck(true);
                    whiteKing.setCheckByWhom(p.getSquare());
                    System.out.println("the knight that is checking is "+p.getSquare());
                    break;

                }
            }

        }
        // System.out.println("done7");

        System.out.println("now knight against black king");
        opponentPieces.clear();
        for(Piece p:game.getWhitePieces()){
            if(p instanceof Knight){
                opponentPieces.add(p);
            }
        }
        for(Piece p:opponentPieces){
            String[] squares=p.getSquare().split("");
            int file=0;
            int pos=0;
            ArrayList<String> directionalmoves=new ArrayList<String>();
            for(int i=0;i<8;i++){
                if(squares[0].equals(col[i])){
                    file=i;
                    break;
                }
            }
            pos=Integer.parseInt(squares[1]);
            if(file-1>=0 && pos-2>=1){
                directionalmoves.add(col[file-1]+(pos-2));
            }
            if(file+1<=7 && pos-2>=1){
                directionalmoves.add(col[file+1]+(pos-2));
            }
            if(file-2>=0 && pos-1>=1){
                directionalmoves.add(col[file-2]+(pos-1));
            }
            if(file+2<=7 && pos-1>=1){
                directionalmoves.add(col[file+2]+(pos-1));
            }
            if(file-1>=0 && pos+2<=8){
                directionalmoves.add(col[file-1]+(pos+2));
            }
            if(file+1<=7 && pos+2<=8){
                directionalmoves.add(col[file+1]+(pos+2));
            }
            if(file-2>=0 && pos+1<=8){
                directionalmoves.add(col[file-2]+(pos+1));
            }
            if(file+2<=7 && pos+1<=8){
                directionalmoves.add(col[file+2]+(pos+1));
            }
            for(String s:directionalmoves){
                if(s.equals(blackKing.getSquare())){
                    System.out.println("yes this knight is checking the king");
                    blackKing.setUnderCheck(true);
                    blackKing.setCheckByWhom(p.getSquare());
                    System.out.println("the knight that is checking is "+p.getSquare());
                    break;

                }
            }

        }

        // System.out.println("done8");

        System.out.println("now pawns attacking white king");
        opponentPieces.clear();
        for(Piece p:game.getBlackPieces()){
            if(p instanceof Pawn){
                opponentPieces.add(p);
            }
        }
        for(Piece p:opponentPieces){
            String[] squares=p.getSquare().split("");
            int file=0;
            int pos=0;
            ArrayList<String> directionalmoves=new ArrayList<String>();
            for(int i=0;i<8;i++){
                if(squares[0].equals(col[i])){
                    file=i;
                    break;
                }
            }
            pos=Integer.parseInt(squares[1]);
            if(file-1>=0 && pos-1>=1){
                directionalmoves.add(col[file-1]+(pos-1));
            }
            if(file+1<=7 && pos-1>=1){
                directionalmoves.add(col[file+1]+(pos-1));
            }
            for(String s:directionalmoves){
                if(s.equals(whiteKing.getSquare())){
                    System.out.println("yes this pawn is checking the king");
                    whiteKing.setUnderCheck(true);
                    whiteKing.setCheckByWhom(p.getSquare());
                    break;
                }
            }

        }


        System.out.println("now pawns attacking black king");
        opponentPieces.clear();
        for(Piece p:game.getWhitePieces()){
            if(p instanceof Pawn){
                opponentPieces.add(p);
            }
        }
        for(Piece p:opponentPieces){
            System.out.println("this pawn is on square: "+p.getSquare());
            String[] squares=p.getSquare().split("");
            int file=0;
            int pos=0;
            ArrayList<String> directionalmoves=new ArrayList<String>();
            for(int i=0;i<8;i++){
                if(squares[0].equals(col[i])){
                    file=i;
                    break;
                }
            }
            pos=Integer.parseInt(squares[1]);
            if(file+1<=7 && pos+1<=8){
                directionalmoves.add(col[file+1]+(pos+1));
            }
            if(file-1>=0 && pos+1<=8){
                directionalmoves.add(col[file-1]+(pos+1));
            }
            for(String s:directionalmoves){
                if(s.equals(blackKing.getSquare())){
                    System.out.println("yes this pawn is checking the king");
                    blackKing.setUnderCheck(true);
                    blackKing.setCheckByWhom(p.getSquare());
                    break;
                }
            }

        }


        System.out.println("checking for checkmate");
        int whiteCheckmateFlag=0;
        ArrayList<String>checkmateMoves=new ArrayList<String>();
        
        for(Piece p:game.getWhitePieces()){
            if(p instanceof King){
                checkmateMoves=KingMoves((King)p);
            }
            else if(p instanceof Pawn){
                checkmateMoves=PawnMoves((Pawn)p,whiteKing);
            }
            else if(p instanceof Knight){
                checkmateMoves=KnightMoves((Knight)p,whiteKing);
            }
            else if(p instanceof Rook){
                checkmateMoves=RookMoves((Rook)p,whiteKing);
            }
            else if(p instanceof Bishop){
                checkmateMoves=BishopMoves((Bishop)p,whiteKing);
            }
            else if(p instanceof Queen){
                checkmateMoves=QueenMoves((Queen)p,whiteKing);
            }
            if(checkmateMoves.size()>0){
                System.out.println("no checkmate dear white has moves");
                whiteCheckmateFlag=1;
                break;
            }
        }
        if(whiteKing.getUnderCheck()){
            
            if(whiteCheckmateFlag==0){
                System.out.println("white got checkmated as no moves for it");
                moveResponse.setGameEnded(true);
                moveResponse.setGameEndReason("Checkmate");
                moveResponse.setWinner("black");
                game.setWinner("black");
                game.setActive(false);
                game.setGameEndReason("Checkmate");
                return ;
            }
            else{
                System.out.println("white in check but has moves to save it");
            }    

        }
        else{
            System.out.println("white not in check so no checkmate");
        }
        checkmateMoves.clear();
        int blackCheckmateFlag=0;
        for(Piece p:game.getBlackPieces()){
            if(p instanceof King){
                checkmateMoves=KingMoves((King)p);
            }
            else if(p instanceof Pawn){
                checkmateMoves=PawnMoves((Pawn)p,blackKing);
            }
            else if(p instanceof Knight){
                checkmateMoves=KnightMoves((Knight)p,blackKing);
            }
            else if(p instanceof Rook){
                checkmateMoves=RookMoves((Rook)p,blackKing);
            }
            else if(p instanceof Bishop){
                checkmateMoves=BishopMoves((Bishop)p,blackKing);
            }
            else if(p instanceof Queen){
                checkmateMoves=QueenMoves((Queen)p,blackKing);
            }
            if(checkmateMoves.size()>0){
                System.out.println("no checkmate dear black has moves");
                blackCheckmateFlag=1;
                break;
            }
        }
        if(blackKing.getUnderCheck()){
            
            if(blackCheckmateFlag==0){
                System.out.println("black got checkmated as no moves for it");
                moveResponse.setGameEnded(true);
                moveResponse.setGameEndReason("Checkmate");
                moveResponse.setWinner("white");
                
                game.setWinner("white");
                game.setActive(false);
                game.setGameEndReason("Checkmate");
                
                return ;

            }

        }
        else{
            System.out.println("black not in check so no checkmate");
        }
        if(playerColor.equals("white")){
            System.out.println("now checking for stalemate for black");
            if(blackCheckmateFlag==0){
                System.out.println("game ended in draw");
                moveResponse.setGameEnded(true);
                moveResponse.setGameEndReason("draw");
                moveResponse.setWinner("draw");
                
                game.setWinner("draw");
                game.setActive(false);
                game.setGameEndReason("draw");
                return ;

            }
        }
        else{
            System.out.println("now checking for stalemate for white");
            if(whiteCheckmateFlag==0){
                System.out.println("game ended in draw");
                moveResponse.setGameEnded(true);
                moveResponse.setGameEndReason("draw");
                moveResponse.setWinner("draw");
                
                game.setWinner("draw");
                game.setActive(false);
                game.setGameEndReason("draw");
                return ;

            }
        }


    }





    public ArrayList<String> PawnMoves(Pawn pawn,King king){
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        ArrayList<String> moves=new ArrayList<String>();
        ArrayList<String>tempmoves=new ArrayList<String>();
        if(pawn.getIsAlive()){
            String square[]=pawn.getSquare().split("");
            if(pawn.getColor().equals("white")){
                System.out.println("white pawn");
                for(int i=0;i<8;i++){
                    if(square[1].equals(row[i])){
                        if(i+1<8){
                            // yes it can move ahead if unobstructed
                            int flag=0;
                            String sqAhead=square[0]+row[i+1];
                            // System.out.println("the square ahead is: "+sqAhead);
                            for(Piece p:game.getWhitePieces()){
                                if(p.getSquare().equals(sqAhead)){
                                    flag=1;
                                    break;
                                }
                            }
                            for(Piece p:game.getBlackPieces()){
                                if(p.getSquare().equals(sqAhead)){
                                    flag=1;
                                    break;
                                }
                            }
                            if(flag==0){
                                // it can move ahead
                                tempmoves.add(sqAhead);    
                            }

                        }
                        break;
                    }
                }

                if(pawn.getFirstMove().equals("no")){
                    // it can move 2 squares
                    System.out.println("it can make 2 squares at this point");
                    String sqAhead=square[0]+"4";
                    int flag=0;
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(sqAhead)){
                            flag=1;
                            break;
                        }
                    }
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(sqAhead)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==0){
                        // it can move ahead
                        tempmoves.add(sqAhead);
                    }
                }
                //now lets add the diagonal moves:
                String sqLeft="";
                String sqRight="";
                for(int i=0;i<8;i++){
                    if(col[i].equals(square[0])){
                        if(i-1>=0){
                            sqLeft=col[i-1];
                        }
                        else{
                            sqLeft="no";
                        }
                        if(i+1<=7){
                            sqRight=col[i+1];
                        }
                        else{
                            sqRight="no";
                        }
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(square[1])){
                        if(!sqLeft.equals("no")){
                            if(i+1<=7){
                                sqLeft+=row[i+1];
                            }
                            else{
                                sqLeft="no";
                            }
                        }
                        if(!sqRight.equals("no")){
                            if(i+1<=7){
                                sqRight+=row[i+1];
                            }
                            else{
                                sqRight="no";
                            }
                        }
                        
                        
                    }
                }
                if(!sqLeft.equals("no")){
                    int flag=0;
                    
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(sqLeft) && !(p instanceof King)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqLeft);
                    }
                }
                if(!sqRight.equals("no")){
                    int flag=0;

                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(sqRight) && !(p instanceof King)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqRight);
                    }
                }
                System.out.println("time to see if i can enpassant");
//----------------------------------------------------------------------------
                // now i see for en passant
                // i gotta search which one it can attack
                System.out.println("entered the block to check it");
                sqLeft="";
                sqRight="";
                String sqAboveLeft="";
                String sqAboveRight="";
                for(int i=0;i<8;i++){
                    if(col[i].equals(square[0])){
                        if(i-1>=0){
                            sqLeft=col[i-1];
                            sqAboveLeft=col[i-1];
                        }
                        else{
                            sqLeft="no";
                        }
                        if(i+1<=7){
                            sqRight=col[i+1];
                            sqAboveRight=col[i+1];
                        }
                        else{
                            sqRight="no";
                        }
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(square[1])){
                        if(i+1<=7){
                            sqLeft+=row[i];
                            sqAboveLeft+=row[i+1];
                            sqRight+=row[i];
                            sqAboveRight+=row[i+1];
                        }
                        else{
                            sqLeft="no";
                            sqRight="no";
                        }
                        
                        break;
                    }
                }
                System.out.println("the squares are: "+sqLeft+" "+sqRight+" "+sqAboveLeft+" "+sqAboveRight);
                if(!sqLeft.equals("no")){
                    int flag=0;

                    for(Piece p:game.getBlackPieces()){
                        // System.out.println("opponent piece square is : "+p.getSquare());
                        if(p.getSquare().equals(sqLeft) && p instanceof Pawn){
                            System.out.println("opponent pawn found");
                            if(((Pawn)p).getGetEnPassant().equals("yes")){
                                System.out.println("yes it can enpassant");
                                flag=1;
                                break;
                            }
                            System.out.println(((Pawn)p).getGetEnPassant());
                            
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqAboveLeft);
                    }
                }

                if(!sqRight.equals("no")){
                    int flag=0;

                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(sqRight) && p instanceof Pawn){
                            if(((Pawn)p).getGetEnPassant().equals("yes")){
                                System.out.println("yes it can enpassant");
                                flag=1;
                                break;
                            }
                            
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqAboveRight);
                    }
                }
                // if(pawn.getEnPassant().equals("yes")){
                    
                // }
                System.out.println("came out of the checking");
// -------------------------------------------------------------------------------------------------
                // now i will start eliminating the moves that are not possible
                
                if(king.getUnderCheck()){
                    List<String> block=new ArrayList<String>();
                    // pawn can save king only by attacking the checking piece.. but first make sure that it is not pinned by some other piece
                    System.out.println("----------------------------------------");
                    System.out.println("haaalp king is in check");
                    System.out.println("---------------------------------------");
                    String attackSq="";
                    int flagfind=0;
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }

                    for(Piece p:game.getBlackPieces()){
                        
                        if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                            
                            // in this case you can either kill it or not help.
                            System.out.println("lets see if it can attack the piece");
                            
                            for(String saveKing:tempmoves){
                                if(saveKing.equals(p.getSquare())){
                                    // it might attack the piece
                                    System.out.println("yes it can attack less goooooooo");
                                    flagfind=1;
                                    attackSq=saveKing;
                                    break;
                                }
                            }
                            if(flagfind==1){
                                System.out.println("getting out of this looooop");
                                break;
                                
                            }
                            
                            
                        }
                    }
                    if(flagfind==1){ // you might attack
                        if(!pawn.getIsPinned()){// check if pinned or not
                            // tempmoves.clear();
                            System.out.println("added to possible moves as it can attack the piece");
                            block.add(attackSq);
                        }
                        else{
                            tempmoves.clear();
                        }
                    }

                    // else{   
        
                    // }

                    // all you can do is obstruct the check
                    // find the path of check and see if it can land on any of those squares
                        // Piece attacker=null;  
                    flagfind=0;
                    System.out.println("lets see if it can obstruct the attacker");
                    String king_square[]=king.getSquare().split("");
                    if(pawn.getIsPinned()){ // in this case pawn cant move
                        tempmoves.clear();
                    }
                    else{   // in this case it can obstruct
                        for(Piece p:game.getBlackPieces()){
                            String attacker_square[]=p.getSquare().split("");
                            if(p.getSquare().equals(attackSq)){
                                if(p instanceof Rook){
                                    // file is entire a1 to a8
                                    // rank is entire row a1 to h1
                                    
                                    if(king_square[0].equals(attacker_square[0])){
                                        // they are on the same file
                                        // in this case pawn cant do anything
                                        // tempmoves.clear();
                                        // tempmoves.addAll(block);

                                    }
                                    else{

                                        // they are on the same rank
                                        int attacker_rank=0;
                                        int king_rank=0;
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(king_square[0])){
                                                king_rank=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(attacker_square[0])){
                                                attacker_rank=i;
                                                break;
                                            }
                                        }
                                        
                                        if(king_rank>attacker_rank){

                                            // king is on the right
                                            String eacmove="";
                                            for(int i=attacker_rank+1;i<king_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                            // tempmoves.clear();
                                            // tempmoves.addAll(block);

                                        }
                                        else{
                                            // king is on left
                                            String eacmove=null;
                                            for(int i=king_rank+1;i<attacker_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                            // tempmoves.clear();
                                            // tempmoves.addAll(block);
                                        }
                                    }
                                    
                                }
                                else if(p instanceof Bishop){
                                    // i have to see for diagonal entries
                                    int attacker_letter=0;
                                    int attacker_int=0;
                                    int king_letter=0;
                                    int king_int=0;
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(attacker_square[0])){
                                            attacker_letter=i;
                                            break;
                                        }
                                    }
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(attacker_square[1])){
                                            attacker_int=i;
                                            break;
                                        }
                                    }
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(king_square[0])){
                                            king_letter=i;
                                            break;
                                        }
                                    }
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(king_square[1])){
                                            king_int=i;
                                            break;
                                        }
                                    }
                                    // List<String> block=new ArrayList<String>();
                                    String eachmove="";
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    flagfind=1;
                                }
                                else if(p instanceof Queen){
                                    // first see if it attacks like a rook
                                    if(king_square[0].equals(attacker_square[0])){
                                        // they are on the same file
                                        // in this case pawn cant do anything

                                    }
                                    else if(king_square[1].equals(attacker_square[1])){
                                        // they are on same rank
                                        int attacker_rank=0;
                                        int king_rank=0;
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(king_square[0])){
                                                king_rank=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(attacker_square[0])){
                                                attacker_rank=i;
                                                break;
                                            }
                                        }
                                        // List<String> block=new ArrayList<String>();
                                        if(king_rank>attacker_rank){

                                            // king is on the right
                                            String eacmove="";
                                            for(int i=attacker_rank+1;i<king_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }

                                        }
                                        else{
                                            // king is on left
                                            String eacmove=null;
                                            for(int i=king_rank+1;i<attacker_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    
                                    else{   // its attacking like a bishop
                                        int attacker_letter=0;
                                        int attacker_int=0;
                                        int king_letter=0;
                                        int king_int=0;
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(attacker_square[0])){
                                                attacker_letter=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(row[i].equals(attacker_square[1])){
                                                attacker_int=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(king_square[0])){
                                                king_letter=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(row[i].equals(king_square[1])){
                                                king_int=i;
                                                break;
                                            }
                                        }
                                        // List<String> block=new ArrayList<String>();
                                        String eachmove="";
                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }
                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int-1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    
                                    }
                                }
                                
                                else if(p instanceof Knight|| p instanceof Pawn){
                                    // tempmoves.clear();
                                }
                                
                                break;
                            }
                        }
                    }
                    tempmoves.clear();
                    tempmoves.addAll(block);
                    
                }
                else if(pawn.getIsPinned()){
                    // if its pinned then i have to check
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("this piece is pinned... lets see what it can do now");
                    System.out.println("----------------------------------------------------------------");
                    String pawn_square[]=pawn.getSquare().split("");
                    System.out.println("its pinned by :"+pawn.getPinnedBy());
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(pawn.getPinnedBy())){
                            System.out.println("yes it matched");
                            String attacker_square[]=p.getSquare().split("");
                            // i have to see if it can move in the direction of the pin
                            if(p instanceof Rook){
                                System.out.println("attacker is rook");
                                if(attacker_square[1].equals(pawn_square[1])){
                                    // then we cant do anything
                                    tempmoves.clear();
                                }
                                else{
                                    int pawn_letter=0;
                                    int pawn_int=0;
                                    
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(pawn_square[0])){
                                            pawn_letter=i;
                                            break;
                                        }
                                    }
                                    
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(pawn_square[1])){
                                            pawn_int=i;
                                            break;
                                        }
                                    }
                                    
                                    // now we see for moves
                                    List<String> block=new ArrayList<String>();
                                    String eachmove="";
                                    for(int i=pawn_int+1;i<8;i++){
                                        eachmove=col[pawn_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    tempmoves.clear();
                                    tempmoves.addAll(block);
                                }

                            }
                            else if(p instanceof Bishop){
                                System.out.println("attacker is bishop");
                                // in this case i can move only to attack the piece
                                ArrayList<String> checkMoves=new ArrayList<>(tempmoves);
                                tempmoves.clear();
                                for(String s:checkMoves){
                                    if(s.equals(p.getSquare())){
                                        tempmoves.add(s);
                                        break;
                                    }
                                }
                                
                            }
                            else if(p instanceof Queen){
                                System.out.println("attacker is queen");
                                // first lets see for attack as rook
                                if(attacker_square[1].equals(pawn_square[1])){
                                    // then we cant do anything
                                    tempmoves.clear();
                                }
                                else if(attacker_square[0].equals(pawn_square[0])){
                                    int pawn_letter=0;
                                    int pawn_int=0;
                                    
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(pawn_square[0])){
                                            pawn_letter=i;
                                            break;
                                        }
                                    }
                                    
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(pawn_square[1])){
                                            pawn_int=i;
                                            break;
                                        }
                                    }
                                    
                                    // now we see for moves
                                    List<String> block=new ArrayList<String>();
                                    String eachmove="";
                                    for(int i=pawn_int+1;i<8;i++){
                                        eachmove=col[pawn_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    tempmoves.clear();
                                    tempmoves.addAll(block);
                                }
                                else{
                                    // it moves as bishop
                                    List<String> block=new ArrayList<>(tempmoves);
                                    tempmoves.clear();
                                    for(String s:block){
                                        if(s.equals(p.getSquare())){
                                            tempmoves.add(s);
                                            break;
                                        }
                                    }

                                }

                            }
                        }

                    }
                }
                else{   // no obstruction to pawn and it can move normally
                    // moves.addAll(tempmoves);
                    // tempmoves.clear();
                }
                
            }

//--------------------------------------------------------


            else{ // here you write for black pawn
                System.out.println("black pawn");
                for(int i=0;i<8;i++){
                    if(square[1].equals(row[i])){
                        if(i-1>=0){
                            // yes it can move ahead if unobstructed
                            int flag=0;
                            String sqAhead=square[0]+row[i-1];
                            for(Piece p:game.getBlackPieces()){
                                if(p.getSquare().equals(sqAhead)){
                                    flag=1;
                                    break;
                                }
                            }
                            for(Piece p:game.getWhitePieces()){
                                if(p.getSquare().equals(sqAhead)){
                                    flag=1;
                                    break;
                                }
                            }
                            if(flag==0){
                                // it can move ahead
                                tempmoves.add(sqAhead);    
                            }
        
                        }
                        break;
                    }
                }
                if(pawn.getFirstMove().equals("no")){
                    // it can move 2 squares
                    System.out.println("it can make 2 squares at this point");

                    String sqAhead=square[0]+"5";
                    int flag=0;
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(sqAhead)){
                            flag=1;
                            break;
                        }
                    }
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(sqAhead)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==0){
                        // it can move ahead
                        tempmoves.add(sqAhead);
                    }
                }
                //now lets add the diagonal moves:
                String sqLeft="";
                String sqRight="";
                
                for(int i=0;i<8;i++){
                    if(col[i].equals(square[0])){
                        if(i-1>=0){
                            sqLeft=col[i-1];
                        }
                        else{
                            sqLeft="no";
                        }
                        if(i+1<=7){
                            sqRight=col[i+1];
                        }
                        else{
                            sqRight="no";
                        }
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(square[1])){
                        if(!sqLeft.equals("no")){
                            if(i-1>=0){
                                sqLeft+=row[i-1];
                            }
                            else{
                                sqLeft="no";
                            }    
                        }
                        
                        if(!sqRight.equals("no")){
                            if(i-1>=0){
                                sqRight+=row[i- 1];
                            }
                            else{
                                sqRight="no";
                            }    
                        }
                        
                    }
                }
                if(!sqLeft.equals("no")){
                    int flag=0;
                    
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(sqLeft) && !(p instanceof King)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqLeft);
                    }
                }
                if(!sqRight.equals("no")){
                    int flag=0;

                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(sqRight) && !(p instanceof King)){
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqRight);
                    }
                }
//----------------------------------------------------------------------------
                // now i see for en passant
                // i gotta search which one it can attack
                sqLeft="";
                sqRight="";
                String sqAboveLeft="";
                String sqAboveRight="";
                for(int i=0;i<8;i++){
                    if(col[i].equals(square[0])){
                        if(i-1>=0){
                            sqLeft=col[i-1];
                            sqAboveLeft=col[i-1];
                        }
                        else{
                            sqLeft="no";
                        }
                        if(i+1<=7){
                            sqRight=col[i+1];
                            sqAboveRight=col[i+1];
                        }
                        else{
                            sqRight="no";
                        }
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(square[1])){
                        if(i-1>=0){
                            sqLeft+=row[i];
                            sqAboveLeft+=row[i-1];
                            sqRight+=row[i];
                            sqAboveRight+=row[i-1];
                        }
                        else{
                            sqLeft="no";
                            sqRight="no";
                        }
                        
                        break;
                    }
                }
                if(!sqLeft.equals("no")){
                    int flag=0;

                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(sqLeft) && p instanceof Pawn){
                            if(((Pawn)p).getGetEnPassant().equals("yes")){
                                flag=1;
                                break;
                            }
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqAboveLeft);
                    }
                }

                if(!sqRight.equals("no")){
                    int flag=0;

                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(sqRight) && p instanceof Pawn){
                            if(((Pawn)p).getGetEnPassant().equals("yes")){
                                flag=1;
                                break;
                            }
                        }
                    }
                    if(flag==1){
                        tempmoves.add(sqAboveRight);
                    }
                }
                // if(pawn.getEnPassant().equals("yes")){
                    
                // }
// -------------------------------------------------------------------------------------------------
                // now i will start eliminating the moves that are not possible
                
                if(king.getUnderCheck()){
                    // pawn can save king only by attacking the checking piece.. but first make sure that it is not pinned by some other piece
                    String attackSq="";
                    int flagfind=0;
                    List<String> block=new ArrayList<String>();
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    for(Piece p:game.getWhitePieces()){
                        
                        if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                            
                            // in this case you can either kill it or not help.
                            
                            for(String saveKing:tempmoves){
                                if(saveKing.equals(p.getSquare())){
                                    // it might attack the piece
                                    flagfind=1;
                                    attackSq=saveKing;
                                    break;
                                }
                            }
                            if(flagfind==1){
                                break;
                                
                            }
                            
                            
                        }
                    }
                    if(flagfind==1){ // you might attack
                        if(!pawn.getIsPinned()){// check if pinned or not
                            // tempmoves.clear();
                            block.add(attackSq);
                        }
                        else{
                            tempmoves.clear();
                        }
                    }
                    // else{  
                        
                    // }
                    // all you can do is obstruct the check
                    // find the path of check and see if it can land on any of those squares
                    // Piece attacker=null; 
                    flagfind=0;
                    String king_square[]=king.getSquare().split("");
                    if(pawn.getIsPinned()){ // in this case pawn cant move
                        tempmoves.clear();
                    }
                    else{   // in this case it can obstruct
                        for(Piece p:game.getWhitePieces()){
                            String attacker_square[]=p.getSquare().split("");
                            if(p.getSquare().equals(attackSq)){
                                if(p instanceof Rook){
                                    // file is entire a1 to a8
                                    // rank is entire row a1 to h1
                                    
                                    if(king_square[0].equals(attacker_square[0])){
                                        // they are on the same file
                                        // in this case pawn cant do anything
                                        // tempmoves.clear();

                                    }
                                    else{

                                        // they are on the same rank
                                        int attacker_rank=0;
                                        int king_rank=0;
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(king_square[0])){
                                                king_rank=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(attacker_square[0])){
                                                attacker_rank=i;
                                                break;
                                            }
                                        }
                                        
                                        if(king_rank>attacker_rank){

                                            // king is on the right
                                            String eacmove="";
                                            for(int i=attacker_rank+1;i<king_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // king is on left
                                            String eacmove=null;
                                            for(int i=king_rank+1;i<attacker_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                }
                                else if(p instanceof Bishop){
                                    // i have to see for diagonal entries
                                    int attacker_letter=0;
                                    int attacker_int=0;
                                    int king_letter=0;
                                    int king_int=0;
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(attacker_square[0])){
                                            attacker_letter=i;
                                            break;
                                        }
                                    }
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(attacker_square[1])){
                                            attacker_int=i;
                                            break;
                                        }
                                    }
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(king_square[0])){
                                            king_letter=i;
                                            break;
                                        }
                                    }
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(king_square[1])){
                                            king_int=i;
                                            break;
                                        }
                                    }
                                    // List<String> block=new ArrayList<String>();
                                    String eachmove="";
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    flagfind=1;
                                }
                                else if(p instanceof Queen){
                                    // first see if it attacks like a rook
                                    if(king_square[0].equals(attacker_square[0])){
                                        // they are on the same file
                                        // in this case pawn cant do anything

                                    }
                                    else if(king_square[1].equals(attacker_square[1])){
                                        // they are on same rank
                                        int attacker_rank=0;
                                        int king_rank=0;
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(king_square[0])){
                                                king_rank=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(attacker_square[0])){
                                                attacker_rank=i;
                                                break;
                                            }
                                        }
                                        // List<String> block=new ArrayList<String>();
                                        if(king_rank>attacker_rank){

                                            // king is on the right
                                            String eacmove="";
                                            for(int i=attacker_rank+1;i<king_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // king is on left
                                            String eacmove=null;
                                            for(int i=king_rank+1;i<attacker_rank;i++){
                                                eacmove=col[i]+attacker_square[1];
                                                for(String s:tempmoves){
                                                    if(s.equals(eacmove)){
                                                        block.add(eacmove);
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    
                                    else{   // its attacking like a bishop
                                        int attacker_letter=0;
                                        int attacker_int=0;
                                        int king_letter=0;
                                        int king_int=0;
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(attacker_square[0])){
                                                attacker_letter=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(row[i].equals(attacker_square[1])){
                                                attacker_int=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(col[i].equals(king_square[0])){
                                                king_letter=i;
                                                break;
                                            }
                                        }
                                        for(int i=0;i<8;i++){
                                            if(row[i].equals(king_square[1])){
                                                king_int=i;
                                                break;
                                            }
                                        }
                                        // List<String> block=new ArrayList<String>();
                                        String eachmove="";
                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }
                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int-1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    
                                        // tempmoves.clear();
                                        // tempmoves.addAll(block);
    
    
                                    }
                                }
                                
                                else if(p instanceof Knight|| p instanceof Pawn){
                                    // tempmoves.clear();
                                }
                                
                                break;
                            }
                        }
                    }
                    tempmoves.clear();
                    tempmoves.addAll(block);
                    
                    
                }
                else if(pawn.getIsPinned()){
                    // if its pinned then i have to check
                    String pawn_square[]=pawn.getSquare().split("");
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(pawn.getPinnedBy())){
                            String attacker_square[]=p.getSquare().split("");
                            // i have to see if it can move in the direction of the pin
                            if(p instanceof Rook){
                                if(attacker_square[1].equals(pawn_square[1])){
                                    // then we cant do anything
                                    tempmoves.clear();
                                }
                                else{
                                    int pawn_letter=0;
                                    int pawn_int=0;
                                    
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(pawn_square[0])){
                                            pawn_letter=i;
                                            break;
                                        }
                                    }
                                    
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(pawn_square[1])){
                                            pawn_int=i;
                                            break;
                                        }
                                    }
                                    
                                    // now we see for moves
                                    List<String> block=new ArrayList<String>();
                                    String eachmove="";
                                    for(int i=pawn_int+1;i<8;i++){
                                        eachmove=col[pawn_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    tempmoves.clear();
                                    tempmoves.addAll(block);
                                }

                            }
                            else if(p instanceof Bishop){
                                
                                // in this case i can move only to attack the piece
                                List<String> block=new ArrayList<>(tempmoves);
                                tempmoves.clear();
                                for(String s:block){
                                    if(s.equals(p.getSquare())){
                                        tempmoves.add(s);
                                        break;
                                    }
                                }
                            }
                            else if(p instanceof Queen){
                                // first lets see for attack as rook
                                if(attacker_square[1].equals(pawn_square[1])){
                                    // then we cant do anything
                                    tempmoves.clear();
                                }
                                else if(attacker_square[0].equals(pawn_square[0])){
                                    int pawn_letter=0;
                                    int pawn_int=0;
                                    
                                    for(int i=0;i<8;i++){
                                        if(col[i].equals(pawn_square[0])){
                                            pawn_letter=i;
                                            break;
                                        }
                                    }
                                    
                                    for(int i=0;i<8;i++){
                                        if(row[i].equals(pawn_square[1])){
                                            pawn_int=i;
                                            break;
                                        }
                                    }
                                    
                                    // now we see for moves
                                    List<String> block=new ArrayList<String>();
                                    String eachmove="";
                                    for(int i=pawn_int+1;i<8;i++){
                                        eachmove=col[pawn_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    tempmoves.clear();
                                    tempmoves.addAll(block);
                                }
                                else{
                                    // it moves as bishop
                                    List<String> block=new ArrayList<>(tempmoves);
                                    tempmoves.clear();
                                    for(String s:block){
                                        if(s.equals(p.getSquare())){
                                            tempmoves.add(s);
                                            break;
                                        }
                                    }

                                }

                            }
                        }

                    }
                }
                else{   // no obstruction to pawn and it can move normally
                    
                }

            }
        }
        moves.addAll(tempmoves);
        return moves;
    }
    public ArrayList<String> RookMoves(Rook rook,King king){
        ArrayList<String> moves=new ArrayList<String>();
        ArrayList<String> tempmoves=new ArrayList<String>();
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        if(rook.getIsAlive()){
            String[]squares=rook.getSquare().split("");
            // first add for W, then N, then E, then S
            if(rook.getColor().equals("white")){
                int pos=Integer.parseInt(squares[1]);   // pos means row
                int file=0; // file means column
                for(int i=0;i<8;i++){
                    if(col[i].equals(squares[0])){
                        file=i;
                        break;
                    }
                }
                int j=pos;
                ArrayList<String>directionalmoves=new ArrayList<String>();
                for(int i=file-1;i>=0;i--){
                    
                    String sqAhead=col[i]+pos;
                    directionalmoves.add(sqAhead);
                        
                    
                }
                // now i check this arraylist;
                int flag1=0; // if its zero then i can check for further piece
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }

                directionalmoves.clear();
                j=pos;
                
                // now for N
                for(j=pos+1;j<=8;j++){
                    int i=file;
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                        
                    
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }

                directionalmoves.clear();
                j=pos;
                // now for E
                for(int i=file+1;i<8;i++){
                    
                    String sqAhead=col[i]+pos;
                    directionalmoves.add(sqAhead);
                        
                    
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }
                
                directionalmoves.clear();
                j=pos;
                // now for S
                for(j=pos-1;j>0;j--){
                    
                    int i=file;
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                    // j--;
                    
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }
//---------------------------------------------------------------
                // now we start eliminating as per cases
                String king_square[]=king.getSquare().split("");
                if(king.getUnderCheck()){
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                            String eachmove="";
                            String attacker_square[]=p.getSquare().split("");
                            int attacker_letter=0;
                            int attacker_int=0;
                            int king_int=0;
                            int king_letter=0;
                            for(int i=0;i<8;i++){
                                if(col[i].equals(attacker_square[0])){
                                    attacker_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(col[i].equals(king_square[0])){
                                    king_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(attacker_square[1])){
                                    attacker_int=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(king_square[1])){
                                    king_int=i;
                                    break;
                                }
                            }
                            List <String> block=new ArrayList<String>();
                            if(rook.getIsPinned()){
                                tempmoves.clear();
                                break;
        
                            }
                            else{   // all we can do is attack or block
                                if(p instanceof Pawn || p instanceof Knight){
                                        for(String s:tempmoves){
                                            if(s.equals(p.getSquare())){
                                                block.add(s);
                                                // break;
                                            }
                                        }
                                        tempmoves.clear();
                                        tempmoves.addAll(block);
                                    
                                }
                                else if(p instanceof Rook){
                                    // either you attack or you block
                                    // first for attack
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }

                                    // now for blocking
                                    if(attacker_letter>king_letter){
                                        // attacker is on right                                        
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }
                                    else if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                    else if(attacker_letter<king_letter){
                                        // attacker is on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }

                                    }
                                    else if(attacker_int<king_int){
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }            
                                    tempmoves.clear();
                                    tempmoves.addAll(block);

                                    
                                }
                                else if(p instanceof Bishop){

                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            break;
                                        }
                                    }
                                    // now we see to block
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    tempmoves.clear();
                                    tempmoves.addAll(block);

                                }
                                else if(p instanceof Queen){
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            
                                            block.add(s);
                                            break;
                                        }
                                    }

                                    // first moves like rook
                                    if(attacker_letter==king_letter){
                                        // they are on the same file
                                        if(attacker_int>king_int){ // attacker on top
                                            int int_inc=king_int+1;
                                            for(int i=int_inc;i<attacker_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                        else{   // attacker on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=int_inc;i<king_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else if(attacker_int==king_int){
                                        // they are on the same rank
                                        if(attacker_letter>king_letter){
                                            // attacker on right
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }

                                        }
                                        else{   // attacker on left
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                    else{   // here are the moves for bishop

                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int-1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    
                                        tempmoves.clear();
                                        tempmoves.addAll(block);
                                    }

                                }
                            }

                        }
                    }
                    
                }
                else if(rook.getIsPinned()){
                    // i have to write this part
                    // you can move only if pinned by rook or queen and that too vertically or horizontally
                    String[] rook_square=rook.getSquare().split("");
                    String[] attacker_square=rook.getPinnedBy().split("");
                    List<String> block=new ArrayList<>();
                    String eachmove="";
                    for(Piece P:game.getBlackPieces()){
                        if(P.getSquare().equals(rook.getPinnedBy())){
                            if(P instanceof Rook || P instanceof Queen){
                                int attacker_letter=0;
                                int attacker_int=0;
                                int rook_int=0;
                                int rook_letter=0;
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(attacker_square[0])){
                                        attacker_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(rook_square[0])){
                                        rook_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(attacker_square[1])){
                                        attacker_int=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(rook_square[1])){
                                        rook_int=i;
                                        break;
                                    }
                                }
                                if(rook_letter==attacker_letter){
                                    
                                    // they are on the same file
                                    if(rook_int>attacker_int){
                                        // rook on top
                                        for(int i=attacker_int;i<rook_int;i++){
                                            eachmove=col[attacker_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_int+1;i<=7;i++){
                                            eachmove=col[attacker_letter]+row[i];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    else{
                                        // rook on bottom
                                        for(int i=rook_int+1;i<=attacker_int;i++){
                                            eachmove=col[attacker_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_int-1;i>=0;i--){
                                            eachmove=col[attacker_letter]+row[i];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                }
                                else if(rook_int==attacker_int){
                                    // they are on the same rank
                                    if(rook_letter>attacker_letter){
                                        // rook on right
                                        for(int i=attacker_letter;i<rook_letter;i++){
                                            eachmove=col[i]+row[attacker_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_letter+1;i<=7;i++){
                                            eachmove=col[i]+row[rook_int];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    else{
                                        // rook on left
                                        for(int i=rook_letter+1;i<=attacker_letter;i++){
                                            eachmove=col[i]+row[attacker_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_letter-1;i>=0;i--){
                                            eachmove=col[i]+row[rook_int];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                }
                                tempmoves.addAll(block);
                                block.clear();
                            }
                            else{
                                tempmoves.clear();
                            }
                        }
                        
                    }

                }
                else{
                    moves.addAll(tempmoves);
                }
                
            }
            else{   // here you write for black rook

                int pos=Integer.parseInt(squares[1]);   // pos means row
                int file=0; // file means column
                for(int i=0;i<8;i++){
                    if(col[i].equals(squares[0])){
                        file=i;
                        break;
                    }
                }
                int j=pos;
                ArrayList<String>directionalmoves=new ArrayList<String>();
                for(int i=file-1;i>=0;i--){
                    
                    String sqAhead=col[i]+pos;
                    directionalmoves.add(sqAhead);
                        
                    
                }
                // now i check this arraylist;
                int flag1=0; // if its zero then i can check for further piece
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos;
                
                // now for N
                for(j=pos+1;j<=8;j++){
                    int i=file;
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                        
                    
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos;
                // now for E
                for(int i=file+1;i<8;i++){
                    
                    String sqAhead=col[i]+pos;
                    directionalmoves.add(sqAhead);
                        
                    
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos;
                // now for S
                for(j=pos-1;j>0;j--){
                    
                    int i=file;
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                    // j--;
                    
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
                }
//---------------------------------------------------------------
                // now we start eliminating as per cases
                String king_square[]=king.getSquare().split("");
                if(king.getUnderCheck()){
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                            String eachmove="";
                            String attacker_square[]=p.getSquare().split("");
                            int attacker_letter=0;
                            int attacker_int=0;
                            int king_int=0;
                            int king_letter=0;
                            for(int i=0;i<8;i++){
                                if(col[i].equals(attacker_square[0])){
                                    attacker_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(col[i].equals(king_square[0])){
                                    king_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(attacker_square[1])){
                                    attacker_int=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(king_square[1])){
                                    king_int=i;
                                    break;
                                }
                            }
                            List <String> block=new ArrayList<String>();
                            if(rook.getIsPinned()){
                                tempmoves.clear();
                                break;
        
                            }
                            else{   // all we can do is attack or block
                                if(p instanceof Pawn || p instanceof Knight){
                                        for(String s:tempmoves){
                                            if(s.equals(p.getSquare())){
                                                block.add(s);
                                                // break;
                                            }
                                        }
                                        tempmoves.clear();
                                        tempmoves.addAll(block);
                                    
                                }
                                else if(p instanceof Rook){
                                    // either you attack or you block
                                    // first for attack
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }

                                    // now for blocking
                                    if(attacker_letter>king_letter){
                                        // attacker is on right                                        
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }
                                    else if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                    else if(attacker_letter<king_letter){
                                        // attacker is on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }

                                    }
                                    else if(attacker_int<king_int){
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }            
                                    tempmoves.clear();
                                    tempmoves.addAll(block);

                                    
                                }
                                else if(p instanceof Bishop){

                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            break;
                                        }
                                    }
                                    // now we see to block
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    tempmoves.clear();
                                    tempmoves.addAll(block);

                                }
                                else if(p instanceof Queen){
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            
                                            block.add(s);
                                            break;
                                        }
                                    }

                                    // first moves like rook
                                    if(attacker_letter==king_letter){
                                        // they are on the same file
                                        if(attacker_int>king_int){ // attacker on top
                                            int int_inc=king_int+1;
                                            for(int i=int_inc;i<attacker_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                        else{   // attacker on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=int_inc;i<king_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else if(attacker_int==king_int){
                                        // they are on the same rank
                                        if(attacker_letter>king_letter){
                                            // attacker on right
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }

                                        }
                                        else{   // attacker on left
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                    else{   // here are the moves for bishop

                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int-1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    
                                        tempmoves.clear();
                                        tempmoves.addAll(block);
                                    }

                                }
                            }

                        }
                    }
                    
                }
                else if(rook.getIsPinned()){
                    // i have to write this part
                    // you can move only if pinned by rook or queen and that too vertically or horizontally
                    String[] rook_square=rook.getSquare().split("");
                    String[] attacker_square=rook.getPinnedBy().split("");
                    List<String> block=new ArrayList<>();
                    String eachmove="";
                    for(Piece P:game.getWhitePieces()){
                        if(P.getSquare().equals(rook.getPinnedBy())){
                            if(P instanceof Rook || P instanceof Queen){
                                int attacker_letter=0;
                                int attacker_int=0;
                                int rook_int=0;
                                int rook_letter=0;
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(attacker_square[0])){
                                        attacker_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(rook_square[0])){
                                        rook_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(attacker_square[1])){
                                        attacker_int=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(rook_square[1])){
                                        rook_int=i;
                                        break;
                                    }
                                }
                                if(rook_letter==attacker_letter){
                                    
                                    // they are on the same file
                                    if(rook_int>attacker_int){
                                        // rook on top
                                        for(int i=attacker_int;i<rook_int;i++){
                                            eachmove=col[attacker_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_int+1;i<=7;i++){
                                            eachmove=col[attacker_letter]+row[i];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    else{
                                        // rook on bottom
                                        for(int i=rook_int+1;i<=attacker_int;i++){
                                            eachmove=col[attacker_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_int-1;i>=0;i--){
                                            eachmove=col[attacker_letter]+row[i];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                }
                                else if(rook_int==attacker_int){
                                    // they are on the same rank
                                    if(rook_letter>attacker_letter){
                                        // rook on right
                                        for(int i=attacker_letter;i<rook_letter;i++){
                                            eachmove=col[i]+row[attacker_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_letter+1;i<=7;i++){
                                            eachmove=col[i]+row[rook_int];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    else{
                                        // rook on left
                                        for(int i=rook_letter+1;i<=attacker_letter;i++){
                                            eachmove=col[i]+row[attacker_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                        // now moves between king and rook
                                        for(int i=rook_letter-1;i>=0;i--){
                                            eachmove=col[i]+row[rook_int];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                }
                                tempmoves.addAll(block);
                                block.clear();
                            }
                            else{
                                tempmoves.clear();
                            }
                        }
                        
                    }

                }
                else{
                    moves.addAll(tempmoves);
                }

            }
            
        }
        return moves;
    }
    public ArrayList<String> KnightMoves(Knight knight,King king){
        System.out.println("hi there i am entering knight function");
        ArrayList<String> moves=new ArrayList<String>();
        ArrayList<String> tempmoves=new ArrayList<String>();
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] squares=knight.getSquare().split("");
        int pos=Integer.parseInt(squares[1]);
        int file=0;
        for(int i=0;i<8;i++){
            if(col[i].equals(squares[0])){
                file=i;
                break;
            }
        }
        if(knight.getIsAlive()){
            // keeping this outside as its common for black and white knight
            ArrayList<String> directionalmoves=new ArrayList<String>();
            if(file-1>=0 && pos-2>=1){
                directionalmoves.add(col[file-1]+(pos-2));
            }
            if(file+1<=7 && pos-2>=1){
                directionalmoves.add(col[file+1]+(pos-2));
            }
            if(file-2>=0 && pos-1>=1){
                directionalmoves.add(col[file-2]+(pos-1));
            }
            if(file+2<=7 && pos-1>=1){
                directionalmoves.add(col[file+2]+(pos-1));
            }
            if(file-1>=0 && pos+2<=8){
                directionalmoves.add(col[file-1]+(pos+2));
            }
            if(file+1<=7 && pos+2<=8){
                directionalmoves.add(col[file+1]+(pos+2));
            }
            if(file-2>=0 && pos+1<=8){
                directionalmoves.add(col[file-2]+(pos+1));
            }
            if(file+2<=7 && pos+1<=8){
                directionalmoves.add(col[file+2]+(pos+1));
            }

            tempmoves.addAll(directionalmoves);
            System.out.println("we got all knight moves");

            if(knight.getColor().equals("white")){
                System.out.println("white knight it is");
                
                // now we see if these moves are valid

//-----------------------------------------------------------
                if(king.getUnderCheck()){
                    System.out.println("-------------------------------------------------------------------");
                    System.out.println("king is under check");
                    System.out.println("-------------------------------------------------------------------");
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    String attackSq=king.getCheckByWhom().get(0);
                    System.out.println("the attacking square is :"+attackSq);
                    List <String> block=new ArrayList<String>();
                    if(!knight.getIsPinned()){
                        System.out.println("knight is not pinned....it might defend");
                        // in this case it can attack the piece or block it
                        // first lets see for attack chances
                        for(String s:tempmoves){
                            if(s.equals(attackSq)){
                                System.out.println("knight can attack the piece");
                                System.out.println("adding this sq to its move");
                                block.add(s);
                                break;
                            }
                        }
                        System.out.println("reached here after checking attack sq ");
                        // now lets see if it can block
                        String king_square[]=king.getSquare().split("");
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(attackSq)){
                                String attacker_square[]=p.getSquare().split("");
                                int attacker_letter=0;
                                int king_letter=0;
                                int attacker_int=0;
                                int king_int=0;
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(attacker_square[0])){
                                        attacker_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(king_square[0])){
                                        king_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(attacker_square[1])){
                                        attacker_int=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(king_square[1])){
                                        king_int=i;
                                        break;
                                    }
                                }
                                if(p instanceof Pawn||p instanceof Knight){
                                    // in this case you cant block
                                }
                                else if(p instanceof Rook){
                                    
                                    String eachmove="";
                                    if(attacker_letter>king_letter){
                                        // attacker is on right                                        
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }
                                    else if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                    else if(attacker_letter<king_letter){
                                        // attacker is on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }

                                    }
                                    else if(attacker_int<king_int){
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }            
                                    

                                }
                                else if(p instanceof Bishop){
                                    // i have to see for diagonal entries
                                    
                                    String eachmove="";
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            System.out.println("attacker belongs to this region");
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            System.out.println("now we check if knight can come on any of the square");
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                System.out.println("checking for "+eachmove);
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        System.out.println("yes it can come on "+eachmove);
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    

                                }
                                else if(p instanceof Queen){
                                    
                                    // first for moves like rook
                                    String eachmove="";
                                    if(attacker_letter==king_letter){
                                        // they are on the same file
                                        if(attacker_int>king_int){ // attacker on top
                                            int int_inc=king_int+1;
                                            for(int i=int_inc;i<attacker_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                        else{   // attacker on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=int_inc;i<king_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else if(attacker_int==king_int){
                                        // they are on the same rank
                                        if(attacker_letter>king_letter){
                                            // attacker on right
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }

                                        }
                                        else{   // attacker on left
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                    else{   // here are the moves for bishop

                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int-1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        } 

                                    }
                                }
                            }
                        }
                    }
                    else{
                        // in this case it cant do anything
                        
                    } 
                    
                    tempmoves.clear();
                    tempmoves.addAll(block);

                }
                
//---------------------------------------------------------
                else if(knight.getIsPinned()){
                    tempmoves.clear();
                }

//------------------------------------------------------------
                else{
                    for(String sq:tempmoves){
                        int flag=0;
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag=1;
                                System.out.println("white piece found");
                                break;
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(p instanceof King){
                                    flag=1;
                                    break;
                                }
                                else{
                                    moves.add(sq);
                                    flag=1;
                                    break;
                                }
                                
                            }
                        }
                        if(flag==0){
                            moves.add(sq);
                        }
                    }    
                    return moves; 
                }
                

            }
            else{ // this is for black knight

                if(king.getUnderCheck()){
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    String attackSq=king.getCheckByWhom().get(0);
                    List <String> block=new ArrayList<String>();
                    if(!knight.getIsPinned()){
                        // in this case it can attack the piece or block it
                        // first lets see for attack chances
                        for(String s:tempmoves){
                            if(s.equals(attackSq)){
                                block.add(s);
                                break;
                            }
                        }
                        // now lets see if it can block
                        String king_square[]=king.getSquare().split("");
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(attackSq)){
                                String attacker_square[]=p.getSquare().split("");
                                int attacker_letter=0;
                                int king_letter=0;
                                int attacker_int=0;
                                int king_int=0;
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(attacker_square[0])){
                                        attacker_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(col[i].equals(king_square[0])){
                                        king_letter=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(attacker_square[1])){
                                        attacker_int=i;
                                        break;
                                    }
                                }
                                for(int i=0;i<8;i++){
                                    if(row[i].equals(king_square[1])){
                                        king_int=i;
                                        break;
                                    }
                                }
                                if(p instanceof Pawn||p instanceof Knight){
                                    // in this case you cant block
                                }
                                else if(p instanceof Rook){
                                    
                                    String eachmove="";
                                    if(attacker_letter>king_letter){
                                        // attacker is on right                                        
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }
                                    else if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                    else if(attacker_letter<king_letter){
                                        // attacker is on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }

                                    }
                                    else if(attacker_int<king_int){
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }            

                                }
                                else if(p instanceof Bishop){
                                    // i have to see for diagonal entries
                                    
                                    String eachmove="";
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    

                                }
                                else if(p instanceof Queen){
                                    
                                    // first for moves like rook
                                    String eachmove="";
                                    if(attacker_letter==king_letter){
                                        // they are on the same file
                                        if(attacker_int>king_int){ // attacker on top
                                            int int_inc=king_int+1;
                                            for(int i=int_inc;i<attacker_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                        else{   // attacker on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=int_inc;i<king_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else if(attacker_int==king_int){
                                        // they are on the same rank
                                        if(attacker_letter>king_letter){
                                            // attacker on right
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }

                                        }
                                        else{   // attacker on left
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                    else{   // here are the moves for bishop

                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int-1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    

                                    }
                                }
                            }
                        }
                    }
                    else{
                        // in this case it cant do anything
                    } 
                    
                    tempmoves.clear();
                    tempmoves.addAll(block);

                }
                
//---------------------------------------------------------
                else if(knight.getIsPinned()){
                    tempmoves.clear();
                }

//------------------------------------------------------------
                else{
                    for(String sq:tempmoves){
                        int flag=0;
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag=1;
                                System.out.println("black piece found");
                                break;
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(p instanceof King){
                                    flag=1;
                                    break;
                                }
                                else{
                                    moves.add(sq);
                                    flag=1;
                                    break;
                                }
                                
                            }
                        }
                        if(flag==0){
                            moves.add(sq);
                        }
                    }    
                    return moves; 
                }

            }
        }
        moves.addAll(tempmoves);
        return moves;
    }
    public ArrayList<String> BishopMoves(Bishop bishop,King king){
        ArrayList<String> moves=new ArrayList<String>();
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        List <String> tempmoves=new ArrayList<String>();
        if(bishop.getIsAlive()){
            String[]squares=bishop.getSquare().split("");
            // first add for N-W, then N-E, then S-E, then S-W
            if(bishop.getColor().equals("white")){
                int pos=Integer.parseInt(squares[1]);
                int file=0;
                for(int i=0;i<8;i++){
                    if(col[i].equals(squares[0])){
                        file=i;
                        break;
                    }
                }
                int j=pos+1;
                ArrayList<String>directionalmoves=new ArrayList<String>();
                for(int i=file-1;i>=0;i--){
                    if(j<=8){
                        String sqAhead=col[i]+j;
                        directionalmoves.add(sqAhead);
                        j++;
                    }
                }
                
                // now i check this arraylist;
                int flag1=0; // if its zero then i can check for further piece
                for(String sq:directionalmoves){
                    int flag2=0; //this is for internal round check for white/black/no piece
                    System.out.println("checking for "+sq);
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    System.out.println("adding this square though");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos+1;
                // now for N-E
                for(int i=file+1;i<8;i++){
                    if(j<=8){
                        String sqAhead=col[i]+j;
                        System.out.println("the square is "+sqAhead);
                        directionalmoves.add(sqAhead);
                        j++;
                    }
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for hi "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("hi1");
                                    System.out.println("black piece found no more squares");
                                    System.out.println("adding this square though");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos-1;
                // now for S-E
                for(int i=file+1;i<8;i++){
                    if(j>0){
                        String sqAhead=col[i]+j;
                        directionalmoves.add(sqAhead);
                        j--;
                    }
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    System.out.println("adding this square though");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos-1;
                // now for S-W
                for(int i=file-1;i>=0;i--){
                    if(j>0){
                        String sqAhead=col[i]+j;
                        directionalmoves.add(sqAhead);
                        j--;
                    }
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    System.out.println("adding this square though");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }

//---------------------------------------------------------------
                // now we start eliminating as per cases
                String king_square[]=king.getSquare().split("");
                if(king.getUnderCheck()){
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    List <String> block=new ArrayList<String>();
                    System.out.println("-----------------------------------------------");
                    System.out.println("HAAAAALP kING IN CHECK");
                    System.out.println("-----------------------------------------------");
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                            String eachmove="";
                            String attacker_square[]=p.getSquare().split("");
                            int attacker_letter=0;
                            int king_letter=0;
                            int attacker_int=0;
                            int king_int=0;
                            System.out.println("got the piece that checked the king");
                            for(int i=0;i<8;i++){
                                if(col[i].equals(attacker_square[0])){
                                    attacker_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(col[i].equals(king_square[0])){
                                    king_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(attacker_square[1])){
                                    attacker_int=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(king_square[1])){
                                    king_int=i;
                                    break;
                                }
                            }
                            
                            // see if you can attack or just pinned
                            if(bishop.getIsPinned()){
                                // we cant stop it
                                System.out.println("bishop is pinned cant move");
                                // tempmoves.clear();
                                break;
                            }
                            else{   // all we can do is attack or block
                                if(p instanceof Pawn || p instanceof Knight){
                                        for(String s:tempmoves){
                                            if(s.equals(p.getSquare())){
                                                System.out.println("got the stupid pawn/knight at "+s);
                                                block.add(s);
                                                // break;
                                            }
                                        }
                                        // tempmoves.clear();
                                        // tempmoves.addAll(block);
                                    
                                }
                                else if(p instanceof Rook){
                                    // either you attack or you block
                                    // first for attack
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }

                                    // now for blocking
                                    if(attacker_letter>king_letter){
                                        // attacker is on right                                        
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }
                                    else if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                    else if(attacker_letter<king_letter){
                                        // attacker is on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }

                                    }
                                    else if(attacker_int<king_int){
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }            
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);

                                    
                                }
                                else if(p instanceof Bishop){

                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);

                                }
                                else if(p instanceof Queen){
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            
                                            block.add(s);
                                            // break;
                                        }
                                    }

                                    // first moves like rook
                                    if(attacker_letter==king_letter){
                                        // they are on the same file
                                        if(attacker_int>king_int){ // attacker on top
                                            int int_inc=king_int+1;
                                            for(int i=int_inc;i<attacker_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                        else{   // attacker on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=int_inc;i<king_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else if(attacker_int==king_int){
                                        // they are on the same rank
                                        if(attacker_letter>king_letter){
                                            // attacker on right
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }

                                        }
                                        else{   // attacker on left
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                    else{   // here are the moves for bishop

                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    
                                        // tempmoves.clear();
                                        // tempmoves.addAll(block);
                                    }

                                }
                            }
                            
                        }
                    }
                    System.out.println("the moves in block is: ");
                    for(String s:block){
                        System.out.println("here is the mve "+s);
                    }
                    tempmoves.clear();
                    tempmoves.addAll(block);

                }
//------------------------------------------------------------------------

                else if(bishop.getIsPinned()){
                    // if pinned you can move only in direction of pin 
                    List <String> block=new ArrayList<String>();
                    String attacker_square[]=bishop.getPinnedBy().split("");
                    String piece_square[]=bishop.getSquare().split("");
                    String eachmove="";
                    int attacker_letter=0;
                    int piece_letter=0;
                    int attacker_int=0;
                    int piece_int=0;
                    for(int i=0;i<8;i++){
                        if(col[i].equals(attacker_square[0])){
                            attacker_letter=i;
                            break;
                        }
                    }
                    for(int i=0;i<8;i++){
                        if(col[i].equals(piece_square[0])){
                            piece_letter=i;
                            break;
                        }
                    }
                    for(int i=0;i<8;i++){
                        if(row[i].equals(attacker_square[1])){
                            attacker_int=i;
                            break;
                        }
                    }
                    for(int i=0;i<8;i++){
                        if(row[i].equals(piece_square[1])){
                            piece_int=i;
                            break;
                        }
                    }
                    
                    // we can move only if its a queen or bishop
                    for(Piece p:game.getBlackPieces()){
                        if(p.getSquare().equals(bishop.getPinnedBy())){
                            if(p instanceof Bishop|| p instanceof Queen){
                                if(attacker_letter>piece_letter){   // this serves both queen and bishop
                                    // attacker on right
                                    if(attacker_int>piece_int){
                                        // attacker on top
                                        int int_inc=piece_int+1;
                                        for(int i=piece_letter+1;i<=attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int-1;
                                        for(int i=piece_letter-1;i>=0;i--){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec--;
                                            }
                                        }
                                    }
                                    else if(attacker_int<piece_int){
                                        // attacker on bottom
                                        int int_inc=piece_int-1;
                                        for(int i=piece_letter+1;i<=attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int+1;
                                        for(int i=piece_letter-1;i>=0;i--){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec++;
                                            }
                                        }
                                    }
                                }
                                else if(attacker_letter<piece_letter){  // this serves both queen and bishop
                                    // attacker on left
                                    if(attacker_int>piece_int){
                                        // attacker on top
                                        int int_inc=piece_int+1;
                                        for(int i=piece_letter-1;i>=attacker_letter;i--){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
            
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int-1;
                                        for(int i=piece_letter+1;i<=7;i++){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec--;
                                            }
                                        }
                                    }
                                    else if(attacker_int<piece_int){
                                        // attacker on bottom
                                        int int_inc=piece_int-1;
                                        for(int i=piece_letter-1;i>=attacker_letter;i--){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int+1;
                                        for(int i=piece_letter+1;i<=7;i++){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec++;
                                            }
                                        }
            
                                    }
                                }
                                else{
                                    // we are pinned by a queen vertically/horizontally and hence cant move
                                    // tempmoves.clear();
                                }
                            }
                            else{
                                // we can move in this case as we pinned by a rook
                                // tempmoves.clear();
                            }
                        }
                    }
                    

                    tempmoves.clear();
                    tempmoves.addAll(block);
                    // block.clear();
                    // return moves;
                }
//-------------------------------------------------------------                
                else{
                    // you are free to move as per will
                    // moves.addAll(tempmoves);
                    // tempmoves.clear();
                    // return moves;
                }
                
                
            }
            else{   // here you write for black bishop

                int pos=Integer.parseInt(squares[1]);
                int file=0;
                for(int i=0;i<8;i++){
                    if(col[i].equals(squares[0])){
                        file=i;
                        break;
                    }
                }
                int j=pos+1;
                ArrayList<String>directionalmoves=new ArrayList<String>();
                for(int i=file-1;i>=0;i--){
                    if(j<=8){
                        String sqAhead=col[i]+j;
                        directionalmoves.add(sqAhead);
                        j++;
                    }
                }
                
                // now i check this arraylist;
                int flag1=0; // if its zero then i can check for further piece
                for(String sq:directionalmoves){
                    int flag2=0; //this is for internal round check for white/black/no piece
                    System.out.println("checking for "+sq);
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }
                directionalmoves.clear();
                j=pos+1;
                // now for N-E
                for(int i=file+1;i<8;i++){
                    if(j<=8){
                        String sqAhead=col[i]+j;
                        System.out.println("the square is "+sqAhead);
                        directionalmoves.add(sqAhead);
                        j++;
                    }
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }
                
                directionalmoves.clear();
                j=pos-1;
                // now for S-E
                for(int i=file+1;i<8;i++){
                    if(j>0){
                        String sqAhead=col[i]+j;
                        directionalmoves.add(sqAhead);
                        j--;
                    }
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }
                
                directionalmoves.clear();
                j=pos-1;
                // now for S-W
                for(int i=file-1;i>=0;i--){
                    if(j>0){
                        String sqAhead=col[i]+j;
                        directionalmoves.add(sqAhead);
                        j--;
                    }
                }
                flag1=0;
                for(String sq:directionalmoves){
                    System.out.println("checking for "+sq);
                    int flag2=0; //this is for internal round check for white/black/no piece
                    if(flag1==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(!(p instanceof King)){
                                    tempmoves.add(sq);
                                    flag1=1; // this means no more adding
                                    flag2=1;
                                    System.out.println("black piece found no more squares");
                                    break;
                                }
                                else{
                                    flag1=1;
                                    flag2=1;
                                    System.out.println("black king collision");
                                    break;
                                }
                                
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag2=1;
                                flag1=1;
                                System.out.println("collision ho jaayega");
                                break;
                                
                                
                            }
                        }
                        if(flag2==0){
                            tempmoves.add(sq);
                            System.out.println("move added");
                        }
                    }
                    else{
                        break;
                    }
                }

//---------------------------------------------------------------
                // now we start eliminating as per cases
                String king_square[]=king.getSquare().split("");
                if(king.getUnderCheck()){
                    if(king.getCheckByWhom().size()>1){
                        System.out.println("double check cant do anything");
                        moves.clear();
                        return moves;
                    }
                    List <String> block=new ArrayList<String>();
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                            String eachmove="";
                            String attacker_square[]=p.getSquare().split("");
                            int attacker_letter=0;
                            int king_letter=0;
                            int attacker_int=0;
                            int king_int=0;
                            for(int i=0;i<8;i++){
                                if(col[i].equals(attacker_square[0])){
                                    attacker_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(col[i].equals(king_square[0])){
                                    king_letter=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(attacker_square[1])){
                                    attacker_int=i;
                                    break;
                                }
                            }
                            for(int i=0;i<8;i++){
                                if(row[i].equals(king_square[1])){
                                    king_int=i;
                                    break;
                                }
                            }
                            
                            // see if you can attack or just pinned
                            if(bishop.getIsPinned()){
                                // we cant stop it
                                // tempmoves.clear();
                                break;
                            }
                            else{   // all we can do is attack or block
                                if(p instanceof Pawn || p instanceof Knight){
                                        for(String s:tempmoves){
                                            if(s.equals(p.getSquare())){
                                                block.add(s);
                                                // break;
                                            }
                                        }
                                        // tempmoves.clear();
                                        // tempmoves.addAll(block);
                                    
                                }
                                else if(p instanceof Rook){
                                    // either you attack or you block
                                    // first for attack
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }

                                    // now for blocking
                                    if(attacker_letter>king_letter){
                                        // attacker is on right                                        
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }
                                    else if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                    else if(attacker_letter<king_letter){
                                        // attacker is on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }

                                    }
                                    else if(attacker_int<king_int){
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                            
                                        }
                                    }            
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);

                                    
                                }
                                else if(p instanceof Bishop){

                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                        
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                            
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                            
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int-1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);

                                }
                                else if(p instanceof Queen){
                                    for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            
                                            block.add(s);
                                            // break;
                                        }
                                    }

                                    // first moves like rook
                                    if(attacker_letter==king_letter){
                                        // they are on the same file
                                        if(attacker_int>king_int){ // attacker on top
                                            int int_inc=king_int+1;
                                            for(int i=int_inc;i<attacker_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                        else{   // attacker on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=int_inc;i<king_int;i++){
                                                eachmove=col[king_letter]+row[i];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else if(attacker_int==king_int){
                                        // they are on the same rank
                                        if(attacker_letter>king_letter){
                                            // attacker on right
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }

                                        }
                                        else{   // attacker on left
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[king_int];
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                                
                                            }
                                        }
                                    }
                                    else{   // here are the moves for bishop

                                        if(attacker_letter>king_letter){
                                            // attacker is on right
                                            
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=king_int+1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                    
                                                }                                                
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=king_int-1;
                                                for(int i=king_letter+1;i<attacker_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
    
                                                }
                                                
                                            }
                                        }
                                        else{
                                            // attacker is on left
                                            if(attacker_int>king_int){
                                                // attacker is on top
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc--;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                            else{
                                                // attacker is on bottom
                                                int int_inc=attacker_int+1;
                                                for(int i=attacker_letter+1;i<king_letter;i++){
                                                    eachmove=col[i]+row[int_inc];
                                                    int_inc++;
                                                    for(String s:tempmoves){
                                                        if(s.equals(eachmove)){
                                                            block.add(eachmove);
                                                        }
                                                    }
                                                }
                                            }
                                        }    
                                        // tempmoves.clear();
                                        // tempmoves.addAll(block);
                                    }

                                }
                            }
                            
                        }
                    }
                    tempmoves.clear();
                    tempmoves.addAll(block);

                }
//------------------------------------------------------------------------

                else if(bishop.getIsPinned()){
                    // if pinned you can move only in direction of pin
                    List <String> block=new ArrayList<String>(); 
                    String attacker_square[]=bishop.getPinnedBy().split("");
                    String piece_square[]=bishop.getSquare().split("");
                    String eachmove="";
                    int attacker_letter=0;
                    int piece_letter=0;
                    int attacker_int=0;
                    int piece_int=0;
                    for(int i=0;i<8;i++){
                        if(col[i].equals(attacker_square[0])){
                            attacker_letter=i;
                            break;
                        }
                    }
                    for(int i=0;i<8;i++){
                        if(col[i].equals(piece_square[0])){
                            piece_letter=i;
                            break;
                        }
                    }
                    for(int i=0;i<8;i++){
                        if(row[i].equals(attacker_square[1])){
                            attacker_int=i;
                            break;
                        }
                    }
                    for(int i=0;i<8;i++){
                        if(row[i].equals(piece_square[1])){
                            piece_int=i;
                            break;
                        }
                    }
                    
                    // we can move only if its a queen or bishop
                    for(Piece p:game.getWhitePieces()){
                        if(p.getSquare().equals(bishop.getPinnedBy())){
                            if(p instanceof Bishop|| p instanceof Queen){
                                if(attacker_letter>piece_letter){   // this serves both queen and bishop
                                    // attacker on right
                                    if(attacker_int>piece_int){
                                        // attacker on top
                                        int int_inc=piece_int+1;
                                        for(int i=piece_letter+1;i<=attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int-1;
                                        for(int i=piece_letter-1;i>=0;i--){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec--;
                                            }
                                        }
                                    }
                                    else if(attacker_int<piece_int){
                                        // attacker on bottom
                                        int int_inc=piece_int-1;
                                        for(int i=piece_letter+1;i<=attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int+1;
                                        for(int i=piece_letter-1;i>=0;i--){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec++;
                                            }
                                        }
                                    }
                                }
                                else if(attacker_letter<piece_letter){  // this serves both queen and bishop
                                    // attacker on left
                                    if(attacker_int>piece_int){
                                        // attacker on top
                                        int int_inc=piece_int+1;
                                        for(int i=piece_letter-1;i>=attacker_letter;i--){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
            
                                        }
                                        // now adding backward moves
                                        int int_dec=piece_int-1;
                                        for(int i=piece_letter+1;i<=7;i++){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec--;
                                            }
                                        }
                                    }
                                    else if(attacker_int<piece_int){
                                        // attacker on bottom
                                        int int_inc=piece_int-1;
                                        for(int i=piece_letter-1;i>=attacker_letter;i--){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                    break;
                                                }
                                            }
                                        }

                                        // now adding backward moves
                                        int int_dec=piece_int+1;
                                        for(int i=piece_letter+1;i<=7;i++){
                                            eachmove=col[i]+row[int_dec];
                                            if(eachmove.equals(king.getSquare())){
                                                break;
                                            }
                                            else{
                                                block.add(eachmove);
                                                int_dec++;
                                            }
                                        }
            
                                    }
                                }
                                else{
                                    // we are pinned by a queen vertically/horizontally and hence cant move
                                    // tempmoves.clear();
                                }
                            }
                            else{
                                // we can move in this case as we pinned by a rook
                                // tempmoves.clear();
                            }
                        }
                    }
                    

                    // moves.addAll(block);
                    tempmoves.clear();
                    tempmoves.addAll(block);
                    // return moves;
                }
//-------------------------------------------------------------                
                else{
                    // you are free to move as per will
                }

            }
            
        }
        moves.addAll(tempmoves);
        return moves;
    }
    public ArrayList<String> QueenMoves(Queen queen,King king){
        System.out.println("---------------------------------------------");
        System.out.println("entered into queen moves");
        System.out.println("---------------------------------------------");
        ArrayList<String> moves=new ArrayList<String>();
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] squares=queen.getSquare().split("");
        int pos=Integer.parseInt(squares[1]);
        int file=0;
        for(int i=0;i<8;i++){
            if(col[i].equals(squares[0])){
                file=i;
                break;
            }
        }
        ArrayList<ArrayList<String>> allMoves=new ArrayList<ArrayList<String>>();
        ArrayList<String> directionalmoves=new ArrayList<String>();
        ArrayList<String> tempmoves=new ArrayList<String>();
        ArrayList<String> block=new ArrayList<String>();


        int j=pos+1;
        for(int i=file-1;i>=0;i--){
            if(j<=8){
                String sqAhead=col[i]+j;
                directionalmoves.add(sqAhead);
                j++;
            }
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        j=pos+1;
        // now for N-E
        for(int i=file+1;i<8;i++){
            if(j<=8){
                String sqAhead=col[i]+j;
                // System.out.println("the square is "+sqAhead);
                directionalmoves.add(sqAhead);
                j++;
            }
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        j=pos-1;
        // now for S-E
        for(int i=file+1;i<8;i++){
            if(j>0){
                String sqAhead=col[i]+j;
                directionalmoves.add(sqAhead);
                j--;
            }
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        j=pos-1;
        // now for S-W
        for(int i=file-1;i>=0;i--){
            if(j>0){
                String sqAhead=col[i]+j;
                directionalmoves.add(sqAhead);
                j--;
            }
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        //-------------------------------------------------------------------------------------------------
        // now rook moves to be added
        j=pos;
        for(int i=file-1;i>=0;i--){ 
            String sqAhead=col[i]+pos;
            directionalmoves.add(sqAhead);                            
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        j=pos;
        // now for N
        for(j=pos+1;j<=8;j++){
            int i=file;
            String sqAhead=col[i]+j;
            directionalmoves.add(sqAhead);            
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        j=pos;
        // now for E
        for(int i=file+1;i<8;i++){
            String sqAhead=col[i]+pos;
            directionalmoves.add(sqAhead);   
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();
        j=pos;
        // now for S
        for(j=pos-1;j>0;j--){
            int i=file;
            String sqAhead=col[i]+j;
            directionalmoves.add(sqAhead);
            // j--;
        }
        allMoves.add(new ArrayList<>(directionalmoves));
        directionalmoves.clear();        
        // int flag1=0; // if its zero then i can check for further piece
        if(queen.getColor().equals("white")){
            System.out.println("doing for white queen");
            for(ArrayList<String> movesList:allMoves){
                // System.out.println("entered into allmoves array");
                int flag_array=0; // this is for entire sublist
                
                for(String sq:movesList){
                    // System.out.println("checking for this square in loop "+sq);
                    // System.out.println("");

                    int flag_move=0; // this is for a move
                    
                    if(flag_array==0){
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                if(p instanceof King){
                                    flag_array=1;
                                    flag_move=1;
                                }
                                else{
                                    flag_array=1;
                                    flag_move=1;
                                    tempmoves.add(sq);
                                    // System.out.println("added to possible moves");
                                    // System.out.println("");
                                }
                                
                                break;
                            }
                        }
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                flag_array=1;
                                flag_move=1;
                                break;
                            }
                        }
                        if(flag_move==0){
                            // System.out.println("added to possible moves");
                            // System.out.println("");
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        // System.out.println("no more moves from this array");
                        // System.out.println("");
                        break;
                    }
    
                }
            }
            System.out.println("before elimination here are the moves in tempmoves");
            for(String shit:tempmoves){
                System.out.println("the move is "+shit);
            }
            
//------------------------------------------------------------------
            // now we start eliminating as per cases

            String king_square[]=king.getSquare().split("");
            if(king.getUnderCheck()){ // this is if king in check
                if(king.getCheckByWhom().size()>1){
                    System.out.println("double check cant do anything");
                    moves.clear();
                    return moves;
                }
                for(Piece p:game.getBlackPieces()){
                    if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                        String eachmove="";
                        String attacker_square[]=p.getSquare().split("");
                        int attacker_letter=0;
                        int king_letter=0;
                        int attacker_int=0;
                        int king_int=0;
                        for(int i=0;i<8;i++){
                            if(col[i].equals(attacker_square[0])){
                                attacker_letter=i;
                                break;
                             }
                         }
                         for(int i=0;i<8;i++){
                             if(col[i].equals(king_square[0])){
                                 king_letter=i;
                                 break;
                             }
                         }
                         for(int i=0;i<8;i++){
                             if(row[i].equals(attacker_square[1])){
                                 attacker_int=i;
                                 break;
                             }
                         }
                         for(int i=0;i<8;i++){
                             if(row[i].equals(king_square[1])){
                                 king_int=i;
                                 break;
                             }
                         }
                         if(queen.getIsPinned()){
                            //  tempmoves.clear();
                             break;
                         }
                         else{
                             if(p instanceof Pawn || p instanceof Knight){
                                 for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            block.add(s);
                                            // break;
                                        }
                                    }
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);
                                
                            }
                            else if(p instanceof Rook){
                                // either you attack or you block
                                // first for attack
                                for(String s:tempmoves){
                                    if(s.equals(p.getSquare())){
                                        block.add(s);
                                        // break;
                                    }
                                }

                                // now for blocking
                                if(attacker_letter>king_letter){
                                    // attacker is on right                                        
                                    for(int i=king_letter+1;i<attacker_letter;i++){
                                        eachmove=col[i]+row[king_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }

                                    }
                                }
                                else if(attacker_int>king_int){
                                    // attacker is on top
                                    int int_inc=king_int+1;
                                    for(int i=int_inc;i<attacker_int;i++){
                                        eachmove=col[king_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }

                                    }

                                }
                                else if(attacker_letter<king_letter){
                                    // attacker is on left
                                    for(int i=attacker_letter+1;i<king_letter;i++){
                                        eachmove=col[i]+row[king_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }

                                    }

                                }
                                else if(attacker_int<king_int){
                                    int int_inc=attacker_int+1;
                                    for(int i=int_inc;i<king_int;i++){
                                        eachmove=col[king_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }

                                    }
                                }            
                                // tempmoves.clear();
                                // tempmoves.addAll(block);


                            }
                            else if(p instanceof Bishop){

                                for(String s:tempmoves){
                                    if(s.equals(p.getSquare())){
                                        block.add(s);
                                        // break;
                                    }
                                }
                                if(attacker_letter>king_letter){
                                    // attacker is on right

                                    if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }

                                        }

                                    }
                                    else{
                                        // attacker is on bottom
                                        int int_inc=king_int-1;
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }

                                        }

                                    }
                                }
                                else{
                                    // attacker is on left
                                    if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=attacker_int-1;
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        // attacker is on bottom
                                        int int_inc=attacker_int+1;
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                    }
                                }    
                                // tempmoves.clear();
                                // tempmoves.addAll(block);

                            }
                            else if(p instanceof Queen){
                                for(String s:tempmoves){
                                    if(s.equals(p.getSquare())){

                                        block.add(s);
                                        // break;
                                    }
                                }

                                // first moves like rook
                                if(attacker_letter==king_letter){
                                    // they are on the same file
                                    if(attacker_int>king_int){ // attacker on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }

                                        }
                                    }
                                    else{   // attacker on bottom
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }

                                        }
                                    }
                                }
                                else if(attacker_int==king_int){
                                    // they are on the same rank
                                    if(attacker_letter>king_letter){
                                        // attacker on right
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }

                                        }

                                    }
                                    else{   // attacker on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }

                                        }
                                    }
                                }
                                else{   // here are the moves for bishop

                                    if(attacker_letter>king_letter){
                                        // attacker is on right

                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }                                                
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }

                                            }

                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);
                                }

                            }
                        }
                    }
                    


                }
                tempmoves.clear();
                tempmoves.addAll(block);

            }
            else if(queen.getIsPinned()){  //this is if queen is pinned
                String attacker_square[]=queen.getPinnedBy().split("");
                String piece_square[]=queen.getSquare().split("");
                String eachmove="";
                int attacker_letter=0;
                int piece_letter=0;
                int attacker_int=0;
                int piece_int=0;
                for(int i=0;i<8;i++){
                    if(col[i].equals(attacker_square[0])){
                        attacker_letter=i;
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(col[i].equals(piece_square[0])){
                        piece_letter=i;
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(attacker_square[1])){
                        attacker_int=i;
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(piece_square[1])){
                        piece_int=i;
                        break;
                    }
                }


                for(Piece p:game.getBlackPieces()){
                    if(p.getSquare().equals(queen.getPinnedBy())){
                        if(p instanceof Rook || p instanceof Queen){
                            
                            if(piece_letter==attacker_letter){
                                
                                // they are on the same file
                                if(piece_int>attacker_int){
                                    // rook on top
                                    for(int i=attacker_int;i<piece_int;i++){
                                        eachmove=col[attacker_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_int+1;i<=7;i++){
                                        eachmove=col[attacker_letter]+row[i];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                                else if(piece_int<attacker_int){
                                    // rook on bottom
                                    for(int i=piece_int+1;i<=attacker_int;i++){
                                        eachmove=col[attacker_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_int-1;i>=0;i--){
                                        eachmove=col[attacker_letter]+row[i];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                            }
                            else if(piece_int==attacker_int){
                                // they are on the same rank
                                if(piece_letter>attacker_letter){
                                    // rook on right
                                    for(int i=attacker_letter;i<piece_letter;i++){
                                        eachmove=col[i]+row[attacker_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_letter+1;i<=7;i++){
                                        eachmove=col[i]+row[piece_int];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                                else if(piece_letter<attacker_letter){
                                    // rook on left
                                    for(int i=piece_letter+1;i<=attacker_letter;i++){
                                        eachmove=col[i]+row[attacker_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_letter-1;i>=0;i--){
                                        eachmove=col[i]+row[piece_int];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                            }
                            // tempmoves.addAll(block);
                            // block.clear();
                        }
                        if(p instanceof Bishop|| p instanceof Queen){
                            if(attacker_letter>piece_letter){   // this serves both queen and bishop
                                // attacker on right
                                if(attacker_int>piece_int){
                                    // attacker on top
                                    int int_inc=piece_int+1;
                                    for(int i=piece_letter+1;i<=attacker_letter;i++){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc++;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    }
                                    // now adding backward moves
                                    int int_dec=piece_int-1;
                                    for(int i=piece_letter-1;i>=0;i--){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec--;
                                        }
                                    }
                                }
                                else if(attacker_int<piece_int){
                                    // attacker on bottom
                                    int int_inc=piece_int-1;
                                    for(int i=piece_letter+1;i<=attacker_letter;i++){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc--;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    }
                                    // now adding backward moves
                                    int int_dec=piece_int+1;
                                    for(int i=piece_letter-1;i>=0;i--){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec++;
                                        }
                                    }
                                }
                            }
                            else if(attacker_letter<piece_letter){  // this serves both queen and bishop
                                // attacker on left
                                if(attacker_int>piece_int){
                                    // attacker on top
                                    int int_inc=piece_int+1;
                                    for(int i=piece_letter-1;i>=attacker_letter;i--){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc++;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
        
                                    }

                                    // now adding backward moves
                                    int int_dec=piece_int-1;
                                    for(int i=piece_letter+1;i<=7;i++){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec--;
                                        }
                                    }
                                }
                                else if(attacker_int<piece_int){
                                    // attacker on bottom
                                    int int_inc=piece_int-1;
                                    for(int i=piece_letter-1;i>=attacker_letter;i--){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc--;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    }
                                    // now adding backward moves
                                    int int_dec=piece_int+1;
                                    for(int i=piece_letter+1;i<=7;i++){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec++;
                                        }
                                    }
        
                                }
                            }
                            // tempmoves.addAll(block);
                            // block.clear();
                            
                        }
                    }
                }

                tempmoves.clear();
                tempmoves.addAll(block);


            }
            

            else{      //this is for normal queen

                // System.out.println("i added all moves to queen");
                // moves.addAll(tempmoves);
                // System.out.println("the moves are:");
                // for(String shit:moves){
                //     System.out.println("move is: "+shit);
                // }
                // System.out.println("now printing tempmoves");
                // for(String shit:tempmoves){
                //     System.out.println("tempmove is: "+shit);
                // }
                // System.out.println("i am done for white queen now next is black queen");
                // System.out.println("");
                // System.out.println("");
                // System.out.println("");
                // return moves;

            }
            // moves.addAll(tempmoves);
            // return moves;    
        }

//----------------------------------------------------------------

        else{ // this is for black queen
            System.out.println("i am in black queen");
            for(ArrayList<String> movesList:allMoves){
                int flag_array=0; // this is for entire sublist
                
                for(String sq:movesList){
                    int flag_move=0; // this is for a move
                    
                    if(flag_array==0){
                        for(Piece p:game.getWhitePieces()){
                            if(p.getSquare().equals(sq)){
                                if(p instanceof King){
                                    flag_array=1;
                                    flag_move=1;
                                }
                                else{
                                    flag_array=1;
                                    flag_move=1;
                                    tempmoves.add(sq);
                                }
                                
                                break;
                            }
                        }
                        for(Piece p:game.getBlackPieces()){
                            if(p.getSquare().equals(sq)){
                                flag_array=1;
                                flag_move=1;
                                break;
                            }
                        }
                        if(flag_move==0){
                            tempmoves.add(sq);
                        }
                    }
                    else{
                        break;
                    }
    
                }
            }
//----------------------------------------------------------------
            String king_square[]=king.getSquare().split("");
            if(king.getUnderCheck()){ // this is if king in check
                if(king.getCheckByWhom().size()>1){
                    System.out.println("double check cant do anything");
                    moves.clear();
                    return moves;
                }
                for(Piece p:game.getWhitePieces()){
                    if(p.getSquare().equals(king.getCheckByWhom().get(0))){
                        String eachmove="";
                        String attacker_square[]=p.getSquare().split("");
                        int attacker_letter=0;
                        int king_letter=0;
                        int attacker_int=0;
                        int king_int=0;
                        for(int i=0;i<8;i++){
                            if(col[i].equals(attacker_square[0])){
                                attacker_letter=i;
                                break;
                             }
                         }
                         for(int i=0;i<8;i++){
                             if(col[i].equals(king_square[0])){
                                 king_letter=i;
                                 break;
                             }
                         }
                         for(int i=0;i<8;i++){
                             if(row[i].equals(attacker_square[1])){
                                 attacker_int=i;
                                 break;
                             }
                         }
                         for(int i=0;i<8;i++){
                             if(row[i].equals(king_square[1])){
                                 king_int=i;
                                 break;
                             }
                         }
                         if(queen.getIsPinned()){
                            //  tempmoves.clear();
                             break;
                         }
                         else{
                             if(p instanceof Pawn || p instanceof Knight){
                                 for(String s:tempmoves){
                                        if(s.equals(p.getSquare())){
                                            System.out.println("got this stupid pawn/knight at :"+s);
                                            block.add(s);
                                            // break;
                                        }
                                    }
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);
                                
                            }
                            else if(p instanceof Rook){
                                // either you attack or you block
                                // first for attack
                                for(String s:tempmoves){
                                    if(s.equals(p.getSquare())){
                                        block.add(s);
                                        // break;
                                    }
                                }
                            
                                // now for blocking
                                if(attacker_letter>king_letter){
                                    // attacker is on right                                        
                                    for(int i=king_letter+1;i<attacker_letter;i++){
                                        eachmove=col[i]+row[king_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    
                                    }
                                }
                                else if(attacker_int>king_int){
                                    // attacker is on top
                                    int int_inc=king_int+1;
                                    for(int i=int_inc;i<attacker_int;i++){
                                        eachmove=col[king_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    
                                    }
                                
                                }
                                else if(attacker_letter<king_letter){
                                    // attacker is on left
                                    for(int i=attacker_letter+1;i<king_letter;i++){
                                        eachmove=col[i]+row[king_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    
                                    }
                                
                                }
                                else if(attacker_int<king_int){
                                    int int_inc=attacker_int+1;
                                    for(int i=int_inc;i<king_int;i++){
                                        eachmove=col[king_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    
                                    }
                                }            
                                // tempmoves.clear();
                                // tempmoves.addAll(block);
                            
                            
                            }
                            else if(p instanceof Bishop){
                            
                                for(String s:tempmoves){
                                    if(s.equals(p.getSquare())){
                                        block.add(s);
                                        // break;
                                    }
                                }
                                if(attacker_letter>king_letter){
                                    // attacker is on right
                                
                                    if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=king_int+1;
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        
                                        }
                                    
                                    }
                                    else{
                                        // attacker is on bottom
                                        int int_inc=king_int-1;
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        
                                        }
                                    
                                    }
                                }
                                else{
                                    // attacker is on left
                                    if(attacker_int>king_int){
                                        // attacker is on top
                                        int int_inc=attacker_int-1;
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc--;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        // attacker is on bottom
                                        int int_inc=attacker_int+1;
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[int_inc];
                                            int_inc++;
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        }
                                    }
                                }    
                                // tempmoves.clear();
                                // tempmoves.addAll(block);
                            
                            }
                            else if(p instanceof Queen){
                                for(String s:tempmoves){
                                    if(s.equals(p.getSquare())){
                                    
                                        block.add(s);
                                        // break;
                                    }
                                }
                            
                                // first moves like rook
                                if(attacker_letter==king_letter){
                                    // they are on the same file
                                    if(attacker_int>king_int){ // attacker on top
                                        int int_inc=king_int+1;
                                        for(int i=int_inc;i<attacker_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        
                                        }
                                    }
                                    else{   // attacker on bottom
                                        int int_inc=attacker_int+1;
                                        for(int i=int_inc;i<king_int;i++){
                                            eachmove=col[king_letter]+row[i];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        
                                        }
                                    }
                                }
                                else if(attacker_int==king_int){
                                    // they are on the same rank
                                    if(attacker_letter>king_letter){
                                        // attacker on right
                                        for(int i=king_letter+1;i<attacker_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        
                                        }
                                    
                                    }
                                    else{   // attacker on left
                                        for(int i=attacker_letter+1;i<king_letter;i++){
                                            eachmove=col[i]+row[king_int];
                                            for(String s:tempmoves){
                                                if(s.equals(eachmove)){
                                                    block.add(eachmove);
                                                }
                                            }
                                        
                                        }
                                    }
                                }
                                else{   // here are the moves for bishop
                                
                                    if(attacker_letter>king_letter){
                                        // attacker is on right
                                    
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=king_int+1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            
                                            }                                                
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=king_int-1;
                                            for(int i=king_letter+1;i<attacker_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            
                                            }
                                        
                                        }
                                    }
                                    else{
                                        // attacker is on left
                                        if(attacker_int>king_int){
                                            // attacker is on top
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc--;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            // attacker is on bottom
                                            int int_inc=attacker_int+1;
                                            for(int i=attacker_letter+1;i<king_letter;i++){
                                                eachmove=col[i]+row[int_inc];
                                                int_inc++;
                                                for(String s:tempmoves){
                                                    if(s.equals(eachmove)){
                                                        block.add(eachmove);
                                                    }
                                                }
                                            }
                                        }
                                    }    
                                    // tempmoves.clear();
                                    // tempmoves.addAll(block);
                                }
                            
                            }
                        }
                    }

                
                
                }
                tempmoves.clear();
                tempmoves.addAll(block);
            
            }
            else if(queen.getIsPinned()){  //this is if queen is pinned
                String attacker_square[]=queen.getPinnedBy().split("");
                String piece_square[]=queen.getSquare().split("");
                String eachmove="";
                int attacker_letter=0;
                int piece_letter=0;
                int attacker_int=0;
                int piece_int=0;
                for(int i=0;i<8;i++){
                    if(col[i].equals(attacker_square[0])){
                        attacker_letter=i;
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(col[i].equals(piece_square[0])){
                        piece_letter=i;
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(attacker_square[1])){
                        attacker_int=i;
                        break;
                    }
                }
                for(int i=0;i<8;i++){
                    if(row[i].equals(piece_square[1])){
                        piece_int=i;
                        break;
                    }
                }
            
            
                for(Piece p:game.getWhitePieces()){
                    if(p.getSquare().equals(queen.getPinnedBy())){
                        if(p instanceof Rook || p instanceof Queen){

                            if(piece_letter==attacker_letter){

                                // they are on the same file
                                if(piece_int>attacker_int){
                                    // rook on top
                                    for(int i=attacker_int;i<piece_int;i++){
                                        eachmove=col[attacker_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_int+1;i<=7;i++){
                                        eachmove=col[attacker_letter]+row[i];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                                else if(piece_int<attacker_int){
                                    // rook on bottom
                                    for(int i=piece_int+1;i<=attacker_int;i++){
                                        eachmove=col[attacker_letter]+row[i];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_int-1;i>=0;i--){
                                        eachmove=col[attacker_letter]+row[i];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                            }
                            else if(piece_int==attacker_int){
                                // they are on the same rank
                                if(piece_letter>attacker_letter){
                                    // rook on right
                                    for(int i=attacker_letter;i<piece_letter;i++){
                                        eachmove=col[i]+row[attacker_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_letter+1;i<=7;i++){
                                        eachmove=col[i]+row[piece_int];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                                else if(piece_letter<attacker_letter){
                                    // rook on left
                                    for(int i=piece_letter+1;i<=attacker_letter;i++){
                                        eachmove=col[i]+row[attacker_int];
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                            }
                                        }
                                    }
                                    // now moves between king and rook
                                    for(int i=piece_letter-1;i>=0;i--){
                                        eachmove=col[i]+row[piece_int];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                        }
                                    }
                                }
                            }
                            // tempmoves.addAll(block);
                            // block.clear();
                        }
                        if(p instanceof Bishop|| p instanceof Queen){
                            if(attacker_letter>piece_letter){   // this serves both queen and bishop
                                // attacker on right
                                if(attacker_int>piece_int){
                                    // attacker on top
                                    int int_inc=piece_int+1;
                                    for(int i=piece_letter+1;i<=attacker_letter;i++){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc++;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    }

                                    // now adding backward moves
                                    int int_dec=piece_int-1;
                                    for(int i=piece_letter-1;i>=0;i--){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec--;
                                        }
                                    }
                                }
                                else if(attacker_int<piece_int){
                                    // attacker on bottom
                                    int int_inc=piece_int-1;
                                    for(int i=piece_letter+1;i<=attacker_letter;i++){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc--;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    }

                                    // now adding backward moves
                                    int int_dec=piece_int+1;
                                    for(int i=piece_letter-1;i>=0;i--){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec++;
                                        }
                                    }
                                }
                            }
                            else if(attacker_letter<piece_letter){  // this serves both queen and bishop
                                // attacker on left
                                if(attacker_int>piece_int){
                                    // attacker on top
                                    int int_inc=piece_int+1;
                                    for(int i=piece_letter-1;i>=attacker_letter;i--){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc++;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    
                                    }


                                    // now adding backward moves
                                    int int_dec=piece_int-1;
                                    for(int i=piece_letter+1;i<=7;i++){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec--;
                                        }
                                    }

                                }
                                else if(attacker_int<piece_int){
                                    // attacker on bottom
                                    int int_inc=piece_int-1;
                                    for(int i=piece_letter-1;i>=attacker_letter;i--){
                                        eachmove=col[i]+row[int_inc];
                                        int_inc--;
                                        for(String s:tempmoves){
                                            if(s.equals(eachmove)){
                                                block.add(eachmove);
                                                break;
                                            }
                                        }
                                    }

                                    // now adding backward moves
                                    int int_dec=piece_int+1;
                                    for(int i=piece_letter+1;i<=7;i++){
                                        eachmove=col[i]+row[int_dec];
                                        if(eachmove.equals(king.getSquare())){
                                            break;
                                        }
                                        else{
                                            block.add(eachmove);
                                            int_dec++;
                                        }
                                    }
                                
                                }
                            }
                            // tempmoves.addAll(block);
                            // block.clear();

                        }
                    }
                }
                tempmoves.clear();
                tempmoves.addAll(block);
            
            
            
            
            }


            else{      //this is for normal queen
                // moves.addAll(tempmoves);
                // return moves;
            
            }
            

        }
         
        System.out.println("i reached here");
        System.out.println("i will printf moves");
        for(String shit:moves){
            System.out.println("move possible is :"+shit);
        }
        System.out.println("i will print tempmoves");
        for(String shit:tempmoves){
            System.out.println("move possible is :"+shit);
        } 
        System.out.println("returning to main function");
        
        moves.addAll(tempmoves);
        return moves;
    }
    public ArrayList<String> KingMoves(King king){
        System.out.println("i am in king moves");
        ArrayList<String> moves=new ArrayList<String>();
        ArrayList<String> tempmoves=new ArrayList<String>();
        ArrayList<String> possibleMove= new ArrayList<String>();
        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] squares=king.getSquare().split("");
        int pos=Integer.parseInt(squares[1]);
        int file=0;
        for(int i=0;i<8;i++){
            if(col[i].equals(squares[0])){
                file=i;
                break;
            }
        }


        int shortCastle=0;
        int longCastle=0;
        ArrayList<String> shortCastleMoves=new ArrayList<String>();
        ArrayList<String> longCastleMoves=new ArrayList<String>();

        // now i write all moves
        // starting from 12:00 clockwise
        // North
        if(file<=7 && pos+1<=8){
            tempmoves.add(col[file]+(pos+1));
        }
        // North East
        if(file+1<=7 && pos+1<=8){
            tempmoves.add(col[file+1]+(pos+1));
        }
        // East
        if(file+1<=7 && pos<=8){
            tempmoves.add(col[file+1]+(pos));
        }
        // South East
        if(file+1<=7 && pos-1>=1){
            tempmoves.add(col[file+1]+(pos-1));
        }
        // South
        if(file<=7 && pos-1>=1){
            tempmoves.add(col[file]+(pos-1));
        }
        // South West
        if(file-1>=0 && pos-1>=1){
            tempmoves.add(col[file-1]+(pos-1));
        }
        // West
        if(file-1>=0 && pos<=8){
            tempmoves.add(col[file-1]+(pos));
        }
        // North West
        if(file-1>=0 && pos+1<=8){
            tempmoves.add(col[file-1]+(pos+1));
        }
        King whiteKing=null;
        King blackKing=null;
        for(Piece p:game.getWhitePieces()){
            if(p instanceof King){
                whiteKing=(King)p;
            }
        }
        for(Piece p:game.getBlackPieces()){
            if(p instanceof King){
                blackKing=(King)p;
            }
        }

        // now we start eliminating as per condition
        // here i am not writing against opp king moves
        if(king.getColor().equals("white")){
            // now i have to see that it doesnt collide with same color piece
            // i am adding the castling moves
            if(king.getFirstMove().equals("no") && !(king.getUnderCheck())){
                shortCastleMoves.add("g1");
                shortCastleMoves.add("f1");
                longCastleMoves.add("c1");
                longCastleMoves.add("d1");
                longCastleMoves.add("b1");

                tempmoves.add("g1");

                tempmoves.add("c1");
                // longCastleMoves.add("c1");
            }
            

            

            for(String m: tempmoves){
                int flag=0;
                for(Piece p:game.getWhitePieces()){
                    if(p.getSquare().equals(m)){
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    possibleMove.add(m);
                }
            }
            tempmoves.clear();
            // possibleMove.addAll(shortCastleMoves);
            // possibleMove.addAll(longCastleMoves);
            for(String m:possibleMove){
                int flag=0;
                for(Piece p:game.getBlackPieces()){
                    if(p instanceof Pawn && flag==0) {
                        String[] oppPawnsquares=p.getSquare().split("");
                        pos=Integer.parseInt(oppPawnsquares[1]);
                        file=0;
                        for(int i=0;i<8;i++){
                            if(col[i].equals(oppPawnsquares[0])){
                                file=i;
                                break;
                            }
                        }
                        String eachmove="";
                        if(file-1>=0 && pos-1>=1){
                            eachmove=col[file-1]+(pos-1);
                            if(eachmove.equals(m)){
                                flag=1;
                                break;
                            }
                        }
                        // North East
                        if(file+1<=7 && pos-1>=1){
                            eachmove=col[file+1]+(pos-1);
                            if(eachmove.equals(m)){
                                flag=1;
                                break;
                            }
                            
                        }
                        
                    }
                    if(p instanceof King && flag==0) {
                        String[] oppKingsquares=p.getSquare().split("");
                        pos=Integer.parseInt(oppKingsquares[1]);
                        file=0;
                        ArrayList<String> oppKingtempmoves=new ArrayList<String>();

                        for(int i=0;i<8;i++){
                            if(col[i].equals(oppKingsquares[0])){
                                file=i;
                                break;
                            }
                        }
                        // starting from 12:00 clockwise
                        // North
                        if(file<=7 && pos+1<=8){
                            oppKingtempmoves.add(col[file]+(pos+1));
                        }
                        // North East
                        if(file+1<=7 && pos+1<=8){
                            oppKingtempmoves.add(col[file+1]+(pos+1));
                        }
                        // East
                        if(file+1<=7 && pos<=8){
                            oppKingtempmoves.add(col[file+1]+(pos));
                        }
                        // South East
                        if(file+1<=7 && pos-1>=1){
                            oppKingtempmoves.add(col[file+1]+(pos-1));
                        }
                        // South
                        if(file<=7 && pos-1>=1){
                            oppKingtempmoves.add(col[file]+(pos-1));
                        }
                        // South West
                        if(file-1>=0 && pos-1>=1){
                            oppKingtempmoves.add(col[file-1]+(pos-1));
                        }
                        // West
                        if(file-1>=0 && pos<=8){
                            oppKingtempmoves.add(col[file-1]+(pos));
                        }
                        // North West
                        if(file-1>=0 && pos+1<=8){
                            oppKingtempmoves.add(col[file-1]+(pos+1));
                        }
                        for(String s:oppKingtempmoves){
                            if(s.equals(m)){
                                flag=1;
                                break;
                            }
                        }
                    }
                    else if(flag==0) {
                        ArrayList<String> temp=restrictedMoves(p,"black");
                        for(String s:temp){
                            if(s.equals(m)){
                                flag=1;
                                break;
                            }
                        }
                    }
                    
                }
                
                if(flag==0){
                    tempmoves.add(m);
                }
            }
            

        }
        else{// black king
            // now i have to see that it doesnt collide with same color piece
            // i am adding the castling moves
            if(king.getFirstMove().equals("no") && !(king.getUnderCheck())){
                shortCastleMoves.add("g8");
                shortCastleMoves.add("f8");
                longCastleMoves.add("c8");
                longCastleMoves.add("d8");
                longCastleMoves.add("b8");
                tempmoves.add("g8");

                tempmoves.add("c8");
                // longCastleMoves.add("c1");
            }
            for(String m: tempmoves){
                int flag=0;
                for(Piece p:game.getBlackPieces()){
                    if(p.getSquare().equals(m)){
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    possibleMove.add(m);
                }
            }
            tempmoves.clear();
            // possibleMove.addAll(shortCastleMoves);
            // possibleMove.addAll(longCastleMoves);
            for(String m:possibleMove){
                int flag=0;
                for(Piece p:game.getWhitePieces()){
                    if(p instanceof Pawn && flag==0) {
                        String[] oppPawnsquares=p.getSquare().split("");
                        pos=Integer.parseInt(oppPawnsquares[1]);
                        file=0;
                        for(int i=0;i<8;i++){
                            if(col[i].equals(oppPawnsquares[0])){
                                file=i;
                                break;
                            }
                        }
                        String eachmove="";
                        if(file-1>=0 && pos+1<=8){
                            eachmove=col[file-1]+(pos+1);
                            if(eachmove.equals(m)){
                                flag=1;
                                break;
                            }
                        }
                        // North East
                        if(file+1<=7 && pos+1<=8){
                            eachmove=col[file+1]+(pos+1);
                            if(eachmove.equals(m)){
                                flag=1;
                                break;
                            }
                            
                        }
                    }
                    if(p instanceof King && flag==0) {
                        String[] oppKingsquares=p.getSquare().split("");
                        pos=Integer.parseInt(oppKingsquares[1]);
                        file=0;
                        ArrayList<String> oppKingtempmoves=new ArrayList<String>();

                        for(int i=0;i<8;i++){
                            if(col[i].equals(oppKingsquares[0])){
                                file=i;
                                break;
                            }
                        }
                        // starting from 12:00 clockwise
                        // North
                        if(file<=7 && pos+1<=8){
                            oppKingtempmoves.add(col[file]+(pos+1));
                        }
                        // North East
                        if(file+1<=7 && pos+1<=8){
                            oppKingtempmoves.add(col[file+1]+(pos+1));
                        }
                        // East
                        if(file+1<=7 && pos<=8){
                            oppKingtempmoves.add(col[file+1]+(pos));
                        }
                        // South East
                        if(file+1<=7 && pos-1>=1){
                            oppKingtempmoves.add(col[file+1]+(pos-1));
                        }
                        // South
                        if(file<=7 && pos-1>=1){
                            oppKingtempmoves.add(col[file]+(pos-1));
                        }
                        // South West
                        if(file-1>=0 && pos-1>=1){
                            oppKingtempmoves.add(col[file-1]+(pos-1));
                        }
                        // West
                        if(file-1>=0 && pos<=8){
                            oppKingtempmoves.add(col[file-1]+(pos));
                        }
                        // North West
                        if(file-1>=0 && pos+1<=8){
                            oppKingtempmoves.add(col[file-1]+(pos+1));
                        }
                        for(String s:oppKingtempmoves){
                            if(s.equals(m)){
                                flag=1;
                                break;
                            }
                        }
                    }
                    else if(flag==0) {
                        ArrayList<String> temp=restrictedMoves(p,"white");
                        for(String s:temp){
                            if(s.equals(m)){
                                flag=1;
                                break;
                            }
                        }
                    }
                }
                
                if(flag==0){
                    tempmoves.add(m);
                }
            }
           
        }

        // after coming here i know for all moves if they are valid or not
        // only thing to check is for castling moves
        
        // tempmoves.clear();
        ArrayList<String> castleMoves=new ArrayList<>();
        if(king.getColor().equals("white")){
            castleMoves.add("g1");
            castleMoves.add("c1");
            if(king.getFirstMove().equals("no") && !(king.getUnderCheck()) ){
                System.out.println("i am not in check... i will castle");
                // it means it might castle
                for(String s:castleMoves){
                    if(s.equals("g1")){
                        // castleMoves.addAll(shortCastleMoves);
                        int flag_rook_found=0;
                        for(Piece p:game.getWhitePieces()){
                            if(p instanceof Rook && p.getSquare().equals("h1") && ((Rook)p).getFirstMove().equals("no")){
                                // i can castle if path is clear and if all are available in tempmoves
                                // first lets see clearity of path
                                flag_rook_found=1;
                                for(String m:shortCastleMoves){
                                    for(Piece wp:game.getWhitePieces()){
                                        if(wp.getSquare().equals(m)){
                                            shortCastle=1;
                                            break;
                                        }
                                    }
                                    for(Piece bp:game.getBlackPieces()){
                                        if(bp.getSquare().equals(m)){
                                            shortCastle=1;
                                            break;
                                        }
                                    }
                                }
                                if(shortCastle==0){
                                    // the squares are empty
                                    // now see if they all exist in castleMoves
                                    for(String m:shortCastleMoves){
                                        for(String n:tempmoves){
                                            if(m.equals(n)){
                                                shortCastle=1;
                                                break;
                                            }
                                            else{
                                                shortCastle=0;
                                            }
                                        }
                                    }
                                    if(shortCastle==1){
                                        // i can castle
                                        
                                    }
                                    else{
                                        // i cant castle
                                        tempmoves.remove("g1");
                                    }
                                    
                                }
                                else{
                                    // remove short castle moves
                                    tempmoves.remove("g1");
                                    
                                    
                                }
                            }
                        }
                        if(flag_rook_found==0){
                            // remove short castle moves
                            tempmoves.remove("g1");
                        }
                    }
                    if(s.equals("c1")){
                        int flag_rook_found=0;
                        for(Piece p:game.getWhitePieces()){
                            if(p instanceof Rook && p.getSquare().equals("a1") && ((Rook)p).getFirstMove().equals("no")){
                                // i can castle if path is clear and if all are available in tempmoves
                                // first lets see clearity of path
                                flag_rook_found=1;
                                for(String m:longCastleMoves){
                                    for(Piece wp:game.getWhitePieces()){
                                        if(wp.getSquare().equals(m)){
                                            longCastle=1;
                                            break;
                                        }
                                    }
                                    for(Piece bp:game.getBlackPieces()){
                                        if(bp.getSquare().equals(m)){
                                            longCastle=1;
                                            break;
                                        }
                                    }
                                }
                                if(longCastle==0){
                                    // the squares are empty
                                    // now see if they all exist in castleMoves
                                    longCastleMoves.remove("b1");
                                    for(String m:longCastleMoves){
                                        for(String n:tempmoves){
                                            if(m.equals(n)){
                                                longCastle=1;
                                                break;
                                            }
                                            else{
                                                longCastle=0;
                                            }
                                        }
                                    }
                                    if(longCastle==1){
                                        // i can castle
                                        
                                    }
                                    else{
                                        // i cant castle
                                        tempmoves.remove("c1");
                                    }
                                    
                                    
                                }
                                else{
                                    // remove short castle moves
                                    tempmoves.remove("c1");
                                
                                }
                            }
                        }
                        if(flag_rook_found==0){
                            // remove short castle moves
                            tempmoves.remove("c1");
                        }
                    }
                }
            }
        }
        else{
            castleMoves.add("g8");
            castleMoves.add("c8");
            if(king.getFirstMove().equals("no") && !(king.getUnderCheck()) ){
                // it means it might castle
                for(String s:castleMoves){
                    if(s.equals("g8")){
                        int flag_rook_found=0;
                        for(Piece p:game.getBlackPieces()){
                            if(p instanceof Rook && p.getSquare().equals("h8") && ((Rook)p).getFirstMove().equals("no")){
                                // i can castle if path is clear and if all are available in tempmoves
                                // first lets see clearity of path
                                flag_rook_found=1;
                                for(String m:shortCastleMoves){
                                    for(Piece wp:game.getWhitePieces()){
                                        if(wp.getSquare().equals(m)){
                                            shortCastle=1;
                                            break;
                                        }
                                    }
                                    for(Piece bp:game.getBlackPieces()){
                                        if(bp.getSquare().equals(m)){
                                            shortCastle=1;
                                            break;
                                        }
                                    }
                                }
                                if(shortCastle==0){
                                    // the squares are empty
                                    // now see if they all exist in castleMoves
                                    for(String m:shortCastleMoves){
                                        for(String n:tempmoves){
                                            if(m.equals(n)){
                                                shortCastle=1;
                                                break;
                                            }
                                            else{
                                                shortCastle=0;
                                            }
                                        }
                                    }
                                    if(shortCastle==1){
                                        // i can castle
                                        
                                    }
                                    else{
                                        // i cant castle
                                        tempmoves.remove("g8");
                                    }
                                    
                                }
                                else{
                                    // remove short castle moves
                                    tempmoves.remove("g8");
                                    
                                    
                                }
                            }
                        }
                        if(flag_rook_found==0){
                            // rook not found... removing castling move
                            tempmoves.remove("g8");
                        }
                    }
                    if(s.equals("c8")){
                        int flag_rook_found=0;
                        for(Piece p:game.getBlackPieces()){
                            if(p instanceof Rook && p.getSquare().equals("a8") && ((Rook)p).getFirstMove().equals("no")){
                                // i can castle if path is clear and if all are available in tempmoves
                                // first lets see clearity of path
                                flag_rook_found=1;
                                for(String m:longCastleMoves){
                                    for(Piece wp:game.getWhitePieces()){
                                        if(wp.getSquare().equals(m)){
                                            longCastle=1;
                                            break;
                                        }
                                    }
                                    for(Piece bp:game.getBlackPieces()){
                                        if(bp.getSquare().equals(m)){
                                            longCastle=1;
                                            break;
                                        }
                                    }
                                }
                                if(longCastle==0){
                                    // the squares are empty
                                    // now see if they all exist in castleMoves
                                    longCastleMoves.remove("b8");
                                    for(String m:longCastleMoves){
                                        for(String n:tempmoves){
                                            if(m.equals(n)){
                                                longCastle=1;
                                                break;
                                            }
                                            else{
                                                longCastle=0;
                                            }
                                        }
                                    }
                                    if(longCastle==1){
                                        // i can castle
                                        
                                    }
                                    else{
                                        // i cant castle
                                        tempmoves.remove("c8");
                                    }
                                    
                                    
                                }
                                else{
                                    // remove short castle moves
                                    tempmoves.remove("c8");
                                
                                }
                            }
                        }
                        if(flag_rook_found==0){
                            // remove short castle moves
                            tempmoves.remove("c8");
                        }
                    }
                }
            }

        }

        
        
        moves.addAll(tempmoves);
        return moves;
    }


    

    private String getPieceSymbol(Piece piece) {
        if (piece instanceof Pawn) return piece.getColor().equals("white") ? "♙" : "♟";
        if (piece instanceof Rook) return piece.getColor().equals("white") ? "♖" : "♜";
        if (piece instanceof Knight) return piece.getColor().equals("white") ? "♘" : "♞";
        if (piece instanceof Bishop) return piece.getColor().equals("white") ? "♗" : "♝";
        if (piece instanceof Queen) return piece.getColor().equals("white") ? "♕" : "♛";
        if (piece instanceof King) return piece.getColor().equals("white") ? "♔" : "♚";
        return "";
    }


    public ArrayList<String> restrictedMoves(Piece piece,String color){
        ArrayList<String> moves=new ArrayList<>();
        ArrayList<String> tempmoves=new ArrayList<>();
        ArrayList<String> directionalmoves=new ArrayList<String>();
        ArrayList<ArrayList<String>> allMoves=new ArrayList<ArrayList<String>>();

        String[] col = {"a", "b", "c", "d","e", "f", "g", "h"};
        String[] row = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] squares=piece.getSquare().split("");
        int pos=Integer.parseInt(squares[1]);
        int file=0;
        for(int i=0;i<8;i++){
            if(col[i].equals(squares[0])){
                file=i;
                break;
            }
        }
        if(piece instanceof Knight){
            if(file-1>=0 && pos-2>=1){
                directionalmoves.add(col[file-1]+(pos-2));
            }
            if(file+1<=7 && pos-2>=1){
                directionalmoves.add(col[file+1]+(pos-2));
            }
            if(file-2>=0 && pos-1>=1){
                directionalmoves.add(col[file-2]+(pos-1));
            }
            if(file+2<=7 && pos-1>=1){
                directionalmoves.add(col[file+2]+(pos-1));
            }
            if(file-1>=0 && pos+2<=8){
                directionalmoves.add(col[file-1]+(pos+2));
            }
            if(file+1<=7 && pos+2<=8){
                directionalmoves.add(col[file+1]+(pos+2));
            }
            if(file-2>=0 && pos+1<=8){
                directionalmoves.add(col[file-2]+(pos+1));
            }
            if(file+2<=7 && pos+1<=8){
                directionalmoves.add(col[file+2]+(pos+1));
            }

            tempmoves.addAll(directionalmoves);

        }
        else if(piece instanceof Bishop ||piece instanceof Rook ||piece instanceof Queen){

            int j=pos+1;
            for(int i=file-1;i>=0;i--){
                if(j<=8){
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                    j++;
                }
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            j=pos+1;
            // now for N-E
            for(int i=file+1;i<8;i++){
                if(j<=8){
                    String sqAhead=col[i]+j;
                    // System.out.println("the square is "+sqAhead);
                    directionalmoves.add(sqAhead);
                    j++;
                }
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            j=pos-1;
            // now for S-E
            for(int i=file+1;i<8;i++){
                if(j>0){
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                    j--;
                }
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            j=pos-1;
            // now for S-W
            for(int i=file-1;i>=0;i--){
                if(j>0){
                    String sqAhead=col[i]+j;
                    directionalmoves.add(sqAhead);
                    j--;
                }
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            //-------------------------------------------------------------------------------------------------
            // now rook moves to be added
            j=pos;
            for(int i=file-1;i>=0;i--){ 
                String sqAhead=col[i]+pos;
                directionalmoves.add(sqAhead);                            
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            j=pos;
            // now for N
            for(j=pos+1;j<=8;j++){
                int i=file;
                String sqAhead=col[i]+j;
                directionalmoves.add(sqAhead);            
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            j=pos;
            // now for E
            for(int i=file+1;i<8;i++){
                String sqAhead=col[i]+pos;
                directionalmoves.add(sqAhead);   
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();
            j=pos;
            // now for S
            for(j=pos-1;j>0;j--){
                int i=file;
                String sqAhead=col[i]+j;
                directionalmoves.add(sqAhead);
                // j--;
            }
            allMoves.add(new ArrayList<>(directionalmoves));
            directionalmoves.clear();

            if(piece instanceof Bishop || piece instanceof Queen){
                if(color.equals("white")){
                    for(int i=0;i<4;i++){
                        int flag_array=0; // this is for entire sublist
                        for(String sq:allMoves.get(i)){
                            // System.out.println("entered into allmoves array");
                            
                            
                            // System.out.println("checking for this square in loop "+sq);
                            // System.out.println("");
        
                            int flag_move=0; // this is for a move
                            
                            if(flag_array==0){
                                for(Piece p:game.getBlackPieces()){
                                    if(p.getSquare().equals(sq)){
                                        if(p instanceof King){
                                            flag_array=1;
                                            flag_move=1;
                                        }
                                        else{
                                            flag_array=1;
                                            flag_move=1;
                                            tempmoves.add(sq);
                                            // System.out.println("added to possible moves");
                                            // System.out.println("");
                                        }
                                        
                                        break;
                                    }
                                }
                                for(Piece p:game.getWhitePieces()){
                                    if(p.getSquare().equals(sq)){
                                        flag_array=1;
                                        flag_move=1;
                                        tempmoves.add(sq);
                                        break;
                                    }
                                }
                                if(flag_move==0){
                                    // System.out.println("added to possible moves");
                                    // System.out.println("");
                                    tempmoves.add(sq);
                                }
                            }
                            else{
                                // System.out.println("no more moves from this array");
                                // System.out.println("");
                                break;
                            }
                
                            
                        }
                    }


                }
                else{

                    for(int i=0;i<4;i++){
                        int flag_array=0; // this is for entire sublist
                        for(String sq:allMoves.get(i)){
                            // System.out.println("entered into allmoves array");
                            
                            
                            // System.out.println("checking for this square in loop "+sq);
                            // System.out.println("");
        
                            int flag_move=0; // this is for a move
                            
                            if(flag_array==0){
                                for(Piece p:game.getWhitePieces()){
                                    if(p.getSquare().equals(sq)){
                                        if(p instanceof King){
                                            flag_array=1;
                                            flag_move=1;
                                        }
                                        else{
                                            flag_array=1;
                                            flag_move=1;
                                            tempmoves.add(sq);
                                            // System.out.println("added to possible moves");
                                            // System.out.println("");
                                        }
                                        
                                        break;
                                    }
                                }
                                for(Piece p:game.getBlackPieces()){
                                    if(p.getSquare().equals(sq)){
                                        flag_array=1;
                                        flag_move=1;
                                        tempmoves.add(sq);
                                        break;
                                    }
                                }
                                if(flag_move==0){
                                    // System.out.println("added to possible moves");
                                    // System.out.println("");
                                    tempmoves.add(sq);
                                }
                            }
                            else{
                                // System.out.println("no more moves from this array");
                                // System.out.println("");
                                break;
                            }
                
                            
                        }
                    }

                }
            }

            if(piece instanceof Rook || piece instanceof Queen){
                if(color.equals("white")){
                    for(int i=4;i<8;i++){
                        int flag_array=0; // this is for entire sublist
                        for(String sq:allMoves.get(i)){
                            // System.out.println("entered into allmoves array");
                            
                            
                            // System.out.println("checking for this square in loop "+sq);
                            // System.out.println("");
        
                            int flag_move=0; // this is for a move
                            
                            if(flag_array==0){
                                for(Piece p:game.getBlackPieces()){
                                    if(p.getSquare().equals(sq)){
                                        if(p instanceof King){
                                            flag_array=1;
                                            flag_move=1;
                                        }
                                        else{
                                            flag_array=1;
                                            flag_move=1;
                                            tempmoves.add(sq);
                                            // System.out.println("added to possible moves");
                                            // System.out.println("");
                                        }
                                        
                                        break;
                                    }
                                }
                                for(Piece p:game.getWhitePieces()){
                                    if(p.getSquare().equals(sq)){
                                        flag_array=1;
                                        flag_move=1;
                                        tempmoves.add(sq);
                                        break;
                                    }
                                }
                                if(flag_move==0){
                                    // System.out.println("added to possible moves");
                                    // System.out.println("");
                                    tempmoves.add(sq);
                                }
                            }
                            else{
                                // System.out.println("no more moves from this array");
                                // System.out.println("");
                                break;
                            }
                
                            
                        }
                    }


                }
                else{

                    for(int i=4;i<8;i++){
                        int flag_array=0; // this is for entire sublist
                        for(String sq:allMoves.get(i)){
                            // System.out.println("entered into allmoves array");
                            
                            
                            // System.out.println("checking for this square in loop "+sq);
                            // System.out.println("");
        
                            int flag_move=0; // this is for a move
                            
                            if(flag_array==0){
                                for(Piece p:game.getWhitePieces()){
                                    if(p.getSquare().equals(sq)){
                                        if(p instanceof King){
                                            flag_array=1;
                                            flag_move=1;
                                        }
                                        else{
                                            flag_array=1;
                                            flag_move=1;
                                            tempmoves.add(sq);
                                            // System.out.println("added to possible moves");
                                            // System.out.println("");
                                        }
                                        
                                        break;
                                    }
                                }
                                for(Piece p:game.getBlackPieces()){
                                    if(p.getSquare().equals(sq)){
                                        flag_array=1;
                                        flag_move=1;
                                        tempmoves.add(sq);
                                        break;
                                    }
                                }
                                if(flag_move==0){
                                    // System.out.println("added to possible moves");
                                    // System.out.println("");
                                    tempmoves.add(sq);
                                }
                            }
                            else{
                                // System.out.println("no more moves from this array");
                                // System.out.println("");
                                break;
                            }
                
                            
                        }
                    }

                }
            }

        }

        moves.addAll(tempmoves);
        return moves;
    }




}
