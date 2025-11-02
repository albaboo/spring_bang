package hibernate.projects.Controller;

import java.util.List;

import hibernate.projects.Entity.Role;
import hibernate.projects.Enum.TypeRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class RoleDAO {

    public static List<Role> list(EntityManager em) {

        List<Role> roles = em.createQuery("FROM Role", Role.class).getResultList();

        return roles;
    }

    public static void checkRoles(EntityManager em) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            for (TypeRole type : TypeRole.values()) {
                Long count = em.createQuery("SELECT COUNT(r) FROM Role r WHERE r.type = :type", Long.class)
                        .setParameter("type", type)
                        .getSingleResult();
                if (count == 0) {
                    Role role = new Role();
                    role.type = type;
                    role.objective = type.objective;
                    em.persist(role);
                }
            }
            em.flush();
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            System.err.println("\u001B[31mError comprobando roles: " + e.getMessage() + "\u001B[0m");
        }
    }
}
