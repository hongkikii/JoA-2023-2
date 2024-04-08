package com.mjuAppSW.joA.domain.heart.service;

import static com.mjuAppSW.joA.common.constant.AlarmConstants.giveHeart;
import static com.mjuAppSW.joA.common.constant.Constants.Heart.ANONYMOUS;

import com.mjuAppSW.joA.domain.heart.entity.Heart;
import com.mjuAppSW.joA.domain.heart.dto.HeartRequest;
import com.mjuAppSW.joA.domain.heart.dto.HeartResponse;
import com.mjuAppSW.joA.domain.heart.repository.HeartRepository;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberQueryService;
import com.mjuAppSW.joA.fcm.service.FCMService;
import com.mjuAppSW.joA.fcm.vo.FCMInfoVO;
import com.mjuAppSW.joA.geography.block.service.BlockQueryService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final HeartQueryService heartQueryService;
    private final RoomInMemberQueryService roomInMemberQueryService;
    private final BlockQueryService blockQueryService;
    private final MemberQueryService memberQueryService;
    private final FCMService fcmService;

    @Transactional
    public HeartResponse send(HeartRequest request) {
        Member giveMember = memberQueryService.getNormalBySessionId(request.getGiveId());
        Long giveMemberId = giveMember.getId();
        Long takeMemberId = request.getTakeId();
        Member takeMember = memberQueryService.getById(takeMemberId);

        blockQueryService.validateNoBlock(giveMemberId, takeMemberId);
        heartQueryService.validateNoTodayHeart(giveMemberId, takeMemberId);

        Heart heart = create(giveMemberId, takeMember);
        heartRepository.save(heart);
        roomInMemberQueryService.validateNoRoom(giveMember, takeMember);
        boolean isMatched = heartQueryService.isTodayHeartExisted(takeMemberId, giveMemberId);

        fcmService.send(decide(takeMember, giveMember.getName(), request.getNamed()));

        return HeartResponse.of(isMatched, giveMember, takeMember);
    }

    private FCMInfoVO decide(Member takeMember, String giveMemberName, boolean named){
        if(named) return FCMInfoVO.of(takeMember, giveMemberName, giveHeart);
        else return FCMInfoVO.of(takeMember, ANONYMOUS, giveHeart);
    }

    private Heart create(Long giveId, Member takeMember) {
        return Heart.builder()
                .giveId(giveId)
                .member(takeMember)
                .date(LocalDateTime.now())
                .build();
    }
}
