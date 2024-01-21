package com.mjuAppSW.joA.domain.report.message;

import static com.mjuAppSW.joA.common.constant.Constants.MessageReport.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.message.Message;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.message.exception.MessageNotFoundException;
import com.mjuAppSW.joA.domain.report.ReportCategory;
import com.mjuAppSW.joA.domain.report.ReportCategoryRepository;
import com.mjuAppSW.joA.domain.report.message.dto.request.CheckMessageReportRequest;
import com.mjuAppSW.joA.domain.report.message.dto.request.ReportRequest;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportAlreadyExistedException;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportAlreadyReportException;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportAlreadyReportedException;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportNotFoundException;
import com.mjuAppSW.joA.domain.report.vote.exception.ReportCategoryNotFoundException;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;

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
    private final RoomInMemberRepository roomInMemberRepository;
    private final MemberChecker memberChecker;

    @Transactional
    public void messageReport(ReportRequest request, LocalDateTime messageReportDate){
        Message message = messageRepository.findById(request.getMessageId()).orElseThrow(MessageNotFoundException::new);
        ReportCategory reportCategory = reportCategoryRepository.findById(request.getCategoryId()).orElseThrow(
            ReportCategoryNotFoundException::new);
        MessageReport check = messageReportRepository.findByMessage(message).orElse(null);

        if(check != null){
            throw new MessageReportAlreadyExistedException();
        }

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

    public boolean check(List<MessageReport> messageReports, Long memberId1, Long memberId2){
        Set<Room> roomIds = new HashSet<>();
        if(messageReports != null){
            for(MessageReport mr : messageReports){
                roomIds.add(mr.getMessage_id().getRoom());
            }
        }

        for(Room rId : roomIds){
            List<RoomInMember> roomInMembers = roomInMemberRepository.findByAllRoom(rId);
            boolean memberId1Exists = roomInMembers.stream()
                    .anyMatch(rim -> rim.getMember().getId().equals(memberId1));
            boolean memberId2Exists = roomInMembers.stream()
                    .anyMatch(rim -> rim.getMember().getId().equals(memberId2));
            if(memberId1Exists && memberId2Exists){
                return true;
            }
        }
        return false;
    }
    public void checkMessageReport(CheckMessageReportRequest request){
        Member member1 = memberChecker.findBySessionId(request.getMemberId1());
        Member member2 = memberChecker.findById(request.getMemberId2());

        List<MessageReport> myMessageReport = messageReportRepository.findByMemberId(member1.getId());
        List<MessageReport> opponentMessageReport = messageReportRepository.findByMemberId(member2.getId());
        Boolean reported = check(myMessageReport, member1.getId(), member2.getId());
        Boolean report = check(opponentMessageReport, member1.getId(), member2.getId());
        if(reported && report){throw new MessageReportAlreadyReportedException();}
        else if(reported){throw new MessageReportAlreadyReportedException();}
        else if(report){throw new MessageReportAlreadyReportException();}
    }
    @Transactional
    public void deleteMessageReportAdmin(Long id){
        MessageReport messageReport = messageReportRepository.findById(id).orElseThrow(MessageReportNotFoundException::new);
        messageReportRepository.delete(messageReport);
    }

    public Long calculationHour(LocalDateTime getTime){
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(getTime, currentDateTime);
        Long hours = duration.toHours();
        return hours;
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
}
