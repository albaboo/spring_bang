package spring.projects.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import spring.projects.Entity.Card;
import spring.projects.Entity.EquipmentCard;
import spring.projects.Entity.Game;
import spring.projects.Entity.Player;
import spring.projects.Entity.UseCard;
import spring.projects.Entity.WeaponCard;
import spring.projects.Enum.Suit;
import spring.projects.Enum.TypeCard;
import spring.projects.Enum.TypeEquipment;
import spring.projects.Enum.TypeUse;

public class PlayerDAO {

    // Devuelve la lista de jugadores registrados en la base de datos
    public static List<Player> list(EntityManager em) {

        List<Player> players = em.createQuery("FROM Player", Player.class).getResultList();

        return players;
    }

     // Muestra la lista de jugadores registrados en la base de datos
    public static void showPlayers(EntityManager em) {
        List<Player> players = new ArrayList<Player>();

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

    // Permite añadir un nuevo jugador a la base de datos
    public static void addPlayer(EntityManager em, Scanner in) {
        EntityTransaction transaction = em.getTransaction();

        boolean creating = true;
        in.nextLine();
        while (creating) {
            System.out.print("\nEscribe un nombre de jugador: ");
            String name = in.nextLine();

            if (name.length() > 0) {
                Player newPlayer = new Player();
                newPlayer.name = name;
                try {
                    if (!transaction.isActive())
                        transaction.begin();

                    em.persist(newPlayer);
                    transaction.commit();
                    System.out.println("Añadido: " + newPlayer.name);
                    creating = false;
                } catch (Exception e) {
                    if (transaction != null && transaction.isActive())
                        transaction.rollback();
                    System.err.println(
                            "\n\u001B[31mError durante la inserción de datos: " + e.getMessage() + "\u001B[0m");
                }
            }

        }

    }

    // Selecciona a un oponente para atacar
    public static int selectOpponent(EntityManager em, int idPlayer, int idGame, Scanner in) {

        boolean selecting = true;
        Player selectedPlayer = null;
        Game game = em.find(Game.class, idGame);

        while (selecting) {
            PlayerDAO.showPlayers(em);
            System.out.print("\t0 - Volver atras");

            int option = -1;
            while (option == -1) {
                System.out.print("\nSelecciona un número de jugador: ");
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();
            }

            if (option == 0)
                return 0;

            selectedPlayer = em.find(Player.class, option);

            if (selectedPlayer == null || !game.players.contains(selectedPlayer))
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
            else if (selectedPlayer.id == idPlayer)
                System.err.println("\n\u001B[31mEl jugador oponente no puede ser el mismo que el atacante.\u001B[0m");
            else
                selecting = false;

        }

        return selectedPlayer.id;
    }

    // Devuelve la mano de un jugador
    public static List<Card> getHand(EntityManager em, int idPlayer) {

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

    // Muestra la mano de un jugador
    public static void showHand(EntityManager em, int idPlayer) {

        try {
            List<Card> hand = getHand(em, idPlayer);

            if (hand == null || hand.isEmpty()) {
                System.out.println("\u001B[31mNo se ha encontrado ninguna carta en la mano del jugador con ID "
                        + idPlayer + ".\u001B[0m");
                return;
            }

            System.out.println(
                    "\n==================================== MANO DEL JUGADOR ====================================");
            for (Card card : hand) {
                System.out.println("\t" + card);
            }
            System.out
                    .println("=======================================================================================");

        } catch (PersistenceException e) {
            System.err.println(
                    "\n\u001B[31mError durante la recuperación de la mano del jugador: " + e.getMessage()
                            + "\u001B[0m");
        }
    }

    // Muestra la información de un jugador
    public static void show(EntityManager em, int idPlayer) {
        Player player = em.find(Player.class, idPlayer);
        em.refresh(player);
        if (player == null) {
            System.out.println("\u001B[31mNo se ha encontrado ningún jugador con ID " + idPlayer + ".\u001B[0m");
            return;
        }

        if (player.role == null) {
            System.out.println("\u001B[31mEl jugador con ID " + idPlayer + " no tiene un rol asignado.\u001B[0m");
            return;
        }

        System.out.println(
                "\n==================================== RESUMEN DEL JUGADOR ====================================");
        System.out.println("\tRol: " + player.role.type);
        System.out.println("\tObjetivo: " + player.role.objective);
        System.out.println("\tVida actual: " + player.currentLife + "/" + player.maxLife);
        System.out.println("\tArma: " + player.weapon.type);
        System.out.println("\tEquipamiento:" + player.equipments.size());
        if (player.equipments.size() > 0) {
            for (EquipmentCard card : player.equipments) {
                if (card.type != TypeEquipment.BARREL)
                    System.out.println("\t\t" + card.name + " - " + card.type + ", " + card.description
                            + " ( Modifier: " + card.distanceModifier + ", Suit: " + card.suit + " )");
                else
                    System.out.println("\t\t" + card.name + " - " + card.type + ", " + card.description
                            + " ( Suit: " + card.suit + " )");
            }
        }
        System.out.println(
                "===============================================================================================");
    }

    // Utiliza una carta de cerveza
    public static void useBeer(EntityManager em, int idPlayer, int idGame) {

        EntityTransaction transaction = em.getTransaction();

        try {
            if (!transaction.isActive())
                transaction.begin();

            Player player = em.find(Player.class, idPlayer);
            Game game = em.find(Game.class, idGame);

            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            if (game == null) {
                System.err.println("\n\u001B[31mPartida no encontrada.\u001B[0m");
                return;
            }

            for (Card card : player.hand) {
                if (TypeCard.USE.name().equals(card.name)) {
                    UseCard use = (UseCard) card;
                    if (use.type == TypeUse.BEER) {
                        if (player.currentLife + 1 <= player.maxLife) {
                            player.currentLife++;
                            discardCard(em, idPlayer, use.id, idGame);
                            transaction.commit();
                            System.out.println(player.name + " ha usado una carta BEER y ha recuperado 1 vida.");
                        } else {
                            System.out.println(player.name + " ya tiene la vida máxima.");
                        }
                        break;
                    }
                }
            }

        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\n\u001B[31mError durante el uso de la carta BEER: " + e.getMessage() + "\u001B[0m");
            return;
        }
    }

    // Utiliza una carta BANG
    public static void useBang(EntityManager em, int idAttacker, int idDefender, int idGame) {
        EntityTransaction transaction = em.getTransaction();

        try {

            if (!transaction.isActive())
                transaction.begin();

            Player attacker = em.find(Player.class, idAttacker);
            Player defender = em.find(Player.class, idDefender);
            em.refresh(attacker);
            em.refresh(defender);

            if (attacker == null || defender == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            UseCard bang = null;

            for (Card card : attacker.hand) {
                if (TypeCard.USE.name().equals(card.name)) {
                    UseCard use = (UseCard) card;
                    if (use.type == TypeUse.BANG) {
                        bang = use;
                        break;
                    }
                }
            }

            if (bang == null) {
                System.err.println("\n\u001B[31mEl jugador no tiene una carta BANG para usar.\u001B[0m");
                return;
            }

            if (!checkDistanceAttack(em, attacker.id, defender.id)) {
                System.err.println("\n\u001B[31mLa distancia entre jugadores no es valida para el ataque.\u001B[0m");
                return;
            }

            discardCard(em, idAttacker, bang.id, idGame);

            System.out.println("El jugador " + attacker.name + " ha jugado un BANG contra " + defender.name);

            UseCard failed = null;
            for (Card card : defender.hand) {
                if (TypeCard.USE.name().equals(card.name)) {
                    UseCard use = (UseCard) card;
                    if (use.type == TypeUse.FAILED) {
                        failed = use;
                        break;
                    }
                }
            }

            if (failed == null) {
                EquipmentCard barrel = null;
                for (EquipmentCard card : defender.equipments) {
                    if (TypeCard.EQUIPMENT.name().equals(card.name)) {
                        if (card.type == TypeEquipment.BARREL) {
                            barrel = card;
                            break;
                        }
                    }
                }

                if (barrel == null) {
                    defender.currentLife--;
                    em.merge(defender);
                    System.out.println(defender.name + " ha perdido una vida.");
                } else {
                    discardCard(em, idDefender, barrel.id, idGame);
                    Suit suit = GameDAO.showCard(em, idGame);
                    if (suit == Suit.HEART) {
                        System.out.println(defender.name + " ha usado una carta BARRIL para evitar el daño.");
                    } else {
                        defender.currentLife--;
                        em.merge(defender);
                        System.out.println(defender.name + " ha perdido una vida.");
                    }
                }
            } else {
                discardCard(em, idDefender, failed.id, idGame);
                System.out.println(defender.name + " ha usado una carta FALLASTE para evitar el daño.");
            }

            transaction.commit();

            checkElimination(em, idDefender, idGame);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\n\u001B[31mError durante el ataque: " + e.getMessage() + "\u001B[0m");
        }

    }

    // Descarta una carta de un jugador
    public static void discardCard(EntityManager em, int idPlayer, int idCard, int idGame) {
        try {

            Player player = em.find(Player.class, idPlayer);

            if (player == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return;
            }

            Card card = null;

            for (Card cardFind : player.hand) {
                if (cardFind.id == idCard) {
                    card = cardFind;
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

            if (!player.hand.contains(card)) {
                System.err.println("\n\u001B[31mLa carta no está en la mano del jugador.\u001B[0m");
                return;
            }

            card.player = null;
            player.hand.remove(card);
            game.discardedCards.add(card);
            card.gamesDiscarded.add(game);

            em.merge(game);
            em.merge(card);

            System.out.println("Carta descartada: " + card.name);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // Comprueba si un jugador ha sido eliminado
    public static void checkElimination(EntityManager em, int idPlayer, int idGame) {
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

            if (player.currentLife <= 0) {
                game.players.remove(player);

                List<Card> hand = new ArrayList<>(player.hand);

                game.discardedCards.addAll(hand);
                for (Card card : hand) {
                    card.player = null;
                    card.gamesDiscarded.add(game);
                    em.merge(card);
                }

                em.merge(game);

                System.out.println("El jugador " + player.name + " ha sido eliminado del juego.");

            }

            transaction.commit();
            GameDAO.checkVictory(em, idGame);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println(
                    "\n\u001B[31mError al comprobar la eliminación del jugador: " + e.getMessage() + "\u001B[0m");
        }

    }

    // Roba una carta de la pila
    public static void stealCard(EntityManager em, int idPlayer, int idGame) {
        EntityTransaction transaction = em.getTransaction();

        try {
            if (!transaction.isActive())
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

            Collections.shuffle(game.playingCards);

            Card card = game.playingCards.remove(0);

            card.gamesPlaying.remove(game);
            card.player = player;
            player.hand.add(card);

            em.merge(game);
            em.merge(card);
            transaction.commit();

            System.out.println("El jugador " + player.name + " ha robado la carta " + card.name + ".");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError al robar la carta: " + e.getMessage() + "\u001B[0m");
        }

    }
    
    // Pasa el turno al siguiente jugador y hace antes las comprovaciones del limite de cartas del jugador actual
    public static void passTurn(EntityManager em, int idGame, Scanner in) {
        EntityTransaction transaction = em.getTransaction();
        try {
            if (!transaction.isActive())
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

                int option = -1;
                while (option == -1) {
                    System.out.print("\nElige una número: ");
                    if (in.hasNextInt())
                        option = in.nextInt();
                    else
                        in.next();
                }

                Card card = null;
                for (Card cardHand : player.hand) {
                    if (cardHand.id == option) {
                        card = cardHand;
                        break;
                    }
                }

                if (card == null)
                    System.out.println("\n\u001B[31mCarta no encontrada.\u001B[0m");
                else {
                    discardCard(em, player.id, card.id, idGame);
                    player = em.find(Player.class, player.id);
                }
            }

            game.turn++;

            em.merge(game);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError al pasar el turno: " + e.getMessage() + "\u001B[0m");
        }

    }

    // Permite al jugador seleccionar una carta de su mano
    public static int selectCard(EntityManager em, int idPlayer, Class<? extends Card> type, Scanner in) {
        boolean choosing = true;
        Card card = null;
        Player player = em.find(Player.class, idPlayer);

        while (choosing) {
            showHand(em, idPlayer);
            System.out.print("\t0 - Volver atras");

            int option = -1;
            while (option == -1) {
                System.out.print("\nElige una número: ");
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();
            }

            if (option == 0)
                return 0;

            for (Card cardHand : player.hand) {
                if (cardHand.id == option) {
                    card = cardHand;
                    break;
                }
            }

            if (card == null)
                System.out.println("\n\u001B[31mCarta no encontrada.\u001B[0m");
            else if (type.isInstance(card))
                choosing = false;

        }
        return card.id;
    }

    // Permite al jugador equipar una carta
    public static void equipCard(EntityManager em, int idPlayer, int idCard) {
        EntityTransaction transaction = em.getTransaction();
        try {
            if (!transaction.isActive())
                transaction.begin();

            Player player = em.find(Player.class, idPlayer);
            Card card = em.find(Card.class, idCard);

            em.refresh(player);

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

            if (TypeCard.WEAPON.name().equals(card.name)) {
                WeaponCard weapon = (WeaponCard) card;
                card.player = null;
                player.hand.remove(card);
                em.merge(card);
                System.out.println(player.name + " ha equipado el arma " + weapon.type);

            } else if (TypeCard.EQUIPMENT.name().equals(card.name)) {
                boolean hasSame = false;
                EquipmentCard cardToEquip = (EquipmentCard) card;

                for (EquipmentCard equipment : player.equipments) {
                    if (equipment.name.equals(cardToEquip.name) && cardToEquip.type == equipment.type) {
                        hasSame = true;
                        break;
                    }
                }

                if (!hasSame) {
                    cardToEquip.equippedPlayer = player;
                    player.hand.remove(cardToEquip);
                    em.merge(cardToEquip);
                    System.out.println(player.name + " ha equipado el equipo " + cardToEquip.type);
                } else {
                    System.err.println("\n\u001B[31mEl jugador ya tiene un equipo de ese tipo.\u001B[0m");
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError al equipar la carta: " + e.getMessage() + "\u001B[0m");
        }

    }

    // Calcula la distancia entre dos jugadores
    public static int calculateDistance(EntityManager em, int idAttacker, int idDefender) {
        try {
            Player attacker = em.find(Player.class, idAttacker);
            Player defender = em.find(Player.class, idDefender);
            em.refresh(attacker);
            em.refresh(defender);

            if (attacker == null || defender == null) {
                System.err.println("\n\u001B[31mJugador no encontrado.\u001B[0m");
                return 0;
            }

            int distance = Math.abs(attacker.id - defender.id);

            for (Card card : attacker.equipments) {
                if (TypeCard.EQUIPMENT.name().equals(card.name)) {
                    EquipmentCard equipment = (EquipmentCard) card;
                    if (equipment.type == TypeEquipment.HORSE)
                        distance -= equipment.distanceModifier;
                    else if (equipment.type == TypeEquipment.TELESCOPIC_SIGHT)
                        distance -= equipment.distanceModifier;

                }
            }

            for (Card card : defender.equipments) {
                if (TypeCard.EQUIPMENT.name().equals(card.name)) {
                    EquipmentCard equipment = (EquipmentCard) card;

                    if (equipment.type == TypeEquipment.HORSE)
                        distance += equipment.distanceModifier;

                }
            }

            distance -= attacker.offDistanceModifier + defender.defDistanceModifier;

            return Math.max(distance, 1);
        } catch (Exception e) {
            System.err.println("\n\u001B[31mError al calcular la distancia: " + e.getMessage() + "\u001B[0m");
        }

        return 1;
    }

    // Comprueba si un ataque es válido
    public static boolean checkDistanceAttack(EntityManager em, int idAttacker, int idDefender) {

        Player attacker = em.find(Player.class, idAttacker);

        if (attacker.weapon == null)
            return false;

        int distance = calculateDistance(em, idAttacker, idDefender);

        return distance <= attacker.weapon.distance;
    }

    // Comprueba si un jugador tiene una carta de un tipo específico
    public static boolean hasCard(EntityManager em, int playerId, Class<? extends Card> type) {
        Player player = em.find(Player.class, playerId);

        if (player == null || player.hand == null)
            return false;

        for (Card card : player.hand) {
            if (type.isInstance(card))
                return true;

        }
        return false;
    }

    // Comprueba si un jugador tiene una carta de uso específico
    public static boolean hasUseCard(EntityManager em, int playerId, TypeUse type) {
        Player player = em.find(Player.class, playerId);

        if (player == null || player.hand == null)
            return false;

        for (Card card : player.hand) {
            if (card instanceof UseCard) {
                UseCard useCard = (UseCard) card;
                if (useCard.type == type)
                    return true;
            }
        }
        return false;
    }
}
