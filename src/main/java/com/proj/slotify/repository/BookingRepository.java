package com.proj.slotify.repository;

import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, String> {
    List<BookingEntity> findByOwner(UserEntity owner);
    List<BookingEntity> findByOwnerId(String ownerId);
    List<BookingEntity> findByOwnerAndStatus(UserEntity owner, BookingStatus status);

}
