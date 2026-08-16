package com.proj.slotify.dto;

import java.util.List;

public record BrevoEmailRequestDTO(
        Sender sender,
        List<Recipient> to,
        String subject,
        String htmlContent
) {
    public record Sender(String name, String email){}
    public record Recipient(String email, String name){}
}
