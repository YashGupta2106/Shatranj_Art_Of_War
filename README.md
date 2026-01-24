# Shatranj: Art of War ♟️

A modern, real-time multiplayer chess web application featuring beautiful UI, WebSocket-based gameplay, and comprehensive backend architecture.

**Live Demo:** [Coming Soon - Deployed on Vercel & Render]

---

## ✨ Features

- 🔐 **Secure Authentication** - Firebase-based user registration and login
- ♟️ **Real-time Multiplayer** - Play chess against opponents worldwide  
- 🌐 **Live Synchronization** - Instant move updates via WebSocket
- 📱 **Responsive Design** - Beautiful UI that works on all devices
- 🎯 **Auto-Matchmaking** - Automatic player pairing system
- 💾 **Game History** - Review and replay your past games
- 🔒 **Session Management** - Secure Redis-based sessions
- 🧪 **Well-Tested** - 59 unit tests with 65-70% code coverage

---

## 🛠️ Technology Stack

### Frontend
- **React 18** - Modern UI with hooks and functional components
- **React Router** - Client-side routing
- **Firebase Auth** - User authentication
- **SockJS & STOMP** - WebSocket client for real-time communication
- **CSS3** - Modern, animated styling with glassmorphism effects

### Backend
- **Spring Boot 3.3.11** - Java 21 web framework
- **Spring WebSocket** - Real-time bidirectional communication
- **MongoDB Atlas** - Cloud NoSQL database
- **Redis Cloud** - Session management and caching
- **Firebase Admin SDK** - Token validation
- **Maven** - Build and dependency management

### Testing
- **JUnit 5** - Modern testing framework
- **Mockito** - Mocking for unit tests  
- **AssertJ** - Fluent assertions
- **JaCoCo** - Code coverage reporting (65-70% coverage)

---

## 🏗️ Architecture

### Backend Structure
```
backend/
├── Controllers
│   ├── AuthController - User authentication & sessions
│   ├── GameHistoryController - Game data & replay
│   └── OnlineGameController - WebSocket game handler
├── Services
│   ├── MatchmakingService - Player queue & matching
│   ├── RedisService - Session storage
│   └── OnlineGameHandler - Game logic (9000+ lines)
├── Models
│   ├── Game - Complete game state
│   ├── Player - User data
│   ├── Move - Individual moves
│   └── Piece - Chess pieces (6 types)
└── Repositories
    ├── GameRepository - MongoDB game access
    └── PlayerRepository - MongoDB player access
```

### Frontend Structure
```
frontend/
├── LoginRegister - Authentication UI
├── HomePage - Main lobby
├── ChessBoard - Interactive game board
├── GameHistory - Past games viewer
└── WebSocketService - Real-time communication
```

---

## 🚀 Local Development Setup

