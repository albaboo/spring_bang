package com.exemple.bang.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import spring.projects.Enum.TypeUse;

@Entity
@Table(name = "use_card")
public class UseCard extends Card {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public TypeUse type;

    /** Relaciones */

}
