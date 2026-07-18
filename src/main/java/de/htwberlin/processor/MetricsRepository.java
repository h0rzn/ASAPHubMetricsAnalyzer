package de.htwberlin.processor;

import de.htwberlin.model.ConnectionRequest;
import de.htwberlin.model.Session;
import de.htwberlin.model.StartDataSession;
import de.htwberlin.model.Register;

import java.util.List;

/**
 * Repository to save any metrics.
 */
public interface MetricsRepository {
    /**
     * Saves any model that can be processed
     * by the specific implementation of this repository.
     * E.g. a Hibernate implementation requires entity
     * to be an jakarta.persistence.Entity.
     * @param entity
     */
    void persist(Object entity);

    Session createSession();

    List<Register> findAllPeerInfos();
    List<ConnectionRequest> findAllConnectionRequests();
    List<StartDataSession> findAllDataSessions();
}
