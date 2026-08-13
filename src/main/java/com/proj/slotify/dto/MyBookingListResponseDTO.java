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
    private String startTime;
    private String endTime;
    private String status;
}
