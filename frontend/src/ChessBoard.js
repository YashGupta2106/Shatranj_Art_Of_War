import React, { useState, useEffect, useRef } from "react";
import { createGameManager } from "./manageGame";
import "./ChessBoard.css";


// Chess piece Unicode symbols
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

// Initial board setup - WHITE on rows 0,1 and BLACK on rows 6,7
const createInitialBoard = () => {
  const board = Array(8).fill(null).map(() => Array(8).fill(null));
  
  // Place WHITE pawns on row 1
  for (let i = 0; i < 8; i++) {
    board[1][i] = { type: 'pawn', color: 'white' };
  }
  
  // Place BLACK pawns on row 6  
  for (let i = 0; i < 8; i++) {
    board[6][i] = { type: 'pawn', color: 'black' };
  }
  
  // Place other pieces
  const pieceOrder = ['rook', 'knight', 'bishop', 'queen', 'king', 'bishop', 'knight', 'rook'];
  
  // WHITE pieces on row 0
  for (let i = 0; i < 8; i++) {
    board[0][i] = { type: pieceOrder[i], color: 'white' };
  }
  
  // BLACK pieces on row 7
  for (let i = 0; i < 8; i++) {
    board[7][i] = { type: pieceOrder[i], color: 'black' };
  }
  
  return board;
};

