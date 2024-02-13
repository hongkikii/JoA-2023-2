package com.mjuAppSW.joA.domain.vote.repository;

import com.mjuAppSW.joA.domain.vote.entity.Vote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteJpaRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT vc.name FROM Vote v JOIN v.voteCategory vc WHERE v.member.id = :id AND v.isValid = true GROUP BY vc.name ORDER BY COUNT(vc.name) DESC")
    List<String> findVoteCategoryById(@Param("id") Long id, Pageable pageable);

    @Query("SELECT v FROM Vote v WHERE v.giveId = :giveId AND v.member.id = :takeId AND v.voteCategory.id = :categoryId AND DATE(v.date) = current_date")
    Optional<Vote> findTodayVote(@Param("giveId")Long giveId, @Param("takeId")Long takeId, @Param("categoryId")Long categoryId);

    @Query("SELECT v FROM Vote v WHERE v.member.id = :takeId AND v.isValid = true ORDER BY v.date DESC")
    List<Vote> findValidAllByTakeId(@Param("takeId") Long takeId, Pageable pageable);

    @Query("SELECT v FROM Vote v WHERE v.giveId = :giveId AND v.member.id = :takeId AND v.isValid = false")
    List<Vote> findInvalidVotes(@Param("giveId") Long giveId, @Param("takeId") Long takeId);
}
