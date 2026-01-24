import React, { useState } from "react";
import { auth } from "./firebase-config";
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "firebase/auth";
import { sendTokenToBackend } from "./authHelp";
import { useNavigate, useLocation } from "react-router-dom";
import './LoginRegister.css';

export default function LoginRegister() {

  const navigate = useNavigate();
  const location = useLocation();

  const [isRegistering, setIsRegistering] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState(""); // success, error, warning
  const [isLoading, setIsLoading] = useState(false);



  // Get the page user was trying to access, default to /home
  const from = location.state?.from?.pathname || '/home';

  const handleSubmit = async () => {
    if (!email || !password) {
      setMessage("Please fill in all fields");
      setMessageType("error");
      return;
    }

    setIsLoading(true);
    setMessage("");

    try {
      let userCredential;

      if (isRegistering) {
        userCredential = await createUserWithEmailAndPassword(auth, email, password);
        setMessage("Registered successfully!");
        setMessageType("success");
      }
      else {
        userCredential = await signInWithEmailAndPassword(auth, email, password);
        setMessage("Login successful!");
        setMessageType("success");
      }

      // Wait a bit for Firebase to fully set the current user
      setTimeout(async () => {
        try {
          const response = await sendTokenToBackend();
          const txt = await response.json();

          if (response.status === 200) {
            setMessage(prev => prev + " Backend verification successful!");
            setMessageType("success");
            console.log("Backend response:", txt.message);
            setTimeout(() => {
              navigate(from, { replace: true });
            }, 1000);
          } else {
            throw new Error(txt.message);
          }

        } catch (backendError) {
          console.error("Backend verification failed:", backendError);
          setMessage(prev => prev + " Warning: Backend verification failed.");
          setMessageType("warning");
        } finally {
          setIsLoading(false);
        }
      }, 1000);

    }
    catch (err) {
      console.error("Authentication error:", err);
      setMessage(err.message);
      setMessageType("error");
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSubmit();
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="chess-icon">♔</div>
        <h2>{isRegistering ? "Create Account" : "Welcome Back"}</h2>

        <div className="input-group">
          <label>Email Address</label>
          <input
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyPress={handleKeyPress}
            type="email"
            disabled={isLoading}
          />
          <span className="input-icon">✉</span>
        </div>

        <div className="input-group">
          <label>Password</label>
          <input
            placeholder="Enter your password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={isLoading}
          />
          <span className="input-icon">🔒</span>
        </div>

        <button
          className={`btn-primary ${isLoading ? 'loading' : ''}`}
          onClick={handleSubmit}
          disabled={isLoading}
        >
          {isRegistering ? "Create Account" : "Sign In"}
        </button>

        {message && (
          <div className={`message ${messageType}`}>
            {message}
          </div>
        )}

        <div className="toggle-auth">
          {isRegistering ? "Already have an account?" : "Don't have an account?"}
          <button
            className="btn-secondary"
            onClick={() => {
              setIsRegistering(!isRegistering);
              setMessage("");
              setMessageType("");
            }}
            disabled={isLoading}
          >
            {isRegistering ? "Sign In" : "Create Account"}
          </button>
        </div>
      </div>
    </div>
  );
}
