package com.mjuAppSW.joA.geography.block.repository;

import com.mjuAppSW.joA.geography.block.Block;
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
    public Optional<Block> findEqualBlock(Long blockerId, Long blockedId) {
        return blockJpaRepository.findEqualBlock(blockerId, blockedId);
    }

    @Override
    public List<Block> findBlockByIds(Long blockerId, Long blockedId) {
        return blockJpaRepository.findBlockByIds(blockerId, blockedId);
    }

}
