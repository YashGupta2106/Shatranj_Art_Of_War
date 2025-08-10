// import React, { createContext, useContext, useEffect, useState } from 'react';
// import { onAuthStateChanged } from 'firebase/auth';
// import { auth } from './firebase-config';
// const API_BASE_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';

// const AuthContext = createContext();

// export const useAuth = () => {
//   return useContext(AuthContext);
// };

// export const AuthProvider = ({ children }) => {
//   const [currentUser, setCurrentUser] = useState(null);
//   const [loading, setLoading] = useState(true);

//   useEffect(() => {
//     const unsubscribe = onAuthStateChanged(auth, (user) => {
//       console.log("hi");
//       console.log("hi");
//       console.log("hi");
//       console.log('Auth state changed:', user?.email || 'No user');
//       console.log("hi");
//       console.log("hi");
//       console.log("hi");
//       setCurrentUser(user);
//       setLoading(false);
//     });

//     return unsubscribe;
//   }, []);

//   const value = {
//     currentUser,
//     loading
//   };

  
//   useEffect(()=>{
//     const checkSession=async()=>{
//       try{
//         // here i will make the call to backend to look for any cookie session existing
//         const data=await fetch(`${API_BASE_URL}/api/auth/login`,{
//           method: "POST",
//           credentials: 'include',
//         })
//         const response = await data.json();
//         if(data.ok){
//           console.log("i got the mail as ",response.userMail);
//           setCurrentUser(response.userMail);
//           setLoading(false);
//         }
//         else{
//           throw new Error(response.error);
//         }
//       }
//       catch(err){
//         console.log(err);
//       }
      
//   };
//   console.log("time to call the check session");
//   checkSession();
//   console.log("came out of check session");
//   },[]);

//   return (
//     <AuthContext.Provider value={value}>
//       {!loading && children}
//     </AuthContext.Provider>
//   );
// };

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
    // This function will check for a user and will not proceed to the
    // next check if one is found.
    const verifyAuth = async () => {
      try {
        // 1. Check for the backend session first.
        const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
          method: "POST",
          credentials: 'include',
        });
        
        if (res.ok) {
          const sessionData = await res.json();
          console.log("✅ Backend session found for:", sessionData.userMail);
          
          // IMPORTANT: Set the user and stop loading.
          setCurrentUser({ email: sessionData.userMail });
          setLoading(false);
          
          // IMPORTANT: Return here to prevent the Firebase check from running.
          return;
        }
      } 
      catch (error) {
        console.error("Backend session check failed, proceeding to Firebase.", error);
        // If the fetch fails, we let the function continue to the Firebase check.
      }

      // 2. If the backend check failed or found no session, fall back to Firebase.
      // This code is ONLY reached if the 'return' statement above was not hit.
      console.log("🤔 No backend session, checking Firebase auth state...");
      const unsubscribe = onAuthStateChanged(auth, (user) => {
        if (user) {
          console.log("✅ Firebase user found:", user.email);
        } else {
          console.log("❌ No active user session found in backend or Firebase.");
        }
        
        // This is now the final authority on user state.
        setCurrentUser(user);
        setLoading(false);
      });

      // Return the Firebase listener's cleanup function.
      return unsubscribe;
    };

    verifyAuth();

  }, []); // Empty array ensures this runs only once.

  const value = {
    currentUser,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};