package com.example.chess_app;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface GameRepository extends MongoRepository<Game, String> {
    
    Game findByWhitePlayer(String whitePlayer);
    Game findByBlackPlayer(String blackPlayer);
    Game findByGameId(String id);

    @Query("{ '$or': [ { 'whitePlayer': ?0 }, { 'blackPlayer': ?0 } ], 'isActive': false }")
    List<Game> findCompletedGamesByPlayerEmail(String email);

}
