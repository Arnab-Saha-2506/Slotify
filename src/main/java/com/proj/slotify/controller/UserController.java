package com.proj.slotify.controller;

import com.proj.slotify.dto.SlotResponseDTO;
import com.proj.slotify.dto.UserResponseDTO;
import com.proj.slotify.service.SlotService;
import com.proj.slotify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final SlotService slotService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() throws Exception{
        logger.info("[UserController] GET /api/v1/users/me");

        UserResponseDTO response = userService.getCurrentUser();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}/slots")
    public ResponseEntity<List<SlotResponseDTO>> getAvailableSlots(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String timezone) throws Exception{
        logger.info("[UserController] GET /api/v1/users/{}/slots - date={}, duration={}, timezone={}", userId, date, duration, timezone);

        List<SlotResponseDTO> slotsList = slotService.getAvailableSlots(userId, date, duration, timezone);
        logger.info("[UserController] Returning {} slots for user {}", slotsList.size(), userId);

        return ResponseEntity.ok().body(slotsList);
    }

}
