package com.proj.slotify.repository;

import com.proj.slotify.entity.AvailabilityEntity;
import com.proj.slotify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, String> {
    List<AvailabilityEntity> findByUser(UserEntity user);

    AvailabilityEntity findByUserAndDayOfWeek(UserEntity user, DayOfWeek dayOfWeek);

    Boolean existsByUserAndDayOfWeek(UserEntity user, DayOfWeek dayOfWeek);
}
