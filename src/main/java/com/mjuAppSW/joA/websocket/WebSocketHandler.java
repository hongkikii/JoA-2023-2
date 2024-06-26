package com.mjuAppSW.joA.websocket;

import static com.mjuAppSW.joA.common.constant.AlarmConstants.ChatInChattingRoom;
import static com.mjuAppSW.joA.common.constant.Constants.RoomInMember.*;
import static com.mjuAppSW.joA.common.constant.Constants.WebSocketHandler.*;

import com.mjuAppSW.joA.common.constant.Constants;
import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.member.vo.UserFcmTokenVO;
import com.mjuAppSW.joA.domain.room.service.RoomQueryService;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.message.service.MessageService;
import com.mjuAppSW.joA.domain.messageReport.entity.MessageReport;
import com.mjuAppSW.joA.domain.messageReport.repository.MessageReportRepository;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.room.service.RoomService;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberQueryService;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberService;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;

import com.mjuAppSW.joA.fcm.service.FCMService;
import com.mjuAppSW.joA.fcm.vo.FCMInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    private final RoomInMemberQueryService roomInMemberQueryService;
    private final MessageService messageService;
    private final MemberQueryService memberQueryService;
    private final RoomService roomService;
    private final RoomQueryService roomQueryService;
    private final MessageReportRepository messageReportRepository;
    private final FCMService fcmService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String[] arr = payload.split(SEPARATOR, LIMIT_SEPARATOR);

        String separator = arr[0];
        if(separator.equals(R_SEPARATOR)){
            createRoomManage(arr[1], sessionIdToMemberId(arr[2]), arr[3]);
        }else if(separator.equals(M_SEPARATOR)){
            sendMessageManage(arr[1], sessionIdToMemberId(arr[2]), arr[3], session);
        }
    }

    public String sessionIdToMemberId(String session){
        Member member = memberQueryService.getBySessionId(Long.parseLong(session));
        String memberId = String.valueOf(member.getId());
        return memberId;
    }

    public void createRoomManage(String roomId, String memberId1, String memberId2) {
        roomService.findByRoom(Long.parseLong(roomId));
        String[] idArr = {memberId1, memberId2};
        roomInMemberService.create(Long.parseLong(roomId), idArr);
    }

    public void sendMessageManage(String roomId, String memberId, String content, WebSocketSession session) throws Exception {
        if(checkCondition(Long.parseLong(roomId), Long.parseLong(memberId), session)){
            List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
            if(roomSessionsList == null || roomSessionsList.isEmpty()){
                throw BusinessException.RoomSessionListNullException;
            }
            int count = MAX_CAPACITY_IN_ROOM;
            for(int i=0; i<roomSessionsList.size(); i++) count--;
            String isChecked = String.valueOf(count);
            Long saveId = saveMessage(roomId, memberId, content, isChecked);
            updateCurrentMessage(roomId, memberId);
            sendMessage(content, saveId, session, roomSessionsList);
            makeFCMVO(roomId, memberId, content);
        }
    }

    private void makeFCMVO(String roomId, String memberId, String content){
        Room room = roomQueryService.getById(Long.parseLong(roomId));
        Member member = memberQueryService.getById(Long.parseLong(memberId));

        UserFcmTokenVO opponentTokenVO = roomInMemberQueryService.getOpponentFcmTokenByRoomAndMember(room, member);

        fcmService.send(FCMInfoVO.ofWithContent(opponentTokenVO.getFcmToken(), member.getName(), ChatInChattingRoom, content));
    }

    private Boolean checkCondition(Long roomId, Long memberId, WebSocketSession session) throws IOException {
        if(checkMessageReport(roomId, session) && checkExpired(roomId, memberId, session)
            && checkIsWithDrawal(roomId, memberId, session) && checkTime(roomId, session)) return true;
        return false;
    }

    private Boolean checkMessageReport(Long roomId, WebSocketSession session) throws IOException {
        Room room = roomQueryService.getById(roomId);
        List<MessageReport> checkMessageReport = messageReportRepository.findByRoom(room);
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
        if(roomSessionsList == null || roomSessionsList.isEmpty()){
            throw BusinessException.RoomSessionListNullException;
        }
        for (WebSocketSession targetSession : roomSessionsList) {
            if (targetSession.equals(session)) {
                targetSession.sendMessage(new TextMessage(message));
            }
        }
    }

    private Long saveMessage(String roomId, String memberId, String content, String isChecked) {
        LocalDateTime createdMessageDate = LocalDateTime.now();
        Long saveId = messageService.save(Long.parseLong(roomId), Long.parseLong(memberId), content, isChecked, createdMessageDate);
        return saveId;
    }

    private void sendMessage(String content, Long saveId, WebSocketSession session, List<WebSocketSession> roomSessionsList) throws Exception {
        for (WebSocketSession targetSession : roomSessionsList) {
            if (targetSession.isOpen() && !targetSession.equals(session)) {
                targetSession.sendMessage(new TextMessage(saveId + SEPARATOR + content));
            }
            if (targetSession.isOpen() && targetSession.equals(session) && roomSessionsList.size() == MAX_CAPACITY_IN_ROOM) {
                targetSession.sendMessage(new TextMessage(OPPONENT_CHECK_MESSAGE));
            }
        }
    }

    public void updateCurrentMessage(String roomId, String memberId) throws Exception {
        Room room = roomQueryService.getById(Long.parseLong(roomId));
        Member member = memberQueryService.getById(Long.parseLong(memberId));
        RoomInMember roomInMember = roomInMemberQueryService.getOpponentByRoomAndMemberAndExpired(room, member, NOT_EXIT);

        List<WebSocketSession> memberSessionsList = memberSessions.get(String.valueOf(roomInMember.getMember().getId()));

        if(memberSessionsList != null) {
            for (WebSocketSession targetSession : memberSessionsList) {
                if (targetSession.isOpen()) {
                    RoomInfoExceptDateVO roomVO = roomInMemberService.getUpdateRoomInfo(room, member);
                    String message = roomVO.getRoomId() + SEPARATOR + roomVO.getName() + SEPARATOR +
                        roomVO.getUrlCode() + SEPARATOR + roomVO.getUnCheckedMessage() + SEPARATOR
                        + roomVO.getContent();
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

    public Boolean checkURIOfRoomId(WebSocketSession session, String roomId){
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
                Member member = memberQueryService.getBySessionId(onlyMemberId);

                memberId = String.valueOf(member.getId());
                if (checkURIOfMemberId(session, memberId)) memberSessions.computeIfAbsent(memberId, key -> new ArrayList<>()).add(session);
                break;
            case 2:
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberQueryService.getBySessionId(memberOfSessionId);

                memberId = String.valueOf(mem.getId());
                if (checkURIOfRoomId(session, roomId)) {
                    roomSessions.computeIfAbsent(roomId, key -> new ArrayList<>()).add(session);

                    roomInMemberService.updateEntryTime(roomId, memberId);
                    messageService.updateIsChecked(roomId, memberId);

                    List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);
                    if(roomSessionsList.isEmpty() || roomSessionsList == null) {
                        throw BusinessException.RoomSessionListNullException;
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
                Member member = memberQueryService.getBySessionId(onlyMemberId);

                memberId = String.valueOf(member.getId());
                List<WebSocketSession> memberSessionsList = memberSessions.get(memberId);

                if(memberSessionsList.isEmpty() || memberSessionsList == null){
                    throw BusinessException.MemberSessionListNullException;
                }

                memberSessionsList.remove(session);
                if(memberSessionsList.isEmpty()) memberSessions.remove(memberId);
                break;
            case 2 :
                String roomId = getRoomId(session);
                Long memberOfSessionId = Long.parseLong(getMemberId(session));
                Member mem = memberQueryService.getBySessionId(memberOfSessionId);

                memberId = String.valueOf(mem.getId());
                List<WebSocketSession> roomSessionsList = roomSessions.get(roomId);

                if(roomSessionsList.isEmpty() || roomSessionsList == null){
                    throw BusinessException.RoomSessionListNullException;
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
