package hibernate.projects.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "current_life")
    public int currentLife;

    @Column(name = "max_life")
    public int maxLife;

    @Column(name = "def_distance_modifier")
    public int defDistanceModifier;

    @Column(name = "off_distance_modifier")
    public int offDistanceModifier;

    /** Relaciones */

    @ManyToOne
    @JoinColumn(name = "role_id")
    public Role role;

    @OneToOne
    @JoinColumn(name = "weapon_id", referencedColumnName = "id")
    public WeaponCard weapon;

    @OneToMany(mappedBy = "equippedPlayer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<EquipmentCard> equipments = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Card> hand = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "player_game", joinColumns = @JoinColumn(name = "player_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
    public List<Game> games = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "distance_players", joinColumns = @JoinColumn(name = "player1_id"), inverseJoinColumns = @JoinColumn(name = "player2_id"))
    public List<Player> distance = new ArrayList<>();

}
