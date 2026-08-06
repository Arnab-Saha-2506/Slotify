package com.proj.slotify.mapper;

import com.proj.slotify.dto.UserResponseDTO;
import com.proj.slotify.entity.UserEntity;

public class UserMapper {
    public static UserResponseDTO toDTO(UserEntity entity) {
        return UserResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .timezone(entity.getTimezone())
                .build();
    }
}
