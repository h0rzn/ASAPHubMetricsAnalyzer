package de.htwberlin.model;

import jakarta.persistence.*;

@Entity
@Table(name = "disconnect")
public class Disconnect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourcePeerId;
    private String targetPeerId;

    public Disconnect() {}

    public Disconnect(String sourcePeerId, String targetPeerId) {
        this.sourcePeerId = sourcePeerId;
        this.targetPeerId = targetPeerId;
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

}
