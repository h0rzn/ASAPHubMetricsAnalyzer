package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unregister")
public class Unregister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;
    private String peerId;

    public Unregister(String peerId) {
        this.peerId = peerId;
    }

    public Unregister() {}

    public String getPeerId() {
        return peerId;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
