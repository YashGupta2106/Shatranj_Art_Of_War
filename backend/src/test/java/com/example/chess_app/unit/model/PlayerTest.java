package com.example.chess_app.unit.model;

import com.example.chess_app.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Player entity class.
 * Tests player creation and data integrity.
 */
@DisplayName("Player Model Tests")
class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    @DisplayName("Should create player")
    void testPlayerCreation() {
        assertThat(player).isNotNull();
    }

    @Test
    @DisplayName("Should set and get UID")
    void testUid() {
        player.setUid("firebase-uid-123");
        assertThat(player.getUid()).isEqualTo("firebase-uid-123");
    }

    @Test
    @DisplayName("Should set and get email")
    void testEmail() {
        player.setEmail("player@example.com");
        assertThat(player.getEmail()).isEqualTo("player@example.com");
    }

    @Test
    @DisplayName("Should set and get username")
    void testUsername() {
        player.setUsername("ChessMaster");
        assertThat(player.getUsername()).isEqualTo("ChessMaster");
    }

    @Test
    @DisplayName("Should handle complete player data")
    void testCompletePlayerData() {
        player.setUid("uid-456");
        player.setEmail("test@chess.com");
        player.setUsername("TestPlayer");
        
        assertThat(player.getUid()).isEqualTo("uid-456");
        assertThat(player.getEmail()).isEqualTo("test@chess.com");
        assertThat(player.getUsername()).isEqualTo("TestPlayer");
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        assertThat(player.getUid()).isNull();
        assertThat(player.getEmail()).isNull();
        assertThat(player.getUsername()).isNull();
    }

    @Test
    @DisplayName("Should update player data")
    void testUpdatePlayerData() {
        player.setUsername("OldName");
        assertThat(player.getUsername()).isEqualTo("OldName");
        
        player.setUsername("NewName");
        assertThat(player.getUsername()).isEqualTo("NewName");
    }
}
