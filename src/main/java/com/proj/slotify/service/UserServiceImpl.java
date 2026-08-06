package com.proj.slotify.service;

import com.proj.slotify.dto.UserResponseDTO;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.mapper.RegisterMapper;
import com.proj.slotify.mapper.UserMapper;
import com.proj.slotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getCurrentUser() throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("User not found!!");
        }
        return UserMapper.toDTO(user);
//        return null;
    }
}
