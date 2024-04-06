package com.mjuAppSW.joA.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmConstants {
    giveHeart("님이 하트를 눌렀어요!", "상대방에게 하트를 눌러주세요!"),
    VoteGame("님이 투표를 했어요!", "투표 내용을 확인해보러 가실까요?"),
    CreateChattingRoom("님과의 채팅방이 생성되었어요!", "확인해보러 가실까요?"),
    VoteChattingRoom("님이 채팅방 연장을 신청했어요!", "상대방과 채팅을 더 나누고 싶으면 연장을 신청하세요!"),
    ExtendChattingRoom("님과의 채팅방이 7일 연장되었어요!", "더 많은 대화를 나눠 보세요!"),
    ChatInChattingRoom("님이 채팅을 보냈어요!", null);

    private final String title;
    private final String body;
}
