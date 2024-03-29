package com.mjuAppSW.joA.domain.message.entity;

import static com.mjuAppSW.joA.common.constant.Constants.Message.*;

import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Message")
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="Message_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="Member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="Room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;

    @Column(name="Is_checked", nullable = false)
    private String isChecked;

    @Builder
    public Message(Long id, Member member, Room room, String content, LocalDateTime date, String isChecked) {
        this.id = id;
        this.member = member;
        this.room = room;
        this.content = content;
        this.time = date;
        this.isChecked = isChecked;
    }

    public void updateIsChecked(){
        this.isChecked = CHECKED;
    }
}
