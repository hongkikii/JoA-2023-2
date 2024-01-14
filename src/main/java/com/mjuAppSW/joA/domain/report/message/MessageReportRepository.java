package com.mjuAppSW.joA.domain.report.message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mjuAppSW.joA.domain.message.Message;
import com.mjuAppSW.joA.domain.room.Room;

@Repository
public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {
    @Query("SELECT mr FROM MessageReport mr Where mr.message_id = :message")
    Optional<MessageReport> findByMessage(@Param("message") Message message);

    @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.member.id = :memberId1")
    List<MessageReport> findByMemberId(@Param("memberId1") Long memberId1);

    // @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.room.id = :roomId")
    // List<MessageReport> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.room = :room")
    List<MessageReport> findByRoomId(@Param("room") Room room);
}
