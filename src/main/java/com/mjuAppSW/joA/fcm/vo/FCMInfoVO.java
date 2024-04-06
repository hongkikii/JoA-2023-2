package com.mjuAppSW.joA.fcm.vo;

import com.mjuAppSW.joA.common.constant.AlarmConstants;
import com.mjuAppSW.joA.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class FCMInfoVO {
    private final Member targetMember;
    private final String memberName;
    private final AlarmConstants constants;
    private final String content;

    public FCMInfoVO(Member targetMember, String memberName, AlarmConstants constants, String content){
        this.targetMember = targetMember;
        this.memberName = memberName;
        this.constants = constants;
        this.content = content;
    }

    public static FCMInfoVO of(Member targetMember, String memberName, AlarmConstants constants) {
        return FCMInfoVO.builder()
            .targetMember(targetMember)
            .memberName(memberName)
            .constants(constants)
            .build();
    }

    public static FCMInfoVO ofWithContent(Member targetMember, String memberName, AlarmConstants constants, String content){
        return FCMInfoVO.builder()
            .targetMember(targetMember)
            .memberName(memberName)
            .constants(constants)
            .content(content)
            .build();
    }

}
