package com.mjuAppSW.joA.domain.roomInMember;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.roomInMember.dto.RoomInfo;
import com.mjuAppSW.joA.domain.roomInMember.dto.UserInfo;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomInMemberRepository extends JpaRepository<RoomInMember, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomInMember rim WHERE rim.room = :room")
    void deleteByRoom(@Param("room") Room room);

    @Query("SELECT rim FROM RoomInMember rim WHERE rim.room = :room AND rim.member = :member")
    RoomInMember findByRoomAndMember(@Param("room") Room room, @Param("member") Member member);

    @Query("SELECT rim FROM RoomInMember rim WHERE rim.member = :member AND rim.expired = '1'")
    List<RoomInMember> findByAllMember(@Param("member") Member member);

    @Query("SELECT rim FROM RoomInMember rim WHERE rim.room = :room")
    List<RoomInMember> findByAllRoom(@Param("room") Room room);

    @Query("SELECT rm FROM RoomInMember rm " +
            "WHERE rm.room.id IN (SELECT r.id FROM Room r " +
            "WHERE r.id IN (SELECT rm1.room.id FROM RoomInMember rm1 WHERE rm1.member = :member1) " +
            "AND r.id IN (SELECT rm2.room.id FROM RoomInMember rm2 WHERE rm2.member = :member2))")
    List<RoomInMember> checkRoomInMember(@Param("member1") Member member1, @Param("member2") Member member2);

    @Query("SELECT rim.room AS room, rim.room.date AS date, m.name AS name, m.urlCode AS urlCode, mes.content AS content " +
            "FROM RoomInMember rim " +
            "LEFT JOIN Member m ON rim.member.id = m.id " +
            "LEFT JOIN Room r ON rim.room.id = r.id " +
            "LEFT JOIN Message mes ON rim.member = mes.member AND rim.room = mes.room " +
            "WHERE rim.member = :member AND rim.room = :room " +
            "AND (mes.content IS NULL OR mes.content IS NOT NULL) " +
            "AND (mes.time IS NULL OR mes.time = (SELECT MAX(mes2.time) FROM Message mes2 WHERE mes2.member = :member AND mes2.room = :room))")
    RoomInfo findRoomInfoValue(@Param("member") Member member, @Param("room") Room room);


    @Modifying
    @Transactional
    @Query("UPDATE RoomInMember rim set rim.result = :result  WHERE rim.room = :room AND rim.member = :member")
    void saveVote(@Param("room") Room room, @Param("member") Member member, @Param("result") String result);

    @Query("SELECT rim FROM RoomInMember rim Where rim.room = :room")
    List<RoomInMember> findAllRoom(@Param("room") Room room);

    @Query("SELECT rim FROM RoomInMember rim Where rim.room = :room AND rim.member <> :member")
    RoomInMember checkExpired(@Param("room") Room room, @Param("member") Member member);

    @Query("SELECT rim.room.id AS roomId ,m.id AS memberId, m.name AS name, m.urlCode AS urlCode, m.bio AS bio " +
            "From RoomInMember rim LEFT JOIN Member m ON rim.member.id = m.id " +
            "WHERE rim.room = :room AND rim.member <> :member")
    UserInfo getUserInfo(@Param("room") Room room, @Param("member") Member member);

    @Modifying
    @Transactional
    @Query("UPDATE RoomInMember rim set rim.expired = :expired WHERE rim.room = :room and rim.member = :member")
    void updateExpired(@Param("room") Room room, @Param("member") Member member, @Param("expired") String expired);

    @Modifying
    @Transactional
    @Query("UPDATE RoomInMember rim set rim.entryTime = :date WHERE rim.room = :room and rim.member = :member")
    void updateEntryTime(@Param("room") Room room, @Param("member") Member member, @Param("date") LocalDateTime date);

    @Modifying
    @Transactional
    @Query("UPDATE RoomInMember rim set rim.exitTime = :date WHERE rim.room = :room and rim.member = :member")
    void updateExitTime(@Param("room") Room room, @Param("member") Member member, @Param("date") LocalDateTime date);

}

