package com.exemple.bang.enums;

public enum TypeUse {
    BEER("Carta de curación; recupera 1 punto de vida en el jugador que la juega."),
    FAILED("Carta de defensa; puede anular un BANG si se juega a tiempo."),
    BANG("Carta de ataque; se utiliza para hacer daño a un jugador a distancia válida.");

    public final String description;

    TypeUse(String description) {
        this.description = description;
    }
}
