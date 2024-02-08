package com.mjuAppSW.joA.domain.vote;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.vote.repository.VoteRepository;
import com.mjuAppSW.joA.domain.vote.voteCategory.VoteCategoryQueryService;
import com.mjuAppSW.joA.geography.block.BlockQueryService;
import com.mjuAppSW.joA.domain.vote.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteListResponse;
import com.mjuAppSW.joA.domain.vote.voteCategory.VoteCategory;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteQueryService voteQueryService;
    private final VoteCategoryQueryService voteCategoryQueryService;
    private final BlockQueryService blockQueryService;
    private final MemberQueryService memberQueryService;

    @Transactional
    public void send(VoteRequest request) {
        Member giveMember = memberQueryService.getNormalBySessionId(request.getGiveId());
        Member takeMember = memberQueryService.getById(request.getTakeId());
        VoteCategory voteCategory = voteCategoryQueryService.getBy(request.getCategoryId());

        Long giveMemberId = giveMember.getId();
        Long takeMemberId = takeMember.getId();

        voteQueryService.validateNoTodayVote(giveMemberId, takeMemberId, voteCategory.getId());
        voteQueryService.validateNoInvalidVotes(giveMemberId, takeMemberId);
        blockQueryService.validateNoBlock(giveMemberId, takeMemberId);

        create(giveMember, takeMember, voteCategory, request.getHint());
    }

    private void create(Member giveMember, Member takeMember, VoteCategory voteCategory, String hint) {
        voteRepository.save(Vote.builder()
                            .giveId(giveMember.getId())
                            .member(takeMember)
                            .voteCategory(voteCategory)
                            .date(LocalDateTime.now())
                            .hint(hint)
                            .build());
    }

    public VoteListResponse get(Long sessionId) {
        Member takeMember = memberQueryService.getNormalBySessionId(sessionId);
        return VoteListResponse.of(voteQueryService.getValidAllBy(takeMember.getId()));
    }
}
