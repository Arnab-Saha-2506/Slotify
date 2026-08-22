package com.proj.slotify.service;

import com.proj.slotify.dto.GoogleAuthResponseDTO;

public interface GoogleAuthService {
    public GoogleAuthResponseDTO authenticateWithGoogle(String idToken);
}
