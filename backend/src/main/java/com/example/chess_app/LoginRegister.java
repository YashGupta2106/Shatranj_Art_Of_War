// package com.chess.chess_app;

// import javafx.geometry.Insets;
// import javafx.geometry.Point2D;
// import javafx.geometry.Pos;
// import javafx.scene.Node;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.PasswordField;
// import javafx.scene.control.ProgressIndicator;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.*;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Circle;
// import javafx.stage.Modality;
// import javafx.stage.Stage;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.simp.stomp.StompFrameHandler;
// import org.springframework.messaging.simp.stomp.StompHeaders;
// import org.springframework.stereotype.Component;

// import com.chess.chess_app.MatchmakingService.MatchNotification;

// import java.util.ArrayList;
// import java.util.List;
// import javafx.animation.Animation;
// import javafx.animation.KeyFrame;
// import javafx.animation.Timeline;
// import javafx.application.Platform;
// import javafx.util.Duration;
// import javafx.scene.control.Label;
// import java.lang.reflect.Type;


// class TimerWrapper {
//     Timeline whiteTimer;
//     Timeline blackTimer;
//     int whiteTimeSeconds = 600; // Default 10 minutes
//     int blackTimeSeconds = 600; // Default 10 minutes
//     Label whiteTimerLabel;
//     Label blackTimerLabel;
// }



// @Component
// public class LoginRegister {
//     @Autowired
//     private PlayerRepository playerRepository;
//     @Autowired
//     private GameRepository gameRepository;
//     @Autowired
//     private MatchmakingService matchmakingService;
//     private StompClient stompClient;
//     private String lastSquareClicked="";
    
//     // Store references to pieces and their labels
//     private class PieceLabel {
//         Piece piece;
//         Label label;
//         PieceLabel(Piece piece, Label label) {
//             this.piece = piece;
//             this.label = label;
//         }
//     }
//     private List<PieceLabel> pieceLabelMap = new ArrayList<>();

//     public void start(Stage primaryStage) {
//         primaryStage.setTitle("Login/Register");

//         GridPane grid = new GridPane();
//         grid.setAlignment(Pos.CENTER);
//         grid.setHgap(10);
//         grid.setVgap(10);
//         grid.setPadding(new Insets(25, 25, 25, 25));

//         Label userNameLabel = new Label("Username:");
//         grid.add(userNameLabel, 0, 1);

//         TextField userNameTextField = new TextField();
//         grid.add(userNameTextField, 1, 1);

//         Label emailLabel = new Label("Email:");
//         grid.add(emailLabel, 0, 2);

//         TextField emailTextField = new TextField();
//         grid.add(emailTextField, 1, 2);

//         Label passwordLabel = new Label("Password:");
//         grid.add(passwordLabel, 0, 3);

//         PasswordField passwordField = new PasswordField();
//         grid.add(passwordField, 1, 3);

//         Button loginButton = new Button("Login");
//         grid.add(loginButton, 1, 4);

//         Button registerButton = new Button("Register");
//         grid.add(registerButton, 1, 5);

//         registerButton.setOnAction(e -> handleRegister(primaryStage, userNameTextField, emailTextField, passwordField));
//         loginButton.setOnAction(e -> handleLogin(primaryStage, userNameTextField, emailTextField, passwordField));

//         Scene scene = new Scene(grid, 300, 300);
//         primaryStage.setScene(scene);
//         primaryStage.show();
//     }

//     private void handleRegister(Stage primaryStage, TextField userNameTextField, TextField emailTextField, PasswordField passwordField) {
//         String username = userNameTextField.getText();
//         String email = emailTextField.getText();
//         String password = passwordField.getText();

//         Player existingPlayerByEmail = playerRepository.findByEmail(email);
//         if (existingPlayerByEmail != null) {
//             System.out.println("Email already exists.");
//             return;
//         }

//         Player newPlayer = new Player();
//         newPlayer.setUsername(username);
//         newPlayer.setEmail(email);
//         newPlayer.setPassword(password);

//         playerRepository.save(newPlayer);
//         System.out.println("Player registered successfully!");
//         navigateToHome(primaryStage, newPlayer);
//     }

//     private void handleLogin(Stage primaryStage, TextField userNameTextField, TextField emailTextField, PasswordField passwordField) {
//         String email = emailTextField.getText();
//         String password = passwordField.getText();

//         Player existingPlayerByEmail = playerRepository.findByEmail(email);
//         if (existingPlayerByEmail == null) {
//             System.out.println("Email does not exist.");
//             return;
//         }

//         if (!existingPlayerByEmail.getPassword().equals(password)) {
//             System.out.println("Incorrect password.");
//             return;
//         }

//         System.out.println("Player logged in successfully!");
//         navigateToHome(primaryStage, existingPlayerByEmail);
//     }

//     private void navigateToHome(Stage primaryStage, Player player) {
//         GridPane mainGameGrid = new GridPane();
//         primaryStage.setTitle("Home Page");
//         mainGameGrid.setAlignment(Pos.CENTER);
//         mainGameGrid.setHgap(10);
//         mainGameGrid.setVgap(20);
//         mainGameGrid.setPadding(new Insets(50, 50, 50, 50));

//         Label welcomeLabel = new Label("Welcome to the Chess Game!: " + player.getUsername());
//         welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
//         mainGameGrid.add(welcomeLabel, 0, 0);

//         Button practiceModeButton = new Button("Practice Mode");
//         practiceModeButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
//         practiceModeButton.setOnAction(e -> navigateToPracticeMode(primaryStage, player));
//         mainGameGrid.add(practiceModeButton, 0, 1);

