package de.htwberlin.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "data_session_info")
public class StartDataSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourcePeerId;

    private String targetPeerId;

    private int timeoutMs;

    private Instant startedAt;

    private Instant endedAt;

    public StartDataSession() {}

    public StartDataSession(
            String sourcePeerId,
            String targetPeerId,
            int timeoutMs,
            Instant startedAt,
            Instant endedAt
    ) {
        this.sourcePeerId = sourcePeerId;
        this.targetPeerId = targetPeerId;
        this.timeoutMs = timeoutMs;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSourcePeerId() {
        return sourcePeerId;
    }

    public String getTargetPeerId() {
        return targetPeerId;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}
