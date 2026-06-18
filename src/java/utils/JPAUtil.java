package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Provides a single EntityManagerFactory for JPA access.
 */
public final class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "PRJ301-EVehvicleRentalPU";
    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static <T> T execute(EntityManagerCallback<T> callback) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return callback.execute(em);
        } finally {
            close(em);
        }
    }

    public static <T> T executeInTransaction(EntityManagerCallback<T> callback) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            T result = callback.execute(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            close(em);
        }
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    private static void close(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    private static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory;
    }

    public interface EntityManagerCallback<T> {
        T execute(EntityManager em);
    }
}
