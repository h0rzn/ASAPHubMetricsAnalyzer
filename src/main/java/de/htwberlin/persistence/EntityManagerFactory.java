package de.htwberlin.persistence;

import jakarta.persistence.Persistence;

public class EntityManagerFactory implements AutoCloseable {

    private final jakarta.persistence.EntityManagerFactory emf;

    public EntityManagerFactory() {
        this.emf = Persistence.createEntityManagerFactory("tool-pu");
    }

    public void persist(Object entity) {
        jakarta.persistence.EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        emf.close();
    }
}
