import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";
import "./GameHistory.css";
const API_BASE_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';




export default function GameHistory() {
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  

  useEffect(() => {
    const fetchGames = async () => {
      try {
        setLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/api/games/results`,{
          credentials:"include",
        })
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
      console.log("Fetching game history for user:", currentUser.email);
      fetchGames();
    }
    else{
      console.log("i am not able to see the user how do i fetch the games")
    }
  }, [currentUser]);

  const handleBackToHome = () => {
    navigate('/home');
  };


  const checkSessionValidity = async () => {
    try {
      const checkSession = await fetch(`${API_BASE_URL}/api/auth/sessionVerify`, {
        credentials: 'include',
      });
      if (!checkSession.ok) {
        alert("Session expired. Please log in again.");
        navigate('/login');
        return false; // session invalid
      }
      return true; // session valid
    } catch (err) {
      console.log("Error checking session validity:", err);
      navigate('/login');
      return false;
    }
  };

  const handleGameClick = async(gameId) => {
    const valid = await checkSessionValidity();
    if(valid===false){
      alert("Session expired. Please log in again.");
      navigate('/login');
      return;
    }
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
