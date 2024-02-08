package com.mjuAppSW.joA.geography.block.repository;

import com.mjuAppSW.joA.geography.block.Block;
import java.util.List;
import java.util.Optional;

public interface BlockRepository {
    void save(Block newBlock);

    Optional<Block> findEqualBy(Long blockerId, Long blockedId);

    List<Block> findBy(Long takeMemberId, Long giveMemberId);
}