//         Button playOnlineButton = new Button("Play Online");
//         playOnlineButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
//         playOnlineButton.setOnAction(e -> navigateToPlayOnline(primaryStage, player));
//         mainGameGrid.add(playOnlineButton, 0, 2);

//         Button playWithFriendsButton = new Button("Play with Friends");
//         playWithFriendsButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
//         mainGameGrid.add(playWithFriendsButton, 0, 3);

//         Button myGamesButton = new Button("My Games");
//         myGamesButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
//         mainGameGrid.add(myGamesButton, 0, 4);

//         mainGameGrid.setStyle("-fx-background-color: #f0f0f0;");

//         Scene mainGameScene = new Scene(mainGameGrid, 800, 600);
//         primaryStage.setScene(mainGameScene);
//     }


//     private void navigateToPlayOnline(Stage primaryStage,Player player){
//         // Create a waiting screen
//         BorderPane waitingPane = new BorderPane();
//         waitingPane.setPadding(new Insets(20));
        
//         VBox centerContent = new VBox(20);
//         centerContent.setAlignment(Pos.CENTER);
        
//         Label waitingLabel = new Label("Waiting for an opponent...");
//         waitingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
//         ProgressIndicator progressIndicator = new ProgressIndicator();
//         progressIndicator.setPrefSize(100, 100);
//         Button cancelButton = new Button("Cancel");
//         cancelButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");
//         centerContent.getChildren().addAll(waitingLabel, progressIndicator, cancelButton);
//         waitingPane.setCenter(centerContent);
        
//         Scene waitingScene = new Scene(waitingPane, 800, 600);
//         primaryStage.setScene(waitingScene);
//         // Add the player to the matchmaking queue
//         matchmakingService.findPlayerToPlay(player);
        
//         // Set up WebSocket connection to receive match notifications
//         // This would depend on your WebSocket client implementation
//         // For example, using SockJS and STOMP:

//         // Set up WebSocket connection to receive match notifications
//         StompClient stompClient = new StompClient();

//         stompClient.connect("ws://localhost:8080/ws", frame -> {
//             // Subscribe to user-specific destination
//             String destination = "/topic/" + player.getEmail();


//             stompClient.subscribe(destination, new StompFrameHandler() {
//                 @Override
//                 public Type getPayloadType(StompHeaders headers) {
//                     return MatchNotification.class; // Automatically maps JSON to Java object
//                 }
            
//                 @Override
//                 public void handleFrame(StompHeaders headers, Object payload) {
//                     if (payload instanceof MatchNotification) {
//                         MatchNotification response = (MatchNotification) payload;
                    
//                         // Extract details from received message
//                         String gameId = response.getGameId();
//                         String opponent = response.getOppName();
//                         String color = response.getColor();
//                         String gameChannel = "/topic/game/" + gameId;
                        
//                         stompClient.subscribe(gameChannel, new StompFrameHandler() {
//                             @Override
//                             public Type getPayloadType(StompHeaders headers) {
//                                 return String.class; // Or a GameMove class if you have one
//                             }

//                             @Override
//                             public void handleFrame(StompHeaders headers, Object payload) {
//                                 // Handle game moves or state updates
//                                 Platform.runLater(() -> {
//                                     // Update the game UI based on received move
//                                     // This will depend on how you structure your game move messages
//                                 });
//                             }
//                         });
                    
//                         // Use this data (e.g., start a new game)
//                         Platform.runLater(() -> {

//                             createOnlineGame(primaryStage, player, gameId, opponent, color);
//                         });
//                     }
//                 }
//             });


            
//         });

        
//         // Create a cancel button handler
//         cancelButton.setOnAction(e -> {
//             // Remove player from queue
//             matchmakingService.removePlayerFromQueue(player);
//             // Navigate back to home
//             navigateToHome(primaryStage, player);
//         });
//     }




//     private void createOnlineGame(Stage primaryStage,Player player,String gameId, String opponent, String color) {
//         // Navigate to the game screen
//         // first we will set the board and then we will set the pieces on it
//         final TimerWrapper timerWrapper = new TimerWrapper();
//         timerWrapper.whiteTimerLabel = new Label("White: 10:00");
//         timerWrapper.blackTimerLabel = new Label("Black: 10:00");
        
//         // Style the labels
//         String timerStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #f0f0f0; -fx-padding: 5px;";
//         timerWrapper.whiteTimerLabel.setStyle(timerStyle);
//         timerWrapper.blackTimerLabel.setStyle(timerStyle);
        
//         // Position the labels
//         timerWrapper.whiteTimerLabel.setTranslateX(650);
//         timerWrapper.whiteTimerLabel.setTranslateY(100);
//         timerWrapper.blackTimerLabel.setTranslateX(650);
//         timerWrapper.blackTimerLabel.setTranslateY(150);
//         StackPane root = new StackPane();
//         root.setAlignment(Pos.CENTER);
//         root.setPadding(new Insets(25));

//         // Three layers
//         GridPane squareLayer = new GridPane();
//         squareLayer.setAlignment(Pos.CENTER);
//         squareLayer.setHgap(0);
//         squareLayer.setVgap(0);

//         Pane pieceLayer = new Pane();
//         pieceLayer.setMinSize(8 * 80, 8 * 80);  // Same size as board
//         pieceLayer.setMaxSize(8 * 80, 8 * 80);
//         Pane highlightLayer = new Pane();
//         highlightLayer.setMinSize(8 * 80, 8 * 80);
//         highlightLayer.setMaxSize(8 * 80, 8 * 80);
//         squareLayer.setPickOnBounds(true);
//         pieceLayer.setMouseTransparent(true);
//         highlightLayer.setMouseTransparent(true);

