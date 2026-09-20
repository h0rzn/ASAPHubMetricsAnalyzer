package de.htwberlin.persistence;

import de.htwberlin.model.ConnectionRequest;
import de.htwberlin.model.Session;
import de.htwberlin.model.StartDataSession;
import de.htwberlin.model.Register;
import de.htwberlin.processor.MetricsRepository;
import jakarta.persistence.Persistence;

import java.util.List;

/**
 * Hibernate-based implementation of {@link MetricsRepository}. Manages the JPA
 * {@link jakarta.persistence.EntityManagerFactory} lifecycle. Each write operation
 * runs in its own transaction and rolls back on failure.
 */
public class EntityManagerFactory implements AutoCloseable, MetricsRepository {
    private final jakarta.persistence.EntityManagerFactory emf;

    public EntityManagerFactory() {
        this.emf = Persistence.createEntityManagerFactory(
                "hub-metrics-persistence-unit"
        );
    }

    /**
     * Persists the given entity in a single transaction. Rolls back and rethrows on failure.
     */
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
    public Session createSession() {
        Session session = new Session();
        persist(session);
        return session;
    }

    @Override
    public List<Register> findAllPeerInfos() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("FROM Register", Register.class).getResultList();
        }
    }

    @Override
    public List<ConnectionRequest> findAllConnectionRequests() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("FROM ConnectionRequest", ConnectionRequest.class).getResultList();
        }
    }

    @Override
    public List<StartDataSession> findAllDataSessions() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("FROM StartDataSession", StartDataSession.class).getResultList();
        }
    }

    @Override
    public void close() {
        emf.close();
    }
}
