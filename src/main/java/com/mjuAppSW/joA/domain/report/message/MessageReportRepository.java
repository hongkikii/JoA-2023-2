package com.mjuAppSW.joA.domain.report.message;

import com.mjuAppSW.joA.domain.message.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {
    @Query("SELECT mr FROM MessageReport mr Where mr.message_id = :message")
    MessageReport findByMessage(@Param("message") Message message);

    @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.member.id = :memberId1")
    List<MessageReport> findByMemberId(@Param("memberId1") Long memberId1);

    @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.room.id = :roomId")
    List<MessageReport> findByRoomId(@Param("roomId") Long roomId);
}
