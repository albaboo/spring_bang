package hibernate.projects.Controller;

import java.util.Deque;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import hibernate.projects.Entity.Card;
import hibernate.projects.Entity.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class PlayerDAO {

    public static Set<Player> list(EntityManager em) {

        Set<Player> players = new HashSet<>(em.createQuery("FROM Player", Player.class).getResultList());

        return players;
    }

    public static void showPlayers(EntityManager em) {
        Set<Player> players = new HashSet<Player>();
        try {
            players = PlayerDAO.list(em);
            System.out.println("\n==================== LISTA DE JUGADORES ====================");
            for (Player player : players) {
                System.out.println("\t" + player.id + " - " + player.name);
            }
            System.out.println("============================================================");
        } catch (PersistenceException e) {
            System.err.println("\n\u001B[31mError durante la recuperación de datos: " + e.getMessage() + "\u001B[0m");

        }
    }

    public static void addPlayer(EntityManager em, Scanner in) {
        EntityTransaction transaction = em.getTransaction();
        try {
            boolean creating = true;
            in.nextLine();
            while (creating) {
                System.out.print("\nEscribe un nombre de jugador: ");
                String name = in.nextLine();
                if (name.length() > 0) {
                    Player newPlayer = new Player();
                    newPlayer.name = name;
                    transaction.begin();
                    em.persist(newPlayer);
                    transaction.commit();
                    System.out.println("Añadido: " + newPlayer.name);
                    creating = false;
                }

            }

        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\n\u001B[31mError durante la inserción de datos: " + e.getMessage() + "\u001B[0m");
        }

    }

    public static Deque<Card> getHand(EntityManager em, int idPlayer) {

        try {
            Player player = em.find(Player.class, idPlayer);

            if (player == null) {
                System.out.println("\u001B[31mNo se ha encontrado ningún jugador con ID " + idPlayer + ".\u001B[0m");
                return null;
            }

            return player.hand;
        } catch (PersistenceException e) {
            System.err.println(
                    "\n\u001B[31mError durant la recuperació de la mà del jugador: " + e.getMessage() + "\u001B[0m");
            return null;
        }
    }

    public static void showHand(EntityManager em, int idPlayer) {

        try {
            Deque<Card> hand = getHand(em, idPlayer);

            if (hand == null || hand.isEmpty()) {
                System.out.println("\u001B[31mNo se ha encontrado ninguna carta en la mano del jugador con ID "
                        + idPlayer + ".\u001B[0m");
                return;
            }

            System.out.println("\n==================== MANO DEL JUGADOR ====================");
            for (Card card : hand) {
                System.out.println("\t" + card);
            }
            System.out.println("=========================================================");

        } catch (PersistenceException e) {
            System.err.println(
                    "\n\u001B[31mError durante la recuperación de la mano del jugador: " + e.getMessage()
                            + "\u001B[0m");
        }
    }

    public static void useBang(int idAttacker, int idObjective) {

    }

    public static void discardCard(int idPlayer, int idCard) {

    }

    public static void checkElimination(int idPlayer) {

    }

    public static void stealCard(int idPlayer) {

    }

    public static void passTurn(int idPlayer) {

    }

    public static void equipCard(int idPlayer, int idCard) {

    }

    public static int calculateDistance(int idPlayerOrigin, int idPlayerDestination) {

        return idPlayerOrigin - idPlayerDestination;
    }

    public static boolean checkDistanceAttack(int idAttacker, int idObjective) {

        return false;
    }

}
