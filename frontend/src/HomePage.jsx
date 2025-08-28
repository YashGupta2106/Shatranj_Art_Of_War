import React from "react";
import { useNavigate } from "react-router-dom";
import { signOut } from "firebase/auth";
import { auth } from "./firebase-config";
import { useAuth } from "./AuthContext";
import "./HomePage.css";
const API_BASE_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';

export default function HomePage() {

  const navigate = useNavigate();
  const { currentUser } = useAuth();

  // const handlePracticeMode = () => {
  //   console.log("Practice Mode clicked");
  //   navigate('/practice');
  // };

  const checkSessionValidity = async () => {
    try {
      console.log("Checking session validity... by sending a request to backend");
      const checkSession = await fetch(`${API_BASE_URL}/api/auth/sessionVerify`, {
        credentials: 'include',
      });

      if (!checkSession.ok) {
        alert("Session expired. Please log in again.");
        navigate('/login');
        return false; // session invalid
      }

      return true; // session valid
    } 
    catch (err) {
      console.log("Error checking session validity:", err);
      navigate('/login');
      return false;
    }
  };

  const handlePlayOnline = async() => {
    const valid= await checkSessionValidity();
    if (valid===false) return;
    console.log("Play Online clicked");
    navigate('/online');
  };

  const handleMyGames = async() => {
    console.log("Checking session validity before navigating to My Games");
    const valid= await checkSessionValidity();
    if (valid===false) return;
    console.log("My Games clicked");
    
    navigate('/my-games');
    
  };

  const handleLogout = async () => {
    try {
      await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: 'POST',
        credentials: 'include', // so browser sends the cookie
      });
      
      await signOut(auth);
      console.log("User logged out successfully");
      navigate('/login'); // Redirect to login page after logout
    } 
    catch (error) {
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
          {/* <button className="mode-btn practice-btn" onClick={handlePracticeMode}>
            <div className="btn-icon">🎯</div>
            <h3>Practice Mode</h3>
            <p>Sharpen your skills against AI</p>
          </button> */}

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
