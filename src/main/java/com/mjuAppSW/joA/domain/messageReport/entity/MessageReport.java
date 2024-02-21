package com.mjuAppSW.joA.domain.messageReport.entity;

import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.reportCategory.entity.ReportCategory;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name="Message_report")
public class MessageReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MReport_id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "Message_id")
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
