package com.mjuAppSW.joA.geography.block.repository;

import com.mjuAppSW.joA.geography.block.Block;
import java.util.List;
import java.util.Optional;

public interface BlockRepository {
    void save(Block newBlock);

    Optional<Block> findEqualBlock(Long blockerId, Long blockedId);

    List<Block> findBlockByIds(Long takeMemberId, Long giveMemberId);
}
