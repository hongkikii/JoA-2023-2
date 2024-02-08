package com.mjuAppSW.joA.geography.block;

import com.mjuAppSW.joA.geography.block.exception.BlockAccessForbiddenException;
import com.mjuAppSW.joA.geography.block.exception.BlockAlreadyExistedException;
import com.mjuAppSW.joA.geography.block.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockQueryService {

    private final BlockRepository blockRepository;

    public void checkAndThrowIfBlockExists(Long blockerId, Long blockedId) {
        blockRepository.findEqualBy(blockerId, blockedId)
                .ifPresent(block -> {
                    throw new BlockAlreadyExistedException();});
    }

    public void checkAndThrowIfBlocked(Long blockerId, Long blockedId) {
        if (!blockRepository.findBy(blockedId, blockerId).isEmpty()) {
            throw new BlockAccessForbiddenException();
        }
    }
}
