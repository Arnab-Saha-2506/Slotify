package com.proj.slotify.mapper;

import com.proj.slotify.dto.BookingRequestDTO;
import com.proj.slotify.dto.BookingResponseDTO;
import com.proj.slotify.dto.MyBookingListResponseDTO;
import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingMapper {

    public static BookingEntity toEntity(BookingRequestDTO dto, UserEntity owner, LocalDateTime endTime){
        return BookingEntity.builder()
                .bookingId(UUID.randomUUID().toString().substring(0,8))
                .owner(owner)
                .guestName(dto.getGuestName())
                .guestEmail(dto.getGuestEmail())
                .startTime(dto.getStartTime())
                .endTime(endTime)
                .status(BookingStatus.BOOKED)
                .build();
    }

    public static BookingResponseDTO toDTO(BookingEntity entity){
        return BookingResponseDTO.builder()
                .bookingId(entity.getBookingId())
                .status(entity.getStatus().name())
                .message("Meeting booked successfully!")
                .build();
    }
    public static MyBookingListResponseDTO toListItemDTO(BookingEntity entity){
        return MyBookingListResponseDTO.builder()
                .bookingId(entity.getBookingId())
                .guestName(entity.getGuestName())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus().name())
                .build();
    }
}
