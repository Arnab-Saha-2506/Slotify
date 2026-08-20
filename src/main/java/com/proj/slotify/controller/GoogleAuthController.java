package com.proj.slotify.controller;

import com.proj.slotify.dto.GoogleAuthRequestDTO;
import com.proj.slotify.dto.GoogleAuthResponseDTO;
import com.proj.slotify.service.GoogleAuthService;
import com.proj.slotify.service.GoogleAuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class GoogleAuthController {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);
    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<GoogleAuthResponseDTO> googleLogin(@Valid @RequestBody GoogleAuthRequestDTO dto){
        logger.info("[GoogleAuthController] POST /api/v1/auth/google");

        GoogleAuthResponseDTO response = googleAuthService.authenticateWithGoogle(dto.getIdToken());

        logger.info("[GoogleAuthController] Google login successful: email={}, newUser={}", response.getEmail(), response.isNewUser());

        return ResponseEntity.ok().body(response);

    }
}
