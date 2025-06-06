package com.example.chess_app;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
    Game findByWhitePlayer(String whitePlayer);
    Game findByBlackPlayer(String blackPlayer);
    Game findByGameId(String id);

}
