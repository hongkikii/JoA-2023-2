package com.mjuAppSW.joA.geography.block.entity;

import com.mjuAppSW.joA.geography.location.entity.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Block_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blocker_id")
    private Location blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_id")
    private Location blocked;

    public Block(Location blocker, Location blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
