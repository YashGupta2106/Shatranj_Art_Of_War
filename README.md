# Shatranj: Art of War ♟️

A modern, real-time multiplayer chess application built with React frontend and Spring Boot backend, featuring Firebase authentication, WebSocket-based gameplay, and comprehensive testing suite.

---

## 🎓 Important Note for Evaluators/TAs

> **All credentials are already included in the project files for your convenience!**  
> You do NOT need to create any Firebase, MongoDB, or Redis accounts.  
> All configuration is ready to use - just clone, install, and run!
>
> **Why credentials are exposed:**  
> To make evaluation quick and seamless. These are temporary evaluation credentials with limited access that will be removed from the repository after evaluation. This approach ensures all evaluators have a consistent testing environment and can focus on code quality rather than setup complexity.
>
> **See `EVALUATOR_CREDENTIALS.txt` for detailed quick-start instructions.**

---

## 👥 Group Members

- **[Yash Gupta]** - Roll Number: [IMT2023125]
- **[Pranay Kelotra]** - Roll Number: [IMT2023563]  
- **[Hitanshu Seth]** - Roll Number: [IMT2023100]
- **[Sahas Sangal]** - Roll Number: [IMT2023556]

---

## 📋 Project Overview

Shatranj: Art of War is a full-stack web application that enables users to play chess in real-time against other players online. The application features secure authentication, automatic matchmaking, live game synchronization, and persistent game history.

### Key Features

- 🔐 **Firebase Authentication** - Secure user registration and login
- ♟️ **Real-time Chess Gameplay** - Live multiplayer chess matches  
- 🌐 **WebSocket Communication** - Instant move synchronization
- 📱 **Responsive Design** - Works on desktop and mobile devices
- 🎯 **Matchmaking System** - Automatic player pairing
- 💾 **Game State Persistence** - MongoDB database for game history
- 🔒 **Session Management** - Redis-based secure sessions
- 🧪 **Comprehensive Testing** - 59 unit tests with 65-70% coverage

---

## 🛠️ Technology Stack

### Frontend
- **React 18** - Modern UI with hooks
- **React Router** - Client-side routing
- **Firebase Auth** - Authentication service
- **SockJS & STOMP** - WebSocket client
- **CSS3** - Responsive styling

### Backend
- **Spring Boot 3.3.11** - Java web framework (Java 21)
- **Spring WebSocket** - Real-time communication
- **MongoDB** - NoSQL database for game storage
- **Redis** - Session management (Required)
- **Firebase Admin SDK** - Token validation
- **Maven** - Build and dependency management

### Testing & Quality
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **JaCoCo** - Code coverage reporting

---

## 🏗️ Project Architecture

### Backend Components

1. **Controllers**
   - `AuthController` - Handles user authentication and session management
   - `GameHistoryController` - Provides game history and replay data
   - `OnlineGameController` - WebSocket handler for real-time gameplay

2. **Services**
   - `MatchmakingService` - Queue-based player matching
   - `RedisService` - Session storage and retrieval
   - Game logic handlers

3. **Models**
   - `Game` - Complete game state (players, pieces, moves, timestamps)
   - `Player` - User information
   - `Move` - Individual move data
   - `Piece` - Chess piece hierarchy (Pawn, Rook, Knight, Bishop, Queen, King)

4. **Repositories**
   - `GameRepository` - MongoDB data access for games
   - `PlayerRepository` - MongoDB data access for players

### Frontend Components
- Authentication pages (Login, Register)
- Game board with drag-and-drop interface
- Matchmaking lobby
- Game history viewer
- Real-time move synchronization

---

## 🎓 For Evaluators - Quick Start

> **⚠️ ALL CREDENTIALS ALREADY CONFIGURED!** Complete working credentials are already included in:
> - `backend/src/main/resources/application.properties` (MongoDB, Firebase, Redis)
> - `frontend/.env` (Firebase config, backend URLs)
>
> **No need to create any accounts - everything is ready to use!**
>
> See `EVALUATOR_CREDENTIALS.txt` for detailed 2-minute setup guide.

### Prerequisites

- **Node.js** v16 or higher ([Download](https://nodejs.org/))
- **Java 21** ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))

**Note:** MongoDB and Redis are cloud-hosted. Credentials provided in separate file.

### Quick Setup (5 minutes)

# Backend will start on http://localhost:8080
# All credentials already configured in application.properties
```

**Step 3: Install Dependencies & Run Frontend**
```bash
cd frontend
npm install
npm start

# Frontend will start on http://localhost:3000
# All credentials already configured in .env file
```
```bash
cd backend
mvn test

# Expected: 59 tests passing, 0 failures
```

---

## 🎮 Testing the Application

### ⚠️ CRITICAL: Two Players Required!

> **This is a multiplayer chess game. You NEED TWO USERS to test gameplay!**

**IMPORTANT: Use Two DIFFERENT Browsers**

> ⚠️ **DO NOT use Incognito/Private mode** - JWT tokens won't persist!

**How to Test:**
1. Open **Chrome**: `http://localhost:3000`
2. Open **Firefox**: `http://localhost:3001` (or Edge, Safari)
3. Register/login as **Player 1** in Chrome
4. Register/login as **Player 2** in Firefox  
5. Both click "Find Match" simultaneously
6. Game starts and moves sync in real-time!

**Why Two Browsers?**
- Same browser = session conflict
- Incognito = tokens don't persist
- Different browsers = works perfectly

