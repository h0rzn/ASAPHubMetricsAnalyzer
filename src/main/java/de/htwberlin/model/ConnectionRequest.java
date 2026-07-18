package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "connection_request")
public class ConnectionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourcePeerId;
    private String targetPeerId;
    private int timeoutMs;

    protected ConnectionRequest() {}

    public ConnectionRequest(String sourcePeerId, String targetPeerId, int timeoutMs) {
        this.sourcePeerId = sourcePeerId;
        this.targetPeerId = targetPeerId;
        this.timeoutMs = timeoutMs;
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

}
