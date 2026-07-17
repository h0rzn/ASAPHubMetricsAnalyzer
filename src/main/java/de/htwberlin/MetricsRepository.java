package de.htwberlin;

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
}
