package com.mjuAppSW.joA.domain.vote;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.geography.block.exception.BlockAccessForbiddenException;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.memberProfile.exception.AccessForbiddenException;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteOwnerResponse;
import com.mjuAppSW.joA.domain.vote.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteContent;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteListResponse;
import com.mjuAppSW.joA.domain.vote.exception.VoteAlreadyExistedException;
import com.mjuAppSW.joA.domain.vote.exception.VoteCategoryNotFoundException;
import com.mjuAppSW.joA.domain.vote.voteCategory.VoteCategory;
import com.mjuAppSW.joA.domain.vote.voteCategory.VoteCategoryRepository;
import com.mjuAppSW.joA.geography.block.BlockRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final BlockRepository blockRepository;
    private final MemberChecker memberChecker;

    @Transactional
    public void sendVote(VoteRequest request) {
        Member giveMember = memberChecker.findBySessionId(request.getGiveId());
        Member takeMember = memberChecker.findById(request.getTakeId());
        VoteCategory voteCategory = findVoteCategoryById(request.getCategoryId());

        Long giveMemberId = giveMember.getId();
        Long takeMemberId = takeMember.getId();

        checkEqualVote(giveMemberId, takeMemberId, voteCategory.getId());
        checkInvalidVote(giveMemberId, takeMemberId);
        checkBlock(giveMemberId, takeMemberId);

        createVote(giveMember, takeMember, voteCategory, request.getHint());
    }

    private VoteCategory findVoteCategoryById(Long id) {
        return voteCategoryRepository.findById(id)
                .orElseThrow(VoteCategoryNotFoundException::new);
    }

    private void checkEqualVote(Long giveId, Long takeId, Long categoryId) {
        voteRepository.findTodayEqualVote(giveId, takeId, categoryId, LocalDate.now())
                .ifPresent(vote -> {
                    throw new VoteAlreadyExistedException();});
    }

    private void checkInvalidVote(Long giveId, Long takeId) {
        if (voteRepository.findInvalidVotes(giveId, takeId).size() != 0) {
            throw new AccessForbiddenException();
        }
    }

    private void createVote(Member giveMember, Member takeMember, VoteCategory voteCategory, String hint) {
        voteRepository.save(Vote.builder()
                            .giveId(giveMember.getId())
                            .member(takeMember)
                            .voteCategory(voteCategory)
                            .date(LocalDate.now())
                            .hint(hint)
                            .build());
    }

    public VoteListResponse getVotes(Long sessionId) {
        Member findTakeMember = memberChecker.findBySessionId(sessionId);
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

    public VoteOwnerResponse getVoteOwner(Long sessionId) {
        return VoteOwnerResponse.of(memberChecker.findBySessionId(sessionId));
    }

    private void checkBlock(Long giveId, Long takeId) {
        if (blockRepository.findBlockByIds(giveId, takeId).size() != 0) {
            throw new BlockAccessForbiddenException();
        }
    }

    private List<Vote> findVotesByTakeId(Long id, Pageable pageable) {
        return voteRepository.findValidAllByTakeId(id, pageable);
    }
}