//         String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
//         GameSquares[][] board = new GameSquares[8][8];
//         StackPane[][] squarePanes = new StackPane[8][8];
//         // Add layers to root
//         root.getChildren().addAll(squareLayer, pieceLayer, highlightLayer);
//         root.getChildren().addAll(timerWrapper.whiteTimerLabel, timerWrapper.blackTimerLabel);

//         if(color.equals("white")){
//             squareLayer.setScaleY(-1);
//         }

//         for (int row = 1; row <= 8; row++) {
//             for (int col = 0; col < 8; col++) {
//                 String squareId = columns[col] + row;
//                 GameSquares square = new GameSquares();
//                 square.setSquare(squareId);
//                 square.setOccupied("no");
//                 square.setSqColor((row + col) % 2 == 0 ? "lightgray" : "darkgreen");
//                 board[row - 1][col] = square;
            
//                 StackPane squarePane = createSquarePane(square, row, col);
//                 squarePanes[row-1][col] = squarePane;
//                 // squarePane.setOnMouseClicked(e -> {
//                 //     System.out.println("Square clicked: " + square.getSquare());
//                 // });
//                 squareLayer.add(squarePane, col , row);
//             }
//         }


//         Scene practiceModeScene = new Scene(root, 800, 800); // Adjusted scene size for larger squares
//         primaryStage.setScene(practiceModeScene);

//         // Initialize white timer
//         timerWrapper.whiteTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
//             timerWrapper.whiteTimeSeconds--;
//             updateTimerLabel(timerWrapper.whiteTimerLabel, "White", timerWrapper.whiteTimeSeconds);
//             if (timerWrapper.whiteTimeSeconds <= 0) {
//                 // Handle timer expiration
//                 timerWrapper.whiteTimer.stop();
//                 timerWrapper.blackTimer.stop();
//                 System.out.println("White's time expired! Black wins.");

//                 // // Update game state
//                 // gameHandler.getGameState().setGameActive(); // here false value comes
//                 // gameHandler.getGameState().setWinner("black");

//                 // // Save game and return to home
//                 // game.setWinner("Black");
//                 // gameRepository.save(game);
//                 navigateToHome(primaryStage, player);
//             }
//         }));
//         timerWrapper.whiteTimer.setCycleCount(Animation.INDEFINITE);

//         // Initialize black timer
//         timerWrapper.blackTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
//             timerWrapper.blackTimeSeconds--;
//             updateTimerLabel(timerWrapper.blackTimerLabel, "Black", timerWrapper.blackTimeSeconds);
//             if (timerWrapper.blackTimeSeconds <= 0) {
//                 // Handle timer expiration
//                 timerWrapper.whiteTimer.stop();
//                 timerWrapper.blackTimer.stop();
//                 System.out.println("Black's time expired! White wins.");

//                 // Update game state
//                 // gameHandler.getGameState().setGameActive();
//                 // gameHandler.getGameState().setWinner("white");

//                 // // Save game and return to home
//                 // game.setWinner("White");
//                 // gameRepository.save(game);
//                 navigateToHome(primaryStage, player);
//             }
//         }));
//         timerWrapper.blackTimer.setCycleCount(Animation.INDEFINITE);
        
//         timerWrapper.whiteTimer.play();


//         for (int i = 0; i < 8; i++) {
//             // System.out.println("inside click event handler outer loop");
//             for (int j = 0; j < 8; j++) {
//                 // System.out.println("inside click event handler inner loop");
//                 final int row = i;
//                 final int col = j;
//                 StackPane squarepane = squarePanes[i][j];
//                 squarePanes[i][j].setOnMouseClicked(e -> {
//                     System.out.println("-----------------------------------------------------------------------------------------------------------");
//                     System.out.println("-----------------------------------------------------------------------------------------------------------");
//                     System.out.println("");
//                     System.out.println("");
//                     System.out.println("Square clicked: " + board[row][col].getSquare());
//                     int tp=0;
//                     System.out.println("sending message to server");
//                     MoveMessage moveMessage = new MoveMessage();
//                     moveMessage.setGameId(gameId);
//                     moveMessage.setSquareClicked(board[row][col].getSquare());
//                     moveMessage.setColor(color);
//                     moveMessage.setWhiteTime(timerWrapper.whiteTimeSeconds);
//                     moveMessage.setBlackTime(timerWrapper.blackTimeSeconds);
//                     String hasCircle="no";
//                     boolean hasHighlight;
//                     if(color.equals("white")){
//                         hasHighlight = hasWhiteHighlightAt(board[row][col], highlightLayer);

