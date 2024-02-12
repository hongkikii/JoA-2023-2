package com.mjuAppSW.joA.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import com.mjuAppSW.joA.domain.room.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r Where r.status = :status")
    List<Room> findByStatus(@Param("status") String status);

    @Query("SELECT r FROM Room r WHERE r.status = :status AND r.id Not IN :roomIds")
    List<Room> findByStatusAndNotRoomIds(@Param("status") String status, @Param("roomIds") Set<Long> roomIds);
}