export default function ChessBoard({ user,onBackToHome,  gameMode }) {
  // UI State - managed by ChessBoard
  const [board, setBoard] = useState(createInitialBoard()); // Show board immediately
  const [selectedSquare, setSelectedSquare] = useState(null);
  const [currentPlayer, setCurrentPlayer] = useState('white');
  const [gameStatus, setGameStatus] = useState('active');
  const [gameMessage, setGameMessage] = useState('');
  const [possibleMoves, setPossibleMoves] = useState([]);
  const [whiteTime, setWhiteTime] = useState(600); // 10 minutes
  const [blackTime, setBlackTime] = useState(600);
  const [isConnected, setIsConnected] = useState(false);
  const [playerColor, setPlayerColor] = useState('null'); // Default to white
  const [gameId, setGameId] = useState("practice-game"); // Default game ID for practice mode
  // let gameId = "practice-game";
  // NEW: Game ready state for online games
  const [gameReady, setGameReady] = useState(gameMode === "practice"); // Practice is always ready

  // Game Manager instance
  const [gameManager, setGameManager] = useState(null);

  // ✅ Use useRef to prevent re-initialization
  const gameManagerRef = useRef(null);
  const [isInitialized, setIsInitialized] = useState(false);


  // Initialize Game Manager on component mount
  useEffect(() => {
    if(!isInitialized && user?.email){
      console.log(`🎮 Initializing ${gameMode} game with ID: ${gameId}`);
      console.log(`👤 Player email: ${user?.email}`);

      if (gameMode === "online" && !user?.email) {
        console.error('❌ No user email available for online game');
        setGameMessage('Error: User not authenticated for online play');
        return;
      }

      // Set initial message based on mode
      if (gameMode === "online") {
        setGameMessage('Finding opponent...');
      } else {
        setGameMessage('Practice mode - Play against yourself!');
      }

      // Create game manager with callback functions
      const manager = createGameManager({
        gameId:null,
        gameMode,
        initialBoard: board,
        playerEmail: user?.email,
        playerName: user?.displayName || user?.email?.split('@')[0] || 'Player',
        // Callback functions for manageGame to update UI
        callbacks: {
          onBoardUpdate: (newBoard) => {
            console.log('🎨 Updating board display');
            setBoard(newBoard);
          },

          onPlayerChange: (newPlayer) => {
            console.log(`👤 Player changed to: ${newPlayer}`);
            setCurrentPlayer(newPlayer);
          },

          onGameStatusChange: (status) => {
            console.log(`🎯 Game status: ${status}`);
            setGameStatus(status);
          },

          onMessageChange: (message) => {
            console.log(`💬 Game message: ${message}`);
            setGameMessage(message);
          },

          onPossibleMovesChange: (moves) => {
            console.log(`✨ Possible moves:`, moves);
            setPossibleMoves(moves);
          },

          onSelectionChange: (square) => {
            console.log(`🎯 Selection changed:`, square);
            setSelectedSquare(square);
          },

          onTimeUpdate: (white, black) => {
            setWhiteTime(white);
            setBlackTime(black);
          },

          onConnectionChange: (connected) => {
            console.log(`🌐 Connection status: ${connected ? 'Connected' : 'Disconnected'}`);
            setIsConnected(connected);
            if (gameMode === "online") {
              if (connected) {
                setGameMessage('Connected! Waiting for opponent...');
              } else {
                setGameMessage('Connection lost. Trying to reconnect...');
              }
            }
          },

          // NEW: Game ready callback
          onGameReady: (ready) => {
            console.log(`🎮 Game ready status: ${ready}`);
            setGameReady(ready);
            if (ready && gameMode === "online") {
              setGameMessage('Game started! Your turn.');
            }
          },
          onColorDecide:(color)=>{
            console.log(`🎮 Color decided: ${color}`);
            setPlayerColor(color);
          },
          onGameId:(id)=>{
            console.log(`🎮 Game ID: ${id}`);
            setGameId(id);
          }

        }
      });

      gameManagerRef.current = manager;
      setGameManager(manager);
      setIsInitialized(true);
    }
    

    // Cleanup on component unmount
    return () => {
      console.log('🧹 Cleaning up game manager');
      if (manager) {
        manager.cleanup();
        gameManagerRef.current.cleanup();
      }
    };
  }, [gameMode, user?.email]);

  // Convert row, col to chess notation (e.g., 0,0 -> a1, 7,7 -> h8)
  const getSquareNotation = (row, col,color) => {
    const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    const ranks = ['1', '2', '3', '4', '5', '6', '7', '8'];
    // if(color === 'white'){
    //   console.log("yes this is a white player");
    //   return files[col] + ranks[7-row];
    // }
    return files[col] + ranks[row];
  };

  // Handle square click - delegate to game manager
  const handleSquareClick = (row, col) => {
    // NEW: Check if game is ready first
    if (!gameReady) {
      console.log('🚫 Game not ready yet, ignoring click');
      if (gameMode === "online") {
        setGameMessage('Waiting for opponent to join...');
      }
      return;
    }

    if (!gameManager) {
      console.warn('⚠️ Game manager not initialized yet');
      return;
    }

    if (gameStatus !== 'active') {
      console.log('🚫 Game is not active, ignoring click');
      return;
    }
    console.log(`🖱️ Square clicked: Row ${row}, Col ${col}`);
    const square = getSquareNotation(row, col,playerColor);
    console.log(`👆 Square clicked: ${square}`);
    
    // Delegate to game manager - it handles all the logic
    if(currentPlayer===playerColor){

      gameManager.handleSquareClick(square);
    }
    else{
      console.log("Not your turn");
    }
  };

  // Handle game control buttons
  const handleNewGame = () => {
    if (gameMode === "practice" && gameManager) {
      console.log('🔄 Starting new practice game');
      gameManager.resetGame();
    }
  };

  const handleResign = () => {
    if (gameManager && gameStatus === 'active' && gameReady) {
      console.log(`🏳️ ${currentPlayer} resigned`);
      gameManager.resign();
    }
  };

  const handleDrawOffer = () => {
    if (gameManager && gameStatus === 'active' && gameReady) {
      console.log(`🤝 ${currentPlayer} offered draw`);
      gameManager.offerDraw();
    }
  };

  // Render a chess piece
  const renderPiece = (piece) => {
    if (!piece) return null;
    return (
      <span className={`chess-piece ${piece.color}`}>
        {PIECES[piece.color][piece.type]}
      </span>
    );
  };

  // Check if square is selected
  const isSelected = (row, col) => {
    return selectedSquare && selectedSquare[0] === row && selectedSquare[1] === col;
  };

  // Check if square has a possible move highlight
  const hasPossibleMove = (row, col) => {
    const square = getSquareNotation(row, col);
    return possibleMoves.includes(square);
  };

  // Get square color (alternating light/dark)
  const getSquareColor = (row, col) => {
    return (row + col) % 2 === 0 ? 'light' : 'dark';
  };

  // Format time display (MM:SS)
  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="chess-game-container">
      <header className="chess-header">
        <h1>{gameMode === "online" ? "Online Game" : "Practice Mode"}</h1>
        <div className="game-info">
          {/* Timer Display */}
          <div className="timers">
            <div className={`timer white-timer ${currentPlayer === 'white' ? 'active' : ''}`}>
              ⚪ White: {formatTime(whiteTime)}
            </div>
            <div className={`timer black-timer ${currentPlayer === 'black' ? 'active' : ''}`}>
              ⚫ Black: {formatTime(blackTime)}
            </div>
          </div>

          {/* Current Player */}
          <span className="current-player">
            Current Turn: <strong>{currentPlayer.toUpperCase()}</strong>
          </span>

          {/* Connection Status for Online Games */}
          {gameMode === "online" && (
            <span className={`connection-status ${isConnected ? 'connected' : 'disconnected'}`}>
              {isConnected ? "🟢 Connected" : "🔴 Disconnected"}
            </span>
          )}

          {/* Back Button */}
          <button className="back-btn" onClick={onBackToHome}>
            Back to Home
          </button>
        </div>
      </header>

      {/* Game Message Display */}
      {gameMessage && (
        <div className={`game-message ${gameStatus === 'ended' ? 'game-over' : ''}`}>
          {gameMessage}
        </div>
      )}

      <main className="chess-main">
        {/* Chess Board Container with overlay support */}
        <div className="chess-board-container">
          <div className={`chess-board ${!gameReady ? 'disabled' : ''} ${playerColor === 'white' ? 'rotated' : ''}`}>
            {board.map((row, rowIndex) =>
              row.map((piece, colIndex) => (
                <div
                  key={`${rowIndex}-${colIndex}`}
                  className={`chess-square ${getSquareColor(rowIndex, colIndex)} ${
                    isSelected(rowIndex, colIndex) ? 'selected' : ''
                  } ${hasPossibleMove(rowIndex, colIndex) ? 'possible-move' : ''}`}
                  onClick={() => handleSquareClick(rowIndex, colIndex)}
                >
                  {/* Square coordinates for debugging */}
                  <div className="square-notation">
                    {getSquareNotation(rowIndex, colIndex)}
                  </div>
                  
                  {/* Chess Piece */}
                  {renderPiece(piece)}
                  
                  {/* Possible Move Indicator */}
                  {hasPossibleMove(rowIndex, colIndex) && (
                    <div className="move-indicator">●</div>
                  )}
                </div>
              ))
            )}
          </div>
          
          {/* NEW: Overlay when game is not ready (online mode only) */}
          {!gameReady && gameMode === "online" && (
            <div className="game-overlay">
              <div className="waiting-message">
                <h3>{gameMessage}</h3>
                <div className="loading-spinner">⏳</div>
                <p>Please wait while we find your opponent...</p>
              </div>
            </div>
          )}
        </div>

        {/* Game Sidebar */}
        <div className="game-sidebar">
          {/* Game Status */}
          <div className="game-status">
            <h3>Game Status</h3>
            <p><strong>Status:</strong> {gameStatus}</p>
            <p><strong>Mode:</strong> {gameMode}</p>
            <p><strong>Ready:</strong> {gameReady ? '✅' : '⏳'}</p>
            {gameId && <p><strong>Game ID:</strong> {gameId}</p>}
          </div>

          {/* Move History */}
          <div className="move-history">
            <h3>Moves</h3>
            <div className="moves-list">
              {/* TODO: Add move history from gameManager */}
              <p>Move history will appear here</p>
            </div>
          </div>
          
          {/* Game Controls */}
          <div className="game-controls">
            <button 
              className="control-btn new-game-btn" 
              onClick={handleNewGame}
              disabled={gameMode === "online" || !gameManager || !gameReady}
            >
              🔄 New Game
            </button>
            
            <button 
              className="control-btn draw-btn" 
              onClick={handleDrawOffer}
              disabled={gameStatus !== 'active' || !gameManager || !gameReady}
            >
              🤝 Offer Draw
            </button>
            
            <button 
              className="control-btn resign-btn" 
              onClick={handleResign}
              disabled={gameStatus !== 'active' || !gameManager || !gameReady}
            >
              🏳️ Resign
            </button>
            <div>
              Your color is: <strong>{playerColor.toUpperCase()}</strong>
            </div>
            
          </div>

          {/* Debug Info (remove in production) */}
          {process.env.NODE_ENV === 'development' && (
            <div className="debug-info">
              <h4>Debug Info</h4>
              <p>Selected: {selectedSquare ? `${selectedSquare[0]},${selectedSquare[1]}` : 'None'}</p>
              <p>Possible Moves: {possibleMoves.length}</p>
              <p>Game Manager: {gameManager ? '✅' : '❌'}</p>
              <p>Game Ready: {gameReady ? '✅' : '❌'}</p>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
