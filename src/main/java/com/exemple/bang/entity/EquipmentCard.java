package com.exemple.bang.entity;

import com.exemple.bang.enums.TypeEquipment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipment_card")
public class EquipmentCard extends Card {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public TypeEquipment type;

    @Column(name = "distance_modifier", nullable = false)
    public int distanceModifier;

    /** Relaciones */

    @ManyToOne
    @JoinColumn(name = "equipped_player_id")
    public Player equippedPlayer;
}
