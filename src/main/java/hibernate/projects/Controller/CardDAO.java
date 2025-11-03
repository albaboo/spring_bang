package hibernate.projects.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hibernate.projects.Entity.Card;
import hibernate.projects.Entity.EquipmentCard;
import hibernate.projects.Entity.Game;
import hibernate.projects.Entity.UseCard;
import hibernate.projects.Enum.Suit;
import hibernate.projects.Enum.TypeCard;
import hibernate.projects.Enum.TypeEquipment;
import hibernate.projects.Enum.TypeUse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class CardDAO {

    public static List<Card> list(EntityManager em, Game game) {

        List<Card> cards = em.createQuery("FROM Card c WHERE :game MEMBER OF c.gamesPlaying", Card.class)
                .setParameter("game", game)
                .getResultList();

        return cards;
    }

    public static List<Card> shuffle(EntityManager em, Game game) {

        List<Card> cards = em
                .createQuery("SELECT c FROM Card c WHERE :game MEMBER OF c.gamesPlaying ORDER BY function('RAND')",
                        Card.class)
                .setParameter("game", game)
                .getResultList();

        Collections.shuffle(cards);
        return cards;
    }

    public static void checkCards(EntityManager em, Game game) {
        EntityTransaction transaction = em.getTransaction();
        try {
            if (!transaction.isActive())
                transaction.begin();

            final int NUMBER_CARDS = 80;
            Long existing = 0L;

            Suit[] suits = Suit.values();
            int suitIndex = 0;

            while (existing < NUMBER_CARDS) {

                List<TypeUse> uses = Arrays.asList(TypeUse.values());
                int useCount = uses.size();
                int created = 0;
                int index = 0;
                while (created < 43 && existing < NUMBER_CARDS) {
                    TypeUse type = uses.get(index % useCount);
                    UseCard useCard = new UseCard();
                    useCard.gamesPlaying.add(game);
                    game.playingCards.add(useCard);
                    useCard.name = TypeCard.USE.name();
                    useCard.description = type.description;
                    useCard.type = type;
                    useCard.suit = suits[suitIndex % suits.length];
                    em.persist(useCard);

                    created++;
                    existing++;
                    suitIndex++;
                    if (created == 6 || created == 18)
                        index++;
                }

                if (existing < NUMBER_CARDS) {
                    for (int i = 0; i < 4 && existing < NUMBER_CARDS; i++) {
                        for (TypeEquipment type : TypeEquipment.values()) {
                            EquipmentCard equipmentCard = new EquipmentCard();
                            equipmentCard.gamesPlaying.add(game);
                            game.playingCards.add(equipmentCard);
                            equipmentCard.name = TypeCard.EQUIPMENT.name();
                            equipmentCard.description = type.description;
                            equipmentCard.type = type;
                            if (type == TypeEquipment.HORSE || type == TypeEquipment.TELESCOPIC_SIGHT)
                                equipmentCard.distanceModifier = 1;
                            else
                                equipmentCard.distanceModifier = 0;

                            equipmentCard.suit = suits[suitIndex % suits.length];
                            em.persist(equipmentCard);

                            suitIndex++;
                            existing++;
                        }
                    }

                }
            }

            em.flush();
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\u001B[31mError comprobando cartas: " + e.getMessage() + "\u001B[0m");
        }
    }

}
