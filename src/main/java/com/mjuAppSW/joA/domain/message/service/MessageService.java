package com.mjuAppSW.joA.domain.message.service;

import static com.mjuAppSW.joA.common.constant.Constants.Encrypt.*;
import static com.mjuAppSW.joA.common.constant.Constants.Message.*;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.message.dto.response.MessageResponse;
import com.mjuAppSW.joA.domain.message.vo.MessageVO;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.message.repository.MessageRepository;
import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.room.service.RoomQueryService;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberQueryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MemberQueryService memberQueryService;
    private final RoomQueryService roomQueryService;
    private final RoomInMemberQueryService roomInMemberQueryService;
    private final MessageRepository messageRepository;

    @Transactional
    public Long save(Long roomId, Long memberId, String content, String isChecked, LocalDateTime createdMessageDate) {
        Room room = roomQueryService.getById(roomId);
        Member member = memberQueryService.getById(memberId);
        String encryptedMessage = encrypt(content, room.getEncryptKey());
        if(encryptedMessage == null){
            throw BusinessException.FailDecryptException;
        }
        Message message = create(room, member, encryptedMessage, createdMessageDate, isChecked);
        Message saveMessage = messageRepository.save(message);
        return saveMessage.getId();
    }

    private Message create(Room room, Member member, String encryptedMessage, LocalDateTime createdMessageDate, String isChecked){
        return Message.builder()
            .member(member)
            .room(room)
            .content(encryptedMessage)
            .date(createdMessageDate)
            .isChecked(isChecked)
            .build();
    }

    public MessageResponse get(Long roomId, Long memberId) {
        Room room = roomQueryService.getById(roomId);
        Member member = memberQueryService.getBySessionId(memberId);
        RoomInMember roomInMember = roomInMemberQueryService.getByRoomAndMember(room, member);

        List<Message> messageList = messageRepository.findByRoom(roomInMember.getRoom());
        if(messageList.isEmpty()){ return MessageResponse.of(new ArrayList<>());}

        List<MessageVO> messageVOList = messageList.stream()
            .map(message -> devide(message, roomInMember.getMember(), roomInMember.getRoom()))
            .map(MessageVO::new)
            .collect(Collectors.toList());

        return MessageResponse.of(messageVOList);
    }

    private String devide(Message message, Member member, Room room) {
        String decryptedMessage = decrypt(message.getContent(), room.getEncryptKey());
        if(decryptedMessage == null){
            throw BusinessException.FailDecryptException;
        }
        String messageType = (message.getMember() == member) ? "R" : "L";
        return messageType + " " + message.getId() + " " + message.getIsChecked() + " " + decryptedMessage;
    }

    @Transactional
    public void updateIsChecked(String roomId, String memberId){
        Room room = roomQueryService.getById(Long.parseLong(roomId));
        Member member = memberQueryService.getById(Long.parseLong(memberId));

        List<Message> getMessages = messageRepository.findOpponentByRoomAndMemberAndIsChecked(room, member, NOT_CHECKED);
        if(!getMessages.isEmpty()){
            for(Message message : getMessages){
                message.updateIsChecked();
            }
        }
    }

    private String encrypt(String text, String encryptionKey) {
        try{
            Cipher cipher = Cipher.getInstance(ALG);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            IvParameterSpec IV = new IvParameterSpec(encryptionKey.substring(0,16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, IV);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }catch (Exception e){
            log.error("Error Text to encrypt: " + e.getMessage());
            return null;
        }
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
