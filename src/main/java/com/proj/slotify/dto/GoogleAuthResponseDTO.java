package com.proj.slotify.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthResponseDTO {
    private String token;
    private String email;
    private String name;
    private boolean newUser;
}
