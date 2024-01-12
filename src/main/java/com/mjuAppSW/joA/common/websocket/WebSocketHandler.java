package com.mjuAppSW.joA.common.websocket;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberRepository;
import com.mjuAppSW.joA.domain.message.MessageService;
import com.mjuAppSW.joA.domain.report.message.MessageReport;
import com.mjuAppSW.joA.domain.report.message.MessageReportRepository;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.room.RoomService;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberService;
import com.mjuAppSW.joA.domain.roomInMember.dto.RoomListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private Map<String, List<WebSocketSession>> memberSessions = new ConcurrentHashMap<>();
    private RoomInMemberService roomInMemberService;
    private RoomInMemberRepository roomInMemberRepository;
    private MessageService messageService;
    private RoomRepository roomRepository;
    private MemberRepository memberRepository;
    private RoomService roomService;
    private MessageReportRepository messageReportRepository;

    @Autowired
    public WebSocketHandler(RoomInMemberService roomInMemberService, MessageService messageService,
                            RoomRepository roomRepository, MemberRepository memberRepository, RoomService roomService,
                            RoomInMemberRepository roomInMemberRepository, MessageReportRepository messageReportRepository){
        this.roomInMemberService = roomInMemberService;
        this.messageService = messageService;
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
        this.roomService = roomService;
        this.roomInMemberRepository = roomInMemberRepository;
        this.messageReportRepository = messageReportRepository;
    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);
        String[] arr = payload.split(" ", 4);

        String separator = arr[0];
        if(separator.equals("R")){
            makeRoom(arr[1], arr[2], arr[3]);
        }else if(separator.equals("M")){
            sendMessage(arr[1], arr[2], arr[3], session);
        }
    }

    public String sessionIdToMemberId(String token){
        Member member = memberRepository.findBysessionId(Long.parseLong(token)).orElse(null);
        if(member != null){
            String memberId = String.valueOf(member.getId());
            return memberId;
        }
        return null;
    }

    public void makeRoom(String roomId, String memberId1, String memberId2){
        String memberId = sessionIdToMemberId(memberId1);

        Boolean checkRoomId = roomInMemberService.findByRoom(Long.parseLong(roomId));

        if(checkRoomId){
            Boolean room1 = roomInMemberService.createRoom(Long.parseLong(roomId), Long.parseLong(memberId));
            Boolean room2 = roomInMemberService.createRoom(Long.parseLong(roomId), Long.parseLong(memberId2));
            if(room1 && room2) {log.info("makeRoom : roomId = {}, memberId1 = {}, memberId2 = {}", roomId, memberId, memberId2);}
            else{log.warn("makeRoom : getValue's not correct or already exist / roomId = {}, memberId1 = {}, memberId2 = {}", roomId, memberId1, memberId2);}
        }else log.warn("makeRoom : getValue's not correct or already exist / roomId = {}", roomId);
    }

    public void sendMessage(String roomId, String memberId1, String content,
                            WebSocketSession session) throws Exception {
        String memberId = sessionIdToMemberId(memberId1);
        // check MessageReport
        List<MessageReport> checkMessageReport = messageReportRepository.findByRoomId(Long.parseLong(roomId));
        // check Expired
        Boolean checkExpired = roomInMemberService.checkExpired(Long.parseLong(roomId), Long.parseLong(memberId));
        // check createdAt
        Integer checkTime = roomService.checkTime(Long.parseLong(roomId));
        // check isWithDrawal
        Boolean checkIsWithDrawal = roomInMemberService.checkIsWithDrawal(Long.parseLong(roomId), Long.parseLong(memberId));

        if(checkExpired && checkTime == 0 && checkIsWithDrawal && checkMessageReport.isEmpty()){
            List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
            if(roomSessionsList != null){
                int count = 2;
                for(int i=0; i<roomSessionsList.size(); i++){
                    count--;
                }
                String isChecked = String.valueOf(count);
                Long saveId = messageService.saveMessage(Long.parseLong(roomId), Long.parseLong(memberId), content, isChecked);
                if(saveId != null){
                    Boolean update = updateCurrentMessage(roomId, memberId);
                    if(update){log.info("updateRoomList : roomId = {}, memberId = {}", roomId, memberId);}
                    log.info("SaveMessage : roomId = {}, memberId = {}, content = {}, isChecked = {}", roomId, memberId, content, isChecked);
                    for (WebSocketSession targetSession : roomSessionsList) {
                        if (targetSession.isOpen() && !targetSession.equals(session)) {
                            log.info("sendMessage : roomId = {}, memberId = {}, content = {}", roomId, memberId, content);
                            targetSession.sendMessage(new TextMessage(saveId + " " + content));
                        }
                        if (targetSession.isOpen() && targetSession.equals(session) && roomSessionsList.size() == 2){
                            log.info("sendMessage : send 0 sessionURI = {}", session.getUri());
                            targetSession.sendMessage(new TextMessage("0"));
                        }
                    }
                }else{
                    log.warn("SaveMessage : getValue's not correct / roomId = {}, memberId = {}", roomId, memberId);
                }
            }
        }else if(!checkExpired){
            log.info("checkExpired '0' : roomId = {}", roomId);
            String exitMessage = "상대방이 채팅방을 나갔습니다.";
            sendExceptionMessage(roomId, session, exitMessage);
        }else if(checkTime != 0){
            String alarmMessage = "방 유효시간이 지났기 때문에 메시지를 보낼 수 없습니다.";
            if(checkTime == 1){
                log.info("checkTime over 24hours : roomId = {}", roomId);
            }else if(checkTime == 7){
                log.info("checkTime over 7days : roomId = {}", roomId);
            }else{
                log.warn("checkTime : getValue's not correct / roomId = {}", roomId);
                alarmMessage = "방 정보가 유효하지 않습니다.";
            }
            sendExceptionMessage(roomId, session, alarmMessage);
        }else if(!checkIsWithDrawal){
            log.info("checkIsWithDrawal : roomId = {}", roomId);
            String outMessage = "상대방이 탈퇴하였습니다.";
            sendExceptionMessage(roomId, session, outMessage);
        }else if(!checkMessageReport.isEmpty()){
            log.info("checkMessageReport : roomId = {}", roomId);
            String outMessage = "신고된 방입니다.";
            sendExceptionMessage(roomId, session, outMessage);
        }
    }

    private void sendExceptionMessage(String roomId, WebSocketSession session, String message) throws IOException {
        List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
        if (roomSessionsList != null) {
            for (WebSocketSession targetSession : roomSessionsList) {
                if (targetSession.equals(session)) {
                    targetSession.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    public Boolean updateCurrentMessage(String roomId, String memberId) throws Exception {
        Room room = roomRepository.findById(Long.parseLong(roomId)).orElse(null);
        Member member = memberRepository.findById(Long.parseLong(memberId)).orElse(null);
        if(room != null && member != null){
            RoomInMember roomInMember = roomInMemberRepository.checkExpired(room, member);
            List<WebSocketSession> memberSessionsList = memberSessions.get(String.valueOf(roomInMember.getMember().getId()));
            if(memberSessionsList == null || memberSessionsList.isEmpty()){
                return false;
            }
            for(WebSocketSession targetSession : memberSessionsList){
                if(targetSession.isOpen()){
                    RoomListDTO roomDTO = roomInMemberService.getUpdateRoom(room, member);
                    String message = roomDTO.getRoomId() + " " + roomDTO.getName() + " " +
                            roomDTO.getUrlCode() + " " + roomDTO.getUnCheckedMessage() + " " + roomDTO.getContent();
                    log.info("updateCurrentMessage : message = {}", message);
                    targetSession.sendMessage(new TextMessage(message));
                    return true;
                }
            }
            log.info("updateCurrentMessage Fail : roomId = {}, memberId = {}", roomInMember.getRoom().getId(), roomInMember.getMember().getId());
        }
        return false;
    }

    private String getRoomId(WebSocketSession session) throws URISyntaxException {
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        String[] arr = query.split("&");
        String getString = arr[arr.length-2];
        String[] getArr = getString.split("=");
        String getRoomId = getArr[getArr.length-1];
        return getRoomId;
    }

    private String getMemberId(WebSocketSession session) throws URISyntaxException {
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        String[] arr = query.split("&");
        String getString = arr[arr.length-1];
        String[] getArr = getString.split("=");
        String getMemberId = getArr[getArr.length-1];
        return getMemberId;
    }

    private String getOnlyMemberId(WebSocketSession session) throws URISyntaxException{
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        String[] getArr = query.split("=");
        String getMemberId = getArr[getArr.length-1];
        return getMemberId;
    }

    public Integer parseUri (WebSocketSession session) throws URISyntaxException{
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        if(query == null){
            return 0;
        }else{
            String[] arr = query.split("&");
            if(arr.length == 1){
                return 1;
            }
            return 2;
        }
    }

    public Boolean checkURI(WebSocketSession session, String roomId){
        List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
        if(roomSessionsList == null || roomSessionsList.isEmpty()){
            return true;
        }else{
            for(WebSocketSession ss : roomSessionsList){
                if(ss.getUri().toString().equals(session.getUri().toString())){
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean checkURIOfMemberId(WebSocketSession session, String memberId){
        List<WebSocketSession> memberRoomList = memberSessions.get(memberId);
        if(memberRoomList == null || memberRoomList.isEmpty()){
            return true;
        }else{
            for(WebSocketSession ss : memberRoomList){
                if(ss.getUri().toString().equals(session.getUri().toString())){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        Integer length = parseUri(session);
        switch (length){
            case 1 :
                Long onlyMemberId = Long.parseLong(getOnlyMemberId(session));
                Member member = memberRepository.findBysessionId(onlyMemberId).orElse(null);
                if(member != null){
                    String memberId = String.valueOf(member.getId());
                    if(checkURIOfMemberId(session, memberId)){
                        log.info("websocketConnect : memberId = {}", memberId);
                        memberSessions.computeIfAbsent(memberId, key -> new ArrayList<>()).add(session);
                    }else{
                        log.info("Already existed websocket : memberId = {}", memberId);
                    }
                }else{
                    log.warn("websocketConnect Fail : memberId = {}", onlyMemberId);
                }
                break;
            case 2 :
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberRepository.findBysessionId(memberOfSessionId).orElse(null);
                if(mem != null) {
                    String memberId = String.valueOf(mem.getId());
                    Boolean checkURI = checkURI(session, roomId);
                    if (checkURI) {
                        log.info("websocketConnect : roomId = {}, memberId = {}", roomId, memberId);
                        roomSessions.computeIfAbsent(roomId, key -> new ArrayList<>()).add(session);
                        Boolean updateEntryTime = roomInMemberService.updateEntryTime(roomId, memberId);
                        if (updateEntryTime) {
                            log.info("updateEntryTime : roomId = {}, memberId = {}", roomId, memberId);
                        } else {
                            log.warn("updateEntryTime : getValue's not correct / roomId = {}, memberId = {}", roomId, memberId);
                        }

                        Boolean updateIsChecked = messageService.updateIsChecked(roomId, memberId);
                        if (updateIsChecked) {
                            log.info("updateIsChecked : roomId = {}, memberId = {}", roomId, memberId);
                        } else {
                            log.warn("updateIsChecked : getValue's not correct / roomId = {}, memberId = {}", roomId, memberId);
                        }

                        List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
                        Integer checkTime = roomService.checkTime(Long.parseLong(roomId));
                        for (WebSocketSession targetSession : roomSessionsList) {
                            if (targetSession.isOpen() && !targetSession.equals(session) && checkTime == 0) {
                                String check = "0";
                                log.info("Opponent has entered, targetSession = {}, check = {}", targetSession, check);
                                targetSession.sendMessage(new TextMessage(check));
                            }
                        }
                    }else{
                        log.warn("already existed websocket : roomId = {}, memberId = {}", roomId, memberId);
                    }
                }else{
                    log.warn("websocketConnect Fail : roomId = {}, sessionId = {}", roomId, memberOfSessionId);
                }
                break;
            default:
                log.info("websocketConnect : /ws");
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer length = parseUri(session);
        switch (length){
            case 1 :
                Long onlyMemberId = Long.parseLong(getOnlyMemberId(session));
                Member member = memberRepository.findBysessionId(onlyMemberId).orElse(null);
                if(member != null){
                    String memberId = String.valueOf(member.getId());
                    log.info("websocketClosed : memberId = {}", memberId);
                    List<WebSocketSession> memberSessionsList = memberSessions.get(memberId);
                    if(memberSessionsList != null){
                        memberSessionsList.remove(session);
                        if(memberSessionsList.isEmpty()){
                            log.info("websocketClosed Remove : memberId = {}", memberId);
                            memberSessions.remove(memberId);
                        }
                    }
                }
                break;
            case 2 :
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberRepository.findBysessionId(memberOfSessionId).orElse(null);
                if(mem != null) {
                    String memberId = String.valueOf(mem.getId());
                    log.info("websocketClosed : roomId = {}, memberId = {}", roomId, memberId);
                    List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
                    if (roomSessionsList != null) {
                        roomSessionsList.remove(session);
                        if (roomSessionsList.isEmpty()) {
                            log.info("websocketClosed Remove : roomId = {}, memberId = {}", roomId, memberId);
                            roomSessions.remove(roomId);
                        }
                    }else{
                        log.warn("websocketClosed can't find : roomId = {}, memberId = {}", roomId, memberId);
                    }
                    Boolean updateExitTime = roomInMemberService.updateExitTime(roomId, memberId);
                    if (updateExitTime) {
                        log.info("updateExitTime : roomId = {}, memberId = {}", roomId, memberId);
                    } else {
                        log.warn("updateExitTime : getValue's not correct / roomId = {}, memberId = {}", roomId, memberId);
                    }
                }else{
                    log.warn("websocketClosed can't find memberId : roomId = {}, session = {}", roomId, memberOfSessionId);
                }
                break;
            default:
                log.info("websocketClosed : /ws");
                break;
        }
    }
}
