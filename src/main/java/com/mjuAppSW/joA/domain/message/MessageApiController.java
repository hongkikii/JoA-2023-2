package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.domain.message.dto.MessageList;
import com.mjuAppSW.joA.domain.message.dto.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class MessageApiController {
    private MessageService messageService;

    @Autowired
    public MessageApiController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/load/message")
    public ResponseEntity<List<MessageResponse>> loadMessage(
            @RequestParam("roomId") Long roomId, @RequestParam("memberId") Long memberId) {
        log.info("loadMessage : roomId = {}, memberId = {}", roomId, memberId);
        MessageList list = messageService.loadMessage(roomId, memberId);
        if (list.getStatus().equals("0") || list.getStatus().equals("1")) {
            log.info("loadMessage Return : OK");
            return ResponseEntity.ok(list.getMessageResponseList());
        } else if (list.getStatus().equals("2")) {
            log.warn("loadMessage Return : NOT_FOUND, not found roomInMember / roomId = {}, memberId = {}", roomId, memberId);
            return ResponseEntity.notFound().build();
        } else {
            log.warn("loadMessage Return : BAD_REQUEST, getValue's not correct / roomId = {}, memberId = {}", roomId, memberId);
            return ResponseEntity.badRequest().build();
        }
    }
}