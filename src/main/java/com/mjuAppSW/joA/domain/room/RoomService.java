package com.mjuAppSW.joA.domain.room;

import com.mjuAppSW.joA.domain.message.Message;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.report.message.MessageReport;
import com.mjuAppSW.joA.domain.report.message.MessageReportRepository;
import com.mjuAppSW.joA.domain.room.dto.RoomResponse;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RoomService {

    private RoomRepository roomRepository;
    private RoomInMemberRepository roomInMemberRepository;
    private MessageRepository messageRepository;
    private MessageReportRepository messageReportRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, RoomInMemberRepository roomInMemberRepository,
                       MessageRepository messageRepository, MessageReportRepository messageReportRepository){
        this.roomRepository = roomRepository;
        this.roomInMemberRepository = roomInMemberRepository;
        this.messageRepository = messageRepository;
        this.messageReportRepository = messageReportRepository;
    }

    public String makeRandomString(){
        byte[] randomBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    @Transactional
    public RoomResponse createRoom(){
        Room room = Room.builder()
                .date(LocalDateTime.now())
                .status("1")
                .encryptKey(makeRandomString())
                .build();
        Room returnRoom = roomRepository.save(room);
        return new RoomResponse(returnRoom.getId());
    }

    public Boolean checkRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if(room != null){
            return true;
        }return false;
    }

    public Long calculationHour(LocalDateTime getTime){
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(getTime, currentDateTime);
        Long hours = duration.toHours();
        return hours;
    }

    public Integer checkCreateAtRoom(Long roomId){
        Optional<Room> checkCreatedAt = Optional.ofNullable(roomRepository.findByDate(roomId));
        if(checkCreatedAt.isPresent()){
            LocalDateTime date = checkCreatedAt.get().getDate();
            Long hours = calculationHour(date);
            if(hours >= 24){
                return 0;
            }else{
                return 1;
            }
        }else{
            return 2;
        }
    }

    public Integer checkTime(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if(room != null){
            String status = room.getStatus();
            LocalDateTime date = room.getDate();
            Long hours = calculationHour(date);
            if(status.equals("0")){
                if(hours >= 168){return 7;}
            }else {
                if (hours >= 24) {return 1;}
            }
            return 0;
        }
        return 9;
    }

    @Transactional
    public void updateRoom(Long roomId, String status){
        roomRepository.updateCreatedAtAndStatus(roomId, LocalDateTime.now(), status);
    }

    @Scheduled(cron = "0 0 0,12 * * *") // Run at 00, 12 o'clock every day
    public void performScheduledTask(){
        log.info("00, 12 delete Room");
        List<MessageReport> messageReports = messageReportRepository.findAll();
        Set<Long> roomIds = new HashSet<>();
        if(messageReports != null){
            for(MessageReport mr : messageReports){
                roomIds.add(mr.getMessage_id().getRoom().getId());
            }
        }
        List<Room> rooms0;
        if(roomIds.isEmpty()){rooms0 = roomRepository.findByStatus("0");}
        else{rooms0 = roomRepository.findByStatusAndRoomIds("0", roomIds);}
        for(Room room : rooms0){
            LocalDateTime date = room.getDate();
            Long hours = calculationHour(date);
            if(hours > 168){
                log.info("delete : roomId = {}", room.getId());
                deleteMemory(room);
            }
        }
        List<Room> rooms1;
        if(roomIds.isEmpty()){rooms1 = roomRepository.findByStatus("1");}
        else{rooms1 = roomRepository.findByStatusAndRoomIds("1", roomIds);}
        for(Room room : rooms1){
            LocalDateTime date = room.getDate();
            Long hours = calculationHour(date);
            if(hours > 24){
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