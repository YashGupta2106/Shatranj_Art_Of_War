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
const getRowColFromNotation = (notation,color) => {
  const col = notation.charCodeAt(0) - 'a'.charCodeAt(0);
  const row = parseInt(notation[1]) - 1;
  // if(color==="white"){
  //   row = 7-row;
  // }
  return [row, col];
};

const renderPiece = (piece) => {
  if (!piece) return null;
  return (
    <span className={`chess-piece ${piece.color}`}>
      {PIECES[piece.color][piece.type]}
    </span>
  );
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
  
  // Game ready state and matchmaking state
  let gameReady = gameMode === "practice"; // Practice is always ready
  let actualGameId = null;
  let actualPlayerEmail = playerEmail || "player@example.com"; // Fallback
  let actualPlayerName = playerName || "Player"; // Fallback
  let isWaitingForMatch = gameMode === "online";

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

      // Handle existing game responses
      if (response.isValid === false) {
        callbacks.onMessageChange?.('Invalid move!');
        return;
      }

      // Handle game end conditions
      if (response.gameEnded) {
        handleGameEnd(response);
        return;
      }

      // Handle move responses
      if (response.clickStatus === "highlight") {
        handleHighlightResponse(response);
      } else if (response.clickStatus === "move") {
        handleMoveResponse(response);
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

   // Update handleMatchFound function
  const handleMatchFound = (response) => {
    console.log('🎯 Match found!', response);
    
    actualGameId = response.gameId;
    const opponentName = response.oppName || 'Opponent';
    const playerColor = response.color || 'white';
    
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
  };

  // Handle highlight response (show possible moves)
  const handleHighlightResponse = (response) => {
    console.log('✨ Handling highlight response');
    
    possibleMoves = response.possibleMoves || [];
    callbacks.onPossibleMovesChange?.(possibleMoves);
    
    if (possibleMoves.length > 0) {
      callbacks.onMessageChange?.(`Selected piece. ${possibleMoves.length} possible moves.`);
    } else {
      callbacks.onMessageChange?.('No possible moves for this piece.');
    }
  };

  // Handle move response (actually move the piece)
  const handleMoveResponse = (response) => {
    console.log('🚀 Handling move response');
    
    // Clear possible moves
    possibleMoves = [];
    callbacks.onPossibleMovesChange?.([]);
    console.log(`🟢 Move response: ${response.fromSquare} → ${response.toSquare}`);
    
    // Update board if move details provided
    if (response.moveFrom && response.moveTo) {
      updateBoardFromMove(response);
    }
    
    // Switch player
    currentPlayer = currentPlayer === 'white' ? 'black' : 'white';
    callbacks.onPlayerChange?.(currentPlayer);
    
    // Update message
    const moveText = response.moveFrom && response.moveTo 
      ? `${response.moveFrom} → ${response.moveTo}` 
      : 'Move made';
    callbacks.onMessageChange?.(`${moveText}. ${currentPlayer}'s turn.`);
    
    // Clear selection
    selectedSquare = null;
    callbacks.onSelectionChange?.(null);
  };

  // Update board based on move
  const updateBoardFromMove = (response) => {
    console.log(`🔄 Updating board: response.fromSquare → response.toSquare`);
    
    const [fromRow, fromCol] = getRowColFromNotation(response.moveFrom,currentPlayer);
    const [toRow, toCol] = getRowColFromNotation(response.moveTo,currentPlayer);

    
    
    if(response.color==="white"){

      if(!response.isCastle==="no"){

        if(response.toSquare==="c1"){
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][0];
          board[fromRow][fromCol]=null;
          board[fromRow][0]=null;
          board[fromRow][2]=kingPiece;
          board[fromRow][3]=rookPiece;

        }
        else if(response.toSquare==="g1"){
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][7];
          board[fromRow][fromCol]=null;
          board[fromRow][7]=null;
          board[fromRow][6]=kingPiece;
          board[fromRow][5]=rookPiece;
        }


      }
      else if(!response.promoteTo==="no"){
        // first removing any existing piece there
        board[toRow][toCol]=null;
        board[fromRow][fromCol]=null;
        if(promoteTo==="queen"){
          board[toRow][toCol]=PIECES.white.queen;
          console.log("Promoting to queen");
        }
        else if(promoteTo==="rook"){
          board[toRow][toCol]=PIECES.white.rook;
          console.log("Promoting to rook");
        }
        else if(promoteTo==="bishop"){
          board[toRow][toCol]=PIECES.white.bishop;
          console.log("Promoting to bishop");
        }
        else if(promoteTo==="knight"){
          board[toRow][toCol]=PIECES.white.knight;
          console.log("Promoting to knight");
        }

      }
      else{
        // case for removal/normal move
        board[toRow][toCol]=null;
        board[toRow][toCol]=board[fromRow][fromCol];
        board[fromRow][fromCol]=null;
      }
    }

    else if(response.color==="black"){

      if(!response.isCastle==="no"){

        if(response.toSquare==="c8"){
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][0];
          board[fromRow][fromCol]=null;
          board[fromRow][0]=null;
          board[fromRow][2]=kingPiece;
          board[fromRow][3]=rookPiece;

        }
        else if(response.toSquare==="g8"){
          const kingPiece=board[fromRow][fromCol];
          const rookPiece=board[fromRow][7];
          board[fromRow][fromCol]=null;
          board[fromRow][7]=null;
          board[fromRow][6]=kingPiece;
          board[fromRow][5]=rookPiece;
        }


      }
      else if(!response.promoteTo==="no"){
        // first removing any existing piece there
        board[toRow][toCol]=null;
        board[fromRow][fromCol]=null;
        if(promoteTo==="queen"){
          board[toRow][toCol]=PIECES.black.queen;
          console.log("Promoting to queen");
        }
        else if(promoteTo==="rook"){
          board[toRow][toCol]=PIECES.black.rook;
          console.log("Promoting to rook");
        }
        else if(promoteTo==="bishop"){
          board[toRow][toCol]=PIECES.black.bishop;
          console.log("Promoting to bishop");
        }
        else if(promoteTo==="knight"){
          board[toRow][toCol]=PIECES.black.knight;
          console.log("Promoting to knight");
        }

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

    gameStatus = 'ended';
    gameReady = false;
    isInActiveGame = false; // ✅ ADD THIS LINE
    isWaitingForMatch = false; // ✅ ADD THIS LINE

    callbacks.onGameStatusChange?.('ended');
    callbacks.onGameReady?.(false);

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
  const handleSquareClick = (square) => {
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
  };

  // Game control functions
  const resign = () => {
    if (gameStatus !== 'active' || !gameReady) return;

    console.log(`🏳️ ${currentPlayer} resigned`);
    
    const resignMessage = {
      messageType: "GAME_MOVE",
      gameId: actualGameId,
      squareClicked: "a1", // Dummy value
      color: currentPlayer,
      hasCircle: "no",
      whiteTime: whiteTime,
      blackTime: blackTime,
      promoteTo: "no",
      buttonClicked: "resign",
      drawOffer: "NA"
    };

    websocketService.sendMove(actualGameId, resignMessage);
  };

  const offerDraw = () => {
    if (gameStatus !== 'active' || !gameReady) return;

    console.log(`🤝 ${currentPlayer} offered draw`);
    
    const drawMessage = {
      messageType: "GAME_MOVE",
      gameId: actualGameId,
      squareClicked: "a1", // Dummy value
      color: currentPlayer,
      hasCircle: "no",
      whiteTime: whiteTime,
      blackTime: blackTime,
      promoteTo: "no",
      buttonClicked: "draw",
      drawOffer: "yes"
    };

    websocketService.sendMove(actualGameId, drawMessage);
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

  // Initialize the connection
  initializeConnection();

  // Return public interface
  return {
    handleSquareClick,
    resign,
    offerDraw,
    resetGame,
    cancelMatchmaking,
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


