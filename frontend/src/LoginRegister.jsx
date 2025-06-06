import React, { useState } from "react";
import { auth } from "./firebase-config";
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "firebase/auth";
import { sendTokenToBackend } from "./authHelp";

export default function LoginRegister({ onLoginSuccess }) {
  const [isRegistering, setIsRegistering] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async () => {
    try {
      let userCredential;
      
      if (isRegistering) {
        userCredential = await createUserWithEmailAndPassword(auth, email, password);
        setMessage("Registered successfully!");
      } else {
        userCredential = await signInWithEmailAndPassword(auth, email, password);
        setMessage("Login successful!");
      }

      // Wait a bit for Firebase to fully set the current user
      setTimeout(async () => {
        try {
          await sendTokenToBackend();
          setMessage(prev => prev + " Backend verification successful!");
          
          // Pass the user object to parent component
          setTimeout(() => {
            onLoginSuccess(userCredential.user);
          }, 1000);
          
        } catch (backendError) {
          console.error("Backend verification failed:", backendError);
          setMessage(prev => prev + " Warning: Backend verification failed.");
          
          // Still redirect even if backend fails (for now)
          setTimeout(() => {
            onLoginSuccess(userCredential.user);
          }, 2000);
        }
      }, 1000);

    } catch (err) {
      console.error("Authentication error:", err);
      setMessage(err.message);
    }
  };

  return (
    <div className="login-container">
      <h2>{isRegistering ? "Register" : "Login"}</h2>
      <input
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={handleSubmit}>
        {isRegistering ? "Register" : "Login"}
      </button>
      <p>{message}</p>
      <p>
        {isRegistering ? "Already have an account?" : "Don't have an account?"}
        <button onClick={() => setIsRegistering(!isRegistering)}>
          {isRegistering ? "Login" : "Register"}
        </button>
      </p>
    </div>
  );
}