//                     }
//                     else{
//                         hasHighlight=hasBlackHighlightAt(board[row][col],highlightLayer);
//                     }
//                     if(hasHighlight==true){
//                         moveMessage.setHasCircle("yes");
//                         hasCircle="yes";
//                     }
//                     else{
//                         moveMessage.setHasCircle("no");
//                     }
//                     // here i will first check if its a promotion and update 
//                     if(color.equals("white") && hasCircle.equals("yes")){
//                         String[] moveToMake=board[row][col].getSquare().split("");
//                         String lastPiece="no";
//                         if(moveToMake[1].equals("8")){
//                             // now search if last click was a pawn
//                             lastPiece=findPawn(lastSquareClicked,pieceLayer,"white");
//                             if(lastPiece.equals("pawn")){
//                                 // its promotion time
//                                 // here we create a modal to ask for what they want to promote to
//                                 String pieceToPromote=handlePawnPromotion(lastSquareClicked,pieceLayer);
//                                 moveMessage.setPromoteTo(pieceToPromote);
//                             }
//                         }
//                     }
//                     else if(color.equals("black") && hasCircle.equals("yes")){
//                         String[] moveToMake=board[row][col].getSquare().split("");
//                         String lastPiece="no";
//                         if(moveToMake[1].equals("1")){
//                             // now search if last click was a pawn
//                             lastPiece=findPawn(lastSquareClicked,pieceLayer,"black");
//                             if(lastPiece.equals("pawn")){
//                                 // its promotion time
//                                 // here we create a modal to ask for what they want to promote to
//                                 String pieceToPromote=handlePawnPromotion(lastSquareClicked,pieceLayer);
//                                 moveMessage.setPromoteTo(pieceToPromote);
//                             }
//                         }
//                     }


//                     stompClient.send("/app/game/" + gameId + "/move", moveMessage);
                    
//                     // gameHandler.handleSquareClick(board[row][col]);
//                 });
//             }
//         }
        
//     }



//     private void navigateToPracticeMode(Stage primaryStage,Player player) {


        
        
//         // Create a single instance of the wrapper
//         final TimerWrapper timerWrapper = new TimerWrapper();
    


//          // Root container
//         StackPane root = new StackPane();
//         root.setAlignment(Pos.CENTER);
//         root.setPadding(new Insets(25));

//         // Three layers
//         GridPane squareLayer = new GridPane();
//         squareLayer.setAlignment(Pos.CENTER);
//         squareLayer.setHgap(0);
//         squareLayer.setVgap(0);

//         Pane pieceLayer = new Pane();
//         pieceLayer.setMinSize(8 * 80, 8 * 80);  // Same size as board
//         pieceLayer.setMaxSize(8 * 80, 8 * 80);
//         Pane highlightLayer = new Pane();
//         highlightLayer.setMinSize(8 * 80, 8 * 80);
//         highlightLayer.setMaxSize(8 * 80, 8 * 80);
//         squareLayer.setPickOnBounds(true);
//         pieceLayer.setMouseTransparent(true);
//         highlightLayer.setMouseTransparent(true);

//         String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
//         GameSquares[][] board = new GameSquares[8][8];
//         StackPane[][] squarePanes = new StackPane[8][8];
//         // Add layers to root
//         root.getChildren().addAll(squareLayer, pieceLayer, highlightLayer);

//         squareLayer.setScaleY(-1);
//         pieceLayer.setScaleY(-1);

//         for (int row = 1; row <= 8; row++) {
//             for (int col = 0; col < 8; col++) {
//                 String squareId = columns[col] + row;
//                 GameSquares square = new GameSquares();
//                 square.setSquare(squareId);
//                 square.setOccupied("no");
//                 square.setSqColor((row + col) % 2 == 0 ? "lightgray" : "darkgreen");
//                 board[row - 1][col] = square;
            
//                 StackPane squarePane = createSquarePane(square, row, col);
//                 squarePanes[row-1][col] = squarePane;
//                 // squarePane.setOnMouseClicked(e -> {
//                 //     System.out.println("Square clicked: " + square.getSquare());
//                 // });
//                 squareLayer.add(squarePane, col , row);
//             }
//         }
//         System.out.println("now we applied click event handlers");
//         // Create and place pieces on the board
//         // Game game=placePieces(board, squareLayer,player,player,"Practice");
//         Game game=initializeGame(board, pieceLayer,player,player,"Practice");
//         Scene practiceModeScene = new Scene(root, 800, 800); // Adjusted scene size for larger squares
//         primaryStage.setScene(practiceModeScene);
//         GameHandler gameHandler = new GameHandler(game);

//         timerWrapper.whiteTimerLabel = new Label("White: 10:00");
//         timerWrapper.blackTimerLabel = new Label("Black: 10:00");
        
//         // Style the labels
//         String timerStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #f0f0f0; -fx-padding: 5px;";
//         timerWrapper.whiteTimerLabel.setStyle(timerStyle);
//         timerWrapper.blackTimerLabel.setStyle(timerStyle);
        
//         // Position the labels
//         timerWrapper.whiteTimerLabel.setTranslateX(650);
//         timerWrapper.whiteTimerLabel.setTranslateY(100);
//         timerWrapper.blackTimerLabel.setTranslateX(650);
//         timerWrapper.blackTimerLabel.setTranslateY(150);
    
//         // Add labels to the root pane
//         root.getChildren().addAll(timerWrapper.whiteTimerLabel, timerWrapper.blackTimerLabel);
        
//         // Initialize white timer
//         timerWrapper.whiteTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
//             timerWrapper.whiteTimeSeconds--;
//             updateTimerLabel(timerWrapper.whiteTimerLabel, "White", timerWrapper.whiteTimeSeconds);
//             if (timerWrapper.whiteTimeSeconds <= 0) {
//                 // Handle timer expiration
//                 timerWrapper.whiteTimer.stop();
//                 timerWrapper.blackTimer.stop();
//                 System.out.println("White's time expired! Black wins.");

//                 // Update game state
//                 gameHandler.getGameState().setGameActive(); // here false value comes
//                 gameHandler.getGameState().setWinner("black");

//                 // Save game and return to home
//                 game.setWinner("Black");
//                 gameRepository.save(game);
//                 navigateToHome(primaryStage, player);
//             }
//         }));
//         timerWrapper.whiteTimer.setCycleCount(Animation.INDEFINITE);

