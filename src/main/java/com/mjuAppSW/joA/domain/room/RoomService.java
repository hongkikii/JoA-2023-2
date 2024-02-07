package com.mjuAppSW.joA.domain.room;

import static com.mjuAppSW.joA.common.constant.Constants.Room.*;
import static com.mjuAppSW.joA.common.constant.Constants.Room.OVER_ONE_DAY;
import static com.mjuAppSW.joA.common.constant.Constants.Room.OVER_SEVEN_DAY;
import static com.mjuAppSW.joA.common.constant.Constants.WebSocketHandler.*;

import com.mjuAppSW.joA.common.encryption.EncryptManager;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberService;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.report.message.MessageReport;
import com.mjuAppSW.joA.domain.report.message.MessageReportRepository;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportAlreadyReportException;
import com.mjuAppSW.joA.domain.report.message.exception.MessageReportAlreadyReportedException;
import com.mjuAppSW.joA.domain.room.dto.response.RoomResponse;
import com.mjuAppSW.joA.domain.room.exception.OverOneDayException;
import com.mjuAppSW.joA.domain.room.exception.RoomAlreadyExtendException;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberAlreadyExistedException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MemberService memberService;
    private final MessageRepository messageRepository;
    private final MessageReportRepository messageReportRepository;
    private final EncryptManager encryptManager;

    @Transactional
    public RoomResponse createRoom(LocalDateTime createdRoomDate){
        Room room = Room.builder()
            .date(createdRoomDate)
            .status(NOT_EXTEND)
            .encryptKey(encryptManager.makeRandomString())
            .build();
        Room saveRoom = roomRepository.save(room);
        return RoomResponse.of(saveRoom.getId());
    }

    @Transactional
    public void updateStatusAndDate(Long roomId, LocalDateTime updateRoomStatusDate){
        Room room = findByRoomId(roomId);
        if(room.getStatus().equals(EXTEND)){
            throw new RoomAlreadyExtendException();
        }
        room.updateStatusAndDate(updateRoomStatusDate);
    }

    public void checkRoomInMember(Long memberId1, Long memberId2) {
        Member member1 = memberService.getBySessionId(memberId1);
        Member member2 = memberService.getById(memberId2);
        List<RoomInMember> roomInMemberList = roomInMemberRepository.checkRoomInMember(member1, member2);
        if(!roomInMemberList.isEmpty()){
            throw new RoomInMemberAlreadyExistedException();
        }
    }

    public void checkMessageReport(Long memberId1, Long memberId2){
        Member member1 = memberService.getBySessionId(memberId1);
        Member member2 = memberService.getById(memberId2);

        List<MessageReport> myMessageReport = messageReportRepository.findByMemberId(member1.getId());
        List<MessageReport> opponentMessageReport = messageReportRepository.findByMemberId(member2.getId());
        Boolean reported = checkReportMessages(myMessageReport, member1.getId(), member2.getId());
        Boolean report = checkReportMessages(opponentMessageReport, member1.getId(), member2.getId());
        if(reported && report){throw new MessageReportAlreadyReportedException();}
        if(reported){throw new MessageReportAlreadyReportedException();}
        if(report){throw new MessageReportAlreadyReportException();}
    }

    private boolean checkReportMessages(List<MessageReport> messageReports, Long memberId1, Long memberId2){
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

    public void checkCreateAtRoom(Long roomId){
        Room room = findByRoomId(roomId);
        LocalDateTime getDate = room.getDate();
        Long hours = calculationHour(getDate);
        if(hours >= ONE_DAY_HOURS){
            throw new OverOneDayException();
        }
    }

    public Integer checkTime(Long roomId){
        Room room = findByRoomId(roomId);

        String status = room.getStatus();
        LocalDateTime date = room.getDate();
        Long hours = calculationHour(date);
        if(status.equals(EXTEND)){
            if (hours >= SEVEN_DAY_HOURS){return OVER_SEVEN_DAY;}
        }
        if(status.equals(NOT_EXTEND)){
            if (hours >= ONE_DAY_HOURS) {return OVER_ONE_DAY;}
        }
        return NORMAL_OPERATION;
    }

    private Long calculationHour(LocalDateTime getTime){
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(getTime, currentDateTime);
        Long hours = duration.toHours();
        return hours;
    }

    public void findByRoom(Long roomId){
        Room room = findByRoomId(roomId);

        List<RoomInMember> roomInMemberList = roomInMemberRepository.findAllRoom(room);
        if(!roomInMemberList.isEmpty()){
            throw new RoomInMemberAlreadyExistedException();
        }
    }

    public Room findByRoomId(Long roomId){
        return roomRepository.findById(roomId)
            .orElseThrow(RoomNotFoundException::new);
    }

    @Scheduled(cron = "0 0 0,12 * * *")
    public void deleteRooms(){
        log.info("00, 12 delete Room");
        List<MessageReport> messageReports = messageReportRepository.findAll();
        Set<Long> roomIds = new HashSet<>();
        if(messageReports != null){
            for(MessageReport mr : messageReports){
                roomIds.add(mr.getMessage_id().getRoom().getId());
            }
        }

        checkStatus(roomIds, EXTEND, SEVEN_DAY_HOURS);
        checkStatus(roomIds, NOT_EXTEND, ONE_DAY_HOURS);
    }

    private void checkStatus(Set<Long> roomIds, String status, int validTime){
        List<Room> rooms;
        if(roomIds.isEmpty()){rooms = roomRepository.findByStatus(status);}
        else{rooms = roomRepository.findByStatusAndNotRoomIds(status, roomIds);}

        for(Room room : rooms){
            LocalDateTime date = room.getDate();
            Long calculateHours = calculationHour(date);
            if(calculateHours > validTime){
                log.info("delete : roomId = {}", room.getId());
                deleteMemory(room);
            }
        }
    }

    private void deleteMemory(Room room) {
        messageRepository.deleteByRoom(room);
        roomInMemberRepository.deleteByRoom(room);
        roomRepository.deleteById(room.getId());
    }
}