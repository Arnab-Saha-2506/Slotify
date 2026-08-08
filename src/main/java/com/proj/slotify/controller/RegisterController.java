package com.proj.slotify.controller;


import com.proj.slotify.dto.LoginRequestDTO;
import com.proj.slotify.dto.LoginResponseDTO;
import com.proj.slotify.dto.RegisterRequestDTO;
import com.proj.slotify.dto.RegisterResponseDTO;
import com.proj.slotify.service.RegisterService;
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
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO dto) throws Exception{
        logger.info("[RegisterController] POST /api/v1/auth/register - email={}", dto.getEmail());

        RegisterResponseDTO response = registerService.registerUser(dto);
        logger.info("[RegisterController] Registration successful: userId={}", response.getId());

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO dto) throws Exception{
        logger.info("[RegisterController] POST /api/v1/auth/login - email={}", dto.getEmail());

        LoginResponseDTO response = registerService.loginUser(dto);
        logger.info("[RegisterController] Login successful for email={}", dto.getEmail());

        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() throws Exception{
        logger.info("[RegisterController] POST /api/v1/auth/logout");
        registerService.logout();
        return ResponseEntity.ok().build();
    }

}
