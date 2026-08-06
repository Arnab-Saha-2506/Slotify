package com.proj.slotify.dto;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponseDTO {
    private String id;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
}
