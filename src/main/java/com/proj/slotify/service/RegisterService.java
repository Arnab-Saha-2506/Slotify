package com.proj.slotify.service;

import com.proj.slotify.dto.LoginRequestDTO;
import com.proj.slotify.dto.LoginResponseDTO;
import com.proj.slotify.dto.RegisterRequestDTO;
import com.proj.slotify.dto.RegisterResponseDTO;

public interface RegisterService {

    RegisterResponseDTO registerUser(RegisterRequestDTO dto) throws Exception;
    LoginResponseDTO loginUser(LoginRequestDTO dto) throws Exception;
}
