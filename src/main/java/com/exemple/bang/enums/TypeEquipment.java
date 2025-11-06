package com.exemple.bang.enums;

public enum TypeEquipment {
    HORSE("Equipamiento que modifica la distancia entre jugadores."), 
    TELESCOPIC_SIGHT("Aumenta el alcance de ataque del jugador."), 
    BARREL("Permite probar suerte cuando eres atacado; si la carta mostrada es de corazones el ataque falla.");

    public final String description;

    TypeEquipment(String description) {
        this.description = description;
    }
}
