package com.exemple.bang.entity;

import com.exemple.bang.enums.TypeUse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "use_card")
public class UseCard extends Card {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public TypeUse type;

    /** Relaciones */

}
