package de.htwberlin.model;

public record EventLogEntry(
        EventType type,
        String peerId,
        String relatedPeerId,
        Boolean canCreateTCPConnections
) {}
