package com.exemple.bang;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.persistence.EntityManager;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        Scanner in = new Scanner(System.in);
        try {

            RoleDAO.checkRoles();

            // Menú principal del juego
            boolean playing = true;
            while (playing) {
                System.out.println("\n========== MENÚ PRINCIPAL ==========");
                System.out.println("\t1 - Jugar");
                System.out.println("\t2 - Listar jugadores");
                System.out.println("\t3 - Añadir jugador");
                System.out.println("\t4 - Salir");
                System.out.println("====================================");
                System.out.print("\nElige una número: ");

                int option = -1;
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();

                switch (option) {
                    case 1:
                        if (PlayerDAO.list().size() > 1) {
                            int idGame = GameDAO.start(in);
                            if (idGame != -1)
                                GameDAO.play(idGame, in);
                        } else
                            System.err.println("\n\u001B[31mNo hay jugadores suficientes registrados\u001B[0m");
                        break;

                    case 2:
                        if (PlayerDAO.list().size() > 0)
                            PlayerDAO.showPlayers();
                        else
                            System.err.println("\n\u001B[31mNo hay jugadores suficientes registrados\u001B[0m");
                        break;

                    case 3:
                        PlayerDAO.addPlayer(in);
                        break;

                    case 4:
                        playing = false;
                        break;
                    default:
                        break;
                }

            }

            System.out.println("Cerrando programa...");

        } catch (Exception e) {
            System.err.println("\n\u001B[31mError durante la ejecución del programa: " + e.getMessage() + "\u001B[0m");
        } finally {
            // Cierra recursos y conexión
            in.close();

        }
    }
}
