package hibernate.projects.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hibernate.projects.Entity.Card;
import hibernate.projects.Entity.EquipmentCard;
import hibernate.projects.Entity.Game;
import hibernate.projects.Entity.Player;
import hibernate.projects.Entity.Role;
import hibernate.projects.Entity.WeaponCard;
import hibernate.projects.Enum.Suit;
import hibernate.projects.Enum.TypeCard;
import hibernate.projects.Enum.TypeRole;
import hibernate.projects.Enum.TypeUse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class GameDAO {

    public static List<Player> listPlayers(EntityManager em, int idGame) {

        Game game = em.find(Game.class, idGame);

        if (game == null)
            return new ArrayList<>();

        return game.players != null ? game.players : new ArrayList<>();
    }

    public static void showPlayers(EntityManager em, int idGame) {
        List<Player> players = listPlayers(em, idGame);

        System.out.println("\n==================== LISTA DE JUGADORES ====================");
        for (Player player : players) {
            System.out.println("\t" + player.id + " - " + player.name);
        }
        System.out.println("============================================================");

    }

    public static void show(EntityManager em, int idGame) {

        Game game = em.find(Game.class, idGame);
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
                    player.weapon = (WeaponCard) player.weapon;
                    System.out.println("\t\tArma: " + player.weapon.type);
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

    public static int start(EntityManager em, Scanner in) {

        Game game = new Game();

        EntityTransaction transaction = em.getTransaction();

        try {
            if (!transaction.isActive())
                transaction.begin();

            em.persist(game);
            transaction.commit();

            CardDAO.checkCards(em, game);

            boolean building = true;
            while (building) {
                System.out.println("\n==================== MENÚ DE PREPARACIÓN ====================");
                System.out.println("\t1 - Añadir jugador a la partida");
                if (!game.players.isEmpty())
                    System.out.println("\t2 - Quitar jugador de la partida");
                if (game.players.size() > 1)
                    System.out.println("\t3 - Continuar");
                System.out.println("=============================================================");
                System.out.print("\nElige una número: ");

                int option = -1;
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();

                switch (option) {
                    case 1:
                        game = addPlayer(in, em, game);
                        break;

                    case 2:
                        if (!game.players.isEmpty())
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

            List<Card> cards = CardDAO.shuffle(em, game);

            if (!transaction.isActive())
                transaction.begin();

            for (Player player : game.players) {
                for (Card card : player.hand) {
                    card.player = null;
                    em.merge(card);
                }

                for (EquipmentCard card : player.equipments) {
                    card.player = null;
                    em.merge(card);
                }

                player.hand.clear();
                player.equipments.clear();

                Role role = roles.get(roleIndex % roles.size());
                player.role = role;
                role.players.add(player);

                player.maxLife = (player.role != null && player.role.type == TypeRole.SHERIFF) ? 5 : 4;
                player.currentLife = player.maxLife;

                WeaponCard colt = new WeaponCard();
                colt.name = TypeCard.WEAPON.name();
                colt.description = "Arma predeterminada";
                colt.type = "Colt";
                colt.distance = 1;
                colt.suit = suits[suitIndex % suits.length];
                colt.equippedPlayer = player;
                em.persist(colt);

                for (int i = 0; i < 4 && !cards.isEmpty(); i++) {
                    Card card = cards.remove(0);
                    card.player = player;
                    player.hand.add(card);
                    em.merge(card);

                }

                for (Player player2 : game.players) {
                    if (player2 != player)
                        player.distance.add(player2);

                }

                player.weapon = colt;

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
        return game.id;
    }

    private static Game addPlayer(Scanner in, EntityManager em, Game game) {

        boolean selecting = true;

        while (selecting) {
            PlayerDAO.showPlayers(em);
            System.out.println("\n\t0 - Volver atras");

            int option = -1;
            while (option == -1) {
                System.out.print("\nSelecciona un número de jugador: ");
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();
            }

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
                        if (!transaction.isActive())
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

            int option = -1;
            while (option == -1) {
                System.out.print("\nSelecciona un número de jugador: ");
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();
            }

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

                        if (!transaction.isActive())
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

    public static void checkVictory(EntityManager em, int idGame) {

        Game game = em.find(Game.class, idGame);
        if (game == null)
            return;

        int sheriff = 0, malfactor = 0, renegade = 0, assistant = 0;

        for (Player player : game.players) {

            if (player.currentLife > 0) {
                switch (player.role.type) {
                    case SHERIFF:
                        sheriff++;
                        break;
                    case MALFACTOR:
                        malfactor++;
                        break;
                    case RENEGADE:
                        renegade++;
                        break;
                    case ASSISTANT:
                        assistant++;
                        break;
                }
            }
        }

        TypeRole winner = (sheriff == 0 && malfactor > 0) ? TypeRole.MALFACTOR
                : (malfactor == 0 && renegade == 0 && sheriff > 0) ? TypeRole.SHERIFF
                        : (sheriff == 0 && assistant == 0 && malfactor == 0 && renegade > 0) ? TypeRole.RENEGADE
                                : null;

        if (winner != null) {
            EntityTransaction transaction = em.getTransaction();

            try {

                transaction.begin();

                if (winner == TypeRole.SHERIFF)
                    game.status = "WIN " + winner + " & " + TypeRole.ASSISTANT;
                else
                    game.status = "WIN " + winner;

                System.out.println(game.status);
                game.active = false;
                em.merge(game);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive())
                    transaction.rollback();
                System.err.println(
                        "\u001B[31mError al comprobar la victoria: " + e.getMessage() + "\u001B[0m");
            }
        }
    }

    public static Suit showCard(EntityManager em, int idGame) {

        Game game = em.find(Game.class, idGame);

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

            if (!transaction.isActive())
                transaction.begin();

            Card card = game.playingCards.remove(0);
            game.discardedCards.add(0, card);

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

    public static void play(EntityManager em, int idGame, Scanner in) {

        Game game = em.find(Game.class, idGame);
        if (game == null) {
            System.out.println("\u001B[31mNo se ha encontrado la partida con ID " + idGame + ".\u001B[0m");
            return;
        }

        while (game.active) {
            Player currentPlayer = game.players.get(game.turn % game.players.size());

            System.out.print("Turno de " + currentPlayer.name + " (" + currentPlayer.role.type + ")");

            PlayerDAO.stealCard(em, currentPlayer.id, idGame);
            PlayerDAO.stealCard(em, currentPlayer.id, idGame);

            boolean playing = true;

            while (playing) {
                System.out.println(
                        "\n==================================== MENÚ DE TURNO ====================================");
                System.out.println("\t1 - Equipar arma");
                System.out.println("\t2 - Equipar equipamiento");
                System.out.println("\t3 - Usar " + TypeUse.BANG.name() + " -> " + TypeUse.BANG.description);
                System.out.println("\t4 - Usar " + TypeUse.BEER.name() + " -> " + TypeUse.BEER.description);
                System.out.println("\t5 - Mostrar mano");
                System.out.println("\t6 - Mostrar resumen de jugador");
                System.out.println("\t7 - Mostrar estado de la partida");
                System.out.println(
                        "=======================================================================================");

                System.out.println("\n\t0 - Pasar turno");

                int option = -1;
                while (option == -1) {
                    System.out.print("\nElige una número: ");
                    if (in.hasNextInt())
                        option = in.nextInt();
                    else
                        in.next();
                }

                switch (option) {
                    case 0:
                        PlayerDAO.passTurn(em, idGame, in);
                        playing = false;
                        break;
                    case 1:
                        if (PlayerDAO.hasCard(em, currentPlayer.id, WeaponCard.class)) {
                            int idCard = PlayerDAO.selectCard(em, currentPlayer.id, WeaponCard.class, in);
                            if (idCard != 0)
                                PlayerDAO.equipCard(em, currentPlayer.id, idCard);
                        } else
                            System.err.println("\n\u001B[31mNo hay cartas de arma disponibles\u001B[0m");
                        break;
                    case 2:
                        if (PlayerDAO.hasCard(em, currentPlayer.id, EquipmentCard.class)) {
                            int idCard = PlayerDAO.selectCard(em, currentPlayer.id, EquipmentCard.class, in);
                            if (idCard != 0)
                                PlayerDAO.equipCard(em, currentPlayer.id, idCard);
                        } else
                            System.err.println("\n\u001B[31mNo hay cartas de equipamiento disponibles\u001B[0m");
                        break;
                    case 3:
                        if (PlayerDAO.hasUseCard(em, currentPlayer.id, TypeUse.BANG)) {
                            int idOpponent = PlayerDAO.selectOpponent(em, currentPlayer.id, idGame, in);
                            if (idOpponent != 0)
                                PlayerDAO.useBang(em, currentPlayer.id, idOpponent, idGame);
                        } else
                            System.err.println("\n\u001B[31mNo hay cartas de este tipo de uso disponibles\u001B[0m");
                        break;
                    case 4:
                        if (PlayerDAO.hasUseCard(em, currentPlayer.id, TypeUse.BEER))
                            PlayerDAO.useBeer(em, currentPlayer.id, idGame);
                        else
                            System.err.println("\n\u001B[31mNo hay cartas de este tipo de uso disponibles\u001B[0m");
                        break;
                    case 5:
                        PlayerDAO.showHand(em, currentPlayer.id);
                        break;
                    case 6:
                        PlayerDAO.show(em, currentPlayer.id);
                        break;
                    case 7:
                        GameDAO.show(em, idGame);
                        break;

                }

                em.refresh(game);

                if (!game.active) {
                    playing = false;
                }
            }
            System.out.println("\n==================== FIN DEL JUEGO ====================");
        }
    }

}
