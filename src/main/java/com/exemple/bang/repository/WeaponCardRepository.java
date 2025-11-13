package com.exemple.bang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Game;
import com.exemple.bang.entity.Player;
import com.exemple.bang.entity.WeaponCard;

@Repository
public interface WeaponCardRepository extends JpaRepository<WeaponCard, Long> {

    // Devuelve todas las cartas de un usuario en una partida
    @Query("SELECT c FROM WeaponCard c WHERE :game MEMBER OF c.gamesPlaying AND c.player = :player")
    List<Card> findByGamesPlaying(
            @Param("game") Game game,
            @Param("player") Player player);

    // Actualizar carta de arma
    void updateWeaponCard(WeaponCard weaponCard);
}
