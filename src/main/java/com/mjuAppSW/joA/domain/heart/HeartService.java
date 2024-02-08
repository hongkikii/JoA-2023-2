package com.mjuAppSW.joA.domain.heart;

import com.mjuAppSW.joA.domain.heart.dto.HeartRequest;
import com.mjuAppSW.joA.domain.heart.dto.HeartResponse;
import com.mjuAppSW.joA.domain.heart.repository.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberService;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberService;
import com.mjuAppSW.joA.geography.block.BlockService;
import com.mjuAppSW.joA.domain.heart.exception.HeartAlreadyExistedException;
import com.mjuAppSW.joA.domain.heart.exception.RoomAlreadyExistedException;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class HeartService {

    private final HeartRepository heartRepository;
    private final RoomInMemberService roomInMemberService;
    private final BlockService blockService;
    private final MemberService memberService;

    @Transactional
    public HeartResponse sendHeart(HeartRequest request) {
        Member giveMember = memberService.getNormalBySessionId(request.getGiveId());
        Long giveMemberId = giveMember.getId();
        Long takeMemberId = request.getTakeId();
        Member takeMember = memberService.getById(takeMemberId);

        blockService.check(giveMemberId, takeMemberId);
        checkEqualHeart(giveMemberId, takeMemberId);

        Heart newHeart = createHeart(giveMemberId, takeMember);
        heartRepository.save(newHeart);
        roomInMemberService.checkRoomExisted(giveMember, takeMember);

        boolean isMatched = isOpponentHeartExisted(takeMemberId, giveMemberId);
        return HeartResponse.of(isMatched, giveMember, takeMember);
    }

    private void checkEqualHeart(Long giveId, Long takeId) {
        heartRepository.findTodayHeart(giveId, takeId)
                    .ifPresent(heart -> {
                        throw new HeartAlreadyExistedException();});
    }

    private Heart createHeart(Long giveId, Member takeMember) {
        return Heart.builder()
                .giveId(giveId)
                .member(takeMember)
                .date(LocalDateTime.now())
                .build();
    }

    private boolean isOpponentHeartExisted(Long takeId, Long giveId) {
        return heartRepository.findTodayHeart(takeId, giveId).isPresent();
    }
}
