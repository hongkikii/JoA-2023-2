package com.mjuAppSW.joA.common.websocket;

import static com.mjuAppSW.joA.common.constant.Constants.RoomInMember.*;
import static com.mjuAppSW.joA.common.constant.Constants.WebSocketHandler.*;

import com.mjuAppSW.joA.common.websocket.exception.MemberSessionListNullException;
import com.mjuAppSW.joA.common.websocket.exception.RoomSessionListNullException;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberEntity;
import com.mjuAppSW.joA.domain.member.service.MemberService;
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
    private final MemberService memberService;
    private final RoomService roomService;
    private final MessageReportRepository messageReportRepository;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String[] arr = payload.split(SEPARATOR, LIMIT_SEPARATOR);

        String separator = arr[0];
        if(separator.equals(R_SEPARATOR)){
            makeRoom(arr[1], sessionIdToMemberId(arr[2]), arr[3]);
        }else if(separator.equals(M_SEPARATOR)){
            sendMessage(arr[1], sessionIdToMemberId(arr[2]), arr[3], session);
        }
    }

    public String sessionIdToMemberId(String session){
        Member member = memberService.getBySessionId(Long.parseLong(session));
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


    private Boolean isMessageSendable(Long roomId, Long memberId, WebSocketSession session) throws IOException {
        if(checkMessageReport(roomId, session) && checkExpired(roomId, memberId, session)
            && checkIsWithDrawal(roomId, memberId, session) && checkTime(roomId, session)) return true;
        return false;
    }

    private Boolean checkMessageReport(Long roomId, WebSocketSession session) throws IOException {
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        List<MessageReport> checkMessageReport = messageReportRepository.findByRoomId(room);
        if(!checkMessageReport.isEmpty()){
            sendExceptionMessage(String.valueOf(roomId), session, ALARM_REPORTED_ROOM);
            return false;
        }return true;
    }

    private Boolean checkExpired(Long roomId, Long memberId, WebSocketSession session) throws IOException {
        Boolean checkExpired = roomInMemberService.checkExpired(roomId, memberId);
        if(!checkExpired){
            sendExceptionMessage(String.valueOf(roomId), session, ALARM_OPPONENT_EXITED);
            return false;
        }return true;
    }

    private Boolean checkIsWithDrawal(Long roomId, Long memberId, WebSocketSession session) throws IOException {
        Boolean checkIsWithDrawal = roomInMemberService.checkIsWithDrawal(roomId, memberId);
        if(!checkIsWithDrawal){
            sendExceptionMessage(String.valueOf(roomId), session, ALARM_OPPONENT_IS_WITH_DRAWAL);
            return false;
        }return true;
    }

    private Boolean checkTime(Long roomId, WebSocketSession session) throws IOException {
        Integer checkTime = roomService.checkTime(roomId);
        if(checkTime != NORMAL_OPERATION){
            handleTimeRelatedIssues(roomId, checkTime, session);
            return false;
        }return true;
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
        if(roomSessionsList.isEmpty() || roomSessionsList == null){
            throw new RoomSessionListNullException();
        }
        for (WebSocketSession targetSession : roomSessionsList) {
            if (targetSession.equals(session)) {
                targetSession.sendMessage(new TextMessage(message));
            }
        }
    }

    private Long saveAndSendMessage(String roomId, String memberId, String content, String isChecked) {
        LocalDateTime createdMessageDate = LocalDateTime.now();
        Long saveId = messageService.saveMessage(Long.parseLong(roomId), Long.parseLong(memberId), content, isChecked, createdMessageDate);
        return saveId;
    }

    private void updateAndNotify(String roomId, String memberId, String content, Long saveId,
        WebSocketSession session, List<WebSocketSession> roomSessionsList) throws Exception {

        updateCurrentMessage(roomId, memberId);

        for (WebSocketSession targetSession : roomSessionsList) {
            if (targetSession.isOpen() && !targetSession.equals(session)) {
                targetSession.sendMessage(new TextMessage(saveId + SEPARATOR + content));
            }
            if (targetSession.isOpen() && targetSession.equals(session) && roomSessionsList.size() == 2) {
                targetSession.sendMessage(new TextMessage(OPPONENT_CHECK_MESSAGE));
            }
        }
    }

    public void updateCurrentMessage(String roomId, String memberId) throws Exception {
        Room room = roomRepository.findById(Long.parseLong(roomId)).orElseThrow(RoomNotFoundException::new);
        Member member = memberService.getById(Long.parseLong(memberId));

        RoomInMember roomInMember = roomInMemberRepository.checkOpponentExpired(room, MemberEntity.fromModel(member), NOT_EXIT).orElseThrow(
            RoomInMemberNotFoundException::new);
        List<WebSocketSession> memberSessionsList = memberSessions.get(String.valueOf(roomInMember.getMember().getId()));

        if(memberSessionsList != null){
            for(WebSocketSession targetSession : memberSessionsList) {
                if (targetSession.isOpen()) {
                    RoomInfoExceptDateVO roomVO = roomInMemberService.getUpdateRoom(room, member);
                    String message = roomVO.getRoomId() + SEPARATOR + roomVO.getName() + SEPARATOR +
                        roomVO.getUrlCode() + SEPARATOR + roomVO.getUnCheckedMessage() + SEPARATOR + roomVO.getContent();
                    targetSession.sendMessage(new TextMessage(message));
                }
            }
        }
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
        if(roomSessionsList == null || roomSessionsList.isEmpty()) return true;
        for(WebSocketSession ss : roomSessionsList){
            if(ss.getUri().toString().equals(session.getUri().toString())) return false;
        }
        return true;
    }

    public Boolean checkURIOfMemberId(WebSocketSession session, String memberId){
        List<WebSocketSession> memberRoomList = memberSessions.get(memberId);
        if(memberRoomList == null || memberRoomList.isEmpty()) return true;
        for(WebSocketSession ss : memberRoomList){
            if(ss.getUri().toString().equals(session.getUri().toString())) return false;
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
                Member member = memberService.getBySessionId(onlyMemberId);

                memberId = String.valueOf(member.getId());
                if (checkURIOfMemberId(session, memberId)) memberSessions.computeIfAbsent(memberId, key -> new ArrayList<>()).add(session);
                break;
            case 2:
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberService.getBySessionId(memberOfSessionId);

                memberId = String.valueOf(mem.getId());
                if (checkURI(session, roomId)) {
                    roomSessions.computeIfAbsent(roomId, key -> new ArrayList<>()).add(session);

                    roomInMemberService.updateEntryTime(roomId, memberId);
                    messageService.updateIsChecked(roomId, memberId);

                    List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
                    if(roomSessionsList.isEmpty() || roomSessionsList == null) {
                        throw new RoomSessionListNullException();
                    }

                    Integer checkTime = roomService.checkTime(Long.parseLong(roomId));
                    for (WebSocketSession targetSession : roomSessionsList) {
                        if (targetSession.isOpen() && !targetSession.equals(session) && checkTime == NORMAL_OPERATION) {
                            targetSession.sendMessage(new TextMessage(OPPONENT_CHECK_MESSAGE));
                        }
                    }
                }
                break;
            default:
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
                Member member = memberService.getBySessionId(onlyMemberId);

                memberId = String.valueOf(member.getId());
                List<WebSocketSession> memberSessionsList = memberSessions.get(memberId);

                if(memberSessionsList.isEmpty() || memberSessionsList == null){
                    throw new MemberSessionListNullException();
                }

                memberSessionsList.remove(session);
                if(memberSessionsList.isEmpty()) memberSessions.remove(memberId);
                break;
            case 2 :
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberService.getBySessionId(memberOfSessionId);

                memberId = String.valueOf(mem.getId());
                List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);

                if(roomSessionsList.isEmpty() || roomSessionsList == null){
                    throw new RoomSessionListNullException();
                }

                roomSessionsList.remove(session);
                if (roomSessionsList.isEmpty()) roomSessions.remove(roomId);

                roomInMemberService.updateExitTime(roomId, memberId);
                break;
            default:
                break;
        }
    }
}
