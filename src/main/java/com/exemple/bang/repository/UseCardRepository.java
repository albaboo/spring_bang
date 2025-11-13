package com.exemple.bang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Game;
import com.exemple.bang.entity.Player;
import com.exemple.bang.entity.UseCard;

@Repository
public interface UseCardRepository extends JpaRepository<UseCard, Long> {

    // Devuelve todas las cartas de un usuario en una partida
    @Query("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying AND c.player = :player")
    List<Card> findByGamesPlayingAndPlayer(
            @Param("game") Game game,
            @Param("player") Player player);

    // Actualizar carta de uso
    void updateUseCard(UseCard useCard);

}
