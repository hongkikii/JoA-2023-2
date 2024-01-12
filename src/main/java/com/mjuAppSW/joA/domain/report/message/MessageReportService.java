package com.mjuAppSW.joA.domain.report.message;


import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberRepository;
import com.mjuAppSW.joA.domain.message.Message;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.report.ReportCategory;
import com.mjuAppSW.joA.domain.report.ReportCategoryRepository;
import com.mjuAppSW.joA.domain.report.message.dto.StatusResponse;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class MessageReportService {

    private MessageReportRepository messageReportRepository;
    private MessageRepository messageRepository;
    private ReportCategoryRepository reportCategoryRepository;
    private RoomInMemberRepository roomInMemberRepository;
    private MemberRepository memberRepository;

    @Autowired
    public MessageReportService(MessageReportRepository message_report_repository, MessageRepository messageRepository,
                                ReportCategoryRepository reportCategoryRepository, RoomInMemberRepository roomInMemberRepository,
                                MemberRepository memberRepository){
        this.messageReportRepository = message_report_repository;
        this.messageRepository = messageRepository;
        this.reportCategoryRepository = reportCategoryRepository;
        this.roomInMemberRepository = roomInMemberRepository;
        this.memberRepository = memberRepository;
    }

    public String messageReport(Long messageId, Long categoryId, String content){
        Message message = messageRepository.findById(messageId).orElse(null);
        ReportCategory reportCategory = reportCategoryRepository.findById(categoryId).orElse(null);
        MessageReport check = messageReportRepository.findByMessage(message);
        if(check != null){return "2";}
        if(message != null && reportCategory != null){
            MessageReport messageReport = MessageReport.builder()
                    .message_id(message)
                    .category_id(reportCategory)
                    .content(content)
                    .date(LocalDateTime.now())
                    .build();
            MessageReport saveMessageReport = messageReportRepository.save(messageReport);

            Member reportedMember = memberRepository.findById(message.getMember().getId()).orElse(null);
            if(reportedMember != null){reportedMember.addReportCount();}
            if(saveMessageReport != null) return "0";
        }
        return "1";
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

    public StatusResponse checkMessageReport(Long memberId1, Long memberId2){
        Member member1 = memberRepository.findBysessionId(memberId1).orElse(null);
        Member member2 = memberRepository.findById(memberId2).orElse(null);
        if(member1 != null && member2 != null){
            List<MessageReport> myMessageReport = messageReportRepository.findByMemberId(member1.getId());
            List<MessageReport> opponentMessageReport = messageReportRepository.findByMemberId(member2.getId());
            Boolean reported = check(myMessageReport, member1.getId(), member2.getId());
            Boolean report = check(opponentMessageReport, member1.getId(), member2.getId());
            if(reported && report){return new StatusResponse(1);}
            else if(reported){return new StatusResponse(1);}
            else if(report){return new StatusResponse(2);}
            else {return new StatusResponse(3);}
        }
        return new StatusResponse(0);
    }

    public void deleteMessageReportAdmin(Long id){
        MessageReport messageReport = messageReportRepository.findById(id).orElse(null);
        if(messageReport != null){
            log.info("deleteMessageReportAdmin : id = {}, message_id = {}, category_id = {}, content = {}, date = {}",
                    messageReport.getId(), messageReport.getMessage_id().getId(), messageReport.getCategory_id().getId(),
                    messageReport.getContent(), messageReport.getDate());
            messageReportRepository.delete(messageReport);
        }
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
            if(hours >= 2160){deleteMessageReports.add(mr);}
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