//         // Initialize black timer
//         timerWrapper.blackTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
//             timerWrapper.blackTimeSeconds--;
//             updateTimerLabel(timerWrapper.blackTimerLabel, "Black", timerWrapper.blackTimeSeconds);
//             if (timerWrapper.blackTimeSeconds <= 0) {
//                 // Handle timer expiration
//                 timerWrapper.whiteTimer.stop();
//                 timerWrapper.blackTimer.stop();
//                 System.out.println("Black's time expired! White wins.");

//                 // Update game state
//                 gameHandler.getGameState().setGameActive();
//                 gameHandler.getGameState().setWinner("white");

//                 // Save game and return to home
//                 game.setWinner("White");
//                 gameRepository.save(game);
//                 navigateToHome(primaryStage, player);
//             }
//         }));
//         timerWrapper.blackTimer.setCycleCount(Animation.INDEFINITE);
        
//         timerWrapper.whiteTimer.play();

//         for (int i = 0; i < 8; i++) {
//             // System.out.println("inside click event handler outer loop");
//             for (int j = 0; j < 8; j++) {
//                 // System.out.println("inside click event handler inner loop");
//                 final int row = i;
//                 final int col = j;
//                 StackPane squarepane = squarePanes[i][j];
//                 squarePanes[i][j].setOnMouseClicked(e -> {
//                     System.out.println("-----------------------------------------------------------------------------------------------------------");
//                     System.out.println("-----------------------------------------------------------------------------------------------------------");
//                     System.out.println("");
//                     System.out.println("");
//                     System.out.println("Square clicked: " + board[row][col].getSquare());
//                     int tp=0;
//                     if(gameHandler.getGameState().getIsGameActive()){
//                         System.out.println("Game is active");
//                         System.out.println("entered this loop for: "+tp);
//                         tp++;
//                         // now lets see if given square has a circle on it:
//                         // boolean hasCircle=hasCircle(squarepane);
//                         // String circleExist="no";
//                         // if(hasCircle){
//                         //     circleExist="yes";
//                         // }
//                         boolean hasHighlight = hasHighlightAt(board[row][col], highlightLayer);
//                         clearHighlights(highlightLayer);
//                         ArrayList<String>possiblemoves=gameHandler.handleSquareClick(board[row][col],hasHighlight ? "yes" : "no",pieceLayer,game.getMoves(),timerWrapper);
//                         gameHandler.getGameState().setSquareSelected(board[row][col].getSquare());
//                         // System.out.println("square clicked now we remove circles");
//                         // clearCircles(squarePanes);
//                         if(gameHandler.getGameState().getIsGameActive()){
//                             clearHighlights(highlightLayer);
//                             if(possiblemoves.size()>0){
//                                 // System.out.println("number of moves are greater than 0");
//                                 // System.out.println("now time to highlight them");
//                                 highlightPossibleMoves(possiblemoves, highlightLayer);
//                                 // System.out.println("now we have highlighted the moves");
//                             }
//                             else{
//                                 System.out.println("no possible moves");
//                             }
//                         }
//                         else{
//                             timerWrapper.whiteTimer.stop();
//                             timerWrapper.blackTimer.stop();
//                             System.out.println("-------------------------------------------------------------------------------");
//                             System.out.println("game got over");
//                             System.out.println("time to setup visuals for the winner");
//                             System.out.println("winner is: "+gameHandler.getGameState().getWinner());
//                             System.out.println("-------------------------------------------------------------------------------");
//                             System.out.println("game got over... time to save it to database");
//                             if(gameHandler.getGameState().getWinner()=="draw"){
//                                 game.setWinner("Draw");
//                             }
//                             else if(gameHandler.getGameState().getWinner()=="white"){
//                                 game.setWinner("White");
//                             }
//                             else{
//                                 game.setWinner("Black");
//                             }
//                             gameRepository.save(game);
//                             System.out.println("the game is saved to database");
//                             navigateToHome(primaryStage, player);
//                             return;
//                         }
                        
//                     }
//                     // gameHandler.handleSquareClick(board[row][col]);
//                 });
//             }
//         }
        
//     }

    

//     private void addColumnLabel(StackPane squarePane, String column, String squareColor) {
//         Label colLabel = new Label(column);
//         colLabel.setStyle("-fx-text-fill: " + (squareColor.equals("lightgray") ? "#000000" : "#ffffff") + 
//                           "; -fx-font-size: 14px; -fx-font-weight: bold;");
        
                          
//         colLabel.setScaleY(-1);
//         StackPane.setAlignment(colLabel, Pos.TOP_RIGHT);
//         StackPane.setMargin(colLabel, new Insets(0, 0, 5, 5));
//         squarePane.getChildren().add(colLabel);
//     }
    
//     private void addRowLabel(StackPane squarePane, int row, String squareColor) {
//         Label rowLabel = new Label(String.valueOf(row));
//         rowLabel.setStyle("-fx-text-fill: " + (squareColor.equals("lightgray") ? "#000000" : "#ffffff") + 
//                           "; -fx-font-size: 14px; -fx-font-weight: bold;");
        
//         rowLabel.setScaleY(-1);  
//         StackPane.setAlignment(rowLabel, Pos.BOTTOM_LEFT);
//         StackPane.setMargin(rowLabel, new Insets(5, 0, 0, 5));
//         squarePane.getChildren().add(rowLabel);
//     }

