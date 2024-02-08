package com.mjuAppSW.joA.geography.block.repository;

import com.mjuAppSW.joA.geography.block.Block;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockJpaRepository extends JpaRepository<Block, Long> {

    @Query("SELECT b FROM Block b WHERE b.blocker.id = :blockerId AND b.blocked.id = :blockedId")
    Optional<Block> findEqualBlock(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);

    @Query("SELECT b FROM Block b WHERE (b.blocker.id = :blockerId AND b.blocked.id = :blockedId) OR (b.blocker.id = :blockedId AND b.blocked.id = :blockerId)")
    List<Block> findBlockByIds(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
}
