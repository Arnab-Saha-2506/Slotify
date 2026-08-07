package com.proj.slotify.controller;

import com.proj.slotify.dto.SlotResponseDTO;
import com.proj.slotify.dto.UserResponseDTO;
import com.proj.slotify.service.SlotService;
import com.proj.slotify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SlotService slotService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() throws Exception{
        UserResponseDTO response = userService.getCurrentUser();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}/slots")
    public ResponseEntity<List<SlotResponseDTO>> getAvailableSlots(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer duration) throws Exception{
        List<SlotResponseDTO> slotsList = slotService.getAvailableSlots(userId, date, duration);
        return ResponseEntity.ok().body(slotsList);
    }

}
