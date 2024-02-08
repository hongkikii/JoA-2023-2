package com.mjuAppSW.joA.domain.vote;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.vote.exception.InvalidVoteExistedException;
import com.mjuAppSW.joA.domain.vote.repository.VoteRepository;
import com.mjuAppSW.joA.geography.block.BlockService;
import com.mjuAppSW.joA.domain.vote.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteContent;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteListResponse;
import com.mjuAppSW.joA.domain.vote.exception.VoteAlreadyExistedException;
import com.mjuAppSW.joA.domain.vote.exception.VoteCategoryNotFoundException;
import com.mjuAppSW.joA.domain.vote.voteCategory.VoteCategory;
import com.mjuAppSW.joA.domain.vote.voteCategory.VoteCategoryRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteCategoryRepository voteCategoryRepository;
    private final BlockService blockService;
    private final MemberQueryService memberQueryService;

    @Transactional
    public void send(VoteRequest request) {
        Member giveMember = memberQueryService.getNormalBySessionId(request.getGiveId());
        Member takeMember = memberQueryService.getById(request.getTakeId());
        VoteCategory voteCategory = findVoteCategoryById(request.getCategoryId());

        Long giveMemberId = giveMember.getId();
        Long takeMemberId = takeMember.getId();

        checkEqualVote(giveMemberId, takeMemberId, voteCategory.getId());
        checkInvalidVote(giveMemberId, takeMemberId);
        blockService.check(giveMemberId, takeMemberId);

        createVote(giveMember, takeMember, voteCategory, request.getHint());
    }

    private VoteCategory findVoteCategoryById(Long id) {
        return voteCategoryRepository.findById(id)
                .orElseThrow(VoteCategoryNotFoundException::new);
    }

    private void checkEqualVote(Long giveId, Long takeId, Long categoryId) {
        voteRepository.findTodayVote(giveId, takeId, categoryId)
                .ifPresent(vote -> {
                    throw new VoteAlreadyExistedException();});
    }

    private void checkInvalidVote(Long giveId, Long takeId) {
        if (voteRepository.findInvalidVotes(giveId, takeId).isEmpty()) {
            throw new InvalidVoteExistedException();
        }
    }

    private void createVote(Member giveMember, Member takeMember, VoteCategory voteCategory, String hint) {
        voteRepository.save(Vote.builder()
                            .giveId(giveMember.getId())
                            .member(takeMember)
                            .voteCategory(voteCategory)
                            .date(LocalDateTime.now())
                            .hint(hint)
                            .build());
    }

    public VoteListResponse get(Long sessionId) {
        Member findTakeMember = memberQueryService.getNormalBySessionId(sessionId);
        return VoteListResponse.of(getVoteList(findTakeMember.getId()));
    }

    private List<VoteContent> getVoteList(Long id) {
        Pageable pageable = PageRequest.of(0, 30);
        return findVotesByTakeId(id, pageable).stream()
                                            .map(this::makeVoteContent)
                                            .collect(Collectors.toList());
    }

    private VoteContent makeVoteContent(Vote vote) {
        return VoteContent.builder()
                        .voteId(vote.getId())
                        .categoryId(vote.getVoteCategory().getId())
                        .hint(vote.getHint())
                        .build();
    }

    private List<Vote> findVotesByTakeId(Long id, Pageable pageable) {
        return voteRepository.findValidAllByTakeId(id, pageable);
    }
}
