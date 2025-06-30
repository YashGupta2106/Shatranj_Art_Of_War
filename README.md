# Shatranj: Art of War ♟️

A modern, real-time multiplayer chess application built with React frontend and Spring Boot backend, featuring Firebase authentication and WebSocket-based gameplay.


## 🚀 Live Demo & Links

- **🎯 Play the Game**: [https://shatranj-art-of-war.vercel.app](https://shatranj-art-of-war.vercel.app)
- **📂 Source Code**: [GitHub Repository](https://github.com/YashGupta2106/Shatranj_Art_Of_War)

> **Note**: The backend may take 30-60 seconds to wake up on first visit (Render free tier limitation)



## 📋 Features

- 🔐 **Firebase Authentication** - Secure user registration and login
- ♟️ **Real-time Chess Gameplay** - Live multiplayer chess matches
- 🌐 **WebSocket Communication** - Instant move synchronization
- 📱 **Responsive Design** - Works on desktop and mobile devices
- 🎯 **Matchmaking System** - Find and play against other players
- 💾 **Game State Persistence** - MongoDB database for game history
- 🔒 **Protected Routes** - Secure user sessions and game access

## 🛠️ Tech Stack

### Frontend
- **React 18** - Modern React with hooks
- **React Router** - Client-side routing
- **Firebase Auth** - Authentication service
- **SockJS & STOMP** - WebSocket communication
- **CSS3** - Responsive styling

### Backend
- **Spring Boot 3.3.11** - Java web framework
- **Spring Security** - Authentication and authorization
- **Spring WebSocket** - Real-time communication
- **MongoDB** - NoSQL database
- **Firebase Admin SDK** - Server-side Firebase integration
- **Maven** - Dependency management

## 🏗️ Project Structure

```
Shatranj_Art_Of_War/
├── frontend/                 # React frontend application
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── AuthContext.js   # Authentication context
│   │   ├── firebase-config.js # Firebase configuration
│   │   ├── WebSocketService.js # WebSocket service
│   │   └── ...
│   ├── public/
│   └── package.json
├── backend/                 # Spring Boot backend
│   ├── src/main/java/com/example/chess_app/
│   │   ├── AuthController.java    # Authentication endpoints
│   │   ├── FirebaseConfig.java    # Firebase setup
│   │   ├── WebSocketConfig.java   # WebSocket configuration
│   │   ├── CorsConfig.java        # CORS configuration
│   │   └── ...
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── firebase-service-account.json
│   └── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- **Node.js** (v16 or higher)
- **Java 17** or higher
- **Maven 3.6+**
- **MongoDB** (local or cloud instance)
- **Firebase Project** with Authentication enabled

### 🔧 Local Development Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/YashGupta2106/Shatranj_Art_Of_War.git
cd Shatranj_Art_Of_War
```

#### 2. Backend Setup

```bash
cd backend

# Install dependencies
mvn clean install

# Set up environment variables (create .env or set system variables)
export MONGODB_URI="mongodb://localhost:27017/Art_Of_War"
export FIREBASE_PROJECT_ID="your-firebase-project-id"
export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:3001"

# Run the backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

#### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create .env file with your Firebase configuration
cat > .env << EOF
REACT_APP_FIREBASE_API_KEY=your-api-key
REACT_APP_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
REACT_APP_FIREBASE_PROJECT_ID=your-project-id
REACT_APP_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
REACT_APP_FIREBASE_APP_ID=your-app-id
REACT_APP_BACKEND_URL=http://localhost:8080
REACT_APP_WEBSOCKET_URL=http://localhost:8080
EOF

# Start the development server
npm start
```

The frontend will start on `http://localhost:3000`

### 🔑 Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Enable **Authentication** → **Sign-in method** → **Email/Password**
4. Go to **Project Settings** → **Service accounts**
5. Generate a new private key and save as `firebase-service-account.json`
6. Get your web app configuration from **Project Settings** → **General** → **Your apps**

### 🗄️ Database Setup

#### MongoDB Local Setup
```bash
# Install MongoDB locally or use MongoDB Atlas
# Create database: Art_Of_War
# Collections will be created automatically
```

#### MongoDB Atlas (Cloud)
1. Create account at [MongoDB Atlas](https://www.mongodb.com/atlas)
2. Create a cluster and get connection string
3. Update `MONGODB_URI` environment variable

## 🌐 Deployment

### Frontend Deployment (Vercel)

1. Connect your GitHub repository to Vercel
2. Set environment variables in Vercel dashboard:
   ```
   REACT_APP_FIREBASE_API_KEY=your-api-key
   REACT_APP_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
   REACT_APP_FIREBASE_PROJECT_ID=your-project-id
   REACT_APP_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
   REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
   REACT_APP_FIREBASE_APP_ID=your-app-id
   REACT_APP_BACKEND_URL=https://your-backend-url.onrender.com
   REACT_APP_WEBSOCKET_URL=https://your-backend-url.onrender.com
   ```
3. Deploy automatically on push to main branch

### Backend Deployment (Render)

1. Connect your GitHub repository to Render
2. Set environment variables in Render dashboard:
   ```
   MONGODB_URI=your-mongodb-connection-string
   FIREBASE_PROJECT_ID=your-firebase-project-id
   FIREBASE_CONFIG={"type":"service_account",...} // Full JSON content
   CORS_ALLOWED_ORIGINS=https://your-frontend-url.vercel.app,http://localhost:3000
   DEBUG_ENABLED=false
   PORT=8080
   ```
3. Deploy automatically on push to main branch

## 🎮 How to Play

1. **Register/Login** - Create an account or sign in with existing credentials
2. **Join Game** - Navigate to the chess board to start or join a game
3. **Make Moves** - Click and drag pieces to make your moves
4. **Real-time Updates** - See opponent moves instantly via WebSocket connection
5. **Game History** - View your past games and statistics

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/verify` - Verify Firebase token
- `GET /api/auth/test` - Test backend connectivity

### WebSocket Endpoints
- `WS /ws` - WebSocket connection endpoint
- `/topic/game/{gameId}` - Subscribe to game updates
- `/app/game/{gameId}/move` - Send game moves

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 Environment Variables Reference

### Frontend (.env)
```env
REACT_APP_FIREBASE_API_KEY=your-firebase-api-key
REACT_APP_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
REACT_APP_FIREBASE_PROJECT_ID=your-firebase-project-id
REACT_APP_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your-messaging-sender-id
REACT_APP_FIREBASE_APP_ID=your-firebase-app-id
REACT_APP_BACKEND_URL=http://localhost:8080
REACT_APP_WEBSOCKET_URL=http://localhost:8080
```

### Backend (application.properties)
```properties
MONGODB_URI=mongodb://localhost:27017/Art_Of_War
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_CONFIG={"type":"service_account",...}
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://your-frontend-domain.com
DEBUG_ENABLED=false
PORT=8080
```

## 🐛 Troubleshooting

### Common Issues

1. **Firebase Auth Error**: Ensure all Firebase environment variables are set correctly
2. **CORS Error**: Add your frontend URL to `CORS_ALLOWED_ORIGINS`
3. **WebSocket Connection Failed**: Check if backend is running and WebSocket endpoint is accessible
4. **Database Connection Error**: Verify MongoDB URI and network connectivity

### Debug Mode

Enable debug mode by setting `DEBUG_ENABLED=true` in backend environment variables for detailed logging.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Yash Gupta**
- GitHub: [@YashGupta2106](https://github.com/YashGupta2106)
- Project: [Shatranj Art of War](https://github.com/YashGupta2106/Shatranj_Art_Of_War)

## 🙏 Acknowledgments

- Firebase for authentication services
- MongoDB for database solutions
- Spring Boot community for excellent documentation
- React community for frontend framework
- Vercel and Render for hosting platforms

---

⭐ **Star this repository if you found it helpful!**
