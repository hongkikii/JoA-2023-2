package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.message.dto.vo.CurrentMessageVO;
import com.mjuAppSW.joA.domain.room.Room;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m Where m.room = :room")
    List<Message> findByRoom(@Param("room") Room room);

    @Query("SELECT m FROM Message m WHERE m.room = :room AND m.isChecked = '1' AND m.member <> :member")
    List<Message> findMessage(@Param("room") Room room, @Param("member") Member member);


    @Query("SELECT COUNT(m) FROM Message m WHERE m.room = :room AND m.member = :member AND m.isChecked = '1'")
    Integer countUnCheckedMessage(@Param("room") Room room, @Param("member") Member member);


    @Query("SELECT m.content AS content, m.time AS time FROM Message m WHERE m.room = :room " +
            "AND m.content IS NULL OR m.content IS NOT NULL " +
            "AND (m.time IS NULL OR m.time = (SELECT MAX(mes2.time) FROM Message mes2 WHERE mes2.room = :room))")
    Optional<CurrentMessageVO> getCurrentMessageAndTime(@Param("room") Room room);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.room = :room")
    void deleteByRoom(@Param("room") Room room);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isChecked = '0' WHERE m In :messages")
    void updateIsChecked(@Param("messages") List<Message> messages);
}
