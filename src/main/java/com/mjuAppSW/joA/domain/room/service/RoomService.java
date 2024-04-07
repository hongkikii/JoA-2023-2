package com.mjuAppSW.joA.domain.room.service;

import static com.mjuAppSW.joA.common.constant.AlarmConstants.CreateChattingRoom;
import static com.mjuAppSW.joA.common.constant.AlarmConstants.ExtendChattingRoom;
import static com.mjuAppSW.joA.common.constant.Constants.Room.*;
import static com.mjuAppSW.joA.common.constant.Constants.Room.OVER_ONE_DAY;
import static com.mjuAppSW.joA.common.constant.Constants.Room.OVER_SEVEN_DAY;
import static com.mjuAppSW.joA.common.constant.Constants.WebSocketHandler.*;

import com.mjuAppSW.joA.common.constant.AlarmConstants;
import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.room.dto.response.RoomResponse;
import com.mjuAppSW.joA.domain.room.repository.RoomRepository;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.message.repository.MessageRepository;
import com.mjuAppSW.joA.domain.messageReport.entity.MessageReport;
import com.mjuAppSW.joA.domain.messageReport.repository.MessageReportRepository;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.repository.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberQueryService;

import com.mjuAppSW.joA.fcm.service.FCMService;
import com.mjuAppSW.joA.fcm.vo.FCMInfoVO;
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
    private final RoomQueryService roomQueryService;
    private final RoomInMemberQueryService roomInMemberQueryService;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MemberQueryService memberQueryService;
    private final MessageRepository messageRepository;
    private final MessageReportRepository messageReportRepository;
    private final FCMService fcmService;

    @Transactional
    public RoomResponse create(LocalDateTime createdRoomDate){
        Room room = Room.builder()
            .date(createdRoomDate)
            .status(NOT_EXTEND)
            .encryptKey(makeRandomKey())
            .build();
        Room saveRoom = roomRepository.save(room);

        return RoomResponse.of(saveRoom.getId());
    }

    private String makeRandomKey(){
        byte[] randomBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    @Transactional
    public void update(Long roomId, LocalDateTime updateRoomStatusDate){
        Room room = roomQueryService.getById(roomId);
        if(room.getStatus().equals(EXTEND)){
            throw BusinessException.RoomAlreadyExtendException;
        }

        makeFCMVO(room, ExtendChattingRoom);

        room.updateStatusAndDate(updateRoomStatusDate);
    }

    private void makeFCMVO(Room room, AlarmConstants alarm){
        List<Member> members = new ArrayList<>();
        for(RoomInMember roomInMember : room.getRoomInMember()){
            members.add(roomInMember.getMember());
        }

        for(int i=0; i<2; i++){
            Member targetMember = members.get(i);
            String name = members.get((i + 1) % members.size()).getName();
            fcmService.send(FCMInfoVO.of(targetMember, name, alarm));
        }
    }

    public void checkExisted(Long memberId1, Long memberId2) {
        Member member1 = memberQueryService.getBySessionId(memberId1);
        Member member2 = memberQueryService.getById(memberId2);
        roomInMemberQueryService.validateNoRoom(member1, member2);
    }

    public void checkMessageReport(Long memberId1, Long memberId2){
        Member member1 = memberQueryService.getBySessionId(memberId1);
        Member member2 = memberQueryService.getById(memberId2);

        List<MessageReport> myMessageReport = messageReportRepository.findByMember(member1);
        List<MessageReport> opponentMessageReport = messageReportRepository.findByMember(member2);
        Boolean reported = checkReportMessages(myMessageReport, member1.getId(), member2.getId());
        Boolean report = checkReportMessages(opponentMessageReport, member1.getId(), member2.getId());
        if(reported && report){throw BusinessException.MessageReportAlreadyReportedException;}
        if(reported){throw BusinessException.MessageReportAlreadyReportedException;}
        if(report){throw BusinessException.MessageReportAlreadyReportException;}
    }

    private boolean checkReportMessages(List<MessageReport> messageReports, Long memberId1, Long memberId2){
        Set<Room> rooms = new HashSet<>();
        if(messageReports != null){
            for(MessageReport mr : messageReports){
                rooms.add(mr.getMessage_id().getRoom());
            }
        }

        for(Room room : rooms){
            List<RoomInMember> roomInMembers = roomInMemberRepository.findByRoom(room);
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

    public void checkCreateAt(Long roomId){
        Room room = roomQueryService.getById(roomId);
        LocalDateTime getDate = room.getDate();
        Long hours = calculationHour(getDate);
        if(hours >= ONE_DAY_HOURS){
            throw BusinessException.OverOneDayException;
        }
    }

    public Integer checkTime(Long roomId){
        Room room = roomQueryService.getById(roomId);

        String status = room.getStatus();
        LocalDateTime date = room.getDate();
        Long hours = calculationHour(date);
        if(status.equals(EXTEND) && hours >= SEVEN_DAY_HOURS){
            return OVER_SEVEN_DAY;
        }
        if(status.equals(NOT_EXTEND) && hours >= ONE_DAY_HOURS){
            return OVER_ONE_DAY;
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
        Room room = roomQueryService.getById(roomId);

        List<RoomInMember> roomInMemberList = roomInMemberRepository.findByRoom(room);
        if(!roomInMemberList.isEmpty()){
            throw BusinessException.RoomInMemberAlreadyExistedException;
        }
    }

    @Scheduled(cron = "0,30 0 0,12 * * *")
    public void deleteRelatedChatData(){
        log.info("00:00, 12:00 delete message, roomInMember, room");
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

    private void checkStatus(Set<Long> roomIds, String status, int validTime) {
        List<Room> rooms;
        if (roomIds.isEmpty()) { rooms = roomRepository.findByStatus(status); }
        else { rooms = roomRepository.findByStatusAndNotRoomIds(status, roomIds); }

        for (Room room : rooms) {
            LocalDateTime date = room.getDate();
            Long calculateHours = calculationHour(date);
            if (calculateHours >= validTime) {
                log.info("delete : roomId = {}", room.getId());
                deleteMemory(room);
            }
        }
    }

    private void deleteMemory(Room room) {
        List<Message> deleteRooms = messageRepository.findByRoom(room);
        if(!deleteRooms.isEmpty()) messageRepository.deleteAll(deleteRooms);

        List<RoomInMember> deleteRoomInMembers = roomInMemberRepository.findByRoom(room);
        if(!deleteRoomInMembers.isEmpty()) roomInMemberRepository.deleteAll(deleteRoomInMembers);

        roomRepository.delete(room);
    }
}
