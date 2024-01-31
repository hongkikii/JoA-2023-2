package com.mjuAppSW.joA.geography.block;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberService;
import com.mjuAppSW.joA.geography.block.dto.BlockRequest;
import com.mjuAppSW.joA.geography.block.exception.BlockAlreadyExistedException;
import com.mjuAppSW.joA.geography.block.exception.LocationNotFoundException;
import com.mjuAppSW.joA.geography.location.Location;
import com.mjuAppSW.joA.geography.location.infrastructure.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final LocationRepository locationRepository;
    private final MemberService memberService;

    @Transactional
    public void create(BlockRequest request) {
        Member blockerMember = memberService.getBySessionId(request.getBlockerId());
        Member blockedMember = memberService.getById(request.getBlockedId());

        Location blockerLocation = findLocation(blockerMember.getId());
        Location blockedLocation = findLocation(blockedMember.getId());

        checkEqual(blockerLocation.getId(), blockedLocation.getId());

        Block newBlock = new Block(blockerLocation, blockedLocation);
        blockRepository.save(newBlock);
    }

    private Location findLocation(Long memberId) {
        return locationRepository.findById(memberId)
                .orElseThrow(LocationNotFoundException::new);
    }

    private void checkEqual(Long blockerId, Long blockedId) {
        blockRepository.findEqualBlock(blockerId, blockedId)
                    .ifPresent(block -> {
                        throw new BlockAlreadyExistedException();});
    }
}
