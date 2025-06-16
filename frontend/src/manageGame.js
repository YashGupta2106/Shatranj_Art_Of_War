import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.isConnected = false;
    this.gameId = null;
    this.playerEmail = null;
    this.onMessageCallback = null;
    this.onConnectionCallback = null;
    this.subscriptions = []; // Track subscriptions
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
  }

  connect(playerEmail, onMessage, onConnection) {
    this.playerEmail = playerEmail;
    this.onMessageCallback = onMessage;
    this.onConnectionCallback = onConnection;

    console.log('🌐 Attempting to connect to WebSocket...');

    try {
      // Create SockJS connection
      const socket = new SockJS('http://localhost:8080/ws');
      
      // Create STOMP client with proper configuration
      this.stompClient = Stomp.over(() => socket);
      
      // Configure STOMP client
      this.stompClient.configure({
        // Connection callback
        onConnect: (frame) => {
          console.log('🌐 STOMP connected:', frame);
          this.isConnected = true;
          this.reconnectAttempts = 0;
          
          // Subscribe to player-specific topic for matchmaking
          this.subscribeToPlayerTopic();
          
          this.onConnectionCallback?.(true);
        },
        
        // Error callback
        onStompError: (frame) => {
          console.error('❌ STOMP error:', frame);
          this.isConnected = false;
          this.onConnectionCallback?.(false);
          this.handleReconnect();
        },
        
        // WebSocket error callback
        onWebSocketError: (error) => {
          console.error('❌ WebSocket error:', error);
          this.isConnected = false;
          this.onConnectionCallback?.(false);
        },
        
        // Disconnect callback
        onDisconnect: (frame) => {
          console.log('🔌 STOMP disconnected:', frame);
          this.isConnected = false;
          this.onConnectionCallback?.(false);
          this.handleReconnect();
        },
        
        // Debug logging (disable in production)
        debug: (str) => {
          if (process.env.NODE_ENV === 'development') {
            console.log('🔧 STOMP Debug:', str);
          }
        },
        
        // Heartbeat configuration
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        
        // Reconnect delay
        reconnectDelay: 5000
      });

      // Activate the connection
      this.stompClient.activate();
      
    } catch (error) {
      console.error('❌ Failed to create WebSocket connection:', error);
      this.onConnectionCallback?.(false);
      this.handleReconnect();
    }
  }

  handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`🔄 Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
      
      setTimeout(() => {
        if (!this.isConnected && this.playerEmail) {
          this.connect(this.playerEmail, this.onMessageCallback, this.onConnectionCallback);
        }
      }, 3000 * this.reconnectAttempts); // Exponential backoff
    } else {
      console.error('❌ Max reconnection attempts reached');
      this.onConnectionCallback?.(false);
    }
  }

  // Subscribe to player-specific topic for matchmaking responses
  subscribeToPlayerTopic() {
    if (!this.playerEmail || !this.stompClient || !this.isConnected) {
      console.warn('⚠️ Cannot subscribe to player topic - missing requirements');
      return;
    }
    
    const playerTopic = `/topic/${this.playerEmail}`;
    console.log('📡 Subscribing to:', playerTopic);
    
    try {
      const subscription = this.stompClient.subscribe(playerTopic, (message) => {
        try {
          const response = JSON.parse(message.body);
          console.log('📥 Received from player topic:', response);
          
          // Handle match found - subscribe to game topic
          if (response.messageType === "MATCH_FOUND") {
            this.subscribeToGameTopic(response.gameId);
          }
          
          this.onMessageCallback?.(response);
        } catch (error) {
          console.error('❌ Error parsing player topic message:', error);
        }
      });
      
      this.subscriptions.push(subscription);
      console.log('✅ Successfully subscribed to player topic');
      
    } catch (error) {
      console.error('❌ Failed to subscribe to player topic:', error);
    }
  }

  // Subscribe to game-specific topic for game messages
  subscribeToGameTopic(gameId) {
    if (!gameId || !this.stompClient || !this.isConnected) {
      console.warn('⚠️ Cannot subscribe to game topic - missing requirements');
      return;
    }
    
    this.gameId = gameId;
    const gameTopic = `/topic/game/${gameId}`;
    console.log('🎮 Subscribing to game topic:', gameTopic);
    
    try {
      const subscription = this.stompClient.subscribe(gameTopic, (message) => {
        try {
          const response = JSON.parse(message.body);
          console.log('📥 Received from game topic:', response);
          this.onMessageCallback?.(response);
        } catch (error) {
          console.error('❌ Error parsing game topic message:', error);
        }
      });
      
      this.subscriptions.push(subscription);
      console.log('✅ Successfully subscribed to game topic');
      
    } catch (error) {
      console.error('❌ Failed to subscribe to game topic:', error);
    }
  }

  // Send messages using STOMP
  sendMessage(message) {
    if (this.isConnected && this.stompClient) {
      console.log('📤 Sending STOMP message:', message);
      try {
        this.stompClient.publish({
          destination: '/app/chess-game',
          body: JSON.stringify(message)
        });
        console.log('✅ Message sent successfully');
      } catch (error) {
        console.error('❌ Failed to send message:', error);
      }
    } else {
      console.error('❌ Cannot send message: STOMP not connected');
      console.log('🔍 Connection status:', {
        isConnected: this.isConnected,
        hasStompClient: !!this.stompClient,
        stompState: this.stompClient?.connected
      });
    }
  }

  // Send move using single endpoint
  sendMove(gameId, moveMessage) {
    moveMessage.messageType = "GAME_MOVE";
    moveMessage.gameId = gameId;
    this.sendMessage(moveMessage);
  }

  // Send matchmaking request
  sendMatchmakingRequest(playerEmail, playerName) {
    const message = {
      messageType: "FIND_MATCH",
      playerEmail: playerEmail,
      playerName: playerName
    };
    
    console.log('🔍 Sending matchmaking request:', message);
    this.sendMessage(message);
  }

  // Send player ready
  sendPlayerReady(gameId, playerEmail) {
    const message = {
      messageType: "PLAYER_READY",
      gameId: gameId,
      playerEmail: playerEmail
    };
    
    this.sendMessage(message);
  }

  // Cancel matchmaking
  cancelMatchmaking(playerEmail) {
    const message = {
      messageType: "CANCEL_MATCH",
      playerEmail: playerEmail
    };
    
    this.sendMessage(message);
  }

  showMove(gameId,currentMove, playerEmail,number) {
    
    const message = {
      messageType: "SHOW_MOVE",
      gameId: gameId,
      currentMove: currentMove,
      playerEmail: playerEmail,
      number: number
    };

    this.sendMessage(message);
  }

  disconnect() {
    console.log('🔌 Disconnecting WebSocket...');
    
    // Unsubscribe from all topics
    this.subscriptions.forEach(sub => {
      try {
        sub.unsubscribe();
      } catch (error) {
        console.warn('⚠️ Error unsubscribing:', error);
      }
    });
    this.subscriptions = [];
    
    if (this.stompClient) {
      try {
        this.stompClient.deactivate();
      } catch (error) {
        console.warn('⚠️ Error deactivating STOMP client:', error);
      }
      this.stompClient = null;
    }
    
    this.isConnected = false;
    this.gameId = null;
    this.playerEmail = null;
    this.reconnectAttempts = 0;
    
    console.log('✅ WebSocket disconnected');
  }
}


// Render a chess piece
const createPiece = (type, color) => {
  return { type, color };
};

// Create a single instance
const websocketService = new WebSocketService();
let isInActiveGame = false;

// Helper function to convert row, col to chess notation
const getSquareNotation = (row, col) => {
  const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
  const ranks = ['1', '2', '3', '4', '5', '6', '7', '8'];
  return files[col] + ranks[row];
};

// Helper function to convert chess notation to row, col
const getRowColFromNotation = (notation) => {
  const col = notation.charCodeAt(0) - 'a'.charCodeAt(0);
  const row = parseInt(notation[1]) - 1;
  // if(color==="white"){
  //   row = 7-row;
  // }
  return [row, col];
};


const PIECES = {
  white: {
    king: "♔",
    queen: "♕", 
    rook: "♖",
    bishop: "♗",
    knight: "♘",
    pawn: "♙"
  },
  black: {
    king: "♚",
    queen: "♛",
    rook: "♜", 
    bishop: "♝",
    knight: "♞",
    pawn: "♟"
  }
};

// Main game manager factory function
export const createGameManager = ({ gameId, gameMode, initialBoard,playerEmail,playerName, callbacks }) => {
  console.log(`🎮 Creating game manager for ${gameMode} mode with ID: ${gameId}`);
  console.log(`👤 Player: ${playerName} (${playerEmail})`);
  // Local state tracking
  let board = initialBoard.map(row => [...row]); // Deep copy
  let currentPlayer = 'TBD';
  let selectedSquare = null;
  let possibleMoves = [];
  let gameStatus = 'active';
  let whiteTime = 600; // 10 minutes
  let blackTime = 600; // 10 minutes
  let isGameActive=false;
  let currentMove = 0; // Track current move number that is being viewed
  let actualMoveNumber=0;
  let actualPlayerColor = null;
  
  // Game ready state and matchmaking state
  let gameReady = gameMode === "practice"; // Practice is always ready
  let actualGameId = null;
  let actualPlayerEmail = playerEmail || "player@example.com"; // Fallback
  let actualPlayerName = playerName || "Player"; // Fallback
  let isWaitingForMatch = gameMode === "online";

  let timerInterval = null;
  let lastTimerUpdate = Date.now();
  let turnStartTime = Date.now(); // Track when current player's turn started


  // Generate appropriate game ID based on mode
  if (gameMode === "practice") {
    actualGameId = `practice-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  } else {
    // For online games, we'll get the actual gameId from matchmaking
    actualGameId = null;
  }

  console.log(`🆔 Using game ID: ${actualGameId}`);

  // Initialize WebSocket connection for both modes
  const initializeConnection = () => {
    console.log('🌐 Initializing WebSocket connection...');
    console.log(`👤 Using player email: ${actualPlayerEmail}`);
    
    // Set initial connection message
    callbacks.onConnectionChange?.(false);
    callbacks.onMessageChange?.('Connecting to server...');
    
    // Connect to WebSocket
    websocketService.connect(
      actualPlayerEmail,
      handleServerResponse,
      handleConnectionChange
    );
  };


   const startTimer = () => {
    if (timerInterval) {
      clearInterval(timerInterval);
    }
    
    console.log('🕐 Starting game timer...');
    turnStartTime = Date.now(); // Reset turn start time
    lastTimerUpdate = Date.now();
    
    timerInterval = setInterval(() => {
      if (gameStatus !== 'active' || !gameReady) {
        stopTimer();
        return;
      }
      
      const now = Date.now();
      const timeSinceLastUpdate = now - lastTimerUpdate;
      
      // Only update if at least 1 second has passed since last update
      if (timeSinceLastUpdate >= 1000) {
        const secondsToDeduct = Math.floor(timeSinceLastUpdate / 1000);
        lastTimerUpdate = now;
        
        // Update the current player's time
        if (currentPlayer === 'white') {
          whiteTime = Math.max(0, whiteTime - secondsToDeduct);
          console.log(`⏰ White time: ${whiteTime}s (deducted ${secondsToDeduct}s)`);
          if (whiteTime === 0) {
            console.log('⏰ White time up!');
            handleTimeUp('white');
            return;
          }
        } else {
          blackTime = Math.max(0, blackTime - secondsToDeduct);
          console.log(`⏰ Black time: ${blackTime}s (deducted ${secondsToDeduct}s)`);
          if (blackTime === 0) {
            console.log('⏰ Black time up!');
            handleTimeUp('black');
            return;
          }
        }
        
        // Update UI
        callbacks.onTimeUpdate?.(whiteTime, blackTime);
      }
    }, 1000);
  };

  // UPDATED: Reset turn timer when player changes
  const resetTurnTimer = () => {
    console.log(`🔄 Resetting turn timer for ${currentPlayer}`);
    turnStartTime = Date.now();
    lastTimerUpdate = Date.now();
  };


  // NEW: Stop timer function
  const stopTimer = () => {
    if (timerInterval) {
      console.log('🛑 Stopping game timer');
      clearInterval(timerInterval);
      timerInterval = null;
    }
  };






  // Update handleConnectionChange function
  const handleConnectionChange = (connected) => {
    console.log(`🔌 Connection status: ${connected ? 'Connected' : 'Disconnected'}`);
    callbacks.onConnectionChange?.(connected);

    if (connected) {
      if (gameMode === "practice") {
        callbacks.onMessageChange?.('Practice mode - Play against yourself!');
        callbacks.onGameReady?.(true);
      } else if (gameMode === "online") {
        // ✅ FIXED: Only start matchmaking if we're waiting AND not in an active game
        if (isWaitingForMatch && !isInActiveGame) {
          callbacks.onMessageChange?.('Connected! Looking for opponent...');
          setTimeout(() => {
            startMatchmaking();
          }, 1000);
        } else if (isInActiveGame) {
          // ✅ ADD THIS: If we're in an active game, just reconnect to it
          callbacks.onMessageChange?.('Reconnected to game!');
        } else {
          callbacks.onMessageChange?.('Connected to server');
        }
      }
    } else {
      if (gameMode === "online") {
        callbacks.onMessageChange?.('Connection lost. Trying to reconnect...');
        callbacks.onGameReady?.(false);
      }
    }
  };

  // Start matchmaking process
  const startMatchmaking = () => {
    if (gameMode !== "online" || !websocketService.isConnected) {
      console.warn('⚠️ Cannot start matchmaking - not in online mode or not connected');
      return;
    }
    
    console.log('🔍 Starting matchmaking...');
    console.log(`👤 Player: ${actualPlayerName} (${actualPlayerEmail})`);
    
    // Send matchmaking request with actual player info
    websocketService.sendMatchmakingRequest(actualPlayerEmail, actualPlayerName);
    
    callbacks.onMessageChange?.('Looking for opponent...');
  };

  // Handle responses from backend
  const handleServerResponse = (response) => {
    console.log('🎯 Processing server response:', response);

    try {
      // ✅ UPDATED: Handle new message types from MatchmakingService
      if (response.messageType === "QUEUE_JOINED") {
        callbacks.onMessageChange?.(`In queue (position ${response.queuePosition}). Looking for opponent...`);
        return;
      }
      
      if (response.messageType === "MATCH_FOUND") { 
        handleMatchFound(response);
        return;
      }
      
      if (response.messageType === "MATCH_CANCELLED") {
        callbacks.onMessageChange?.('Matchmaking cancelled');
        isWaitingForMatch = false;
        return;
      }
      
      if (response.messageType === "GAME_START") {
        handleGameStart(response);
        return;
      }

      if (response.messageType === "PLAYER_READY") {
        handlePlayerReady(response);
        return;
      }
      if (response.timeUp) {
        handleTimeUpResponse(response);
        return;
      }

      if(response.drawOffer==="yes" && response.gameEnded===false){
        console.log("i have got a draw offer from my opponent");
        handleDrawOffer(response);
        return;
      }

      if(response.messageType === "SHOW_MOVE"){
        handleShowMove(response);
        return;
      }


      // Handle move responses
      if (response.clickStatus === "highlight") {
        handleHighlightResponse(response);
      } else if (response.clickStatus === "move") {
        handleMoveResponse(response);
      }

      // Handle game end conditions
      if (response.gameEnded) {
        console.log("calling game end function as game ended for some reason");
        handleGameEnd(response);
        return;
      }

      // Handle existing game responses
      if (response.isValid === false) {
        callbacks.onMessageChange?.('Invalid move!');
        return;
      }

      // Update timers if provided
      if (response.whiteTime !== undefined) {
        whiteTime = response.whiteTime;
      }
      if (response.blackTime !== undefined) {
        blackTime = response.blackTime;
      }
      callbacks.onTimeUpdate?.(whiteTime, blackTime);

    } catch (error) {
      console.error('❌ Error processing server response:', error);
      callbacks.onMessageChange?.('Error processing server response');
    }
  };

  const handleTimeUpResponse = (response) => {
    console.log('⏰ Time up response received:', response);
    
    gameStatus = 'ended';
    gameReady = false;
    isInActiveGame = false;
    isWaitingForMatch = false;

    callbacks.onGameStatusChange?.('ended');
    callbacks.onGameReady?.(false);

    const winner = response.winner;
    const message = `Time's up! ${winner === 'white' ? 'White' : 'Black'} wins by timeout.`;
    
    callbacks.onMessageChange?.(message);
    
    // Clear possible moves
    possibleMoves = [];
    callbacks.onPossibleMovesChange?.([]);
  };

   // Update handleMatchFound function
  const handleMatchFound = (response) => {
    console.log('🎯 Match found!', response);
    
    actualGameId = response.gameId;
    const opponentName = response.oppName || 'Opponent';
    const playerColor = response.color || 'white';
    actualPlayerColor = playerColor;
    isWaitingForMatch = false;
    isGameActive = true; // ✅ Mark game as active
    
    callbacks.onMessageChange?.(`Match found! Playing against ${opponentName} as ${playerColor}`);
    callbacks.onColorDecide?.(playerColor);
    callbacks.onGameId?.(actualGameId);
    
    websocketService.sendPlayerReady(actualGameId, playerEmail);
  };

  // Handle player ready notification
  const handlePlayerReady = (response) => {
    console.log('👤 Player ready notification:', response);
    
    if (response.playersReady === 2) {
      callbacks.onMessageChange?.('Both players ready! Starting game...');
    } else {
      callbacks.onMessageChange?.('Waiting for opponent to be ready...');
    }
  };

  // Handle game start notification
  const handleGameStart = (response) => {
    console.log('🚀 Game started!', response);
    
    gameReady = true;
    callbacks.onGameReady?.(true);
    callbacks.onMessageChange?.('Game started! Your turn.');
    
    // Update initial game state if provided
    if (response.initialBoard) {
      board = response.initialBoard;
      callbacks.onBoardUpdate?.(board.map(row => [...row]));
    }
    
    if (response.currentPlayer) {
      currentPlayer = response.currentPlayer;
      callbacks.onPlayerChange?.(currentPlayer);
    }
    startTimer();
  };

  // Handle highlight response (show possible moves)
  const handleHighlightResponse = (response) => {
    console.log('✨ Handling highlight response');
    
    possibleMoves = response.possibleMoves || [];
    callbacks.onPossibleMovesChange?.(possibleMoves);
    
    if (possibleMoves.length > 0) {
      callbacks.onMessageChange?.(`Selected piece. ${possibleMoves.length} possible moves.`);
      callbacks.onClickStatusChange?.("move");
    } else {
      callbacks.onMessageChange?.('No possible moves for this piece.');
    }
  };

  // Handle showing previous moves
  const handleShowMove=(response)=>{
    console.log(`going to show the move: {response.moveNumber}`);
    const [fromRow, fromCol] = getRowColFromNotation(response.from_sq);
    const [toRow, toCol] = getRowColFromNotation(response.to_sq);

    if(response.moveStatus===1){
      console.log("this is a move ahead");

      if(response.castling === true){
        // this is for white king

        if(response.to_sq==="c1"){
          // move rook from a1 to d1
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][0];
          board[fromRow][fromCol]=null;
          board[fromRow][0]=null;
          board[fromRow][2]=kingPiece;
          board[fromRow][3]=rookPiece;



        }
        else if(response.to_sq==="g1"){
          // move rook from h1 to f1
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][7];
          board[fromRow][fromCol]=null;
          board[fromRow][7]=null;
          board[fromRow][6]=kingPiece;
          board[fromRow][5]=rookPiece;

        }

        // from here black king
        else if(response.to_sq==="c8"){
          // move rook from a8 to d8
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][0];
          board[fromRow][fromCol]=null;
          board[fromRow][0]=null;
          board[fromRow][2]=kingPiece;
          board[fromRow][3]=rookPiece;

        }
        else if(response.to_sq==="g8"){
          // move rook from h8 to f8
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][7];
          board[fromRow][fromCol]=null;
          board[fromRow][7]=null;
          board[fromRow][6]=kingPiece;
          board[fromRow][5]=rookPiece;

        }

      }

      else if(response.promotion=== true){
        console.log("this is a promotion show time ");
        board[toRow][toCol]=null;
        board[fromRow][fromCol]=null;

        if(response.promotionPiece==="knight"){
          if(toRow===0){
            board[toRow][toCol]=createPiece("knight","black");
          }
          else if(toRow===7){
            board[toRow][toCol]=createPiece("knight","white");
          }

        }
        else if(response.promotionPiece==="bishop"){
          if(toRow===0){
            board[toRow][toCol]=createPiece("bishop","black");
          }
          else if(toRow===7){
            board[toRow][toCol]=createPiece("bishop","white");
          }

        }
        else if(response.promotionPiece==="rook"){
          if(toRow===0){
            board[toRow][toCol]=createPiece("rook","black");
          }
          else if(toRow===7){
            board[toRow][toCol]=createPiece("rook","white");
          }

        }
        else if(response.promotionPiece==="queen"){
          console.log("this is a queen promotion");
          if(toRow===0){
            board[toRow][toCol]=createPiece("queen","black");
          }
          else if(toRow===7){
            console.log("here a queen should come");
            board[toRow][toCol]=createPiece("queen","white");
          }

        }

      }

      else if(response.enPassant===true){
        console.log("this is an en passant move");
        const [remRow, remCol] = getRowColFromNotation(response.squareCaptured);
        board[remRow][remCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;
      }


      else if(response.pieceCaptured !=="no"){
        
        board[toRow][toCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;

      }
      else{
        // simple move without any capture
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;
      }

    }

    else if(response.moveStatus===-1){
      console.log("this is a move behind");
      if(response.castling === true){
        // this is for white king

        if(response.to_sq==="c1"){
          // move rook from d1 to a1
          const kingPiece=board[toRow][toCol];
          const rookPiece=board[toRow][3];
          board[toRow][toCol]=null;
          board[toRow][3]=null;
          board[toRow][0]=rookPiece;
          board[fromRow][fromCol]=kingPiece;



        }
        else if(response.to_sq==="g1"){
          // move rook from f1 to h1
          const kingPiece=board[toRow][toCol];
          const rookPiece=board[toRow][5];
          board[toRow][toCol]=null;
          board[toRow][5]=null;
          board[toRow][7]=rookPiece;
          board[fromRow][fromCol]=kingPiece;

        }

        // from here black king
        else if(response.to_sq==="c8"){
          // move rook from d8 to a8
          const kingPiece=board[toRow][toCol];
          const rookPiece=board[toRow][3];
          board[toRow][toCol]=null;
          board[toRow][3]=null;
          board[toRow][0]=rookPiece;
          board[fromRow][fromCol]=kingPiece;

        }
        else if(response.to_sq==="g8"){
          // move rook from f8 to h8
          const kingPiece=board[toRow][toCol];
          const rookPiece=board[toRow][5];
          board[toRow][toCol]=null;
          board[toRow][5]=null;
          board[toRow][7]=rookPiece;
          board[fromRow][fromCol]=kingPiece;

        }
      
      }

      else if(response.promotion=== true){
        board[toRow][toCol]=null;
        if(response.pieceCaptured!=="no"){
          if(response.pieceCaptured==="knight"){
            if(response.moveNumber%2===0){
              board[toRow][toCol]=createPiece("knight","white");
            }
            else{
              board[toRow][toCol]=createPiece("knight","black");
            }

          } 
          else if(response.pieceCaptured==="bishop"){
            if(response.moveNumber%2===0){
              board[toRow][toCol]=createPiece("bishop","white");
            }
            else{
              board[toRow][toCol]=createPiece("bishop","black");
            }

          } 
          else if(response.pieceCaptured==="rook"){
            if(response.moveNumber%2===0){
              board[toRow][toCol]=createPiece("rook","white");
            }
            else{
              board[toRow][toCol]=createPiece("rook","black");
            }

          } 
          else if(response.pieceCaptured==="queen"){
            if(response.moveNumber%2===0){
              board[toRow][toCol]=createPiece("queen","white");
            }
            else{
              board[toRow][toCol]=createPiece("queen","black");
            }

          }  
        }

        // now putting back the pawn there
        if(response.moveNumber%2===0){
          board[fromRow][fromCol]=createPiece("pawn","black");
        }
        else{
          board[fromRow][fromCol]=createPiece("pawn","white");
        }
        

      }

      else if(response.enPassant===true){
        console.log("this is an en passant move");
        const [remRow, remCol] = getRowColFromNotation(response.squareCaptured);
        board[remRow][remCol]=createPiece("pawn","black");
        board[fromRow][fromCol]=board[toRow][toCol];
        board[toRow][toCol]=null;
      }


      else if(response.pieceCaptured !=="no"){
        board[toRow][toCol]=null;
        board[fromRow][fromCol]=board[toRow][toCol];
        if(response.pieceCaptured==="knight"){
          if(response.moveNumber%2===0){
            board[toRow][toCol]=createPiece("knight","white");
          }
          else{
            board[toRow][toCol]=createPiece("knight","black");
          }

        }
        else if(response.pieceCaptured==="bishop"){
          if(response.moveNumber%2===0){
            board[toRow][toCol]=createPiece("bishop","white");
          }
          else{
            board[toRow][toCol]=createPiece("bishop","black");
          }
        
        }
        else if(response.pieceCaptured==="rook"){
          if(response.moveNumber%2===0){
            board[toRow][toCol]=createPiece("rook","white");
          }
          else{
            board[toRow][toCol]=createPiece("rook","black");
          }

        }
        else if(response.pieceCaptured==="queen"){
          if(response.moveNumber%2===0){
            board[toRow][toCol]=createPiece("queen","white");
          }
          else{
            board[toRow][toCol]=createPiece("queen","black");
          }

        }
        else if(response.pieceCaptured==="Pawn"){
          if(response.moveNumber%2===0){
            board[toRow][toCol]=createPiece("pawn","white");
          }
          else{
            board[toRow][toCol]=createPiece("pawn","black");
          }

        }

      } 
      else{
        // simple move without any capture
        board[fromRow][fromCol]=board[toRow][toCol];
        board[toRow][toCol]=null;
      }
    }

    // Update the display
    callbacks.onBoardUpdate?.(board.map(row => [...row]));
    
    
  };

  // Handle move response (actually move the piece)
  const handleMoveResponse = (response) => {
    console.log('🚀 Handling move response');
    
    if(actualMoveNumber===currentMove){
      // Clear possible moves
      possibleMoves = [];
      callbacks.onPossibleMovesChange?.([]);
      console.log(`🟢 Move response: ${response.fromSquare} → ${response.toSquare}`);
      
      // Update board if move details provided
      if (response.moveFrom && response.moveTo) {
        updateBoardFromMove(response);
      }
      actualMoveNumber=response.moveNumber;
      currentMove=response.moveNumber;

    }
    else{
      console.log("Opponent made a move");
      actualMoveNumber=response.moveNumber;
    }
    // Switch player
    currentPlayer = currentPlayer === 'white' ? 'black' : 'white';
    callbacks.onPlayerChange?.(currentPlayer);
    resetTurnTimer(); // Reset turn timer for new player
    // Update message
    const moveText = response.moveFrom && response.moveTo 
      ? `${response.moveFrom} → ${response.moveTo}` 
      : 'Move made';
    callbacks.onMessageChange?.(`${moveText}. ${currentPlayer}'s turn.`);
    // Clear selection
    selectedSquare = null;
    callbacks.onSelectionChange?.(null);
    callbacks.onClickStatusChange?.("highlight");
    
  };

  // Update board based on move
  const updateBoardFromMove = (response) => {
    console.log(`🔄 Updating board: response.fromSquare → response.toSquare`);
    console.log("response mein promotion is: ",response.isPromotion);
    
    const [fromRow, fromCol] = getRowColFromNotation(response.moveFrom);
    const [toRow, toCol] = getRowColFromNotation(response.moveTo);
    console.log("this was the move number",actualMoveNumber);

    
    
    if(response.color==="white"){

      if(response.isCastle!=="no"){
        console.log("this is a castle move");

        if(response.moveTo==="c1"){
          board[fromRow][2]=board[fromRow][fromCol];
          board[fromRow][3]=board[fromRow][0];
          board[fromRow][fromCol]=null;
          board[fromRow][0]=null;

        }
        else if(response.moveTo==="g1"){
          
          
          board[fromRow][6]=board[fromRow][fromCol];
          board[fromRow][5]=board[fromRow][7];
          board[fromRow][fromCol]=null;
          board[fromRow][7]=null;
        }


      }
      else if(response.isPromotion!=="no"){
        console.log("this is a promotion move");
        // first removing any existing piece there
        board[toRow][toCol]=null;
        board[fromRow][fromCol]=null;
        if(response.promoteTo==="queen"){
          board[toRow][toCol]=createPiece("queen","white");
          console.log("Promoting to queen");
        }
        else if(response.promoteTo==="rook"){
          board[toRow][toCol]=createPiece("rook","white");
          console.log("Promoting to rook");
        }
        else if(response.promoteTo==="bishop"){
          board[toRow][toCol]=createPiece("bishop","white");
          console.log("Promoting to bishop");
        }
        else if(response.promoteTo==="knight"){
          board[toRow][toCol]=createPiece("knight","white");
          console.log("Promoting to knight");
        }
        else{
          console.log("mujhe toh kuch mila hi nahi kya bhej raha hai");
        }

      }
      else if(response.isEnPassant!=="no"){
        console.log("this is an en passant move");
        const [remRow, remCol] = getRowColFromNotation(response.removePiece);
        board[remRow][remCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;

      }
      else{
        // case for removal/normal move
        console.log("mai toh yaha se chalunga");
        board[toRow][toCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;
      }
    }

    else if(response.color==="black"){

      if(response.isCastle!=="no"){

        if(response.moveTo==="c8"){
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][0];
          board[fromRow][fromCol]=null;
          board[fromRow][0]=null;
          board[fromRow][2]=kingPiece;
          board[fromRow][3]=rookPiece;

        }
        else if(response.moveTo==="g8"){
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][7];
          board[fromRow][fromCol]=null;
          board[fromRow][7]=null;
          board[fromRow][6]=kingPiece;
          board[fromRow][5]=rookPiece;
        }


      }
      else if(response.isPromotion!=="no"){
        // first removing any existing piece there
        board[toRow][toCol]=null;
        board[fromRow][fromCol]=null;
        if(response.promoteTo==="queen"){
          board[toRow][toCol]=createPiece("queen","black");
          console.log("Promoting to queen");
        }
        else if(response.promoteTo==="rook"){
          board[toRow][toCol]=createPiece("rook","black");
          console.log("Promoting to rook");
        }
        else if(response.promoteTo==="bishop"){
          board[toRow][toCol]=createPiece("bishop","black");
          console.log("Promoting to bishop");
        }
        else if(response.promoteTo==="knight"){
          board[toRow][toCol]=createPiece("knight","black");
          console.log("Promoting to knight");
        }

      }

      else if(response.isEnPassant!=="no"){
        console.log("this is an en passant move");
        const [remRow, remCol] = getRowColFromNotation(response.removePiece);
        board[remRow][remCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;

      }

      else{
        // case for removal/normal move
        board[toRow][toCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;
      }
    }

    // Update the display
    callbacks.onBoardUpdate?.(board.map(row => [...row]));

    
    
    
  };

  // Update handleGameEnd function
  const handleGameEnd = (response) => {
    console.log('🏁 Game ended:', response);
    stopTimer();
    gameStatus = 'ended';
    gameReady = false;
    isInActiveGame = false; // ✅ ADD THIS LINE
    isWaitingForMatch = false; // ✅ ADD THIS LINE

    callbacks.onGameStatusChange?.('ended');
    callbacks.onGameReady?.(false);
    console.log(" i got command to end the game")

    let message = 'Game Over! ';
    if (response.winner === 'draw') {
      message += `Draw - ${response.gameEndReason}`;
    } else {
      message += `${response.winner} wins! ${response.gameEndReason}`;
    }

    callbacks.onMessageChange?.(message);

    possibleMoves = [];
    callbacks.onPossibleMovesChange?.([]);
  };

    // Handle square click - main entry point
  const handleSquareClick = (square,message) => {
    // Check if game is ready
    if (!gameReady) {
      console.log('🚫 Game not ready, ignoring click');
      if (gameMode === "online") {
        callbacks.onMessageChange?.('Waiting for game to start...');
      }
      return;
    }

    if (gameStatus !== 'active') {
      console.log('🚫 Game not active, ignoring click');
      return;
    }

    // Check if we have a valid gameId for online games
    if (gameMode === "online" && !actualGameId) {
      console.log('🚫 No game ID available, ignoring click');
      return;
    }

    // const square = getSquareNotation(row, col);
    console.log(`👆 Square clicked: ${square}`);
    if(actualMoveNumber===currentMove){
      // Check if clicked square has a possible move (hasCircle logic)
      const hasCircle = possibleMoves.includes(square) ? "yes" : "no";
      console.log(`🎯 Has circle: ${hasCircle}`);

      // Clear highlights immediately for UI responsiveness
      possibleMoves = [];
      callbacks.onPossibleMovesChange?.([]);

      // Create move message
      const moveMessage = {
        messageType: "GAME_MOVE", // Add message type
        gameId: actualGameId,
        playerEmail: actualPlayerEmail,
        squareClicked: square,
        color: currentPlayer,
        hasCircle: hasCircle,
        whiteTime: whiteTime,
        blackTime: blackTime,
        promoteTo: "no", // TODO: Handle pawn promotion
        buttonClicked: "no",
        drawOffer: "NA"
      };
      if(message.promotion===true){
        moveMessage.promoteTo = message.promoteTo;
      }

      console.log('📤 Sending move message:', moveMessage);

      // Send to backend
      websocketService.sendMove(actualGameId, moveMessage);

      // Update selection state for UI
      if (hasCircle === "no") {
        selectedSquare = square;
        callbacks.onSelectionChange?.(square);
      } else {
        selectedSquare = null;
        callbacks.onSelectionChange?.(null);
      }

    }
    else{
      callbacks.onMessageChange?.('Please go back to current move to play one');
    }

    
  };

  // Game control functions
  const resign = () => {
    if (gameStatus !== 'active' || !gameReady) return;

    console.log(`🏳️ ${currentPlayer} resigned`);
    
    const resignMessage = {
      messageType: "GAME_END",
      gameId: actualGameId,
      playerEmail: actualPlayerEmail,
      hasCircle: "no",
      whiteTime: whiteTime,
      blackTime: blackTime,
      buttonClicked: "resign",
      drawOffer: "NA"
    };

    websocketService.sendMessage(resignMessage);
  };

  const offerDraw = (result) => {
    if (gameStatus !== 'active' || !gameReady) return;

    console.log(`🤝 ${currentPlayer} offered draw`);
    
    const drawMessage = {
      messageType: "GAME_END",
      gameId: actualGameId,        
      playerEmail: actualPlayerEmail,
      whiteTime: whiteTime,
      blackTime: blackTime,
      buttonClicked: "draw",
      drawOffer: result
    };

    websocketService.sendMessage(drawMessage);
  };

  const handleDrawOffer=(response)=>{
    

    // sending this to frontend
    const opponentColor = actualPlayerColor === 'white' ? 'black' : 'white';
    callbacks.onDrawOfferReceived?.(opponentColor);
    return ;


  };

  const resetGame = () => {
    if (gameMode !== "practice") {
      console.log('🚫 Reset only available in practice mode');
      return;
    }

    console.log('🔄 Resetting practice game');
    
    // Reset local state
    board = initialBoard.map(row => [...row]);
    currentPlayer = 'white';
    selectedSquare = null;
    possibleMoves = [];
    gameStatus = 'active';
    gameReady = true; // Practice is always ready
    whiteTime = 600;
    blackTime = 600;

    // Update UI
    callbacks.onBoardUpdate?.(board);
    callbacks.onPlayerChange?.(currentPlayer);
    callbacks.onSelectionChange?.(null);
    callbacks.onPossibleMovesChange?.([]);
    callbacks.onGameStatusChange?.('active');
    callbacks.onGameReady?.(true);
    callbacks.onMessageChange?.('New practice game started!');
    callbacks.onTimeUpdate?.(whiteTime, blackTime);
  };

  // Cancel matchmaking (for online games)
  const cancelMatchmaking = () => {
    if (gameMode !== "online" || !isWaitingForMatch) return;
    
    console.log('❌ Canceling matchmaking');
    
    // ✅ CORRECTED: Use the WebSocketService method
    websocketService.cancelMatchmaking(playerEmail);
    
    // Reset state
    isWaitingForMatch = false;
    callbacks.onMessageChange?.('Matchmaking canceled');
  };

  
  // Update cleanup function
  const cleanup = () => {
    console.log('🧹 Cleaning up game manager');
    stopTimer(); 
    // ✅ Only cleanup if game is not active or explicitly requested
    if (!isGameActive || gameStatus === 'ended') {
      if (isWaitingForMatch && !isGameActive) {
        cancelMatchmaking();
      }
      
      websocketService.disconnect();
    } else {
      console.log('⚠️ Skipping cleanup - game is active');
    }
  };

  const showPreviousMove = () => {
    console.log('🔙 Showing previous move');
    if(currentMove>1){
      console.log(currentMove,actualMoveNumber);
      websocketService.showMove(actualGameId,currentMove,playerEmail,-1);
      currentMove--;
    }

  }

  const showNextMove = () => {
    console.log('🔜 Showing next move');
    if(currentMove<actualMoveNumber){
      currentMove++;
      console.log("current and actual",currentMove,actualMoveNumber);
      websocketService.showMove(actualGameId,currentMove, playerEmail,1);

    }
  }
  // NEW: Handle time up from frontend timer
  const handleTimeUp = (color) => {
    if (gameStatus !== 'active' || !gameReady || !actualGameId) {
      console.log('🚫 Cannot handle time up - game not active');
      return;
    }
    console.log(`⏰ ${color} time up! Sending to server...`);
    
    const timeUpMessage = {
      messageType: "GAME_MOVE",
      gameId: actualGameId,
      playerEmail: actualPlayerEmail,
      squareClicked: "a1", // Dummy value
      color: color,
      hasCircle: "no",
      whiteTime: color === 'white' ? 0 : whiteTime,
      blackTime: color === 'black' ? 0 : blackTime,
      promoteTo: "no",
      buttonClicked: "time_up", // NEW: Special button for time up
      drawOffer: "NA"
    };

    websocketService.sendMove(actualGameId, timeUpMessage);
  };


  const handleBackToHome=()=>{
    console.log('🏠 Back to home');
    if(gameReady===true){
      const message={
        messageType:"Quit",
        gameId:actualGameId,
        playerEmail:actualPlayerEmail,
        color:currentPlayer
      };
      websocketService.sendMessage(message);
    }
    
    websocketService.disconnect();
    window.location.href = '/';
  }
  // Initialize the connection
  initializeConnection();

  // Return public interface
  return {
    handleSquareClick,
    resign,
    offerDraw,
    resetGame,
    cancelMatchmaking,
    showPreviousMove,
    showNextMove,
    handleTimeUp,
    startTimer,
    stopTimer,
    handleBackToHome,
    cleanup,
    
    // Getters for current state (if needed)
    getCurrentPlayer: () => currentPlayer,
    getGameStatus: () => gameStatus,
    getPossibleMoves: () => [...possibleMoves],
    getBoard: () => board.map(row => [...row]),
    isGameReady: () => gameReady,
    getGameId: () => actualGameId
  };
};