//     private StackPane createSquarePane(GameSquares square, int row, int col) {
//         StackPane squarePane = new StackPane();
//         squarePane.setStyle("-fx-background-color: " + 
//             (square.getSqColor().equals("lightgray") ? "#d3d3d3" : "#556b2f"));
//         squarePane.setPrefSize(80, 80);
    
//         if (row == 1) {
//             addColumnLabel(squarePane, String.valueOf((char)('a' + col)), square.getSqColor());
//         }
//         if (col == 0) {
//             addRowLabel(squarePane, row, square.getSqColor());
//         }
    
//         return squarePane;
//     }
//     private String findPawn(String square,Pane pieceLayer,String color){
//         int col = square.charAt(0) - 'a';
//         int row = Character.getNumericValue(square.charAt(1)) - 1;
//         double x = (col) * 80 + 40;
//         double y;
//         if(color.equals("white")){
//             y=(7 - row) * 80 + 40;
//             for (Node node : pieceLayer.getChildren()) {
//                 if (node instanceof Label) {
//                     if (node.getTranslateX() == x && node.getTranslateY() == y) {
//                         String text = ((Label) node).getText();
//                         if(text.equals("♟")){
//                             return "pawn";
//                         }
//                     }
//                 }
//             }
//         }
//         else{
//             y=(row) * 80 + 40;
//             for (Node node : pieceLayer.getChildren()) {
//                 if (node instanceof Label) {
//                     if (node.getTranslateX() == x && node.getTranslateY() == y) {
//                         String text = ((Label) node).getText();
//                         if(text.equals("♙")){
//                             return "pawn";
//                         }
//                     }
//                 }
//             }
//         }
        
//         return "no";
//     }
//     private void placePiece(Piece piece, GameSquares square, Pane pieceLayer,String color) {
//         Label pieceLabel = new Label(getPieceSymbol(piece));
//         // pieceLabel.setText("");
        
//         pieceLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: " + 
//             (piece.getColor().equals("white") ? "#000000" : "#ffffff"));
    
//         int col = square.getSquare().charAt(0) - 'a';
//         int row = Character.getNumericValue(square.getSquare().charAt(1)) - 1;
    
//         double squareSize = 80;
//         double labelWidth = 36;
//         double labelHeight = 36;
        
//         // Remove the +1 from col calculation
//         double x = col * squareSize + (squareSize - labelWidth) / 2;
//         double y = (7 - row) * squareSize + (squareSize - labelHeight) / 2;
        
//         pieceLabel.setTranslateX(x);
//         pieceLabel.setTranslateY(y);
//         if(color.equals("white")){

//             pieceLabel.setRotate(180);
//         }
//         pieceLayer.getChildren().add(pieceLabel);
//     }

//     private void highlightPossibleMoves(ArrayList<String> moves, Pane highlightLayer) {
//         for (String move : moves) {
//             int col = move.charAt(0) - 'a';
//             int row = Character.getNumericValue(move.charAt(1)) - 1;
            
//             Circle highlight = new Circle(10, Color.rgb(0, 0, 255, 0.5));
//             highlight.setTranslateX((col) * 80 + 40);
//             highlight.setTranslateY((7 - row) * 80 + 40);
            
//             highlightLayer.getChildren().add(highlight);
//         }
//     }

//     private boolean hasHighlightAt(GameSquares square, Pane highlightLayer) {
//         int col = square.getSquare().charAt(0) - 'a';
//         int row = Character.getNumericValue(square.getSquare().charAt(1)) - 1;
//         double x = (col) * 80 + 40;
//         double y = (7 - row) * 80 + 40;
        
//         for (Node node : highlightLayer.getChildren()) {
//             if (node instanceof Circle) {
//                 if (node.getTranslateX() == x && node.getTranslateY() == y) {
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }
    

//     private boolean hasWhiteHighlightAt(GameSquares square, Pane highlightLayer) {
//         int col = square.getSquare().charAt(0) - 'a';
//         int row = Character.getNumericValue(square.getSquare().charAt(1)) - 1;
//         double x = (col) * 80 + 40;
//         double y = (7 - row) * 80 + 40;
        
//         for (Node node : highlightLayer.getChildren()) {
//             if (node instanceof Circle) {
//                 if (node.getTranslateX() == x && node.getTranslateY() == y) {
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }

//     private boolean hasBlackHighlightAt(GameSquares square, Pane highlightLayer) {
//         int col = square.getSquare().charAt(0) - 'a';
//         int row = Character.getNumericValue(square.getSquare().charAt(1)) - 1;
//         double x = (col) * 80 + 40;
//         double y = (row) * 80 + 40;
        
//         for (Node node : highlightLayer.getChildren()) {
//             if (node instanceof Circle) {
//                 if (node.getTranslateX() == x && node.getTranslateY() == y) {
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }


//     private void clearHighlights(Pane highlightLayer) {
//         highlightLayer.getChildren().clear();
//     }


//     private void setUpOnlineBoard(GameSquares[][] board,Pane pieceLayer,String Color){
//         // Initialize pawns
        
//         for (int col = 0; col < 8; col++) {
//             Pawn whitePawn = new Pawn("white", board[1][col].getSquare(),board[1][col].getSquare().substring(0,1)+"3");
//             Pawn blackPawn = new Pawn("black", board[6][col].getSquare(),board[6][col].getSquare().substring(0,1)+"6");
//             placePiece(whitePawn, board[1][col], pieceLayer,Color);
//             placePiece(blackPawn, board[6][col], pieceLayer,Color);
//         }
    
