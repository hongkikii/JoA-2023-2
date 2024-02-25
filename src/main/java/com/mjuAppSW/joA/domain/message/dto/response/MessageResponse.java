package com.mjuAppSW.joA.domain.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import com.mjuAppSW.joA.domain.message.vo.MessageVO;

@Getter
@Schema(description = "채팅 목록 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageResponse {
    private final List<MessageVO> messageResponseList;

    public static MessageResponse of(List<MessageVO> messageResponseList){
        return MessageResponse.builder()
            .messageResponseList(messageResponseList)
            .build();
    }
}
