package com.mjuAppSW.joA.fcm.vo;

import com.mjuAppSW.joA.common.constant.AlarmConstants;
import com.mjuAppSW.joA.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class FCMInfoVO {
    private final String targetMemberToken;
    private final String categoryName;
    private final String titleValue;
    private final AlarmConstants constants;
    private final String content;

    public FCMInfoVO(String targetMemberToken, String categoryName, String titleValue, AlarmConstants constants, String content){
        this.targetMemberToken = targetMemberToken;
        this.categoryName = categoryName;
        this.titleValue = titleValue;
        this.constants = constants;
        this.content = content;
    }

    public static FCMInfoVO of(String targetMemberToken, String titleValue, AlarmConstants constants) {
        return FCMInfoVO.builder()
            .targetMemberToken(targetMemberToken)
            .titleValue(titleValue)
            .constants(constants)
            .build();
    }

    public static FCMInfoVO ofWithContent(String targetMemberToken, String titleValue, AlarmConstants constants, String content){
        return FCMInfoVO.builder()
            .targetMemberToken(targetMemberToken)
            .titleValue(titleValue)
            .constants(constants)
            .content(content)
            .build();
    }
}
