package com.proj.slotify.service;

import com.proj.slotify.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO getCurrentUser() throws Exception;
}
