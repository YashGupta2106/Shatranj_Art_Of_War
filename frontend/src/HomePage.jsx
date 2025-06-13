import React from "react";
import { useNavigate } from "react-router-dom"; // ADD THIS
import { signOut } from "firebase/auth";
import { auth } from "./firebase-config";
import { useAuth } from "./AuthContext"; // ADD THIS
import "./HomePage.css";


export default function HomePage() {

  const navigate = useNavigate(); // ADD THIS
  const { currentUser } = useAuth(); // ADD THIS

  const handlePracticeMode = () => {
    console.log("Practice Mode clicked");
    navigate('/practice'); // CHANGE: use navigate instead of callback
  };

  const handlePlayOnline = () => {
    console.log("Play Online clicked");
    navigate('/online'); // CHANGE: use navigate instead of callback
  };

  const handleMyGames = () => {
    console.log("My Games clicked");
    // TODO: Navigate to game history
    
    navigate('/my-games');
    
  };

  const handleLogout = async () => {
    try {
      await signOut(auth);
      console.log("User logged out successfully");
      navigate('/login'); // Redirect to login page after logout
    } catch (error) {
      console.error("Logout error:", error);
    }
  };

  return (
    <div className="homepage-container">
      <header className="homepage-header">
        <h1>Shatranj - Art of War</h1>
        <div className="user-info">
          <span>Welcome, {currentUser?.email || 'Player'}!</span>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="homepage-main">
        <div className="welcome-section">
          <h2>Welcome, Chess Master!</h2>
          <p>Choose your battle</p>
        </div>

        <div className="game-modes">
          <button className="mode-btn practice-btn" onClick={handlePracticeMode}>
            <div className="btn-icon">🎯</div>
            <h3>Practice Mode</h3>
            <p>Sharpen your skills against AI</p>
          </button>

          <button className="mode-btn online-btn" onClick={handlePlayOnline}>
            <div className="btn-icon">⚔️</div>
            <h3>Play Online</h3>
            <p>Challenge players worldwide</p>
          </button>

          <button className="mode-btn games-btn" onClick={handleMyGames}>
            <div className="btn-icon">📚</div>
            <h3>My Games</h3>
            <p>Review your game history</p>
          </button>
        </div>
      </main>
    </div>
  );
}
