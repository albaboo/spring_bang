package com.exemple.bang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Game;
import com.exemple.bang.entity.Player;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

        // Devuelve todas las cartas de una partida
        @Query("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying")
        List<Card> findByGamesPlaying(
                        @Param("game") Game game);

        // Devuelve todas las cartas de un usuario en una partida
        @Query("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying and c.player = :player")
        List<Card> findByPlayerInGamesPlaying(
                        @Param("game") Game game,
                        @Param("player") Player player);

        // Devuelve una carta por su id
        Optional<Card> findById(Long id);

        // Baraja las cartas de una partida
        @Query("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying ORDER BY function('RAND')")
        List<Card> findByGamesPlayingShuffle(
                        @Param("game") Game game);

        // Actualizar carta
        void updateCard(Card card);

}
