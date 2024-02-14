package com.mjuAppSW.joA.domain.member.dto.response;

import com.mjuAppSW.joA.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "마이페이지 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageResponse {
    private final String name;
    @Schema(description = "S3 저장소 내 프로필 사진 고유 코드, 기본 이미지일 시 빈 문자열 반환")
    private final String urlCode;
    private final String bio;
    private final Integer todayHeart;
    private final Integer totalHeart;
    @Schema(description = "사용자가 가장 많이 받은 투표 카테고리 Top3")
    private final List<String> voteTop3;

    public static MyPageResponse of(Member member, Integer todayHeart, Integer totalHeart, List<String> voteTop3) {
        return MyPageResponse.builder()
                .name(member.getName())
                .urlCode(member.getUrlCode())
                .bio(member.getBio())
                .todayHeart(todayHeart)
                .totalHeart(totalHeart)
                .voteTop3(voteTop3)
                .build();
    }
}
