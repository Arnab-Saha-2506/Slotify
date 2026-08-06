package com.proj.slotify.controller;


import com.proj.slotify.dto.LoginRequestDTO;
import com.proj.slotify.dto.LoginResponseDTO;
import com.proj.slotify.dto.RegisterRequestDTO;
import com.proj.slotify.dto.RegisterResponseDTO;
import com.proj.slotify.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO dto) throws Exception{
        RegisterResponseDTO response = registerService.registerUser(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO dto) throws Exception{
        LoginResponseDTO response = registerService.loginUser(dto);
        return ResponseEntity.ok().body(response);
    }

}
