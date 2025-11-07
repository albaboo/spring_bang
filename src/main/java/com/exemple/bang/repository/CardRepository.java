package com.exemple.bang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Game;
import com.exemple.bang.entity.Player;
import com.exemple.bang.enums.Suit;
import com.exemple.bang.enums.TypeEquipment;
import com.exemple.bang.enums.TypeUse;

import jakarta.transaction.Transactional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    // Devuelve todas las cartas de una partida
    @Query("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying")
    List<Card> findByGamesPlaying(
            @Param("game") Game game);

    // Baraja las cartas de una partida
    @Query("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying ORDER BY function('RAND')")
    List<Card> findByGamesPlayingShuffled(
            @Param("game") Game game);

    // Crea una nueva carta de tipo USE
    @Modifying
    @Transactional
    @Query("INSERT INTO Card (name, description, type, suit, gamesPlaying, player) VALUES (:name, :description, :type, :suit, :game, :player)")
    void createUseCard(
            @Param("name") String name,
            @Param("description") String description,
            @Param("type") TypeUse type,
            @Param("suit") Suit suit,
            @Param("game") Game game,
            @Param("player") Player player);

    // Crea una nueva carta de tipo EQUIPMENT
    @Modifying
    @Transactional
    @Query("INSERT INTO Card (name, description, type, suit, gamesPlaying, distanceModifier, player, equippedPlayer) VALUES (:name, :description, :type, :suit, :game, :distanceModifier, :player, :equippedPlayer)")
    void createEquipmentCard(
            @Param("name") String name,
            @Param("description") String description,
            @Param("type") TypeEquipment type,
            @Param("suit") Suit suit,
            @Param("game") Game game,
            @Param("distanceModifier") Integer distanceModifier,
            @Param("player") Player player,
            @Param("equippedPlayer") Player equippedPlayer);

    // Crea una nueva carta de tipo WEAPON
    @Modifying
    @Transactional
    @Query("INSERT INTO Card (name, description, type, suit, gamesPlaying, distance, player) VALUES (:name, :description, :type, :suit, :game, :distance, :player)")
    void createWeaponCard(
            @Param("name") String name,
            @Param("description") String description,
            @Param("type") String type,
            @Param("suit") Suit suit,
            @Param("game") Game game,
            @Param("distance") Integer distance,
            @Param("player") Player player);

}
