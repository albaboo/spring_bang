package hibernate.projects.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "status", columnDefinition = "VARCHAR(255) DEFAULT 'Start'")
    public String status = "Start";

    @Column(name = "turn", nullable = false, columnDefinition = "INT DEFAULT 0")
    public int turn = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Date startDate = new Date();

    @Column(name = "active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    public boolean active = true;

    /** Relaciones */

    @ManyToMany(mappedBy = "games")
    public Set<Player> players = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "game_playing_cards", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "card_id"))
    public List<Card> playingCards = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "game_discarded_cards", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "card_id"))
    public List<Card> discardedCards = new ArrayList<>();

}
