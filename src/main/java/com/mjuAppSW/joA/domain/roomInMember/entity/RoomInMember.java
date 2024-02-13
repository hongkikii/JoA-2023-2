package com.mjuAppSW.joA.domain.roomInMember.entity;

import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.room.entity.Room;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="Room_in_member")
@IdClass(RoomInMemberId.class)
@NoArgsConstructor
public class RoomInMember {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="Member_id", nullable = false)
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="Room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String expired;

    @Column(nullable = false)
    private String result;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="Entry_time")
    private LocalDateTime entryTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="Exit_time")
    private LocalDateTime exitTime;

    @Builder
    public RoomInMember(Room room, Member member, String expired, String result) {
        this.room = room;
        this.member = member;
        this.expired = expired;
        this.result = result;
    }

    public void saveResult(String result) {
        this.result = result;
    }
    public void updateExpired(String expired) { this.expired = expired; }
    public void updateEntryTime(LocalDateTime entryTime){ this.entryTime = entryTime; }
    public void updateExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
}