### Prerequisites
- **Java 21** ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Node.js v16+** ([Download](https://nodejs.org/))

### Environment Variables

You'll need to set up environment variables for local development:

**Backend** (`application-dev.properties`):
```properties
MONGODB_URI=your_mongodb_connection_string
FIREBASE_PROJECT_ID=your_firebase_project_id
FIREBASE_SERVICE_ACCOUNT_PATH=path/to/firebase_service.json
REDIS_HOST=your_redis_host
REDIS_PORT=your_redis_port
REDIS_PASSWORD=your_redis_password
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
PORT=8080
AUTH_SECURE=false
AUTH_CROSS_SITE=Lax
DEBUG_ENABLED=true
```

**Frontend** (`.env`):
```env
REACT_APP_FIREBASE_API_KEY=your_api_key
REACT_APP_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
REACT_APP_FIREBASE_PROJECT_ID=your_project_id
REACT_APP_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
REACT_APP_FIREBASE_APP_ID=your_app_id
REACT_APP_BACKEND_URL=http://localhost:8080
REACT_APP_WEBSOCKET_URL=http://localhost:8080
```

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/YashGupta2106/Shatranj_Art_Of_War.git
cd Shatranj_Art_Of_War
```

**2. Set up Firebase Service Account**

Create `backend/src/main/resources/firebase_service.json` with your Firebase service account credentials.

**3. Run Backend**
```bash
cd backend

# Set environment profile
export SPRING_PROFILES_ACTIVE=dev  # Linux/Mac
$env:SPRING_PROFILES_ACTIVE="dev"  # Windows PowerShell

mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

**4. Run Frontend**
```bash
cd frontend
npm install
npm start
# Opens on http://localhost:3000
```

---

## 🎮 How to Play

### Testing Locally (2 Players Required)

> **⚠️ Important:** This is a multiplayer game requiring TWO users!

**Setup:**
1. Open **Chrome**: `http://localhost:3000`
2. Open **Firefox**: `http://localhost:3000`  
3. Register/login as different users in each browser
4. Both click "Find Match"
5. Game starts automatically!

**Why two browsers?**
- Same browser = session conflict
- Different browsers = works perfectly
- **Don't use Incognito** - JWT tokens won't persist

---

## 🧪 Running Tests

```bash
cd backend

# Run all tests
mvn test
# Result: 59 tests, 0 failures

# Generate coverage report
mvn clean test jacoco:report
# Open: backend/target/site/jacoco/index.html
```

**Test Coverage:**
- Domain Models: ~90%
- Services: ~60%
- Overall: 65-70%

---

## 📚 API Documentation

### Authentication Endpoints
```
POST   /api/auth/login           - User login with Firebase token
POST   /api/auth/sessionVerify   - Verify active session  
POST   /api/auth/logout          - End session
```

### Game History Endpoints
```
GET    /api/game-history         - Get user's game history
GET    /api/game/{id}            - Get specific game details
```

### WebSocket Endpoints
```
CONNECT  /ws                     - Establish WebSocket connection
SEND     /app/game/find-match    - Join matchmaking queue
SEND     /app/game/move          - Send chess move
RECEIVE  /topic/match-found      - Matchmaking notification
RECEIVE  /topic/game-update      - Real-time game updates
```

---

## 🚢 Deployment

### Frontend (Vercel)

1. Fork this repository
2. Connect to Vercel
3. Set environment variables from frontend `.env`
4. Deploy!

### Backend (Render)

1. Create new Web Service on Render
2. Connect GitHub repository
3. Set build command: `mvn clean install`
4. Set start command: `java -jar target/chess_app-0.0.1-SNAPSHOT.jar`
5. Add environment variables from backend config
6. Deploy!

**Environment Variables Required:**
- `MONGODB_URI`
- `FIREBASE_PROJECT_ID`
- `FIREBASE_SERVICE_ACCOUNT_PATH`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `CORS_ALLOWED_ORIGINS` (include your Vercel URL)
- `PORT`
- `AUTH_SECURE`
- `AUTH_CROSS_SITE`

---

## 🐛 Troubleshooting

**Backend won't start:**
```bash
# Check Java version (must be 21)
java -version

# Clean and rebuild
mvn clean install
```

**Frontend can't connect:**
- Ensure backend is running
- Check `REACT_APP_BACKEND_URL` in `.env`
- Verify CORS settings include your frontend URL

**Matchmaking not working:**
- Need TWO users in TWO different browsers
- Both must click "Find Match" simultaneously
- Wait 2-3 seconds for auto-matching

**Tests failing:**
```bash
mvn clean test
# Should pass without external dependencies (uses mocks)
```

---

## 📁 Project Structure

```
Shatranj_Art_Of_War/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/chess_app/
│   │   │   │   ├── Controllers/
│   │   │   │   ├── Services/
│   │   │   │   ├── Models/
│   │   │   │   └── Repositories/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── application-dev.properties
│   │   └── test/
│   │       └── [59 test files]
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── LoginRegister.jsx
│   │   ├── HomePage.jsx
│   │   ├── ChessBoard.js
│   │   ├── GameHistory.jsx
│   │   └── WebSocketService.js
│   ├── package.json
│   └── .env (not in git)
│
└── README.md (this file)
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🙏 Acknowledgments

- Chess piece Unicode characters
- Firebase for authentication
- MongoDB Atlas for database hosting
- Redis Cloud for session management
- Vercel & Render for deployment

---

## 📧 Contact

**Project Link:** [https://github.com/YashGupta2106/Shatranj_Art_Of_War](https://github.com/YashGupta2106/Shatranj_Art_Of_War)

---

**Built with ❤️ using React and Spring Boot**
