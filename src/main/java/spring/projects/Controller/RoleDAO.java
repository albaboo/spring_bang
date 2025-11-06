package spring.projects.Controller;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import spring.projects.Entity.Role;
import spring.projects.Enum.TypeRole;

public class RoleDAO {

    // Devuelve todos los roles almacenados en la base de datos
    public static List<Role> list(EntityManager em) {

        List<Role> roles = em.createQuery("FROM Role", Role.class).getResultList();

        return roles;
    }

    // Verifica que todos los roles del juego existan en la base de datos.
    // Si falta alguno, lo crea automáticamente.
    public static void checkRoles(EntityManager em) {
        EntityTransaction transaction = em.getTransaction();
        try {
            if (!transaction.isActive())
                transaction.begin();

            // Recorre todos los tipos de roles del enum
            for (TypeRole type : TypeRole.values()) {
                Long count = em.createQuery("SELECT COUNT(r) FROM Role r WHERE r.type = :type", Long.class)
                        .setParameter("type", type)
                        .getSingleResult();

                // Si no existe ese tipo de rol, se crea uno nuevo
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
