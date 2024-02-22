package com.mjuAppSW.joA.domain.messageReport.service;

import static com.mjuAppSW.joA.common.constant.Constants.MessageReport.*;

import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.message.dto.request.ReportRequest;
import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.reportCategory.entity.ReportCategory;
import com.mjuAppSW.joA.domain.reportCategory.service.ReportCategoryQueryService;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.message.service.MessageQueryService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mjuAppSW.joA.domain.messageReport.entity.MessageReport;
import com.mjuAppSW.joA.domain.messageReport.repository.MessageReportRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReportService {
    private final MemberQueryService memberQueryService;
    private final MessageReportQueryService messageReportQueryService;
    private final MessageQueryService messageQueryService;
    private final MessageReportRepository messageReportRepository;
    private final ReportCategoryQueryService reportCategoryQueryService;

    @Transactional
    public void execute(ReportRequest request, LocalDateTime messageReportDate){
        Message message = messageQueryService.getById(request.getMessageId());
        ReportCategory reportCategory = reportCategoryQueryService.getBy(request.getCategoryId());
        messageReportQueryService.validateNoExistedMessageReport(message);

        MessageReport messageReport = create(message, reportCategory, messageReportDate);

        messageReportRepository.save(messageReport);

        Member member = memberQueryService.getById(message.getMember().getId());
        member.addReportCount();
    }

    private MessageReport create(Message message, ReportCategory reportCategory, LocalDateTime messageReportDate){
        return MessageReport.builder()
            .message_id(message)
            .category_id(reportCategory)
            .content(message.getContent())
            .date(messageReportDate)
            .build();
    }

    @Transactional
    public void deleteByAdmin(Long id){
        MessageReport messageReport = messageReportQueryService.getById(id);
        messageReportRepository.delete(messageReport);
    }

    @Scheduled(cron = "0 55 23 14,L * ?")
    public void delete(){
        log.info("23:55 1 or 15, deleteMessageReport");
        List<MessageReport> messageReports = messageReportRepository.findAll();
        List<MessageReport> deleteMessageReports = new ArrayList<>();
        for(MessageReport mr : messageReports){
            Long hours = calculationHour(mr.getDate());
            if(hours >= NINETY_DAY_HOURS){deleteMessageReports.add(mr);}
        }

        if(deleteMessageReports != null){
            for(MessageReport mr : deleteMessageReports){
                log.info("deleteMessageReport : id = {}, message_id = {}, category_id = {}, content = {}, date = {}",
                        mr.getId(), mr.getMessage_id().getId(), mr.getCategory_id().getId(), mr.getContent(), mr.getDate());
                messageReportRepository.delete(mr);
            }
        }
    }
    private Long calculationHour(LocalDateTime getTime){
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(getTime, currentDateTime);
        Long hours = duration.toHours();
        return hours;
    }
}
