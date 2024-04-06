package com.mjuAppSW.joA.domain.roomInMember.service;

import static com.mjuAppSW.joA.common.constant.AlarmConstants.CreateChattingRoom;
import static com.mjuAppSW.joA.common.constant.AlarmConstants.VoteChattingRoom;
import static com.mjuAppSW.joA.common.constant.Constants.Encrypt.*;
import static com.mjuAppSW.joA.common.constant.Constants.Message.*;
import static com.mjuAppSW.joA.common.constant.Constants.RoomInMember.*;

import com.mjuAppSW.joA.common.constant.AlarmConstants;
import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.member.dto.response.ChattingPageResponse;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.message.vo.CurrentMessageVO;
import com.mjuAppSW.joA.domain.message.repository.MessageRepository;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.repository.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.room.service.RoomQueryService;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.UpdateExpiredRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.RoomListResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.VoteResponse;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoIncludeMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;

import com.mjuAppSW.joA.fcm.service.FCMService;
import com.mjuAppSW.joA.fcm.vo.FCMInfoVO;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomInMemberService {
    private final RoomQueryService roomQueryService;
	private final RoomInMemberQueryService roomInMemberQueryService;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MessageRepository messageRepository;
    private final MemberQueryService memberQueryService;
    private final FCMService fcmService;

    public RoomListResponse getChattingRoomListPage(Long memberId) {
        Member member = memberQueryService.getBySessionId(memberId);
        memberQueryService.validateNoTemporaryBan(member);

        List<RoomInMember> myRoomInMemberList = roomInMemberRepository.findByMemberAndExpired(member, NOT_EXIT);
        if (myRoomInMemberList.isEmpty()) {return RoomListResponse.of(new ArrayList<>());}

        List<RoomInfoVO> roomWithoutMessageList = new ArrayList<>();
        List<RoomInfoVO> roomWithMessageList = new ArrayList<>();
        for (RoomInMember my : myRoomInMemberList) {
            RoomInMember opponent = roomInMemberQueryService.getOpponentByRoomAndMember(my.getRoom(), my.getMember());
            RoomInfoExceptMessageVO roomInfoEMVO = roomInMemberQueryService.getExceptMessageByRoomAndMember(opponent.getRoom(), opponent.getMember());
            checkRoomInMemberWithMessages(roomWithoutMessageList, roomWithMessageList, opponent, roomInfoEMVO);
        }
        return RoomListResponse.of(combinedList(roomWithoutMessageList, roomWithMessageList));
    }

    private void checkRoomInMemberWithMessages(List<RoomInfoVO> roomWithoutMessageList, List<RoomInfoVO> roomWithMessageList,
        RoomInMember opponent, RoomInfoExceptMessageVO roomInfoEMVO) {
        Integer unCheckedMessage = messageRepository.countUnCheckedMessagesByRoomAndMemberAndIsChecked(opponent.getRoom(), opponent.getMember(), NOT_CHECKED);
        List<CurrentMessageVO> currentMessageVOS = messageRepository.findCurrentMessageByRoom(opponent.getRoom());
        if (!currentMessageVOS.isEmpty()) {
            CurrentMessageVO currentMessage = currentMessageVOS.get(0);
            String decryptedString = decrypt(currentMessage.getContent(), opponent.getRoom().getEncryptKey());
            if (decryptedString == null) {
                throw BusinessException.FailDecryptException;
            }
            RoomInfoVO roomInfoVO = new RoomInfoVO(roomInfoEMVO.getRoom().getId(), roomInfoEMVO.getName(),
                roomInfoEMVO.getUrlCode(), decryptedString, currentMessage.getTime(), String.valueOf(unCheckedMessage));
            roomWithMessageList.add(roomInfoVO);
        } else {
            RoomInfoVO roomInfoVO = new RoomInfoVO(roomInfoEMVO.getRoom().getId(), roomInfoEMVO.getName(),
                roomInfoEMVO.getUrlCode(), null, roomInfoEMVO.getDate(), String.valueOf(unCheckedMessage));
            roomWithoutMessageList.add(roomInfoVO);
        }
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

    public RoomInfoExceptDateVO getUpdateRoomInfo(Room room, Member member){
        RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);
        RoomInfoIncludeMessageVO rlr = roomInMemberQueryService.getIncludeMessageByRoomAndMember(roomInMember.getRoom(), roomInMember.getMember());
        Integer unCheckedMessageCount = messageRepository.countUnCheckedMessagesByRoomAndMemberAndIsChecked(roomInMember.getRoom(), roomInMember.getMember(), NOT_CHECKED);

        String decryptedString = decrypt(rlr.getContent(), rlr.getRoom().getEncryptKey());
        if(decryptedString == null){
            throw BusinessException.FailDecryptException;
        }
        return RoomInfoExceptDateVO.of(rlr.getRoom().getId(), rlr.getName(), rlr.getUrlCode(), decryptedString, String.valueOf(unCheckedMessageCount));
    }

	public ChattingPageResponse getChattingPage(Long roomId, Long memberId){
		Room room = roomQueryService.getById(roomId);
		Member member = memberQueryService.getBySessionId(memberId);
		RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);

		UserInfoVO userInfoVO = roomInMemberQueryService.getOpponentUserInfoByRoomAndMember(roomInMember.getRoom(), roomInMember.getMember());
		return ChattingPageResponse.of(userInfoVO.getName(), userInfoVO.getUrlCode(), userInfoVO.getBio());
	}

    @Transactional
    public void create(Long roomId, String[] idArr){
        Room room = roomQueryService.getById(roomId);

        for(String memberId : idArr){
            Member member = memberQueryService.getById(Long.parseLong(memberId));
            RoomInMember roomInMember = RoomInMember.builder()
                .room(room)
                .member(member)
                .expired(NOT_EXIT)
                .result(BEFORE_VOTE)
                .build();

            roomInMemberRepository.save(roomInMember);
        }

        makeFCMVO(idArr, CreateChattingRoom);
    }

    private void makeFCMVO(String[] memberIds, AlarmConstants alarm){
        List<Member> members = new ArrayList<>();
        for(String memberId : memberIds){
            Member member = memberQueryService.getById(Long.parseLong(memberId));
            members.add(member);
        }

        for(int i=0; i<2; i++){
            Member targetMember = members.get(i);
            String name = members.get((i + 1) % members.size()).getName();
            fcmService.send(FCMInfoVO.of(targetMember, name, alarm));
        }
    }

    @Transactional
    public VoteResponse saveVote(VoteRequest request){
        Room room = roomQueryService.getById(request.getRoomId());
        Member member = memberQueryService.getBySessionId(request.getMemberId());
        RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);
        if (roomInMember.getResult().equals(APPROVE_VOTE) || roomInMember.getResult().equals(DISAPPROVE_VOTE)) {
            throw BusinessException.RoomInMemberAlreadyVoteResultException;
        }

        roomInMember.saveResult(request.getResult());
        RoomInMember anotherRoomInMember = roomInMemberQueryService.getOpponentByRoomAndMember(room, member);

        fcmService.send(FCMInfoVO.of(anotherRoomInMember.getMember(), roomInMember.getMember().getName(), VoteChattingRoom));

        return VoteResponse.of(anotherRoomInMember.getRoom().getId(), anotherRoomInMember.getMember().getId(), anotherRoomInMember.getResult());
    }

    @Transactional
    public void updateExpired(UpdateExpiredRequest request) {
        Room room = roomQueryService.getById(request.getRoomId());
        Member member = memberQueryService.getBySessionId(request.getMemberId());
        RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);

        roomInMember.updateExpired(EXIT);
    }

    @Transactional
    public void updateEntryTime(String sRoomId, String sMemberId){
		Room room = roomQueryService.getById(Long.parseLong(sRoomId));
        Member member = memberQueryService.getById(Long.parseLong(sMemberId));
		RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);

        roomInMember.updateEntryTime(LocalDateTime.now());
    }

    @Transactional
    public void updateExitTime(String sRoomId, String sMemberId){
		Room room = roomQueryService.getById(Long.parseLong(sRoomId));
        Member member = memberQueryService.getById(Long.parseLong(sMemberId));
		RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);

        roomInMember.updateExitTime(LocalDateTime.now());
    }

    public Boolean checkExpired(Long roomId, Long memberId){
		Room room = roomQueryService.getById(roomId);
        Member member = memberQueryService.getById(memberId);
		RoomInMember opponent = roomInMemberQueryService.getOpponentByRoomAndMember(room, member);

        if (opponent.getExpired().equals(NOT_EXIT)) return true;
        return false;
    }

    public Boolean checkIsWithDrawal(Long roomId, Long memberId){
		Room room = roomQueryService.getById(roomId);
        Member member = memberQueryService.getById(memberId);
		RoomInMember opponent = roomInMemberQueryService.getOpponentByRoomAndMember(room, member);

        return memberQueryService.validateIsWithDrawal(opponent.getMember().getId());
    }

    private String decrypt(String cipherText, String encryptionKey){
        try{
            Cipher cipher = Cipher.getInstance(ALG);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            IvParameterSpec IV = new IvParameterSpec(encryptionKey.substring(0,16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, IV);
            byte[] decodeByte = Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decodeByte), "UTF-8");
        }catch(Exception e){
            log.error("Error Text to decrypt: " + e.getMessage());
            return null;
        }
    }
}
