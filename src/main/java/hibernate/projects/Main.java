package hibernate.projects;

import java.util.Scanner;

import hibernate.projects.Controller.GameDAO;
import hibernate.projects.Controller.PlayerDAO;
import hibernate.projects.Controller.RoleDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class Main {

    private static EntityManagerFactory emFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        try {
            if (emFactory == null)
                emFactory = Persistence.createEntityManagerFactory("projectBang");

        } catch (PersistenceException e) {
            System.err.println("Error al construir el EntityManagerFactory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
        return emFactory;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = getEntityManagerFactory().createEntityManager();
            transaction = em.getTransaction();

            RoleDAO.checkRoles(em);

            boolean playing = true;
            while (playing) {
                System.out.println("\n========== MENÚ PRINCIPAL ==========");
                System.out.println("\t1 - Jugar");
                System.out.println("\t2 - Listar jugadores");
                System.out.println("\t3 - Añadir jugador");
                System.out.println("\t4 - Salir");
                System.out.println("====================================");
                System.out.print("\nElige una número: ");
                
                int option = -1;
                if (in.hasNextInt())
                    option = in.nextInt();
                else
                    in.next();

                switch (option) {
                    case 1:
                        if (PlayerDAO.list(em).size() > 1) {
                            int idGame = GameDAO.start(em, in);
                            GameDAO.play(em, idGame, in);
                        } else
                            System.err.println("\n\u001B[31mNo hay jugadores suficientes registrados\u001B[0m");
                        break;

                    case 2:
                        if (PlayerDAO.list(em).size() > 0)
                            PlayerDAO.showPlayers(em);
                        else
                            System.err.println("\n\u001B[31mNo hay jugadores suficientes registrados\u001B[0m");
                        break;

                    case 3:
                        PlayerDAO.addPlayer(em, in);
                        break;

                    case 4:
                        playing = false;
                        break;
                    default:
                        break;
                }

            }

            System.out.println("Cerrando programa...");

        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();

            System.err.println("\n\u001B[31mError durante la ejecución del programa: " + e.getMessage() + "\u001B[0m");
        } finally {
            in.close();

            if (em != null && em.isOpen())
                em.close();

            if (emFactory != null && emFactory.isOpen())
                emFactory.close();
        }

    }

}
