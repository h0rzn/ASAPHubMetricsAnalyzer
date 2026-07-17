package de.htwberlin.model;

import java.time.Instant;

public record DataSessionInfo(
        String sourcePeerId,
        String targetPeerId,
        int timeoutMs,
        Instant startedAt,
        Instant endedAt
) {}
