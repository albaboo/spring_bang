package com.exemple.bang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Game;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c WHERE c.gamesPlaying = :game")
    List<Card> findByGamesPlaying(
            @Param("game") Game game);

}
