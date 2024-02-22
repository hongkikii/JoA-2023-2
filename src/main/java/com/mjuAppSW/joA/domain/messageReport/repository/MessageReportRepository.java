package com.mjuAppSW.joA.domain.messageReport.repository;

import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.messageReport.entity.MessageReport;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {
    @Query("SELECT mr FROM MessageReport mr Where mr.message_id = :message")
    Optional<MessageReport> findByMessage(@Param("message") Message message);

    @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.member = :member")
    List<MessageReport> findByMember(@Param("member") Member member);

    @Query("SELECT mr FROM MessageReport mr WHERE mr.message_id.room = :room")
    List<MessageReport> findByRoom(@Param("room") Room room);
}
