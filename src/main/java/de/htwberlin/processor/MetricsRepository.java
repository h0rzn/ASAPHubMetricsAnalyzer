package de.htwberlin.processor;

import de.htwberlin.model.ConnectionRequestInfo;
import de.htwberlin.model.DataSessionInfo;
import de.htwberlin.model.PeerInfo;

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

    List<PeerInfo> findAllPeerInfos();
    List<ConnectionRequestInfo> findAllConnectionRequests();
    List<DataSessionInfo> findAllDataSessions();
}
