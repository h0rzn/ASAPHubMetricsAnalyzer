package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "register")
public class Register {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;
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

    public void setSession(Session session) {
        this.session = session;
    }
}
