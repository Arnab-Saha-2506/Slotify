package com.proj.slotify.service;

import com.proj.slotify.dto.UserResponseDTO;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.exception.UserNotFoundException;
import com.proj.slotify.mapper.RegisterMapper;
import com.proj.slotify.mapper.UserMapper;
import com.proj.slotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getCurrentUser() throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        logger.info("[getCurrentUser] Fetching profile for email={}", email);

        UserEntity user = userRepository.findByEmail(email);
        if(user == null){
            logger.warn("[getCurrentUser] User not found for email={}", email);
            throw new UserNotFoundException("User not found!!");
        }
        logger.info("[getCurrentUser] Returning profile for user id={}, name={}", user.getId(), user.getName());
        return UserMapper.toDTO(user);
//        return null;
    }
}
