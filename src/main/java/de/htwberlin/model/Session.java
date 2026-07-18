package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Session() {}

    public Long getId() { return id; }
}
