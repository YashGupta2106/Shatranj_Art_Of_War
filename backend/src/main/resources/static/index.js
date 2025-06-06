import React from "react";
import ReactDOM from "react-dom/client";
import LoginRegister from "./LoginRegister";
import "./index.css"; // Optional: if you want to include some global styles

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <LoginRegister />
  </React.StrictMode>
);
