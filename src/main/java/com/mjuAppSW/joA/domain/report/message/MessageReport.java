package com.mjuAppSW.joA.domain.report.message;

import com.mjuAppSW.joA.domain.message.Message;
import com.mjuAppSW.joA.domain.report.ReportCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name="Message_report")
public class MessageReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MReport_id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "Message_Id")
    private Message message_id;

    @ManyToOne
    @JoinColumn(name="Category_id", nullable = false)
    private ReportCategory category_id;

    private String content;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;

    @Builder
    public MessageReport(Message message_id, ReportCategory category_id, String content, LocalDateTime date) {
        this.message_id = message_id;
        this.category_id = category_id;
        this.content = content;
        this.date = date;
    }
}