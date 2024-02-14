package com.mjuAppSW.joA.domain.block.repository;

import com.mjuAppSW.joA.domain.block.entity.Block;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepository{

    private final BlockJpaRepository blockJpaRepository;

    @Override
    public void save(Block block) {
        blockJpaRepository.save(block);
    }

    @Override
    public Optional<Block> findEqualBy(Long blockerId, Long blockedId) {
        return blockJpaRepository.findEqualBy(blockerId, blockedId);
    }

    @Override
    public List<Block> findBy(Long blockerId, Long blockedId) {
        return blockJpaRepository.findBy(blockerId, blockedId);
    }

}
