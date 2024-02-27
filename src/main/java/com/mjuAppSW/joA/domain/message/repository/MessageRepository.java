package com.mjuAppSW.joA.domain.message.repository;

import com.mjuAppSW.joA.domain.message.vo.CurrentMessageVO;
import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.member.entity.Member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m Where m.room = :room")
    List<Message> findByRoom(@Param("room") Room room);

    @Query("SELECT m FROM Message m WHERE m.room = :room AND m.member <> :member AND m.isChecked = :isChecked")
    List<Message> findOpponentByRoomAndMemberAndIsChecked(@Param("room") Room room, @Param("member") Member member, @Param("isChecked") String isChecked);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.room = :room AND m.member = :member AND m.isChecked = :isChecked")
    Integer countUnCheckedMessagesByRoomAndMemberAndIsChecked(@Param("room") Room room, @Param("member") Member member, @Param("isChecked") String isChecked);

    @Query("SELECT m.content AS content, m.time AS time FROM Message m WHERE m.room = :room " +
        "AND (m.content IS NULL OR m.content IS NOT NULL) " +
        "ORDER BY m.time DESC")
    List<CurrentMessageVO> findCurrentMessageByRoom(@Param("room") Room room);
}
