package com.mjuAppSW.joA.domain.room.entity;

import static com.mjuAppSW.joA.common.constant.Constants.Room.*;

import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name="Room")
public class Room{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Room_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;

    @Column(nullable = false)
    private String status;

    @Column(name="Encrypt_key", nullable = false)
    private String encryptKey;

    @OneToMany(mappedBy = "room")
    private List<RoomInMember> roomInMember = new ArrayList<>();

    @Builder
    public Room(Long id, LocalDateTime date, String status, String encryptKey) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.encryptKey = encryptKey;
    }

    public void updateStatusAndDate(LocalDateTime updateRoomStatusDate) {
        this.date = updateRoomStatusDate;
        this.status = EXTEND;
    }
}