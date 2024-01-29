package com.mjuAppSW.joA.domain.report.message;

import static com.mjuAppSW.joA.common.constant.Constants.MessageReport.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.message.Message;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.message.exception.MessageNotFoundException;
import com.mjuAppSW.joA.domain.report.ReportCategory;
import com.mjuAppSW.joA.domain.report.ReportCategoryRepository;
import com.mjuAppSW.joA.domain.report.message.dto.request.ReportRequest;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportAlreadyExistedException;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportNotFoundException;
import com.mjuAppSW.joA.domain.report.vote.exception.ReportCategoryNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReportService {

    private final MessageReportRepository messageReportRepository;
    private final MessageRepository messageRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final MemberChecker memberChecker;

    @Transactional
    public void messageReport(ReportRequest request, LocalDateTime messageReportDate){
        Message message = findByMessageId(request.getMessageId());
        ReportCategory reportCategory = findByCategoryId(request.getCategoryId());
        checkExistedReportMessage(message);

        MessageReport messageReport = MessageReport.builder()
            .message_id(message)
            .category_id(reportCategory)
            .content(message.getContent())
            .date(messageReportDate)
            .build();

        messageReportRepository.save(messageReport);

        Member member = memberChecker.findById(message.getMember().getId());
        member.addReportCount();
    }

    @Transactional
    public void deleteMessageReportAdmin(Long id){
        MessageReport messageReport = findByMessageReportId(id);
        messageReportRepository.delete(messageReport);
    }

    private Message findByMessageId(Long messageId){
        return messageRepository.findById(messageId)
            .orElseThrow(MessageNotFoundException::new);
    }
    private MessageReport findByMessageReportId(Long messageReportId){
        return messageReportRepository.findById(messageReportId)
            .orElseThrow(MessageReportNotFoundException::new);
    }
    private ReportCategory findByCategoryId(Long categoryId){
        return reportCategoryRepository.findById(categoryId)
            .orElseThrow(ReportCategoryNotFoundException::new);
    }

    private void checkExistedReportMessage(Message message){
        messageReportRepository.findByMessage(message)
            .ifPresent(messageReport -> {
                throw new MessageReportAlreadyExistedException();
            });
    }

    @Scheduled(cron = "0 55 23 14,L * ?")
    public void deleteMessageReport(){
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
