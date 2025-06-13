import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";
import "./GameHistory.css";

export default function GameHistory() {
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Mock data for development
  const mockGames = [
    {
      gameId: "game_001",
      opponent: {
        name: "ChessMaster2024",
        email: "master@chess.com"
      },
      userColor: "white",
      result: "win",
      endReason: "checkmate",
      date: "2024-01-15T14:30:00Z",
      duration: 1530, // 25 minutes 30 seconds
      totalMoves: 42
    },
    {
      gameId: "game_002", 
      opponent: {
        name: "RookiePlayer",
        email: "rookie@example.com"
      },
      userColor: "black",
      result: "loss",
      endReason: "resignation",
      date: "2024-01-14T10:15:00Z",
      duration: 890, // 14 minutes 50 seconds
      totalMoves: 28
    },
    {
      gameId: "game_003",
      opponent: {
        name: "DrawMaster",
        email: "draw@chess.com"
      },
      userColor: "white", 
      result: "draw",
      endReason: "stalemate",
      date: "2024-01-13T16:45:00Z",
      duration: 2100, // 35 minutes
      totalMoves: 67
    },
    {
      gameId: "game_004",
      opponent: {
        name: "QuickPlayer",
        email: "quick@speed.com"
      },
      userColor: "black",
      result: "win",
      endReason: "timeout",
      date: "2024-01-12T09:20:00Z", 
      duration: 600, // 10 minutes
      totalMoves: 35
    },
    {
      gameId: "game_005",
      opponent: {
        name: "TacticalGenius",
        email: "tactics@chess.org"
      },
      userColor: "white",
      result: "loss",
      endReason: "checkmate",
      date: "2024-01-11T20:10:00Z",
      duration: 1800, // 30 minutes
      totalMoves: 55
    }
  ];

  useEffect(() => {
    // Simulate API call with mock data
    const fetchGames = async () => {
      try {
        setLoading(true);
        
        
        const response = await fetch(`/api/games/results/${currentUser.email}`)        // const gamesData = await response.json();
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
      
        const gamesData = await response.json();
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        setGames(gamesData);
        setError(null);
      } catch (err) {
        console.error("Error fetching games:", err);
        setError("Failed to load game history");
      } finally {
        setLoading(false);
      }
    };

    if (currentUser?.email) {
      fetchGames();
    }
  }, [currentUser]);

  const handleBackToHome = () => {
    navigate('/home');
  };

  const handleGameClick = (gameId) => {
    console.log(`🎮 Opening replay for game: ${gameId}`);
    navigate(`/replay/${gameId}`);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatDuration = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const getResultIcon = (result) => {
    switch (result) {
      case 'win': return '🏆';
      case 'loss': return '❌';
      case 'draw': return '🤝';
      default: return '❓';
    }
  };

  const getResultClass = (result) => {
    switch (result) {
      case 'win': return 'result-win';
      case 'loss': return 'result-loss';
      case 'draw': return 'result-draw';
      default: return 'result-unknown';
    }
  };

  const getColorIcon = (color) => {
    return color === 'white' ? '⚪' : '⚫';
  };

  if (loading) {
    return (
      <div className="game-history-container">
        <header className="game-history-header">
          <h1>My Games</h1>
          <button className="back-btn" onClick={handleBackToHome}>
            Back to Home
          </button>
        </header>
        <div className="loading-container">
          <div className="loading-spinner">⏳</div>
          <p>Loading your game history...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="game-history-container">
        <header className="game-history-header">
          <h1>My Games</h1>
          <button className="back-btn" onClick={handleBackToHome}>
            Back to Home
          </button>
        </header>
        <div className="error-container">
          <p className="error-message">❌ {error}</p>
          <button className="retry-btn" onClick={() => window.location.reload()}>
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="game-history-container">
      <header className="game-history-header">
        <h1>My Games</h1>
        <div className="header-info">
          <span className="games-count">
            {games.length} completed game{games.length !== 1 ? 's' : ''}
          </span>
          <button className="back-btn" onClick={handleBackToHome}>
            Back to Home
          </button>
        </div>
      </header>

      <main className="game-history-main">
        {games.length === 0 ? (
          <div className="no-games-container">
            <h2>No Games Yet</h2>
            <p>You haven't completed any games yet.</p>
            <button className="play-now-btn" onClick={() => navigate('/online')}>
              Play Your First Game
            </button>
          </div>
        ) : (
          <div className="games-list">
            {games.map((game) => (
              <div 
                key={game.gameId} 
                className="game-card"
                onClick={() => handleGameClick(game.gameId)}
              >
                <div className="game-card-header">
                  <div className="game-result">
                    <span className={`result-badge ${getResultClass(game.result)}`}>
                      {getResultIcon(game.result)} {game.result.toUpperCase()}
                    </span>
                  </div>
                  <div className="game-date">
                    {formatDate(game.date)}
                  </div>
                </div>

                <div className="game-card-body">
                  <div className="opponent-info">
                    <h3 className="opponent-name">vs {game.opponent.name}</h3>
                    <p className="opponent-email">{game.opponent.email}</p>
                  </div>

                  <div className="game-details">
                    <div className="detail-item">
                      <span className="detail-label">Your Color:</span>
                      <span className="detail-value">
                        {getColorIcon(game.userColor)} {game.userColor.toUpperCase()}
                      </span>
                    </div>
                    
                    <div className="detail-item">
                      <span className="detail-label">End Reason:</span>
                      <span className="detail-value">{game.endReason}</span>
                    </div>
                    
                    <div className="detail-item">
                      <span className="detail-label">Duration:</span>
                      <span className="detail-value">{formatDuration(game.duration)}</span>
                    </div>
                    
                    <div className="detail-item">
                      <span className="detail-label">Moves:</span>
                      <span className="detail-value">{game.totalMoves}</span>
                    </div>
                  </div>
                </div>

                <div className="game-card-footer">
                  <span className="replay-hint">Click to replay game →</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
