package com.mjuAppSW.joA.domain.roomInMember;

import static com.mjuAppSW.joA.common.constant.Constants.RoomInMember.*;

import com.mjuAppSW.joA.common.encryption.EncryptManager;
import com.mjuAppSW.joA.domain.heart.exception.RoomAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.exception.MemberNotFoundException;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.member.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.message.MessageRepository;
import com.mjuAppSW.joA.domain.message.exception.FailDecryptException;
import com.mjuAppSW.joA.domain.message.exception.MessageNotFoundException;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomService;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.UpdateExpiredRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.RoomListResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.VoteResponse;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberAlreadyVoteResultException;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoIncludeMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;
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
	private final RoomService roomService;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MessageRepository messageRepository;
    private final MemberQueryService memberQueryService;
    private final EncryptManager encryptManager;

    public RoomListResponse getRoomList(Long memberId) {
        Member member = memberQueryService.getBySessionId(memberId);
        memberQueryService.validateNoTemporaryBan(member);

        List<RoomInMember> myRoomInMemberList = roomInMemberRepository.findByAllMember(member);
        if (myRoomInMemberList.isEmpty()) {return RoomListResponse.of(new ArrayList<>());}

        List<RoomInfoVO> roomWithoutMessageList = new ArrayList<>();
        List<RoomInfoVO> roomWithMessageList = new ArrayList<>();
        for (RoomInMember my : myRoomInMemberList) {
            RoomInMember opponent = findOpponentByRoomAndMember(my.getRoom(), my.getMember());
            RoomInfoExceptMessageVO roomInfoEMVO = findRoomInfoExceptMessageByRoomAndMember(opponent.getRoom(), opponent.getMember());
            checkRoomInMemberWithMessages(roomWithoutMessageList, roomWithMessageList, opponent, roomInfoEMVO);
        }
        return RoomListResponse.of(combinedList(roomWithoutMessageList, roomWithMessageList));
    }

    private void checkRoomInMemberWithMessages(List<RoomInfoVO> roomWithoutMessageList, List<RoomInfoVO> roomWithMessageList,
        RoomInMember opponent, RoomInfoExceptMessageVO roomInfoEMVO) {
        Integer unCheckedMessage = messageRepository.countUnCheckedMessage(opponent.getRoom(), opponent.getMember());
        messageRepository.getCurrentMessageAndTime(opponent.getRoom())
            .ifPresentOrElse(
                currentMessageVO -> {
                    String decryptedString = encryptManager.decrypt(currentMessageVO.getContent(), opponent.getRoom().getEncryptKey());
                    if (decryptedString == null) {
                        throw new FailDecryptException();
                    }
                    RoomInfoVO roomInfoVO = new RoomInfoVO(roomInfoEMVO.getRoom().getId(), roomInfoEMVO.getName(),
                        roomInfoEMVO.getUrlCode(), decryptedString, currentMessageVO.getTime(), String.valueOf(unCheckedMessage));
                    roomWithMessageList.add(roomInfoVO);
                },
                () -> {
                    RoomInfoVO roomInfoVO = new RoomInfoVO(roomInfoEMVO.getRoom().getId(), roomInfoEMVO.getName(),
                        roomInfoEMVO.getUrlCode(), null, roomInfoEMVO.getDate(), String.valueOf(unCheckedMessage));
                    roomWithoutMessageList.add(roomInfoVO);
                }
            );
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

    public RoomInfoExceptDateVO getUpdateRoom(Room room, Member member){
        RoomInMember roomInMember = findByRoomAndMember(room, member);
        RoomInfoIncludeMessageVO rlr = findRoomInfoIncludeMessageByRoomAndMember(roomInMember.getRoom(), roomInMember.getMember());
        Integer unCheckedMessageCount = messageRepository.countUnCheckedMessage(roomInMember.getRoom(), roomInMember.getMember());

        String decryptedString = encryptManager.decrypt(rlr.getContent(), rlr.getRoom().getEncryptKey());
        if(decryptedString == null){
            throw new FailDecryptException();
        }
        return RoomInfoExceptDateVO.of(rlr.getRoom().getId(), rlr.getName(), rlr.getUrlCode(), decryptedString, String.valueOf(unCheckedMessageCount));
    }

    @Transactional
    public void createRoom(Long roomId, String[] idArr){
        Room room = roomService.findByRoomId(roomId);

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
    }

    @Transactional
    public VoteResponse saveVoteResult(VoteRequest request){
        Room room = roomService.findByRoomId(request.getRoomId());
        Member member = memberQueryService.getBySessionId(request.getMemberId());
        RoomInMember roomInMember = findByRoomAndMember(room, member);
        if (roomInMember.getResult().equals(APPROVE_VOTE) || roomInMember.getResult().equals(DISAPPROVE_VOTE)) {
            throw new RoomInMemberAlreadyVoteResultException();
        }

        roomInMember.saveResult(request.getResult());
        RoomInMember anotherRoomInMember = findOpponentByRoomAndMember(room, member);
        return VoteResponse.of(anotherRoomInMember.getRoom().getId(), anotherRoomInMember.getMember().getId(), anotherRoomInMember.getResult());
    }

    @Transactional
    public void updateExpired(UpdateExpiredRequest request) {
        Room room = roomService.findByRoomId(request.getRoomId());
        Member member = memberQueryService.getBySessionId(request.getMemberId());
        RoomInMember roomInMember = findByRoomAndMember(room, member);

        roomInMember.updateExpired(EXIT);
    }

    @Transactional
    public void updateEntryTime(String sRoomId, String sMemberId){
		Room room = roomService.findByRoomId(Long.parseLong(sRoomId));
        Member member = memberQueryService.getById(Long.parseLong(sMemberId));
		RoomInMember roomInMember = findByRoomAndMember(room, member);

        roomInMember.updateEntryTime(LocalDateTime.now());
    }

    @Transactional
    public void updateExitTime(String sRoomId, String sMemberId){
		Room room = roomService.findByRoomId(Long.parseLong(sRoomId));
        Member member = memberQueryService.getById(Long.parseLong(sMemberId));
		RoomInMember roomInMember = findByRoomAndMember(room, member);

        roomInMember.updateExitTime(LocalDateTime.now());
    }

    public Boolean checkExpired(Long roomId, Long memberId){
		Room room = roomService.findByRoomId(roomId);
        Member member = memberQueryService.getById(memberId);
		RoomInMember opponent = findOpponentByRoomAndMember(room, member);

        if(opponent.getExpired().equals(NOT_EXIT)){return true;}
        return false;
    }

    public Boolean checkIsWithDrawal(Long roomId, Long memberId){
		Room room = roomService.findByRoomId(roomId);
        Member member = memberQueryService.getById(memberId);
		RoomInMember rim = findOpponentByRoomAndMember(room, member);

        Member opponentMember = memberQueryService.getById(rim.getMember().getId());
        if (opponentMember != null) return true;
        return false;
    }

	public RoomInMember findByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findByRoomAndMember(room, member)
			.orElseThrow(RoomInMemberNotFoundException::new);
	}

	public RoomInMember findOpponentByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findOpponentByRoomAndMember(room, member)
			.orElseThrow(RoomInMemberNotFoundException::new);
	}

	public UserInfoVO findOpponentUserInfoByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findOpponentUserInfoByRoomAndMember(room, member)
			.orElseThrow(MemberNotFoundException::new);
	}

	public RoomInMember checkOpponentExpired(Room room, Member member){
		return roomInMemberRepository.checkOpponentExpired(room, member, NOT_EXIT)
			.orElseThrow(RoomInMemberNotFoundException::new);
	}

	private RoomInfoExceptMessageVO findRoomInfoExceptMessageByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findRoomInfoExceptMessageByRoomAndMember(room, member)
			.orElseThrow(RoomInMemberNotFoundException::new);
	}

	private RoomInfoIncludeMessageVO findRoomInfoIncludeMessageByRoomAndMember(Room room, Member member){
		List<RoomInfoIncludeMessageVO> roomInfoIncludeMessageVOList = roomInMemberRepository.findRoomInfoIncludeMessage(room, member);
		if(roomInfoIncludeMessageVOList.isEmpty()){
			throw new MessageNotFoundException();
		}
		return roomInfoIncludeMessageVOList.get(0);
	}

    public void validateNoRoom(Member member1, Member member2) {
        if (roomInMemberRepository.checkRoomInMember(member1, member2).size() != 0) {
            throw new RoomAlreadyExistedException();
        }
    }
}
