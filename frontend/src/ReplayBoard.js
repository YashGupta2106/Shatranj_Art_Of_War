import React, { useState, useEffect } from "react";
import "./ChessBoard.css"; // Reuse existing chess board styles
import { useNavigate } from "react-router-dom";
import EvalBar from "./EvalBar"; // for eval bar



// Chess piece Unicode symbols (same as ChessBoard)
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

// Initial board setup (same as ChessBoard)
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

const getRowColFromNotation = (notation) => {
  const col = notation.charCodeAt(0) - 'a'.charCodeAt(0);
  const row = parseInt(notation[1]) - 1;
  return [row, col];
};

const createPiece = (type, color) => {
  return { type, color };
};

export default function ReplayBoard({ gameData }) {
  const navigate = useNavigate();

  // UI State for replay
  const [board, setBoard] = useState(createInitialBoard());
  const [currentMoveIndex, setCurrentMoveIndex] = useState(0); // 0 = initial position
  const [currentPlayer, setCurrentPlayer] = useState('white');
  const [gameMessage, setGameMessage] = useState('');
  const [totalMoves, setTotalMoves] = useState(0);

  // for eval bar
  const [currentEvaluation, setCurrentEvaluation] = useState(0);
  const [isMatePosition, setIsMatePosition] = useState(false);
  const [mateInMoves, setMateInMoves] = useState(0);

  // Initialize replay data
  useEffect(() => {
    if (gameData && gameData.moves) {
      setTotalMoves(gameData.moves.length);
      setGameMessage(`Game Replay - Move 0 of ${gameData.moves.length} (Starting Position)`);
      
      // Set initial board
      setBoard(createInitialBoard());
      setCurrentPlayer('white');
      setCurrentMoveIndex(0);

      const evaluation =0;
      setCurrentEvaluation(evaluation);
      if (Math.abs(evaluation) > 500) {
        setIsMatePosition(true);
        setMateInMoves(Math.ceil((1000 - Math.abs(evaluation)) / 100));
      } else {
        setIsMatePosition(false);
        setMateInMoves(0);
      }
    
    }
  }, [gameData]);

  // Convert row, col to chess notation
  const getSquareNotation = (row, col) => {
    const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    const ranks = ['1', '2', '3', '4', '5', '6', '7', '8'];
    return files[col] + ranks[row];
  };

  // Apply a move to the board
  const applyMoveToBoard = (board, move, forward = true) => {
    const newBoard = board.map(row => [...row]); // Deep copy
    
    if (!move) return newBoard;

    const [fromRow, fromCol] = getRowColFromNotation(move.from_sq);
    const [toRow, toCol] = getRowColFromNotation(move.to_sq);

    if (forward) {
      // Apply move forward
      if (move.castling) {
        // Handle castling
        if (move.to_sq === "c1" || move.to_sq === "c8") {
          // Queen-side castling
          const kingPiece = newBoard[fromRow][fromCol];
          const rookPiece = newBoard[fromRow][0];
          newBoard[fromRow][fromCol] = null;
          newBoard[fromRow][0] = null;
          newBoard[fromRow][2] = kingPiece;
          newBoard[fromRow][3] = rookPiece;
        } else if (move.to_sq === "g1" || move.to_sq === "g8") {
          // King-side castling
          const kingPiece = newBoard[fromRow][fromCol];
          const rookPiece = newBoard[fromRow][7];
          newBoard[fromRow][fromCol] = null;
          newBoard[fromRow][7] = null;
          newBoard[fromRow][6] = kingPiece;
          newBoard[fromRow][5] = rookPiece;
        }
      } else if (move.promotion) {
        // Handle promotion
        newBoard[fromRow][fromCol] = null;
        const promotionColor = move.moveNumber % 2 === 1 ? 'white' : 'black';
        newBoard[toRow][toCol] = createPiece(move.promotionPiece, promotionColor);
      } else if (move.enPassant) {
        // Handle en passant
        const [captureRow, captureCol] = getRowColFromNotation(move.squareCaptured);
        newBoard[captureRow][captureCol] = null;
        newBoard[toRow][toCol] = newBoard[fromRow][fromCol];
        newBoard[fromRow][fromCol] = null;
      } else {
        // Normal move
        newBoard[toRow][toCol] = newBoard[fromRow][fromCol];
        newBoard[fromRow][fromCol] = null;
      }
    } else {
      // Reverse move (going backward)
      if (move.castling) {
        // Reverse castling
        if (move.to_sq === "c1" || move.to_sq === "c8") {
          // Reverse queen-side castling
          const kingPiece = newBoard[toRow][2];
          const rookPiece = newBoard[toRow][3];
          newBoard[toRow][2] = null;
          newBoard[toRow][3] = null;
          newBoard[fromRow][fromCol] = kingPiece;
          newBoard[fromRow][0] = rookPiece;
        } else if (move.to_sq === "g1" || move.to_sq === "g8") {
          // Reverse king-side castling
          const kingPiece = newBoard[toRow][6];
          const rookPiece = newBoard[toRow][5];
          newBoard[toRow][6] = null;
          newBoard[toRow][5] = null;
          newBoard[fromRow][fromCol] = kingPiece;
          newBoard[fromRow][7] = rookPiece;
        }
      } else if (move.promotion) {
        // Reverse promotion
        newBoard[toRow][toCol] = null;
        const pawnColor = move.moveNumber % 2 === 1 ? 'white' : 'black';
        newBoard[fromRow][fromCol] = createPiece('pawn', pawnColor);
        
        // Restore captured piece if any
        if (move.pieceCaptured !== "no") {
          const capturedColor = move.moveNumber % 2 === 1 ? 'black' : 'white';
          newBoard[toRow][toCol] = createPiece(move.pieceCaptured.toLowerCase(), capturedColor);
        }
      } else if (move.enPassant) {
        // Reverse en passant
        const [captureRow, captureCol] = getRowColFromNotation(move.squareCaptured);
        newBoard[fromRow][fromCol] = newBoard[toRow][toCol];
        newBoard[toRow][toCol] = null;
        const capturedPawnColor = move.moveNumber % 2 === 1 ? 'black' : 'white';
        newBoard[captureRow][captureCol] = createPiece('pawn', capturedPawnColor);
      } else {
        // Reverse normal move
        newBoard[fromRow][fromCol] = newBoard[toRow][toCol];
        newBoard[toRow][toCol] = null;
        
        // Restore captured piece if any
        if (move.pieceCaptured !== "no") {
          const capturedColor = move.moveNumber % 2 === 1 ? 'black' : 'white';
          newBoard[toRow][toCol] = createPiece(move.pieceCaptured.toLowerCase(), capturedColor);
        }
      }
    }

    return newBoard;
  };

  // Navigate to specific move
  const navigateToMove = (targetMoveIndex) => {
    if (!gameData || !gameData.moves) return;
    
    // Clamp the target index
    const clampedIndex = Math.max(0, Math.min(targetMoveIndex, totalMoves));
    
    if (clampedIndex === currentMoveIndex) return;

    let newBoard = createInitialBoard();
    
    // Apply all moves up to the target index
    for (let i = 0; i < clampedIndex; i++) {
      const move = gameData.moves[i];
      newBoard = applyMoveToBoard(newBoard, move, true);
    }

    setBoard(newBoard);
    setCurrentMoveIndex(clampedIndex);
    
    // Update current player
    const newCurrentPlayer = clampedIndex % 2 === 0 ? 'white' : 'black';
    setCurrentPlayer(newCurrentPlayer);
    
    // Update message
    if (clampedIndex === 0) {
      setGameMessage(`Game Replay - Starting Position`);
    } else {
      const move = gameData.moves[clampedIndex - 1];
      setGameMessage(`Game Replay - Move ${clampedIndex} of ${totalMoves}: ${move.from_sq} → ${move.to_sq}`);
    }
  };

  // Navigation functions
  const showPreviousMove = () => {
    navigateToMove(currentMoveIndex - 1);
  };

  const showNextMove = () => {
    navigateToMove(currentMoveIndex + 1);
  };

  const goToStart = () => {
    navigateToMove(0);
  };

  const goToEnd = () => {
    navigateToMove(totalMoves);
  };

  // Keyboard event listener - INSIDE the component
  useEffect(() => {
    const handleKeyPress = (event) => {
      switch (event.key) {
        case 'ArrowLeft':
          event.preventDefault();
          showPreviousMove();
          break;
        case 'ArrowRight':
          event.preventDefault();
          showNextMove();
          break;
        case 'Home':
          event.preventDefault();
          goToStart();
          break;
        case 'End':
          event.preventDefault();
          goToEnd();
          break;
        default:
          break;
      }
    };

    // Add event listener
    window.addEventListener('keydown', handleKeyPress);

    // Cleanup function
    return () => {
      window.removeEventListener('keydown', handleKeyPress);
    };
  }, [currentMoveIndex, totalMoves]); // Dependencies for the keyboard handlers

  // Render a chess piece
  const renderPiece = (piece) => {
    if (!piece) return null;
    return (
      <span className={`chess-piece ${piece.color}`}>
        {PIECES[piece.color][piece.type]}
      </span>
    );
  };

  // Get square color (alternating light/dark)
  const getSquareColor = (row, col) => {
    return (row + col) % 2 === 0 ? 'light' : 'dark';
  };

  const handleBackToGames = () => {
    navigate('/my-games');
  };

  if (!gameData) {
    return (
      <div className="chess-game-container">
        <div className="loading-container">
          <p>Loading game data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="chess-game-container">
      <header className="chess-header">
        <h1>Game Replay</h1>
        <div className="game-info">
          {/* Game Info */}
          <span className="current-player">
            <strong>{gameData.players.white.name}</strong> (⚪) vs <strong>{gameData.players.black.name}</strong> (⚫)
          </span>
          
          {/* Back Button */}
          <button className="back-btn" onClick={handleBackToGames}>
            Back to My Games
          </button>
        </div>
      </header>

      

      {/* Game Message Display */}
      {gameMessage && (
        <div className="game-message">
          {gameMessage}
        </div>
      )}

      <main className="chess-main">
        {/* Chess Board */}
        <div className="chess-board-container">
            <div className="eval-bar-section">
              <EvalBar 
                evaluation={currentEvaluation}
                isMate={isMatePosition}
                mateIn={mateInMoves}
                height={400}
              />
            </div>
          <div className={`chess-board ${gameData.userColor === 'white' ? 'rotated' : ''}`}>
            {board.map((row, rowIndex) =>
              row.map((piece, colIndex) => (
                <div
                  key={`${rowIndex}-${colIndex}`}
                  className={`chess-square ${getSquareColor(rowIndex, colIndex)}`}
                >
                  {/* Square coordinates for debugging */}
                  <div className="square-notation">
                    {getSquareNotation(rowIndex, colIndex)}
                  </div>
                  
                  {/* Chess Piece */}
                  {renderPiece(piece)}
                </div>
              ))
            )}
          </div>
        </div>

        {/* Replay Controls Sidebar */}
        <div className="game-sidebar">
          {/* Move Navigation */}
          <div className="move-history">
            <h3>Move Navigation</h3>
            <div className="move-counter">
              Move {currentMoveIndex} of {totalMoves}
            </div>
            
            <div className="replay-controls">
              <button 
                className="control-btn" 
                onClick={goToStart}
                disabled={currentMoveIndex === 0}
                title="Go to start"
              >
                ⏮⏮
              </button>
              
              <button 
                className="control-btn" 
                onClick={showPreviousMove}
                disabled={currentMoveIndex === 0}
                title="Previous move"
              >
                ⏮
              </button>
              
              <button 
                className="control-btn" 
                onClick={showNextMove}
                disabled={currentMoveIndex >= totalMoves}
                title="Next move"
              >
                ⏭
              </button>
                            <button 
                className="control-btn" 
                onClick={goToEnd}
                disabled={currentMoveIndex >= totalMoves}
                title="Go to end"
              >
                ⏭⏭
              </button>
            </div>
          </div>

          {/* Game Information */}
          <div className="game-status">
            <h3>Game Information</h3>
            <p><strong>Result:</strong> {gameData.result.replace('_', ' ').toUpperCase()}</p>
            <p><strong>End Reason:</strong> {gameData.endReason}</p>
            <p><strong>Your Color:</strong> {gameData.userColor.toUpperCase()}</p>
            <p><strong>Total Moves:</strong> {totalMoves}</p>
          </div>

          {/* Move List */}
          <div className="move-list">
            <h3>Move History</h3>
            <div className="moves-list-container">
              {gameData.moves && gameData.moves.length > 0 ? (
                <div className="moves-grid">
                  {gameData.moves.map((move, index) => (
                    <div 
                      key={index}
                      className={`move-item ${index + 1 === currentMoveIndex ? 'current-move' : ''}`}
                      onClick={() => navigateToMove(index + 1)}
                    >
                      <span className="move-number">{index + 1}.</span>
                      <span className="move-notation">
                        {move.from_sq} → {move.to_sq}
                      </span>
                      {move.pieceCaptured !== "no" && (
                        <span className="capture-indicator">×</span>
                      )}
                      {move.promotion && (
                        <span className="promotion-indicator">={move.promotionPiece}</span>
                      )}
                      {move.castling && (
                        <span className="castle-indicator">O-O</span>
                      )}
                    </div>
                  ))}
                </div>
              ) : (
                <p>No moves available</p>
              )}
            </div>
          </div>

          {/* Keyboard Shortcuts Info */}
          <div className="keyboard-shortcuts">
            <h4>Keyboard Shortcuts</h4>
            <div className="shortcuts-list">
              <div className="shortcut-item">
                <kbd>←</kbd> Previous move
              </div>
              <div className="shortcut-item">
                <kbd>→</kbd> Next move
              </div>
              <div className="shortcut-item">
                <kbd>Home</kbd> Go to start
              </div>
              <div className="shortcut-item">
                <kbd>End</kbd> Go to end
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

              
