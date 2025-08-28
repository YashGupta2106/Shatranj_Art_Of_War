import { getAuth } from "firebase/auth";


const API_BASE_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080';

const sendTokenToBackend = async () => {
  const auth = getAuth();
  const user = auth.currentUser;
  
  console.log("Current user:", user);
  
  if (!user) {
    throw new Error("No authenticated user found");
  }

  try {
    console.log("Getting token for user:", user.email);
    const token = await user.getIdToken(true);
    console.log("Token obtained, sending to backend...");

    const response = await fetch(`${API_BASE_URL}/api/auth/verify`, {
      method: "POST",
      credentials:"include",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ message: "hello server" })
    });

    console.log("Response status:", response.status);

    // if (!response.ok) {
    //   const errorText = await response.text();
    //   console.error("Backend error response:", errorText);
    //   throw new Error(`Backend responded with status: ${response.status} - ${errorText}`);
    // }

    // const data = await response.json();
    // console.log("Backend success response:", data);
    return response;
    
  } 
  catch (error) {
    console.error("Error communicating with backend:", error);
    throw error;
  }
};

export { sendTokenToBackend };
