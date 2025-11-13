package com.exemple.bang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.EquipmentCard;
import com.exemple.bang.entity.Game;
import com.exemple.bang.entity.Player;

@Repository
public interface EquipmentCardRepository extends JpaRepository<EquipmentCard, Long> {
    // Devuelve todas las cartas de un usuario en una partida
    @Query("SELECT c FROM EquipmentCard c WHERE :game MEMBER OF c.gamesPlaying AND c.equippedPlayer = :equippedPlayer")
    List<EquipmentCard> findByGamesPlayingAndPlayer(
            @Param("game") Game game,
            @Param("equippedPlayer") Player equippedPlayer);

    // Actualizar carta de equipo
    void updateEquipmentCard(EquipmentCard equipmentCard);
}
