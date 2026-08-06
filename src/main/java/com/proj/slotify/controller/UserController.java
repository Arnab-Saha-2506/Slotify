package com.proj.slotify.controller;

import com.proj.slotify.dto.UserResponseDTO;
import com.proj.slotify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() throws Exception{
        UserResponseDTO response = userService.getCurrentUser();
        return ResponseEntity.ok().body(response);
    }
}
