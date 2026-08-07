package com.proj.slotify.repository;

import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, String> {
    List<BookingEntity> findByOwner(UserEntity owner);
    List<BookingEntity> findByOwnerId(String ownerId);
    List<BookingEntity> findByOwnerAndStatus(UserEntity owner, BookingStatus status);

    //Find conflicting booking
    @Query("SELECT b from BookingEntity b WHERE b.owner.id = :ownerId AND b.status = 'BOOKED' and b.startTime < :endTime AND b.endTime > :startTime")
    List<BookingEntity> findConflictingBookings(@Param("ownerId") String ownerId, @Param("startTime")LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b from BookingEntity b WHERE b.owner.id = :ownerId AND b.status = 'BOOKED'")
    List<BookingEntity> findBookedByOwner(@Param("ownerId") String ownerId);

}
