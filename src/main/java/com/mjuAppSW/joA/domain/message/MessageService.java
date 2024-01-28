package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.common.encryption.EncryptManager;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberEntity;
import com.mjuAppSW.joA.domain.member.service.MemberService;
import com.mjuAppSW.joA.domain.message.dto.vo.MessageVO;
import com.mjuAppSW.joA.domain.message.dto.response.MessageResponse;
import com.mjuAppSW.joA.domain.message.exception.FailDecryptException;
import com.mjuAppSW.joA.domain.message.exception.FailEncryptException;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MemberService memberService;
    private final EncryptManager encryptManager;

    @Transactional
    public Long saveMessage(Long roomId, Long memberId, String content, String isChecked, LocalDateTime createdMessageDate) {
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        Member member = memberService.getById(memberId);
        String encryptedMessage = encryptManager.encrypt(content, room.getEncryptKey());
        if(encryptedMessage == null){
            throw new FailEncryptException();
        }
        Message message = Message.builder()
            .member(MemberEntity.fromModel(member))
            .room(room)
            .content(encryptedMessage)
            .date(createdMessageDate)
            .isChecked(isChecked)
            .build();
        Message saveMessage = messageRepository.save(message);
        return saveMessage.getId();
    }

    public MessageResponse loadMessage(Long roomId, Long memberId) {
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        Member member = memberService.getBySessionId(memberId);
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, MemberEntity.fromModel(member)).orElseThrow(
            RoomInMemberNotFoundException::new);

        List<Message> messageList = messageRepository.findByRoom(room);
        if(messageList.isEmpty()){ return MessageResponse.of(new ArrayList<>());}

        List<MessageVO> messageVOList = messageList.stream()
            .map(message -> makeMessageContent(message, MemberEntity.fromModel(member), room))
            .map(MessageVO::new)
            .collect(Collectors.toList());

        return MessageResponse.of(messageVOList);
    }

    private String makeMessageContent(Message message, MemberEntity member, Room room) {
        String decryptedMessage = encryptManager.decrypt(message.getContent(), room.getEncryptKey());
        if(decryptedMessage == null){
            throw new FailDecryptException();
        }
        String messageType = (message.getMember() == member) ? "R" : "L";
        return messageType + " " + message.getId() + " " + message.getIsChecked() + " " + decryptedMessage;
    }

    @Transactional
    public void updateIsChecked(String roomId, String memberId){
        Room room = roomRepository.findById(Long.parseLong(roomId)).orElseThrow(RoomNotFoundException::new);
        Member member = memberService.getById(Long.parseLong(memberId));

        List<Message> getMessages = messageRepository.findMessage(room, MemberEntity.fromModel(member));
        if(!getMessages.isEmpty()){
            for(Message message : getMessages){
                message.updateIsChecked();
            }
        }
    }
}
