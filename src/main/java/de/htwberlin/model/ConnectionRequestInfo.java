package de.htwberlin.model;

public record ConnectionRequestInfo(
        String sourcePeerId,
        String targetPeerId,
        int timeoutMs
) {}
