package com.exemple.bang.enums;

public enum TypeRole {
    SHERIFF("Los malhechores y el renegado deben quedar eliminados. Identidad publica desde el principio."),
    MALFACTOR("El sheriff debe quedar eliminado."),
    RENEGADE("Ser el último jugador vivo junto al sheriff."),
    ASSISTANT("Los malhechores y el renegado deben quedar eliminados. Ayuda al sheriff.");

    public final String objective;

    TypeRole(String objective) {
        this.objective = objective;
    }

}
