package com.exemple.bang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Card;
import com.exemple.bang.entity.Player;
import com.exemple.bang.entity.Role;
import com.exemple.bang.entity.WeaponCard;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

        // Devuelve la lista de jugadores registrados en la base de datos
        List<Player> findAll();

        // Devuelve un jugador según la id
        Optional<Player> findById(Long id);

        // Devuelve la mano de cartas de un jugador
        List<Card> findHandByPlayer(Player player);

        // Descarta una carta de la mano
        void discardCardFromHand(Player player, Card card);

        // Añade una carta a la mano de un jugador
        void addCardToHand(Player player, Card card);

        // Actualizar jugador
        void updatePlayer(Player player);

        // Añadir arma al jugador
        void addWeaponToPlayer(Player player, WeaponCard weapon);

        // Quitar arma al jugador
        void removeWeaponFromPlayer(Player player);

        // Añadir rol al jugador
        void addRoleToPlayer(Player player, Role role);

        // Quitar rol al jugador
        void removeRoleFromPlayer(Player player);

}