**Pre-created Test Accounts:**
```
Account 1:
Email: guptaji@gmail.com
Password: guptaji

Account 2:
Email: ash@gmail.com  
Password: guptaji
```

### Testing Flow

1. **Both users** login with different accounts
2. **Both users** click "Find Match" button
3. Wait 2-3 seconds for automatic matching
4. Game board appears for both players
5. Take turns making moves
6. Moves sync in real-time!

---

## 🧪 Running Tests

### Test Suite Overview

This project includes **59 comprehensive unit tests** covering:
- **Domain Models** - Game, Move, Player, all Piece types
- **Service Layer** - RedisService with mocks
- **Business Logic** - Game state, move validation, session management

### Running All Tests

```bash
cd backend

# Run all tests
mvn test

# Expected output:
# Tests run: 59, Failures: 0, Errors: 0, Skipped: 1
# BUILD SUCCESS
```

### Generate Coverage Report

```bash
cd backend
mvn clean test jacoco:report

# Open coverage report:
# backend/target/site/jacoco/index.html
# Expected coverage: 65-70%
```

### Test Structure

```
backend/src/test/java/com/example/chess_app/
├── GameTest.java (12 tests)
│   └── Game state management, move tracking
├── PieceTest.java (21 tests)  
│   └── All chess pieces (Pawn, Rook, Knight, Bishop, Queen, King)
├── TestPieceFactory.java (Helper class)
│
└── unit/
    ├── model/
    │   ├── MoveTest.java (12 tests)
    │   └── PlayerTest.java (7 tests)
    └── service/
        └── RedisServiceTest.java (7 tests - with Mockito mocks)
```

### Testing Technologies

- **JUnit 5** - Testing framework
- **Mockito** - Mocking external dependencies  
- **AssertJ** - Fluent assertions
- **JaCoCo** - Code coverage analysis

---

## 🐛 Troubleshooting

### Common Issues

**1. Backend won't start**
```bash
# Check Java version
java -version  # Must be Java 21

# Clean and rebuild
mvn clean install
```

**2. "Cannot connect to MongoDB"**
- Verify credentials in `application.properties` match `EVALUATOR_CREDENTIALS.txt`
- Check internet connection (using cloud MongoDB)

**3. "Cannot connect to Redis"**
- Verify Redis configuration in `application.properties`
- Redis is required - check cloud Redis credentials

**4. Tests failing**
```bash
# Clean and run
mvn clean test

# Tests should pass without MongoDB/Redis (uses mocks)
```

**5. Frontend won't connect to backend**
- Ensure backend is running on port 8080
- Check `REACT_APP_BACKEND_URL` in frontend `.env`
- Verify CORS settings in backend `application.properties`

**6. Can't login/register**
- Check Firebase configuration in frontend `.env`
- Verify Firebase project ID matches in backend
- Ensure internet connection (Firebase is cloud-based)

**7. Matchmaking not working**
- You need **TWO users** logged in simultaneously
- Use two different browsers (Chrome + Firefox)
- **DO NOT use incognito mode** (JWT tokens won't persist)
- Both users must click "Find Match"
- Wait 2-3 seconds for automatic pairing

---

## 📁 Project Structure

```
Shatranj_Art_Of_War/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/chess_app/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── GameHistoryController.java
│   │   │   │   ├── OnlineGameController.java
│   │   │   │   ├── MatchmakingService.java
│   │   │   │   ├── RedisService.java
│   │   │   │   ├── Game.java, Player.java, Move.java
│   │   │   │   └── Piece.java (+ subclasses)
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/com/example/chess_app/
│   │           └── [59 test files]
│   ├── pom.xml
│   └── target/site/jacoco/  # Coverage reports
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── firebase/
│   │   └── App.js
│   └── .env
│
├── README.md (this file)
└── PROJECT_REPORT.md
```

---

## 📊 API Endpoints

### Authentication
```
POST   /api/auth/login          - User login with Firebase token
POST   /api/auth/verify-session - Verify active session  
POST   /api/auth/logout         - End user session
```

### Game History
```
GET    /api/game-history        - Get user's game history
GET    /api/game/{id}           - Get specific game details
```

### WebSocket
```
CONNECT  /ws                     - Establish WebSocket connection
SEND     /app/game/find-match    - Join matchmaking queue
SEND     /app/game/move          - Send game move
RECEIVE  /topic/match-found      - Match notification
RECEIVE  /topic/game-update      - Game state updates
```

---


## 🎯 Evaluation Checklist

- [ ] Java 21 installed
- [ ] Node.js v16+ installed
- [ ] Maven 3.6+ installed
- [ ] Copied credentials from `EVALUATOR_CREDENTIALS.txt`
- [ ] Backend runs: `mvn spring-boot:run`
- [ ] Frontend runs: `npm start`
- [ ] Tests pass: `mvn test` (59/59)
- [ ] Coverage generated: `mvn test jacoco:report`
- [ ] Tested with 2 browsers/users

---

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Yash Gupta**
- GitHub: [@YashGupta2106](https://github.com/YashGupta2106)
- Project: [Shatranj Art of War](https://github.com/YashGupta2106/Shatranj_Art_Of_War)

---

## 📚 Additional Documentation

For detailed information about testing methodology, architecture decisions, and development phases, please refer to `PROJECT_REPORT.md`.

---

