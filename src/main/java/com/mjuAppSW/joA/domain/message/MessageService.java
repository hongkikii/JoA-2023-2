package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberRepository;
import com.mjuAppSW.joA.domain.message.dto.MessageList;
import com.mjuAppSW.joA.domain.message.dto.MessageResponse;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MessageService {
    private static String alg = "AES/CBC/PKCS5Padding";
    private MessageRepository messageRepository;
    private RoomRepository roomRepository;
    private MemberRepository memberRepository;
    private RoomInMemberRepository roomInMemberRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, RoomRepository roomRepository,
                          MemberRepository memberRepository, RoomInMemberRepository roomInMemberRepository){
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
        this.roomInMemberRepository = roomInMemberRepository;
    }

    public Long saveMessage(Long roomId, Long memberId, String content, String isChecked) throws Exception {
        Room room = roomRepository.findById(roomId).orElse(null);
        Member member = memberRepository.findById(memberId).orElse(null);
        if(room != null && member != null){
            Message message = Message.builder()
                    .member(member)
                    .room(room)
                    .content(encrypt(content, room.getEncryptKey()))
                    .date(new Date())
                    .isChecked(isChecked)
                    .build();
            Message saveMessage = messageRepository.save(message);
            if(saveMessage != null){return saveMessage.getId();}
        }
        return null;
    }

    public MessageList loadMessage(Long roomId, Long memberId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        Member member = memberRepository.findBysessionId(memberId).orElse(null);
        if(room != null && member != null){
            RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member);
            if(roomInMember == null){
                return new MessageList(new ArrayList<>(), "2");
            }

            List<Message> messageList = messageRepository.findByRoom(room);
            if(messageList.isEmpty()){
                return new MessageList(new ArrayList<>(), "1");
            }

            List<MessageResponse> messageResponseList = new ArrayList<>();
            for(Message message : messageList){
                String getMessage = makeMessageContent(message, member, room);
                MessageResponse messageResponse = new MessageResponse(getMessage);
                messageResponseList.add(messageResponse);
            }
            return new MessageList(messageResponseList, "0");
        }
        return new MessageList(new ArrayList<>(), "3");
    }

    private String makeMessageContent(Message message, Member member, Room room) {
        String decryptedMessage = decrypt(message.getContent(), room.getEncryptKey());
        String messageType = (message.getMember() == member) ? "R" : "L";
        return messageType + " " + message.getId() + " " + message.getIsChecked() + " " + decryptedMessage;
    }

    public Boolean updateIsChecked(String roomId, String memberId){
        Room room = roomRepository.findById(Long.parseLong(roomId)).orElse(null);
        Member member = memberRepository.findById(Long.parseLong(memberId)).orElse(null);
        if(room != null && member != null){
            List<Message> getMessages = messageRepository.findMessage(room, member);
            if(!getMessages.isEmpty()){
                messageRepository.updateIsChecked(getMessages);
            }
            return true;
        }
        return false;
    }

    public String encrypt(String text, String encryptionKey){
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            IvParameterSpec IV = new IvParameterSpec(encryptionKey.substring(0,16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, IV);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }catch(Exception e){
            log.warn("MessageService, encrypt is Error");
        }
        return null;
    }

    public String decrypt(String cipherText, String encryptionKey){
        try{
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            IvParameterSpec IV = new IvParameterSpec(encryptionKey.substring(0,16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, IV);
            byte[] decodeByte = Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decodeByte), "UTF-8");
        }catch(Exception e){
            log.warn("MessageService, decrypt is Error");
        }
        return null;
    }
}