package com.proj.slotify.service;

import com.proj.slotify.dto.LoginRequestDTO;
import com.proj.slotify.dto.LoginResponseDTO;
import com.proj.slotify.dto.RegisterRequestDTO;
import com.proj.slotify.dto.RegisterResponseDTO;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.exception.BadRequestException;
import com.proj.slotify.exception.InvalidCredsException;
import com.proj.slotify.exception.UserAlreadyExistsException;
import com.proj.slotify.exception.UserNotFoundException;
import com.proj.slotify.mapper.RegisterMapper;
import com.proj.slotify.repository.UserRepository;
import com.proj.slotify.security.JwtUtil;
import com.proj.slotify.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService{

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO dto) throws Exception{
        logger.info("[registerUser] Attempting registration for email={}, name={}", dto.getEmail(), dto.getName());

        if(userRepository.existsByEmail(dto.getEmail())){
            logger.warn("[registerUser] Registration failed: email {} already exists", dto.getEmail());
            throw new UserAlreadyExistsException("User already exists with email: "+dto.getEmail());
        }
        logger.info("[registerUser] Email {} is available, proceeding with registration", dto.getEmail());

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        logger.info("[registerUser] Password hashed successfully for email={}", dto.getEmail());

        UserEntity userDetails = UserEntity.builder()
//                .id(UUID.randomUUID().toString().substring(0,8))
                .id(IdGenerator.generateUserId(dto.getName()))
                .name(dto.getName())
                .email(dto.getEmail())
                .password(hashedPassword)
                .timezone(dto.getTimezone())
                .build();
        logger.info("[registerUser] Generated user ID: {}", userDetails.getId());

        UserEntity savedUser = userRepository.save(userDetails);
        logger.info("[registerUser] User registered successfully: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        return RegisterMapper.toDTO(savedUser);

        // return null;
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO dto) throws Exception{
        logger.info("[loginUser] Login attempt for email={}", dto.getEmail());

        UserEntity userDetails = userRepository.findByEmail(dto.getEmail());

        if(userDetails == null){
            logger.warn("[loginUser] Login failed: user not found for email={}", dto.getEmail());
            throw new InvalidCredsException("User doesn't exist, Register!!!");
        }
        logger.info("[loginUser] User found: id={}, email={}", userDetails.getId(), userDetails.getEmail());

        if(!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())){
            logger.warn("[loginUser] Invalid password for email={}", dto.getEmail());
            throw new InvalidCredsException("Invalid password");
        }
        logger.info("[loginUser] Password matched for email={}", dto.getEmail());

        String token = jwtUtil.generateToken(userDetails.getId(), userDetails.getEmail());
        logger.info("[loginUser] JWT token generated for user id={}", userDetails.getId());

        return LoginResponseDTO.builder()
                .token(token)
                .build();
    }

    @Override
    public void logout() throws Exception{
        logger.info("[logout] Logout requested...");
        logger.info("[logout] Logout succesfull!");
    }
}
