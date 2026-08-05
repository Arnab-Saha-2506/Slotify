package com.proj.slotify.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
    private String id;
    private String name;
    private String email;
    private String timezone;
    private String message;
}
