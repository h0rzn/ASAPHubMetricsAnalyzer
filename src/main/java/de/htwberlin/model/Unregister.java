package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unregister")
public class Unregister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String peerId;

    public Unregister(String peerId) {
        this.peerId = peerId;
    }

    public Unregister() {}
}
