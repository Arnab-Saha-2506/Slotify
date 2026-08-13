package com.proj.slotify.dto;

import lombok.*;

//import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SlotResponseDTO {

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
}
