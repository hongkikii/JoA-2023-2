package com.mjuAppSW.joA.common.websocket;

import static com.mjuAppSW.joA.common.constant.Constants.RoomInMember.*;
import static com.mjuAppSW.joA.common.constant.Constants.WebSocketHandler.*;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.common.websocket.exception.RoomSessionListNullException;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberRepository;
import com.mjuAppSW.joA.domain.message.MessageService;
import com.mjuAppSW.joA.domain.report.message.MessageReport;
import com.mjuAppSW.joA.domain.report.message.MessageReportRepository;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.room.RoomService;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberService;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private Map<String, List<WebSocketSession>> memberSessions = new ConcurrentHashMap<>();
    private final RoomInMemberService roomInMemberService;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MessageService messageService;
    private final RoomRepository roomRepository;
    private final MemberChecker memberChecker;
    private final MemberRepository memberRepository;
    private final RoomService roomService;
    private final MessageReportRepository messageReportRepository;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);
        String[] arr = payload.split(SEPARATOR, LIMIT_SEPARATOR);

        String separator = arr[0];
        if(separator.equals(R_SEPARATOR)){
            makeRoom(arr[1], sessionIdToMemberId(arr[2]), arr[3]);
        }else if(separator.equals(M_SEPARATOR)){
            sendMessage(arr[1], sessionIdToMemberId(arr[2]), arr[3], session);
        }
    }

    public String sessionIdToMemberId(String session){
        Member member = memberChecker.findBySessionId(Long.parseLong(session));
        String memberId = String.valueOf(member.getId());
        return memberId;
    }

    public void makeRoom(String roomId, String memberId1, String memberId2){
        roomInMemberService.findByRoom(Long.parseLong(roomId));
        String[] idArr = {memberId1, memberId2};
        roomInMemberService.createRoom(Long.parseLong(roomId), idArr);
    }

    public void sendMessage(String roomId, String memberId, String content, WebSocketSession session) throws Exception {
        if(isMessageSendable(Long.parseLong(roomId), Long.parseLong(memberId), session)){
            List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
            if(roomSessionsList.isEmpty() || roomSessionsList == null){
                throw new RoomSessionListNullException();
            }
            int count = MAX_CAPACITY_IN_ROOM;
            for(int i=0; i<roomSessionsList.size(); i++) count--;
            String isChecked = String.valueOf(count);
            Long saveId = saveAndSendMessage(roomId, memberId, content, isChecked);
            updateAndNotify(roomId, memberId, content, saveId, session, roomSessionsList);
        }
    }

    private boolean isMessageSendable(Long roomId, Long memberId, WebSocketSession session) throws IOException {
        // find Room
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

        // check MessageReport
        List<MessageReport> checkMessageReport = messageReportRepository.findByRoomId(room);
        // check Expired
        Boolean checkExpired = roomInMemberService.checkExpired(roomId, memberId);
        // check isWithDrawal
        Boolean checkIsWithDrawal = roomInMemberService.checkIsWithDrawal(roomId, memberId);
        // check createdAt
        Integer checkTime = roomService.checkTime(roomId);

        if(!checkMessageReport.isEmpty()){
            sendExceptionMessage(String.valueOf(roomId), session, ALARM_REPORTED_ROOM);
            return false;
        }else if(!checkExpired){
            sendExceptionMessage(String.valueOf(roomId), session, ALARM_OPPONENT_EXITED);
            return false;
        }else if(!checkIsWithDrawal){
            sendExceptionMessage(String.valueOf(roomId), session, ALARM_OPPONENT_IS_WITH_DRAWAL);
            return false;
        }else if(checkTime != NORMAL_OPERATION){
            handleTimeRelatedIssues(roomId, checkTime, session);
            return false;
        }
        return true;
    }

    private void handleTimeRelatedIssues(Long roomId, Integer checkTime, WebSocketSession session) throws IOException {
        String alarmMessage = "";
        if (checkTime == OVER_ONE_DAY) {
            alarmMessage = ALARM_OVER_ONE_DAY;
        } else if (checkTime == OVER_SEVEN_DAY) {
            alarmMessage = ALARM_OVER_SEVEN_DAY;
        }
        sendExceptionMessage(String.valueOf(roomId), session, alarmMessage);
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

    private Long saveAndSendMessage(String roomId, String memberId, String content, String isChecked) {
        LocalDateTime createdMessageDate = LocalDateTime.now();
        Long saveId = messageService.saveMessage(Long.parseLong(roomId), Long.parseLong(memberId), content, isChecked, createdMessageDate);
        log.info("SaveMessage : roomId = {}, memberId = {}, content = {}, isChecked = {}", roomId, memberId, content, isChecked);
        return saveId;
    }

    private void updateAndNotify(String roomId, String memberId, String content, Long saveId,
        WebSocketSession session, List<WebSocketSession> roomSessionsList) throws Exception {
        Boolean update = updateCurrentMessage(roomId, memberId);
        if (update) {log.info("updateRoomList : roomId = {}, memberId = {}", roomId, memberId);}
        for (WebSocketSession targetSession : roomSessionsList) {
            if (targetSession.isOpen() && !targetSession.equals(session)) {
                log.info("sendMessage : roomId = {}, memberId = {}, content = {}", roomId, memberId, content);
                targetSession.sendMessage(new TextMessage(saveId + SEPARATOR + content));
            }
            if (targetSession.isOpen() && targetSession.equals(session) && roomSessionsList.size() == 2) {
                log.info("sendMessage : send 0 sessionURI = {}", session.getUri());
                targetSession.sendMessage(new TextMessage(OPPONENT_CHECK_MESSAGE));
            }
        }
    }

    public Boolean updateCurrentMessage(String roomId, String memberId) throws Exception {
        Room room = roomRepository.findById(Long.parseLong(roomId)).orElse(null);
        Member member = memberRepository.findById(Long.parseLong(memberId)).orElse(null);
        if(room != null && member != null){
            RoomInMember roomInMember = roomInMemberRepository.checkOpponentExpired(room, member, NOT_EXIT).orElseThrow(
                RoomInMemberNotFoundException::new);
            List<WebSocketSession> memberSessionsList = memberSessions.get(String.valueOf(roomInMember.getMember().getId()));
            if(memberSessionsList == null || memberSessionsList.isEmpty()) return false;

            for(WebSocketSession targetSession : memberSessionsList){
                if(targetSession.isOpen()){
                    RoomInfoExceptDateVO roomVO = roomInMemberService.getUpdateRoom(room, member);
                    String message = roomVO.getRoomId() + SEPARATOR + roomVO.getName() + SEPARATOR +
                        roomVO.getUrlCode() + SEPARATOR + roomVO.getUnCheckedMessage() + SEPARATOR + roomVO.getContent();
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
        String[] arr = query.split(AND_OPERATION);
        String getString = arr[arr.length-2];
        String[] getArr = getString.split(EQUAL_OPERATION);
        String getRoomId = getArr[getArr.length-1];
        return getRoomId;
    }

    private String getMemberId(WebSocketSession session) throws URISyntaxException {
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        String[] arr = query.split(AND_OPERATION);
        String getString = arr[arr.length-1];
        String[] getArr = getString.split(EQUAL_OPERATION);
        String getMemberId = getArr[getArr.length-1];
        return getMemberId;
    }

    private String getOnlyMemberId(WebSocketSession session) throws URISyntaxException{
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        String[] getArr = query.split(EQUAL_OPERATION);
        String getMemberId = getArr[getArr.length-1];
        return getMemberId;
    }

    public Integer parseUri (WebSocketSession session) throws URISyntaxException{
        URI uri = new URI(session.getUri().toString());
        String query = uri.getQuery();
        if(query == null) return 0;

        String[] arr = query.split(AND_OPERATION);
        if(arr.length == 1) return 1;
        return 2;
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
        String memberId = "";
        switch (length) {
            case 1:
                Long onlyMemberId = Long.parseLong(getOnlyMemberId(session));
                Member member = memberChecker.findBySessionId(onlyMemberId);

                memberId = String.valueOf(member.getId());
                if (checkURIOfMemberId(session, memberId)) {
                    log.info("websocketConnect : memberId = {}", memberId);
                    memberSessions.computeIfAbsent(memberId, key -> new ArrayList<>()).add(session);
                } else {
                    log.info("Already existed websocket : memberId = {}", memberId);
                }
                break;
            case 2:
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberChecker.findBySessionId(memberOfSessionId);

                memberId = String.valueOf(mem.getId());
                Boolean checkURI = checkURI(session, roomId);
                if (checkURI) {
                    roomSessions.computeIfAbsent(roomId, key -> new ArrayList<>()).add(session);

                    roomInMemberService.updateEntryTime(roomId, memberId);
                    messageService.updateIsChecked(roomId, memberId);

                    List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
                    Integer checkTime = roomService.checkTime(Long.parseLong(roomId));
                    for (WebSocketSession targetSession : roomSessionsList) {
                        if (targetSession.isOpen() && !targetSession.equals(session) && checkTime == NORMAL_OPERATION) {
                            log.info("Opponent has entered, targetSession = {}, check = {}", targetSession,
                                OPPONENT_CHECK_MESSAGE);
                            targetSession.sendMessage(new TextMessage(OPPONENT_CHECK_MESSAGE));
                        }
                    }
                } else {
                    log.warn("already existed websocket : roomId = {}, memberId = {}", roomId, memberId);
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
        String memberId = "";
        switch (length){
            case 1 :
                Long onlyMemberId = Long.parseLong(getOnlyMemberId(session));
                Member member = memberChecker.findBySessionId(onlyMemberId);

                memberId = String.valueOf(member.getId());
                log.info("websocketClosed : memberId = {}", memberId);
                List<WebSocketSession> memberSessionsList = memberSessions.get(memberId);
                if(memberSessionsList != null){
                    memberSessionsList.remove(session);
                    if(memberSessionsList.isEmpty()){
                        log.info("websocketClosed Remove : memberId = {}", memberId);
                        memberSessions.remove(memberId);
                    }
                }
                break;
            case 2 :
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberChecker.findBySessionId(memberOfSessionId);

                memberId = String.valueOf(mem.getId());
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
                roomInMemberService.updateExitTime(roomId, memberId);
                log.info("updateExitTime : roomId = {}, memberId = {}", roomId, memberId);
                break;
            default:
                log.info("websocketClosed : /ws");
                break;
        }
    }
}
