import React, { useState, useEffect } from "react";
import ReactDOM from "react-dom/client";
import { onAuthStateChanged } from "firebase/auth";
import { auth } from "./firebase-config";
import LoginRegister from "./LoginRegister";
import HomePage from "./HomePage";
import ChessBoard from "./ChessBoard";
import "./index.css";

function App() {
  const [currentPage, setCurrentPage] = useState('login'); // 'login', 'home', 'practice', 'online'
  const [user, setUser] = useState(null); // Track authenticated user
  const [loading, setLoading] = useState(true); // Loading state for auth check

  // Listen for authentication state changes
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      console.log("Auth state changed:", user?.email || "No user");
      setUser(user);
      setLoading(false);
      
      // If user is logged out, redirect to login
      if (!user && currentPage !== 'login') {
        setCurrentPage('login');
      }
    });

    return () => unsubscribe();
  }, [currentPage]);

  const handleLoginSuccess = (user) => {
    console.log("Login successful for:", user?.email);
    setUser(user);
    setCurrentPage('home');
  };

  const handleLogout = () => {
    setUser(null);
    setCurrentPage('login');
  };

  const handleNavigateToPractice = () => {
    setCurrentPage('practice');
  };

  const handleNavigateToOnline = () => {
    setCurrentPage('online');
  };

  const handleBackToHome = () => {
    setCurrentPage('home');
  };

  // Show loading while checking auth state
  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh' 
      }}>
        <p>Loading...</p>
      </div>
    );
  }

  const renderCurrentPage = () => {
    switch (currentPage) {
      case 'login':
        return <LoginRegister onLoginSuccess={handleLoginSuccess} />;
      
      case 'home':
        return (
          <HomePage 
            user={user}
            onLogout={handleLogout} 
            onNavigateToPractice={handleNavigateToPractice}
            onNavigateToOnline={handleNavigateToOnline}
          />
        );
      
      case 'practice':
        return (
          <ChessBoard 
            user={user}
            onBackToHome={handleBackToHome} 
            gameMode="practice" 
          />
        );

      case 'online':
        return (
          <ChessBoard 
            user={user}
            onBackToHome={handleBackToHome} 
            gameMode="online" 
          />
        );
      
      default:
        return <LoginRegister onLoginSuccess={handleLoginSuccess} />;
    }
  };

  return (
    <div>
      {renderCurrentPage()}
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
