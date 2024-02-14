package com.mjuAppSW.joA.domain.block.repository;

import com.mjuAppSW.joA.domain.block.entity.Block;
import java.util.List;
import java.util.Optional;

public interface BlockRepository {
    void save(Block block);

    Optional<Block> findEqualBy(Long blockerId, Long blockedId);

    List<Block> findBy(Long blockerId, Long blockedId);
}
