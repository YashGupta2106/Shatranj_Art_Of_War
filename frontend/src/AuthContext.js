import React, { createContext, useContext, useEffect, useState } from 'react';
import { onAuthStateChanged } from 'firebase/auth';
import { auth } from './firebase-config';
const API_BASE_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';

const AuthContext = createContext();

export const useAuth = () => {
  return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      console.log('Auth state changed:', user?.email || 'No user');
      setCurrentUser(user);
      setLoading(false);
    });

    return unsubscribe;
  }, []);

  const value = {
    currentUser,
    loading
  };

  
  useEffect(()=>{
    const checkSession=async()=>{
      try{
        // here i will make the call to backend to look for any cookie session existing
        const data=await fetch(`${API_BASE_URL}/api/auth/login`,{
          credentials: 'include',
        })
        const response = await data.json();
        if(data.ok){
          console.log("i got the mail as ",response.userMail);
          setCurrentUser(response.userMail);
          setLoading(false);
        }
        else{
          throw new Error(response.error);
        }
      }
      catch(err){
        console.log(err);
      }
      checkSession();
  };

  },[]);

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
