package com.proj.slotify.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookingRequestDTO {
    @NotBlank(message = "Owner ID is required")
    private String ownerId;

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @Email(message = "Guest email should be valid")
    @NotBlank(message = "Guest email is required")
    private String guestEmail;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
}
