package com.proj.slotify.service;

import com.proj.slotify.dto.RegisterRequestDTO;
import com.proj.slotify.dto.RegisterResponseDTO;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.mapper.RegisterMapper;
import com.proj.slotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO dto) throws Exception{

        if(userRepository.existsByEmail(dto.getEmail())){
            throw new Exception("User already exists with email: "+dto.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        UserEntity userDetails = UserEntity.builder()
                .id(UUID.randomUUID().toString().substring(0,8))
                .name(dto.getName())
                .email(dto.getEmail())
                .password(hashedPassword)
                .timezone(dto.getTimezone())
                .build();

        UserEntity savedUser = userRepository.save(userDetails);

        return RegisterMapper.toDTO(savedUser);

        // return null;
    }
}
