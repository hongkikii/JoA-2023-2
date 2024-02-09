package com.mjuAppSW.joA.geography.block.service;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.geography.block.Block;
import com.mjuAppSW.joA.geography.block.dto.BlockRequest;
import com.mjuAppSW.joA.geography.block.repository.BlockRepository;
import com.mjuAppSW.joA.geography.location.Location;
import com.mjuAppSW.joA.geography.location.service.LocationQueryService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final LocationQueryService locationQueryService;
    private final MemberQueryService memberQueryService;
    private final BlockQueryService blockQueryService;

    @Transactional
    public void create(BlockRequest request) {
        Member blockerMember = memberQueryService.getBySessionId(request.getBlockerId());
        Member blockedMember = memberQueryService.getById(request.getBlockedId());

        Location blockerLocation = locationQueryService.getBy(blockerMember.getId());
        Location blockedLocation = locationQueryService.getBy(blockedMember.getId());

        blockQueryService.validateNoEqualBlock(blockerLocation.getId(), blockedLocation.getId());

        Block newBlock = new Block(blockerLocation, blockedLocation);
        blockRepository.save(newBlock);
    }
}
