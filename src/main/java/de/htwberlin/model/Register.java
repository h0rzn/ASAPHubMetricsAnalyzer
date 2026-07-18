package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "peer_info")
public class Register {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String peerId;
    private boolean canCreateTCPConnections;

    public Register() {}

    public Register(String peerId, boolean canCreateTCPConnections) {
        this.peerId = peerId;
        this.canCreateTCPConnections = canCreateTCPConnections;
    }

    public Long getId() {
        return id;
    }

    public String getPeerId() {
        return peerId;
    }

    public boolean isCanCreateTCPConnections() {
        return canCreateTCPConnections;
    }
}
