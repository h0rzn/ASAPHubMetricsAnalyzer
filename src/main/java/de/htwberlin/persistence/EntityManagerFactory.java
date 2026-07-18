package de.htwberlin.persistence;

import de.htwberlin.model.ConnectionRequestInfo;
import de.htwberlin.model.DataSessionInfo;
import de.htwberlin.model.PeerInfo;
import de.htwberlin.processor.MetricsRepository;
import jakarta.persistence.Persistence;

import java.util.List;

public class EntityManagerFactory implements AutoCloseable, MetricsRepository {
    private final jakarta.persistence.EntityManagerFactory emf;

    public EntityManagerFactory() {
        this.emf = Persistence.createEntityManagerFactory(
                "hub-metrics-persistence-unit"
        );
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
    public List<PeerInfo> findAllPeerInfos() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("FROM PeerInfo", PeerInfo.class).getResultList();
        }
    }

    @Override
    public List<ConnectionRequestInfo> findAllConnectionRequests() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("FROM ConnectionRequestInfo", ConnectionRequestInfo.class).getResultList();
        }
    }

    @Override
    public List<DataSessionInfo> findAllDataSessions() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("FROM DataSessionInfo", DataSessionInfo.class).getResultList();
        }
    }

    @Override
    public void close() {
        emf.close();
    }
}
