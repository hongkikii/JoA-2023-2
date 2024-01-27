package com.mjuAppSW.joA.domain.roomInMember;

import static com.mjuAppSW.joA.common.constant.Constants.RoomInMember.*;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.common.encryption.EncryptManager;
import com.mjuAppSW.joA.domain.member.MemberEntity;
import com.mjuAppSW.joA.domain.member.service.MemberService;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.message.dto.vo.CurrentMessageVO;
import com.mjuAppSW.joA.domain.message.exception.FailDecryptException;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.CheckRoomInMemberRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.UpdateExpiredRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.RoomListResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.UserInfoResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.VoteResponse;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoIncludeMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberAlreadyExistedException;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberNotFoundException;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomInMemberService {
    private final RoomInMemberRepository roomInMemberRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MemberService memberService;
    private final EncryptManager encryptManager;

    public void findByRoom(Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

        List<RoomInMember> roomInMemberList = roomInMemberRepository.findAllRoom(room);
        for(RoomInMember roomInMember : roomInMemberList) {
            if (roomInMember.getExpired().equals(NOT_EXIT)) {
                throw new RoomInMemberAlreadyExistedException();
            }
        }
    }

    public RoomListResponse getRoomList(Long memberId) {
        MemberEntity member = memberService.findBySessionId(memberId);
        memberService.checkStopped(member);

        List<RoomInMember> memberList = roomInMemberRepository.findByAllMember(member);
        if (memberList.isEmpty()) {return RoomListResponse.of(new ArrayList<>());}

        List<RoomInfoVO> roomWithoutMessageList = new ArrayList<>();
        List<RoomInfoVO> roomWithMessageList = new ArrayList<>();
        for (RoomInMember rim : memberList) {
            RoomInMember anotherRoomInMember = roomInMemberRepository.findByRoomAndExceptMember(rim.getRoom(), rim.getMember())
                .orElseThrow(RoomInMemberNotFoundException::new);
            RoomInfoExceptMessageVO roomInfoEMVO = roomInMemberRepository.findRoomInfoExceptMessage(anotherRoomInMember.getRoom(), anotherRoomInMember.getMember())
                .orElseThrow(RoomInMemberNotFoundException::new);
            CurrentMessageVO currentMessageVO = messageRepository.getCurrentMessageAndTime(anotherRoomInMember.getRoom())
                .orElse(null);
            Integer unCheckedMessage = messageRepository.countUnCheckedMessage(anotherRoomInMember.getRoom(), anotherRoomInMember.getMember());
            if (roomInfoEMVO != null) {
                if (currentMessageVO == null) {
                    RoomInfoVO roomInfoVO = new RoomInfoVO(roomInfoEMVO.getRoom().getId(), roomInfoEMVO.getName(),
                        roomInfoEMVO.getUrlCode(), null, roomInfoEMVO.getDate(), String.valueOf(unCheckedMessage));
                    roomWithoutMessageList.add(roomInfoVO);
                } else {
                    String decryptedString = encryptManager.decrypt(currentMessageVO.getContent(), anotherRoomInMember.getRoom().getEncryptKey());
                    if(decryptedString == null){
                        throw new FailDecryptException();
                    }
                    RoomInfoVO roomInfoVO = new RoomInfoVO(roomInfoEMVO.getRoom().getId(), roomInfoEMVO.getName(),
                        roomInfoEMVO.getUrlCode(), decryptedString, currentMessageVO.getTime(), String.valueOf(unCheckedMessage));
                    roomWithMessageList.add(roomInfoVO);
                }
            }
        }
        return RoomListResponse.of(combinedList(roomWithoutMessageList, roomWithMessageList));
    }

    private List<RoomInfoExceptDateVO> combinedList(List<RoomInfoVO> roomWithoutMessageList, List<RoomInfoVO> roomWithMessageList){
        List<RoomInfoExceptDateVO> roomListVOs = new ArrayList<>();
        List<RoomInfoVO> combinedList = new ArrayList<>();
        combinedList.addAll(roomWithoutMessageList);
        combinedList.addAll(roomWithMessageList);
        Collections.sort(combinedList, new Comparator<RoomInfoVO>() {
            @Override
            public int compare(RoomInfoVO dto1, RoomInfoVO dto2) {
                return dto2.getTime().compareTo(dto1.getTime());
            }
        });
        for(RoomInfoVO roomInfoVO : combinedList) {
            roomListVOs.add(RoomInfoExceptDateVO.of(roomInfoVO.getRoomId(), roomInfoVO.getName(), roomInfoVO.getUrlCode(),
                roomInfoVO.getContent(), roomInfoVO.getUnCheckedMessage()));
        }
        return roomListVOs;
    }

    public RoomInfoExceptDateVO getUpdateRoom(Room room, MemberEntity member){
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);
        RoomInfoIncludeMessageVO rlr = roomInMemberRepository.findRoomInfoIncludeMessage(roomInMember.getRoom(), roomInMember.getMember())
            .orElseThrow(RoomInMemberNotFoundException::new);
        Integer unCheckedMessageCount = messageRepository.countUnCheckedMessage(roomInMember.getRoom(), roomInMember.getMember());

        String decryptedString = encryptManager.decrypt(rlr.getContent(), rlr.getRoom().getEncryptKey());
        if(decryptedString == null){
            throw new FailDecryptException();
        }
        return RoomInfoExceptDateVO.of(rlr.getRoom().getId(), rlr.getName(), rlr.getUrlCode(), decryptedString, String.valueOf(unCheckedMessageCount));
    }

    @Transactional
    public void createRoom(Long roomId, String[] idArr){
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

        for(String memberId : idArr){
            MemberEntity member = memberChecker.findById(Long.parseLong(memberId));
            RoomInMember roomInMember = RoomInMember.builder()
                .room(room)
                .member(member)
                .expired(NOT_EXIT)
                .result(DISAPPROVE_OR_BEFORE_VOTE)
                .build();

            roomInMemberRepository.save(roomInMember);
        }
    }

    @Transactional
    public VoteResponse saveVoteResult(VoteRequest request){
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findBySessionId(request.getMemberId());
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        roomInMember.saveResult(request.getResult());
        RoomInMember anotherRoomInMember = roomInMemberRepository.findByRoomAndExceptMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);
        return VoteResponse.of(anotherRoomInMember.getRoom().getId(), anotherRoomInMember.getMember().getId(), anotherRoomInMember.getResult());
    }

    public void checkRoomInMember(CheckRoomInMemberRequest request){
        MemberEntity member1 = memberChecker.findBySessionId(request.getMemberId1());
        MemberEntity member2 = memberChecker.findById(request.getMemberId2());
        List<RoomInMember> getRoomInMembers = roomInMemberRepository.checkRoomInMember(member1, member2);
        for(RoomInMember rim : getRoomInMembers){
            if(rim.getExpired().equals(NOT_EXIT)){throw new RoomInMemberAlreadyExistedException();}
        }
    }

    public UserInfoResponse getUserInfo(Long roomId, Long memberId){
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findBySessionId(memberId);
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        UserInfoVO userInfoVO = roomInMemberRepository.getUserInfo(room, member);
        return UserInfoResponse.of(userInfoVO.getName(), userInfoVO.getUrlCode(), userInfoVO.getBio());
    }

    @Transactional
    public void updateExpired(UpdateExpiredRequest request) {
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findBySessionId(request.getMemberId());
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        roomInMember.updateExpired(request.getExpired());
    }

    @Transactional
    public void updateEntryTime(String sRoomId, String sMemberId){
        Room room = roomRepository.findById(Long.parseLong(sRoomId)).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findById(Long.parseLong(sMemberId));
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        roomInMember.updateEntryTime(LocalDateTime.now());
    }

    @Transactional
    public void updateExitTime(String sRoomId, String sMemberId){
        Room room = roomRepository.findById(Long.parseLong(sRoomId)).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findById(Long.parseLong(sMemberId));
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        roomInMember.updateExitTime(LocalDateTime.now());
    }

    public Boolean checkExpired(Long roomId, Long memberId){
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findById(memberId);
        RoomInMember roomInMember = roomInMemberRepository.findOpponentByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        if(roomInMember.getExpired().equals(NOT_EXIT)){return true;}
        return false;
    }

    public Boolean checkIsWithDrawal(Long roomId, Long memberId){
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberChecker.findById(memberId);
        RoomInMember rim = roomInMemberRepository.findOpponentByRoomAndMember(room, member).orElseThrow(RoomInMemberNotFoundException::new);

        MemberEntity opponentMember = memberChecker.findById(rim.getMember().getId());
        if (opponentMember != null) return true;
        return false;
    }
}
