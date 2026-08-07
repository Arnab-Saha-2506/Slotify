package com.proj.slotify.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookingResponseDTO {
    private String bookingId;
    private String status;
    private String message;
}
