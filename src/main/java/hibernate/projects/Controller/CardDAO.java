package hibernate.projects.Controller;

import java.util.Arrays;
import java.util.List;

import hibernate.projects.Entity.Card;
import hibernate.projects.Entity.EquipmentCard;
import hibernate.projects.Entity.UseCard;
import hibernate.projects.Enum.Suit;
import hibernate.projects.Enum.TypeEquipment;
import hibernate.projects.Enum.TypeUse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class CardDAO {

    public static List<Card> list(EntityManager em) {

        List<Card> cards = em.createQuery("FROM Card", Card.class).getResultList();

        return cards;
    }

    public static List<Card> shuffle(EntityManager em) {

        List<Card> cards = em.createQuery("SELECT c FROM Card c ORDER BY function('RAND')", Card.class).getResultList();

        return cards;
    }

    public static void checkCards(EntityManager em) {
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            final int NUMBER_CARDS = 80;

            Long total = em.createQuery("SELECT COUNT(c) FROM Card c", Long.class).getSingleResult();

            Long existing = total != null ? total : 0;

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
                    useCard.name = type.name();
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
                            equipmentCard.name = type.name();
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
