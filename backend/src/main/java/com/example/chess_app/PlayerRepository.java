package com.example.chess_app;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<Player, String> {
    Player findByUid(String uid);
    Player findByUsername(String username);
    Player findByEmail(String email);
}
