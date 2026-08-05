package com.proj.slotify.mapper;

import com.proj.slotify.dto.RegisterResponseDTO;
import com.proj.slotify.entity.UserEntity;

public class RegisterMapper {
    public static RegisterResponseDTO toDTO(UserEntity entity){
        return RegisterResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .timezone(entity.getTimezone())
                .message("User registered successfully.")
                .build();
    }
}
