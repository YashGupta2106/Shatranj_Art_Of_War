import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./AuthContext";
import ProtectedRoute from "./ProtectedRoute";
import LoginRegister from "./LoginRegister";
import HomePage from "./HomePage";
import ChessBoard from "./ChessBoard";
import "./index.css";

import GameHistory from "./GameHistory";
import GameReplay from "./GameReplay";

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginRegister />} />
            
            {/* Protected Routes */}
            <Route path="/home" element={
              <ProtectedRoute>
                <HomePage />
              </ProtectedRoute>
            } />
            
            <Route path="/practice" element={
              <ProtectedRoute>
                <ChessBoard gameMode="practice" />
              </ProtectedRoute>
            } />
            
            <Route path="/online" element={
              <ProtectedRoute>
                <ChessBoard gameMode="online" />
              </ProtectedRoute>
            } />

            <Route path="/my-games" element={<GameHistory />} />
            
            <Route path="/replay/:gameId" element={
              <ProtectedRoute>
                <GameReplay />
              </ProtectedRoute>
            } />

            
            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/home" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
