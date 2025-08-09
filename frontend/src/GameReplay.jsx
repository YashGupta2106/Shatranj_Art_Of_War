import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";
import ReplayBoard from "./ReplayBoard";
import "./GameHistory.css";

const API_BASE_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';




export default function GameReplay() {
  const { gameId } = useParams();
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  
  const [gameData, setGameData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  
  useEffect(() => {
    const fetchGameData = async () => {
      try {
        setLoading(true);
        console.log(`🎮 Loading game replay for: ${gameId}`);
        
        const response = await fetch(`${API_BASE_URL}/api/games/replay/${gameId}`,{
          credentials:"include",
        });
        
        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('Game not found');
          } else if (response.status === 400) {
            throw new Error('Game is still active');
          } else {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
        }
        
        const gameReplayData = await response.json();

        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Check if this game belongs to current user
        const isUserGame = gameReplayData.players.white.email === currentUser?.email || 
                          gameReplayData.players.black.email === currentUser?.email;
        
        if (!isUserGame) {
          throw new Error('You do not have access to this game');
        }
        
        // Determine user's color in this game
        const userColor = gameReplayData.players.white.email === currentUser?.email ? 'white' : 'black';
        gameReplayData.userColor = userColor;
        
        setGameData(gameReplayData);
        setError(null);
        
      } catch (err) {
        console.error("Error fetching game data:", err);
        setError(err.message || "Failed to load game");
      } finally {
        setLoading(false);
      }
    };

    if (gameId && currentUser?.email) {
      fetchGameData();
    }
  }, [gameId, currentUser]);

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

  const handleBackToGames = async() => {
    const valid = await checkSessionValidity();
    if(valid===false){
      alert("Session expired. Please log in again.");
      navigate('/login');
      return;
    }
    navigate('/my-games');
  };

  const handleBackToHome = async() => {
    const valid = await checkSessionValidity();
    if(valid===false){
      alert("Session expired. Please log in again.");
      navigate('/login');
      return;
    }
    navigate('/home');
  };

  if (loading) {
    return (
      <div className="game-history-container">
        <div className="loading-container">
          <div className="loading-spinner">⏳</div>
          <h2>Loading Game Replay...</h2>
          <p>Game ID: {gameId}</p>
          <button className="back-btn" onClick={handleBackToGames}>
            Back to My Games
          </button>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="game-history-container">
        <div className="error-container">
          <h2>❌ Error Loading Game</h2>
          <p className="error-message">{error}</p>
          <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', marginTop: '20px' }}>
            <button className="retry-btn" onClick={() => window.location.reload()}>
              Try Again
            </button>
            <button className="back-btn" onClick={handleBackToGames}>
              Back to My Games
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!gameData) {
    return (
      <div className="game-history-container">
        <div className="error-container">
          <h2>Game Not Found</h2>
          <p>The requested game could not be found.</p>
          <button className="back-btn" onClick={handleBackToGames}>
            Back to My Games
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="game-history-container">
      <header className="game-history-header">
        <div>
          <h1>Game Replay</h1>
          <div className="games-count">
            ⚪ {gameData.players.white.name} vs ⚫ {gameData.players.black.name} • 
            Result: {gameData.result.replace('_', ' ').toUpperCase()}
          </div>
        </div>
        
        <div className="header-info">
          <button className="back-btn" onClick={handleBackToGames}>
            ← My Games
          </button>
          <button className="back-btn" onClick={handleBackToHome}>
            🏠 Home
          </button>
        </div>
      </header>

      <ReplayBoard gameData={gameData} />
    </div>
  );
}
