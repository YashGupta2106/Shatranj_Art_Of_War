import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
  import { useAuth } from './AuthContext';

const ProtectedRoute = ({ children }) => {
  const { currentUser, loading } = useAuth();
  const location = useLocation();
  console.log("ProtectedRoute currentUser:", currentUser);
  if (loading) {
    console.log("load hi ho raha hai abhi toh")
    return (
      <div className="loading-spinner">
        <div>Loading...</div>
      </div>
    );
  }

  if (!currentUser) {
    console.log("nope i dont see any user")
    // Redirect to login but save the attempted location
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

export default ProtectedRoute;
