package com.proj.slotify.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SlotResponseDTO {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