//         // Initialize other pieces
//         // Place rooks
//         Rook rook1=new Rook("white", board[0][0].getSquare());
//         rook1.setCastleTo("d1");
//         Rook rook2=new Rook("white", board[0][7].getSquare());
//         rook2.setCastleTo("f1");
//         Rook rook3=new Rook("black", board[7][0].getSquare());
//         rook3.setCastleTo("d8");
//         Rook rook4=new Rook("black", board[7][7].getSquare());
//         rook4.setCastleTo("f8");
//         placePiece(rook1, board[0][0], pieceLayer,Color);
//         placePiece(rook2, board[0][7], pieceLayer,Color);
//         placePiece(rook3, board[7][0], pieceLayer,Color);
//         placePiece(rook4, board[7][7], pieceLayer,Color);
//         // Place knights

//         Knight knight1=new Knight("white", board[0][1].getSquare());
//         Knight knight2=new Knight("white", board[0][6].getSquare());
//         Knight knight3=new Knight("black", board[7][1].getSquare());
//         Knight knight4=new Knight("black", board[7][6].getSquare());
//         placePiece(knight1, board[0][1], pieceLayer,Color);
//         placePiece(knight2, board[0][6], pieceLayer,Color);
//         placePiece(knight3, board[7][1], pieceLayer,Color);
//         placePiece(knight4, board[7][6], pieceLayer,Color);

//         // Place bishops
//         Bishop bishop1=new Bishop("white", board[0][2].getSquare());
//         Bishop bishop2=new Bishop("white", board[0][5].getSquare());
//         Bishop bishop3=new Bishop("black", board[7][2].getSquare());
//         Bishop bishop4=new Bishop("black", board[7][5].getSquare());
//         placePiece(bishop1, board[0][2], pieceLayer,Color);
//         placePiece(bishop2, board[0][5], pieceLayer,Color);
//         placePiece(bishop3, board[7][2], pieceLayer,Color);
//         placePiece(bishop4, board[7][5], pieceLayer,Color);

//         // Place queens
//         Queen queen1=new Queen("white", board[0][3].getSquare());
//         Queen queen2=new Queen("black", board[7][3].getSquare());
//         placePiece(queen1, board[0][3], pieceLayer,Color);
//         placePiece(queen2, board[7][3], pieceLayer,Color);

//         // Place kings
//         King king1=new King("white", board[0][4].getSquare());
//         King king2=new King("black", board[7][4].getSquare());
//         placePiece(king1, board[0][4], pieceLayer,Color);
//         placePiece(king2, board[7][4], pieceLayer,Color);

//         if(Color.equals("white")){
//             // this is the white player:
//             // i will invert the board
//             pieceLayer.setScaleY(-1);

//         }
//         else{
//             // this is black player
//             // no need to invert the board
//         }
//     }

//     private Game initializeGame(GameSquares[][] board, Pane pieceLayer, Player player1,Player player2, String gameType) {
//         List<Piece> whitePieces = new ArrayList<>();
//         List<Piece> blackPieces = new ArrayList<>();
    
//         // Initialize pawns
//         for (int col = 0; col < 8; col++) {
//             Pawn whitePawn = new Pawn("white", board[1][col].getSquare(),board[1][col].getSquare().substring(0,1)+"3");
//             Pawn blackPawn = new Pawn("black", board[6][col].getSquare(),board[6][col].getSquare().substring(0,1)+"6");
//             placePiece(whitePawn, board[1][col], pieceLayer,"white");
//             placePiece(blackPawn, board[6][col], pieceLayer,"white");
//             whitePieces.add(whitePawn);
//             blackPieces.add(blackPawn);
//         }
    
//         // Initialize other pieces
//         // Place rooks
//         Rook rook1=new Rook("white", board[0][0].getSquare());
//         rook1.setCastleTo("d1");
//         Rook rook2=new Rook("white", board[0][7].getSquare());
//         rook2.setCastleTo("f1");
//         Rook rook3=new Rook("black", board[7][0].getSquare());
//         rook3.setCastleTo("d8");
//         Rook rook4=new Rook("black", board[7][7].getSquare());
//         rook4.setCastleTo("f8");
//         placePiece(rook1, board[0][0], pieceLayer,"white");
//         placePiece(rook2, board[0][7], pieceLayer,"white");
//         placePiece(rook3, board[7][0], pieceLayer,"white");
//         placePiece(rook4, board[7][7], pieceLayer,"white");
//         whitePieces.add(rook1);
//         blackPieces.add(rook3);
//         whitePieces.add(rook2);
//         blackPieces.add(rook4);
//         // Place knights

//         Knight knight1=new Knight("white", board[0][1].getSquare());
//         Knight knight2=new Knight("white", board[0][6].getSquare());
//         Knight knight3=new Knight("black", board[7][1].getSquare());
//         Knight knight4=new Knight("black", board[7][6].getSquare());
//         placePiece(knight1, board[0][1], pieceLayer,"white");
//         placePiece(knight2, board[0][6], pieceLayer,"white");
//         placePiece(knight3, board[7][1], pieceLayer,"white");
//         placePiece(knight4, board[7][6], pieceLayer,"white");
//         whitePieces.add(knight1);
//         blackPieces.add(knight3);
//         whitePieces.add(knight2);
//         blackPieces.add(knight4);

//         // Place bishops
//         Bishop bishop1=new Bishop("white", board[0][2].getSquare());
//         Bishop bishop2=new Bishop("white", board[0][5].getSquare());
//         Bishop bishop3=new Bishop("black", board[7][2].getSquare());
//         Bishop bishop4=new Bishop("black", board[7][5].getSquare());
//         placePiece(bishop1, board[0][2], pieceLayer,"white");
//         placePiece(bishop2, board[0][5], pieceLayer,"white");
//         placePiece(bishop3, board[7][2], pieceLayer,"white");
//         placePiece(bishop4, board[7][5], pieceLayer,"white");
        
