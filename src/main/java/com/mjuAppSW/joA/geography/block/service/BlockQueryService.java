package com.mjuAppSW.joA.geography.block.service;

import com.mjuAppSW.joA.geography.block.exception.BlockException;
import com.mjuAppSW.joA.geography.block.repository.BlockRepository;
import com.mjuAppSW.joA.geography.block.exception.BlockAccessForbiddenException;
import com.mjuAppSW.joA.geography.block.exception.BlockAlreadyExistedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryService {

    private final BlockRepository blockRepository;

    public void validateNoEqualBlock(Long blockerId, Long blockedId) {
        blockRepository.findEqualBy(blockerId, blockedId)
                .ifPresent(block -> {
                    throw BlockException.BlockAlreadyExistedException;});
    }

    public void validateNoBlock(Long blockerId, Long blockedId) {
        if (!blockRepository.findBy(blockedId, blockerId).isEmpty()) {
            throw BlockException.BlockAccessForbiddenException;
        }
    }
}
