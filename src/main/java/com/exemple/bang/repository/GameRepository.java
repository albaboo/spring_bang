package com.exemple.bang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Game;
import com.exemple.bang.entity.Player;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // Devuelve un juego según su id
    Optional<Game> findById(Long id);

    // Devuelve la lista de jugadores en una partida
    List<Player> findPlayersByGame(Game game);

    // Añadir un jugador al juego
    void addPlayerToGame(Player player, Game game);

    // Quitar un jugador del juego
    void removePlayerFromGame(Player player, Game game);

    // Actualizar juego
    void updateGame(Game game);

    // Añadir carta al juego en la pila de robo
    void addCardToPlayingCards(Card card, Game game);

    // Quitar carta del juego en la pila de robo
    void removeCardFromPlayingCards(Card card, Game game);

    // Añadir carta al juego en la pila de descartes
    void addCardToDiscardedCards(Card card, Game game);

    // Quitar carta del juego en la pila de descartes
    void removeCardFromDiscardedCards(Card card, Game game);

}
