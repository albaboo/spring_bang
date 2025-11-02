package hibernate.projects.Entity;

import java.util.ArrayList;
import java.util.List;

import hibernate.projects.Enum.TypeRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public TypeRole type;

    @Column(name = "objective")
    public String objective;

    /** Relaciones */

    @OneToMany(mappedBy = "role")
    public List<Player> players = new ArrayList<Player>();

}
