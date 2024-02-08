package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.common.encryption.EncryptManager;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.message.dto.vo.MessageVO;
import com.mjuAppSW.joA.domain.message.dto.response.MessageResponse;
import com.mjuAppSW.joA.domain.message.exception.FailDecryptException;
import com.mjuAppSW.joA.domain.message.exception.FailEncryptException;
import com.mjuAppSW.joA.domain.message.exception.MessageNotFoundException;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomService;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberService;

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
    private final MemberQueryService memberQueryService;
    private final RoomService roomService;
    private final RoomInMemberService roomInMemberService;
    private final MessageRepository messageRepository;
    private final EncryptManager encryptManager;

    @Transactional
    public Long saveMessage(Long roomId, Long memberId, String content, String isChecked, LocalDateTime createdMessageDate) {
        Room room = roomService.findByRoomId(roomId);
        Member member = memberQueryService.getById(memberId);
        String encryptedMessage = encryptManager.encrypt(content, room.getEncryptKey());
        if(encryptedMessage == null){
            throw new FailEncryptException();
        }
        Message message = Message.builder()
            .member(member)
            .room(room)
            .content(encryptedMessage)
            .date(createdMessageDate)
            .isChecked(isChecked)
            .build();
        Message saveMessage = messageRepository.save(message);
        return saveMessage.getId();
    }

    public MessageResponse loadMessage(Long roomId, Long memberId) {
        Room room = roomService.findByRoomId(roomId);
        Member member = memberQueryService.getBySessionId(memberId);
        RoomInMember roomInMember = roomInMemberService.findByRoomAndMember(room, member);

        List<Message> messageList = messageRepository.findByRoom(roomInMember.getRoom());
        if(messageList.isEmpty()){ return MessageResponse.of(new ArrayList<>());}

        List<MessageVO> messageVOList = messageList.stream()
            .map(message -> getMessage(message, roomInMember.getMember(), roomInMember.getRoom()))
            .map(MessageVO::new)
            .collect(Collectors.toList());

        return MessageResponse.of(messageVOList);
    }

    private String getMessage(Message message, Member member, Room room) {
        String decryptedMessage = encryptManager.decrypt(message.getContent(), room.getEncryptKey());
        if(decryptedMessage == null){
            throw new FailDecryptException();
        }
        String messageType = (message.getMember() == member) ? "R" : "L";
        return messageType + " " + message.getId() + " " + message.getIsChecked() + " " + decryptedMessage;
    }

    public Message findByMessageId(Long messageId){
        return messageRepository.findById(messageId)
            .orElseThrow(MessageNotFoundException::new);
    }

    @Transactional
    public void updateIsChecked(String roomId, String memberId){
        Room room = roomService.findByRoomId(Long.parseLong(roomId));
        Member member = memberQueryService.getById(Long.parseLong(memberId));

        List<Message> getMessages = messageRepository.findMessage(room, member);
        if(!getMessages.isEmpty()){
            for(Message message : getMessages){
                message.updateIsChecked();
            }
        }
    }
}
