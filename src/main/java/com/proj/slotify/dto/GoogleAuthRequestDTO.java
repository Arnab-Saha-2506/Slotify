package com.proj.slotify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthRequestDTO {
    @NotBlank(message = "Google ID token is required")
    private String idToken;
}
