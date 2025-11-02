package hibernate.projects.Controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import hibernate.projects.Entity.Card;
import hibernate.projects.Entity.Game;
import hibernate.projects.Entity.Player;
import hibernate.projects.Entity.Role;
import hibernate.projects.Entity.WeaponCard;
import hibernate.projects.Enum.Suit;
import hibernate.projects.Enum.TypeRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class GameDAO {

    public static Set<Player> listPlayers(EntityManager em, int idGame) {

        Game game = em.find(Game.class, idGame);

        if (game == null)
            return new HashSet<>();

        return game.players != null ? game.players : new HashSet<>();
    }

    public static void showPlayers(EntityManager em, int idGame) {
        Set<Player> players = listPlayers(em, idGame);

        System.out.println("\n==================== LISTA DE JUGADORES ====================");
        for (Player player : players) {
            System.out.println("\t" + player.id + " - " + player.name);
        }
        System.out.println("============================================================");

    }

    public static Game getGame(EntityManager em, int idGame) {

        return em.find(Game.class, idGame);
    }

    public static void showGame(EntityManager em, int idGame) {

        Game game = getGame(em, idGame);
        if (game == null) {
            System.out.println("\u001B[31mNo se ha encontrado ninguna partida con ID " + idGame + ".\u001B[0m");
            return;
        }
        List<Player> rolesFound = new ArrayList<Player>();

        System.out.println("\n==================== PARTIDA ====================");
        System.out.println("Jugadores vivos:");

        for (Player player : game.players) {
            if (player.currentLife > 0) {

                System.out.println("\t" + player.name + " (Vidas: " + player.currentLife + ")");

                if (player.weapon != null) {
                    System.out.println("\t\tArma:");
                    System.out.println("\t\t\t" + player.weapon.name);
                }

                if (player.equipments.size() > 0) {
                    System.out.println("\t\tEquipamiento:");
                    for (Card card : player.equipments) {
                        System.out.println("\t\t\t" + card.name);
                    }
                }

                if (player.role.type == TypeRole.SHERIFF)
                    rolesFound.add(player);
            } else
                rolesFound.add(player);

        }

        System.out.println("Roles encontrados:");
        for (Player player : rolesFound) {
            System.out.println("\t" + player.role.type + " (" + player.name + ")");
        }

        System.out.println("=================================================");

    }

    public static Game startGame(EntityManager em, Scanner in) {

        Game game = new Game();
        game.players = new HashSet<>();

        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            em.persist(game);
            transaction.commit();

            boolean building = true;
            while (building) {
                System.out.println("\n==================== MENÚ DE PREPARACIÓN ====================");
                System.out.println("\t1 - Añadir jugador a la partida");
                if (game.players.size() > 0)
                    System.out.println("\t2 - Quitar jugador de la partida");
                if (game.players.size() > 1)
                    System.out.println("\t3 - Continuar");
                System.out.println("=============================================================");
                System.out.print("\nElige una número: ");

                int option = in.nextInt();
                switch (option) {
                    case 1:
                        game = addPlayer(in, em, game);
                        break;

                    case 2:
                        game = removePlayer(in, em, game);
                        break;

                    case 3:
                        if (game.players.size() > 1)
                            building = false;
                        break;
                    default:
                        break;
                }
            }

            List<Role> roles = RoleDAO.list(em);
            int roleIndex = 0;

            Suit[] suits = Suit.values();
            int suitIndex = 0;

            Deque<Card> cards = new ArrayDeque<>(CardDAO.shuffle(em));

            transaction = em.getTransaction();
            transaction.begin();

            for (Player player : game.players) {
                player.role = roles.get(roleIndex % roles.size());
                WeaponCard colt = new WeaponCard();
                colt.name = "COLT";
                colt.description = "Arma predeterminada";
                colt.distance = 1;
                colt.suit = suits[suitIndex % suits.length];
                colt.player = player;
                player.weapon = colt;
                for (int i = 0; i < 4; i++) {
                    player.hand.add(cards.pollFirst());

                }

                em.persist(colt);
                em.merge(player);
                roleIndex++;
                suitIndex++;
            }

            game.playingCards = cards;
            em.merge(game);
            transaction.commit();

        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\n\u001B[31mError durante la inserción de datos: " + e.getMessage() + "\u001B[0m");
        }
        return game;
    }

    private static Game addPlayer(Scanner in, EntityManager em, Game game) {

        boolean selecting = true;

        while (selecting) {
            showPlayers(em, game.id);
            System.out.println("\n\t0 - Volver atras");
            System.out.print("\nSelecciona un número de jugador: ");
            int option = in.nextInt();
            if (option == 0)
                selecting = false;
            else {
                Player selectedPlayer = em.find(Player.class, option);
                if (selectedPlayer == null)
                    System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                else if (game.players.contains(selectedPlayer))
                    System.err.println("\n\u001B[31mJugador ya incluido en la partida.\u001B[0m");
                else {
                    EntityTransaction transaction = em.getTransaction();

                    try {
                        transaction.begin();
                        selectedPlayer.games.add(game);
                        game.players.add(selectedPlayer);

                        em.merge(game);
                        em.merge(selectedPlayer);

                        transaction.commit();

                        System.out.println("Añadido: " + selectedPlayer.name);
                        selecting = false;

                    } catch (Exception e) {
                        if (transaction != null && transaction.isActive())
                            transaction.rollback();

                        System.err.println(
                                "\n\u001B[31mError durante la adición de jugador: " + e.getMessage() + "\u001B[0m");
                    }

                }

            }

        }

        return game;

    }

    private static Game removePlayer(Scanner in, EntityManager em, Game game) {

        boolean selecting = true;
        while (selecting) {
            showPlayers(em, game.id);
            System.out.println("\n\t0 - Volver atras");
            System.out.print("\nSelecciona un número de jugador: ");
            int option = in.nextInt();

            if (option == 0)
                selecting = false;
            else {
                Player selectedPlayer = em.find(Player.class, option);
                if (selectedPlayer == null)
                    System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                else if (!game.players.contains(selectedPlayer))
                    System.err.println("\n\u001B[31mJugador no incluido en la partida.\u001B[0m");
                else {
                    EntityTransaction transaction = em.getTransaction();

                    try {

                        transaction.begin();

                        selectedPlayer.games.remove(game);
                        game.players.remove(selectedPlayer);

                        em.merge(game);
                        em.merge(selectedPlayer);

                        transaction.commit();

                        System.out.println("Eliminado: " + selectedPlayer.name);
                        selecting = false;
                    } catch (Exception e) {
                        if (transaction != null && transaction.isActive())
                            transaction.rollback();
                        System.err.println("\u001B[31mError al eliminar jugador: " + e.getMessage() + "\u001B[0m");
                    }

                }

            }

        }

        return game;
    }

    public static boolean checkVictory(int idGame) {

        return false;
    }

    public static Suit showCard(EntityManager em, int idGame) {

        Game game = getGame(em, idGame);

        if (game == null) {
            System.out.println("\u001B[31mNo se ha encontrado la partida con ID " + idGame + ".\u001B[0m");
            return null;
        }

        if (game.playingCards == null || game.playingCards.isEmpty()) {
            System.out.println("\u001B[33mNo hay cartas disponibles para mostrar.\u001B[0m");
            return null;
        }

        EntityTransaction transaction = em.getTransaction();

        try {

            transaction.begin();

            Card card = game.playingCards.pollFirst();
            game.discardedCards.addFirst(card);

            em.merge(game);
            transaction.commit();

            return card.suit;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\u001B[31mError al mostrar carta: " + e.getMessage() + "\u001B[0m");

            return null;
        }

    }
}
