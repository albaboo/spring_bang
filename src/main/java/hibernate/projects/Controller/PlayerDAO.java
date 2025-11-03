package hibernate.projects.Controller;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import hibernate.projects.Entity.Card;
import hibernate.projects.Entity.EquipmentCard;
import hibernate.projects.Entity.Game;
import hibernate.projects.Entity.Player;
import hibernate.projects.Entity.UseCard;
import hibernate.projects.Entity.WeaponCard;
import hibernate.projects.Enum.Suit;
import hibernate.projects.Enum.TypeCard;
import hibernate.projects.Enum.TypeEquipment;
import hibernate.projects.Enum.TypeUse;
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

            return new ArrayDeque<>(player.hand);
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

    public static void useBang(EntityManager em, int idAttacker, int idDefender, int idGame) {
        EntityTransaction transaction = em.getTransaction();

        try {

            transaction.begin();

            Player attacker = em.find(Player.class, idAttacker);
            Player defender = em.find(Player.class, idDefender);

            if (attacker == null || defender == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            UseCard bang = null;

            for (Card card : attacker.hand) {
                if (card.name == TypeCard.USE.name()) {
                    UseCard use = em.find(UseCard.class, card.id);
                    if (use.type == TypeUse.BANG) {
                        bang = use;
                        break;
                    }
                }
            }

            if (bang == null) {
                System.err.println("\n\u001B[31mEl jugador no tiene una carta Bang para usar.\u001B[0m");
                return;
            }

            if (!checkDistanceAttack(em, attacker.id, defender.id)) {
                System.err.println("\n\u001B[31mLa distancia entre jugadores no es valida para el ataque.\u001B[0m");
                return;
            }

            discardCard(em, idAttacker, bang.id, idGame);

            System.out.println("El jugador " + attacker.name + " ha jugado un BANG! contra " + defender.name);

            UseCard failed = null;
            for (Card card : defender.hand) {
                if (card.name == TypeCard.USE.name()) {
                    UseCard use = em.find(UseCard.class, card.id);
                    if (use.type == TypeUse.FAILED) {
                        failed = use;
                        break;
                    }
                }
            }

            if (failed == null) {

                EquipmentCard barrel = null;
                for (EquipmentCard card : defender.equipments) {
                    if (card.name == TypeCard.EQUIPMENT.name()) {
                        if (card.type == TypeEquipment.BARREL) {
                            barrel = card;
                            break;
                        }
                    }
                }

                if (barrel == null) {
                    defender.currentLife--;
                    System.out.println(defender.name + " ha perdido una vida.");
                } else {
                    discardCard(em, idDefender, barrel.id, idGame);
                    Suit suit = GameDAO.showCard(em, idGame);
                    if (suit == Suit.HEART) {
                        System.out.println(defender.name + " ha usado una carta BARRIL para evitar el daño.");
                    } else {
                        defender.currentLife--;
                        System.out.println(defender.name + " ha perdido una vida.");
                    }
                }
            } else {
                discardCard(em, idDefender, failed.id, idGame);
                System.out.println(defender.name + " ha usado una carta FALLASTE para evitar el daño.");
            }

            em.merge(attacker);
            em.merge(defender);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\n\u001B[31mError durante el ataque: " + e.getMessage() + "\u001B[0m");
        }

    }

    public static void discardCard(EntityManager em, int idPlayer, int idCard, int idGame) {
        EntityTransaction transaction = em.getTransaction();
        try {

            transaction.begin();

            Player player = em.find(Player.class, idPlayer);

            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            Card card = null;

            for (Card cardHCard : player.hand) {
                if (cardHCard.id == idCard) {
                    card = cardHCard;
                    break;
                }
            }

            if (card == null) {
                System.err.println("\n\u001B[31mCarta no encontrada en la mano del jugador.\u001B[0m");
                return;
            }

            Game game = em.find(Game.class, idGame);

            if (game == null || !game.active) {
                System.err.println("\n\u001B[31mEl juego no se encuentra o no está activo.\u001B[0m");
                return;
            }

            if (!game.players.contains(player)) {
                System.err.println("\n\u001B[31mEl jugador no está participando en el juego seleccionado.\u001B[0m");
                return;
            }

            if (!game.playingCards.contains(card)) {
                System.err.println("\n\u001B[31mLa carta no está en el juego activo.\u001B[0m");
                return;
            }

            player.hand.remove(card);
            card.gamesDiscarded.add(game);

            em.merge(player);
            em.merge(game);
            transaction.commit();

            System.out.println("Carta descartada: " + card.name);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\n\u001B[31mError durante la eliminación de la carta: " + e.getMessage() + "\u001B[0m");
        }
    }

    public static boolean checkElimination(EntityManager em, int idPlayer, int idGame) {
        EntityTransaction transaction = em.getTransaction();
        try {

            transaction.begin();

            Player player = em.find(Player.class, idPlayer);

            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return false;
            }

            Game game = em.find(Game.class, idGame);

            if (game == null || !game.active) {
                System.err.println("\n\u001B[31mJuego no encontrado.\u001B[0m");
                return false;
            }

            if (!game.players.contains(player)) {
                System.err.println("\n\u001B[31mEl jugador no está participando en el juego seleccionado.\u001B[0m");
                return false;
            }

            if (player.currentLife <= 0) {

                game.players.remove(player);

                List<Card> hand = player.hand;
                player.hand.clear();
                game.discardedCards.addAll(hand);

                em.merge(game);
                em.merge(player);

                System.out.println("El jugador " + player.name + " ha sido eliminado del juego.");

            }

            transaction.commit();

            return GameDAO.checkVictory(em, idGame);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println(
                    "\n\u001B[31mError al comprobar la eliminación del jugador: " + e.getMessage() + "\u001B[0m");
        }

        return false;

    }

    public static void stealCard(EntityManager em, int idPlayer, int idGame) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Player player = em.find(Player.class, idPlayer);
            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            Game game = em.find(Game.class, idGame);

            if (game == null || !game.active) {
                System.err.println("\n\u001B[31mJuego no encontrado.\u001B[0m");
                return;
            }

            if (!game.players.contains(player)) {
                System.err.println("\n\u001B[31mEl jugador no está participando en el juego seleccionado.\u001B[0m");
                return;
            }

            if (game.playingCards.isEmpty()) {
                System.err.println("\n\u001B[31mNo hay cartas disponibles para robar.\u001B[0m");
                return;
            }

            Card card = game.playingCards.remove(0);

            if (card == null) {
                System.err.println("\n\u001B[31mCarta no disponible.\u001B[0m");
                return;
            }

            player.hand.add(card);

            em.merge(player);
            em.merge(game);
            transaction.commit();

            System.out.println("El jugador " + player.name + " ha robado la carta " + card.name + ".");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError al robar la carta: " + e.getMessage() + "\u001B[0m");
        }

    }

    public static void passTurn(EntityManager em, int idGame) {
        EntityTransaction transaction = em.getTransaction();
        Scanner in = new Scanner(System.in);
        try {
            transaction.begin();

            Game game = em.find(Game.class, idGame);

            if (game == null || !game.active) {
                System.err.println("\n\u001B[31mJuego no encontrado.\u001B[0m");
                return;
            }

            Player player = game.players.get(game.turn % game.players.size());

            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            while (player.hand.size() > 4) {
                System.out.println("\nTienes más de 4 cartas en la mano. Debes descartar una carta.");
                showHand(em, player.id);
                System.out.print("\nElige una número: ");
                int option = in.nextInt();

                // encontrar carta por id
                Card card = null;
                for (Card cardHand : player.hand) {
                    if (cardHand.id == option) {
                        card = cardHand;
                        break;
                    }
                }

                if (card == null) {
                    System.out.println("\n\u001B[31mCarta no encontrada.\u001B[0m");
                } else {
                    discardCard(em, player.id, card.id, idGame);
                    player = em.find(Player.class, player.id);
                }
            }

            game.turn++;

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError al pasar el turno: " + e.getMessage() + "\u001B[0m");
        } finally {
            in.close();
        }

    }

    public static void equipCard(EntityManager em, int idPlayer, int idCard) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Player player = em.find(Player.class, idPlayer);
            Card card = em.find(Card.class, idCard);

            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            if (card == null) {
                System.err.println("\n\u001B[31mCarta no encontrada.\u001B[0m");
                return;
            }

            if (!player.hand.contains(card)) {
                System.err.println("\n\u001B[31mEl jugador no tiene esta carta en la mano.\u001B[0m");
                return;
            }

            if (card.name == TypeCard.WEAPON.name()) {
                WeaponCard weapon = em.find(WeaponCard.class, card.id);
                player.weapon = weapon;
                System.out.println(player.name + " ha equipado el arma " + weapon.name);
            } else if (card.name == TypeCard.EQUIPMENT.name()) {
                boolean hasSame = false;
                EquipmentCard cardToEquip = em.find(EquipmentCard.class, card.id);
                for (EquipmentCard equipment : player.equipments) {
                    if (equipment.name == cardToEquip.name && cardToEquip.type == equipment.type) {
                        hasSame = true;
                        break;
                    }
                }
                if (!hasSame) {
                    player.equipments.add(cardToEquip);
                    player.hand.remove(card);
                    System.out.println(player.name + " ha equipado el equipo " + cardToEquip.name);
                } else {
                    System.err.println("\n\u001B[31mEl jugador ya tiene un equipo de ese tipo.\u001B[0m");
                }
            }
            em.merge(player);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError al equipar la carta: " + e.getMessage() + "\u001B[0m");
        }

    }

    public static int calculateDistance(EntityManager em, int idAttacker, int idDefender) {
        try {
            Player attacker = em.find(Player.class, idAttacker);
            Player defender = em.find(Player.class, idDefender);

            if (attacker == null || defender == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return 0;
            }

            int distance = Math.abs(attacker.id - defender.id);

            for (Card card : attacker.equipments) {
                if (card.name == TypeCard.EQUIPMENT.name()) {
                    EquipmentCard equipment = em.find(EquipmentCard.class, card.id);
                    if (equipment.type == TypeEquipment.HORSE) {
                        distance -= equipment.distanceModifier;
                    } else if (equipment.type == TypeEquipment.TELESCOPIC_SIGHT) {
                        distance -= equipment.distanceModifier;
                    }
                }
            }

            for (Card card : defender.equipments) {
                if (card.name == TypeCard.EQUIPMENT.name()) {
                    EquipmentCard equipment = em.find(EquipmentCard.class, card.id);
                    if (equipment.type == TypeEquipment.HORSE) {
                        distance += equipment.distanceModifier;
                    }
                }
            }

            distance -= attacker.offDistanceModifier + defender.defDistanceModifier;

            return Math.max(distance, 1);
        } catch (Exception e) {
            System.err.println("\n\u001B[31mError al calcular la distancia: " + e.getMessage() + "\u001B[0m");
        }

        return 1;
    }

    public static boolean checkDistanceAttack(EntityManager em, int idAttacker, int idDefender) {

        Player attacker = em.find(Player.class, idAttacker);

        int distance = calculateDistance(em, idAttacker, idDefender);

        return distance <= attacker.weapon.distance;
    }

}
