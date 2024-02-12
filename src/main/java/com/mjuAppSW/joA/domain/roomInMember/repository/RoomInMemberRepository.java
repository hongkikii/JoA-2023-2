package com.mjuAppSW.joA.domain.roomInMember.repository;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoIncludeMessageVO;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomInMemberRepository extends JpaRepository<RoomInMember, Long> {

    @Query("SELECT rim FROM RoomInMember rim WHERE rim.room = :room AND rim.member = :member")
    Optional<RoomInMember> findByRoomAndMember(@Param("room") Room room, @Param("member") Member member);

    @Query("SELECT rim FROM RoomInMember rim WHERE rim.member = :member AND rim.expired = '1'")
    List<RoomInMember> findByAllMember(@Param("member") Member member);

    @Query("SELECT rim FROM RoomInMember rim WHERE rim.room = :room")
    List<RoomInMember> findByAllRoom(@Param("room") Room room);

    @Query("SELECT rm FROM RoomInMember rm " +
        "WHERE rm.room.id IN (SELECT r.id FROM Room r " +
        "WHERE r.id IN (SELECT rm1.room.id FROM RoomInMember rm1 WHERE rm1.member = :member1) " +
        "AND r.id IN (SELECT rm2.room.id FROM RoomInMember rm2 WHERE rm2.member = :member2))")
    List<RoomInMember> checkRoomInMember(@Param("member1") Member member1, @Param("member2") Member member2);

    @Query("SELECT rim.room AS room, rim.room.date AS date, rim.member.name AS name, rim.member.urlCode AS urlCode, mes.content AS content " +
            "FROM RoomInMember rim " +
            "LEFT JOIN Message mes ON rim.member = mes.member AND rim.room = mes.room " +
            "WHERE rim.member = :member AND rim.room = :room " +
            "ORDER BY mes.time DESC")
    List<RoomInfoIncludeMessageVO> findRoomInfoIncludeMessage(@Param("room") Room room, @Param("member") Member member);

    @Query("SELECT rim.room AS room, rim.room.date AS date, rim.member.name AS name, rim.member.urlCode AS urlCode " +
        "From RoomInMember rim WHERE rim.member = :member AND rim.room = :room")
    Optional<RoomInfoExceptMessageVO> findRoomInfoExceptMessageByRoomAndMember(@Param("room") Room room, @Param("member") Member member);

    @Query("SELECT rim FROM RoomInMember rim Where rim.room = :room")
    List<RoomInMember> findAllRoom(@Param("room") Room room);

    @Query("SELECT rim FROM RoomInMember rim Where rim.room = :room AND rim.member <> :member")
    Optional<RoomInMember> findOpponentByRoomAndMember(@Param("room") Room room, @Param("member") Member member);

    @Query("SELECT rim FROM RoomInMember rim Where rim.room = :room AND rim.member <> :member AND rim.expired = :expired")
    Optional<RoomInMember> checkOpponentExpired(@Param("room") Room room, @Param("member") Member member, @Param("expired") String expired);

    @Query("SELECT rim.room.id AS roomId ,rim.member.id AS memberId, rim.member.name AS name, rim.member.urlCode AS urlCode, rim.member.bio AS bio " +
        "FROM RoomInMember rim WHERE rim.room = :room AND rim.member <> :member")
    Optional<UserInfoVO> findOpponentUserInfoByRoomAndMember(@Param("room") Room room, @Param("member") Member member);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomInMember rim WHERE rim.room = :room")
    void deleteByRoom(@Param("room") Room room);
}

