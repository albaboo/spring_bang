package hibernate.projects.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "weapon_card")
public class WeaponCard extends Card {

    @Column(name = "distance")
    public int distance;

    /** Relacions */

    @OneToOne(mappedBy = "weapon", orphanRemoval = true)
    public Player equippedPlayer;
}
