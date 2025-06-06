import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WEBSOCKET_URL = process.env.REACT_APP_WEBSOCKET_URL || 'http://localhost:8080';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
  }

  connect(gameId, onMoveReceived, onMatchFound) {
    // Create SockJS connection
    const socket = new SockJS(`${WEBSOCKET_URL}/chess-game`);
    
    // Create STOMP client
    this.client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log('🔌 WebSocket:', str),
      
      onConnect: (frame) => {
        console.log('✅ Connected to WebSocket');
        this.connected = true;
        
        // Subscribe to matchmaking notifications
        this.client.subscribe('/topic/user@email.com', (message) => {
          const matchData = JSON.parse(message.body);
          onMatchFound(matchData);
        });
        
        // Subscribe to game moves
        this.client.subscribe(`/topic/game/${gameId}`, (message) => {
          const moveData = JSON.parse(message.body);
          onMoveReceived(moveData);
        });
      },
      
      onDisconnect: () => {
        console.log('🔌 Disconnected from WebSocket');
        this.connected = false;
      },
      
      onStompError: (frame) => {
        console.error('❌ WebSocket error:', frame);
      }
    });

    this.client.activate();
  }

  sendMove(gameId, moveData) {
    if (this.connected && this.client) {
      this.client.publish({
        destination: `/app/game/${gameId}/move`,
        body: JSON.stringify(moveData)
      });
      console.log('📤 Move sent:', moveData);
    } else {
      console.error('❌ Cannot send move: WebSocket not connected');
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
    }
  }
}

export default new WebSocketService();
