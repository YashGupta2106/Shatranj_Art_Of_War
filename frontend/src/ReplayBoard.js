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
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [evaluationCache, setEvaluationCache] = useState(new Map()); // Cache evaluations

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

  useEffect(() => {
    if (gameData && gameData.moves && board && !isAnalyzing) {
      const currentFEN = getBoardFEN(board, currentMoveIndex);
      analyzePosition(currentFEN);
    }
  }, [currentMoveIndex]); // ✅ Only analyze when move changes

  // Convert row, col to chess notation
  const getSquareNotation = (row, col) => {
    const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    const ranks = ['1', '2', '3', '4', '5', '6', '7', '8'];
    return files[col] + ranks[row];
  };


  const getBoardFEN = (board, moveNumber) => {
    // This is a simplified FEN generator - you might need a more complete one
    let fen = '';
    
    // Convert board to FEN notation
    for (let row = 7; row >= 0; row--) {
      let emptyCount = 0;
      for (let col = 0; col < 8; col++) {
        const piece = board[row][col];
        if (piece) {
          if (emptyCount > 0) {
            fen += emptyCount;
            emptyCount = 0;
          }
          // Convert piece to FEN notation
          let pieceChar = piece.type.charAt(0);
          if (piece.type === 'knight') pieceChar = 'n';
          if (piece.color === 'white') pieceChar = pieceChar.toUpperCase();
          fen += pieceChar;
        } else {
          emptyCount++;
        }
      }
      if (emptyCount > 0) fen += emptyCount;
      if (row > 0) fen += '/';
    }
    
    // Add additional FEN components (simplified)
    const activeColor = moveNumber % 2 === 0 ? 'w' : 'b';
    fen += ` ${activeColor} KQkq - 0 ${Math.floor(moveNumber / 2) + 1}`;
    
    return fen;
  };


  // Function to call Stockfish API
  const analyzePosition = async (fen) => {
    try {
      setIsAnalyzing(true);
      
      // Check cache first
      if (evaluationCache.has(fen)) {
        const cachedResult = evaluationCache.get(fen);
        setCurrentEvaluation(cachedResult.evaluation);
        setIsMatePosition(cachedResult.isMate);
        setMateInMoves(cachedResult.mateIn);
        setIsAnalyzing(false);
        return;
      }

      console.log(`🔍 Analyzing position: ${fen}`);
      
      const response = await fetch('http://localhost:8080/api/games/stockfish', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ fen: fen })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.text();
      console.log('📊 Stockfish response:', result);
      
      // Parse Stockfish response
      const evaluation = parseStockfishResponse(result);
      
      // Cache the result
      evaluationCache.set(fen, evaluation);
      setEvaluationCache(new Map(evaluationCache));
      
      // Update state
      setCurrentEvaluation(evaluation.evaluation);
      setIsMatePosition(evaluation.isMate);
      setMateInMoves(evaluation.mateIn);
      
    } catch (error) {
      console.error('❌ Error analyzing position:', error);
      // Fallback to neutral evaluation
      setCurrentEvaluation(0);
      setIsMatePosition(false);
      setMateInMoves(0);
    } finally {
      setIsAnalyzing(false);
    }
  };

const parseStockfishResponse = (response) => {
  try {
    console.log('🔍 Raw Stockfish response:', response);
    
    const data = JSON.parse(response);
    console.log('📊 Parsed data:', data);
    
    if (data.success && data.evaluation !== undefined) {
      const evalValue = parseFloat(data.evaluation);
      const mateValue = data.mate; // This will be null or a number
      
      console.log('🎯 Evaluation:', evalValue);
      console.log('♟️ Mate value:', mateValue, 'Type:', typeof mateValue);
      
      // ✅ Check for mate properly
      if (mateValue !== null && mateValue !== undefined) {
        console.log('🏁 Mate detected:', mateValue);
        return {
          evaluation: mateValue > 0 ? 1000 : -1000,
          isMate: true,
          mateIn: Math.abs(mateValue)
        };
      }
      
      // ✅ Regular evaluation (convert to centipawns)
      const centipawns = Math.round(evalValue * 100);
      console.log('📊 Regular evaluation:', centipawns, 'centipawns');
      
      return {
        evaluation: centipawns,
        isMate: false,
        mateIn: 0
      };
    }
    
    // Fallback
    console.log('⚠️ Invalid response format, using fallback');
    return { evaluation: 0, isMate: false, mateIn: 0 };
    
  } catch (error) {
    console.error('❌ Error parsing Stockfish response:', error);
    return { evaluation: 0, isMate: false, mateIn: 0 };
  }
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
          <span className="current-player">
            <strong>{gameData.players.white.name}</strong> (⚪) vs <strong>{gameData.players.black.name}</strong> (⚫)
          </span>
          
          {isAnalyzing && (
            <span className="analysis-status">
              🔍 Analyzing...
            </span>
          )}
          
          <button className="back-btn" onClick={handleBackToGames}>
            Back to My Games
          </button>
        </div>
      </header>

      {gameMessage && (
        <div className="game-message">
          {gameMessage}
          {isAnalyzing && <span className="analyzing-indicator"> • Analyzing position...</span>}
        </div>
      )}

      <main className="chess-main">
        <div className="eval-bar-section">
          <EvalBar 
            evaluation={currentEvaluation}
            isMate={isMatePosition}
            mateIn={mateInMoves}
            height={400}
            isAnalyzing={isAnalyzing}
          />
        </div>

        <div className="chess-board-container">
          <div className={`chess-board ${gameData.userColor === 'white' ? 'rotated' : ''}`}>
            {board.map((row, rowIndex) =>
              row.map((piece, colIndex) => (
                <div
                  key={`${rowIndex}-${colIndex}`}
                  className={`chess-square ${getSquareColor(rowIndex, colIndex)}`}
                >
                  <div className="square-notation">
                    {getSquareNotation(rowIndex, colIndex)}
                  </div>
                  
                  {renderPiece(piece)}
                </div>
              ))
            )}
          </div>
        </div>

        <div className="game-sidebar">
          <div className="move-history">
            <h3>Move Navigation</h3>
            <div className="move-counter">
              Move {currentMoveIndex} of {totalMoves}
              {isAnalyzing && <span className="analyzing-dot">🔍</span>}
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

          <div className="game-status">
            <h3>Game Information</h3>
            <p><strong>Result:</strong> {gameData.result.replace('_', ' ').toUpperCase()}</p>
            <p><strong>End Reason:</strong> {gameData.endReason}</p>
            <p><strong>Your Color:</strong> {gameData.userColor.toUpperCase()}</p>
            <p><strong>Total Moves:</strong> {totalMoves}</p>
            
            <div className="current-analysis">
              <h4>Current Position</h4>
              {isAnalyzing ? (
                <p>🔍 Analyzing...</p>
              ) : (
                <>
                  {isMatePosition ? (
                    <p><strong>Mate in {mateInMoves}</strong></p>
                  ) : (
                    <p><strong>Evaluation:</strong> {(currentEvaluation / 100).toFixed(1)}</p>
                  )}
                </>
              )}
            </div>
          </div>

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

              
