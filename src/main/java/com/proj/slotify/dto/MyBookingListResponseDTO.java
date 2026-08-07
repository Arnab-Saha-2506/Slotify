package com.proj.slotify.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MyBookingListResponseDTO {
    private String bookingId;
    private String guestName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
