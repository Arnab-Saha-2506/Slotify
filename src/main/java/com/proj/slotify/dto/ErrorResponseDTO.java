package com.proj.slotify.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public static ErrorResponseDTO of(int status, String error, String message, String path){
        return ErrorResponseDTO.builder()
                .timeStamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}
