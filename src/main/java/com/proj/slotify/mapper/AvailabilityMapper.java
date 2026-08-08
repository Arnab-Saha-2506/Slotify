package com.proj.slotify.mapper;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;
import com.proj.slotify.entity.AvailabilityEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.util.IdGenerator;

import java.time.DayOfWeek;
import java.util.UUID;

public class AvailabilityMapper {

    public static AvailabilityEntity toEntity(AvailabilityRequestDTO dto, UserEntity user) {
        DayOfWeek dayOfWeek = null;
        if(dto.getDayOfWeek()!=null && !dto.getDayOfWeek().isBlank()){
            dayOfWeek = DayOfWeek.valueOf(dto.getDayOfWeek().toUpperCase());
        }
        return AvailabilityEntity.builder()
                // .id(UUID.randomUUID().toString().substring(0, 8))
                .id(IdGenerator.generateForAvailability())
                .dayOfWeek(dayOfWeek)
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .user(user)
                .build();
    }

    public static AvailabilityResponseDTO toDTO(AvailabilityEntity entity) {
        return AvailabilityResponseDTO.builder()
                .id(entity.getId())
                .day(entity.getDayOfWeek() != null? entity.getDayOfWeek().name():null)
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }
}
