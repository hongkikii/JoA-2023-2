package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.room.Room;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Message")
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="Message_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="Member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name="Room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;

    @Column(nullable = false)
    private String isChecked;

    @Builder
    public Message(Member member, Room room, String content, LocalDateTime date, String isChecked) {
        this.member = member;
        this.room = room;
        this.content = content;
        this.time = date;
        this.isChecked = isChecked;
    }
}