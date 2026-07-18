package de.htwberlin.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notify_connection_ended")
public class NotifyConnectionEnded {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourcePeerId;
    private String targetPeerId;
    private Instant endedAt;

    public NotifyConnectionEnded() {}

    public NotifyConnectionEnded(String sourcePeerId, String targetPeerId, Instant endedAt) {
        this.sourcePeerId = sourcePeerId;
        this.targetPeerId = targetPeerId;
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

    public Instant getEndedAt() {
        return endedAt;
    }
}
