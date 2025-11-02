package hibernate.projects.Entity;

import java.util.ArrayDeque;
import java.util.Deque;

import hibernate.projects.Enum.Suit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "card")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "suit")
    public Suit suit;

    /** Relaciones */

    @ManyToOne
    @JoinColumn(name = "player_id")
    public Player player;

    @ManyToMany(mappedBy = "playingCards")
    public Deque<Game> gamesPlaying = new ArrayDeque<>();

    @ManyToMany(mappedBy = "discardedCards")
    public Deque<Game> gamesDiscarded = new ArrayDeque<>();

    @Override
    public String toString() {
        String suitName = (suit != null) ? suit.name() : "Sense pal";

        return "Card{" +
                "name='" + name + '\'' +
                ", description='" + (description != null ? description : "") + '\'' +
                ", suit=" + suitName + '}';
    }

}