//         whitePieces.add(bishop1);
//         blackPieces.add(bishop3);
//         whitePieces.add(bishop2);
//         blackPieces.add(bishop4);

//         // Place queens
//         Queen queen1=new Queen("white", board[0][3].getSquare());
//         Queen queen2=new Queen("black", board[7][3].getSquare());
//         placePiece(queen1, board[0][3], pieceLayer,"white");
//         placePiece(queen2, board[7][3], pieceLayer,"white");
        
//         whitePieces.add(queen1);
//         blackPieces.add(queen2);

//         // Place kings
//         King king1=new King("white", board[0][4].getSquare());
//         King king2=new King("black", board[7][4].getSquare());
//         placePiece(king1, board[0][4], pieceLayer,"white");
//         placePiece(king2, board[7][4], pieceLayer,"white");
        
//         whitePieces.add(king1);
//         blackPieces.add(king2);
    
//         return new Game(player1.getEmail(), player2.getEmail(), "Practice", whitePieces, blackPieces);
//     }

    
//     private String getPieceSymbol(Piece piece) {
//         if (piece instanceof Pawn) return piece.getColor().equals("white") ? "♙" : "♟";
//         if (piece instanceof Rook) return piece.getColor().equals("white") ? "♖" : "♜";
//         if (piece instanceof Knight) return piece.getColor().equals("white") ? "♘" : "♞";
//         if (piece instanceof Bishop) return piece.getColor().equals("white") ? "♗" : "♝";
//         if (piece instanceof Queen) return piece.getColor().equals("white") ? "♕" : "♛";
//         if (piece instanceof King) return piece.getColor().equals("white") ? "♔" : "♚";
//         return "";
//     }
    
//     private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
//         for (Node node : gridPane.getChildren()) {
//             if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
//                 return node;
//             }
//         }
//         return null;
//     }
    
//     private void updateTimerLabel(Label label, String playerColor, int timeSeconds) {
//         int minutes = timeSeconds / 60;
//         int seconds = timeSeconds % 60;
//         label.setText(playerColor + ": " + String.format("%02d:%02d", minutes, seconds));
//     }

//     private String handlePawnPromotion(String square,  Pane squarePanes) {
//         // Create a popup stage for promotion selection
//         String[] promotionPiece = new String[1];
//         Stage promotionStage = new Stage();
//         promotionStage.initModality(Modality.APPLICATION_MODAL);
//         promotionStage.setTitle("Pawn Promotion");

//         // Create a grid for the promotion options
//         GridPane promotionGrid = new GridPane();
//         promotionGrid.setHgap(10);
//         promotionGrid.setVgap(10);
//         promotionGrid.setPadding(new Insets(10, 10, 10, 10));

//         // Create buttons for each promotion option
//         Button queenBtn = new Button("Queen");
//         Button rookBtn = new Button("Rook");
//         Button bishopBtn = new Button("Bishop");
//         Button knightBtn = new Button("Knight");

//         // Add buttons to the grid
//         promotionGrid.add(queenBtn, 0, 0);
//         promotionGrid.add(rookBtn, 1, 0);
//         promotionGrid.add(bishopBtn, 0, 1);
//         promotionGrid.add(knightBtn, 1, 1);

//         // Set button actions
//         queenBtn.setOnAction(e -> {
//             promotionPiece[0] = "Queen";
//             // promotePawn(square, color, "Queen", squarePanes);
//             promotionStage.close();
//         });
//         rookBtn.setOnAction(e -> {
//             promotionPiece[0] = "Rook";
//             // promotePawn(square, color, "Rook", squarePanes);
//             promotionStage.close();
//         });

//         bishopBtn.setOnAction(e -> {
//             promotionPiece[0] = "Bishop";
//             // promotePawn(square, color, "Bishop", squarePanes);
//             promotionStage.close();
//         });

//         knightBtn.setOnAction(e -> {
//             promotionPiece[0] = "Knight";
//             // promotePawn(square, color, "Knight", squarePanes);
//             promotionStage.close();
//         });

//         // Create scene and show the stage
//         Scene scene = new Scene(promotionGrid);
//         promotionStage.setScene(scene);

//         // Position the popup near the promotion square
//         int col = square.charAt(0) - 'a';
//         int row = Character.getNumericValue(square.charAt(1)) - 1;

//         // Get the position of the square on screen
//         Node squareNode = findSquareNode(square, squarePanes);
//         if (squareNode != null) {
//             Point2D point = squareNode.localToScreen(40, 40);
//             promotionStage.setX(point.getX());
//             promotionStage.setY(point.getY());
//         }

//         promotionStage.showAndWait();
//         return promotionPiece[0];
//     }

//     // Helper method to find a square node
//     private Node findSquareNode(String square, Pane squarePanes) {
//         int col = square.charAt(0) - 'a';
//         int row = Character.getNumericValue(square.charAt(1)) - 1;

//         for (Node node : squarePanes.getChildren()) {
//             if (node instanceof StackPane) {
//                 double x = node.getLayoutX();
//                 double y = node.getLayoutY();

//                 if (Math.abs(x - col * 80) < 1 && Math.abs(y - row * 80) < 1) {
//                     return node;
//                 }
//             }
//         }
//         return null;
//     }






// }


