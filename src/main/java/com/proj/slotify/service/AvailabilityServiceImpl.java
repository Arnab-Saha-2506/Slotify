package com.proj.slotify.service;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;
import com.proj.slotify.entity.AvailabilityEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.exception.AvailabilityNotFoundException;
import com.proj.slotify.exception.BadRequestException;
import com.proj.slotify.exception.UnauthorizedException;
import com.proj.slotify.exception.UserNotFoundException;
import com.proj.slotify.mapper.AvailabilityMapper;
import com.proj.slotify.repository.AvailabilityRepository;
import com.proj.slotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService{

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Override
    public AvailabilityResponseDTO setAvailability(AvailabilityRequestDTO dto) throws Exception{

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        if(!dto.getStartTime().isBefore(dto.getEndTime())){
            throw new BadRequestException("Start time must be before End time");
        }

        if(availabilityRepository.existsByUserAndDayOfWeek(user, dto.getDayOfWeek())){
            throw new BadRequestException("Availability already set for "+dto.getDayOfWeek());
        }

        AvailabilityEntity entity = AvailabilityMapper.toEntity(dto, user);

        AvailabilityEntity savedEntity = availabilityRepository.save(entity);

        return AvailabilityMapper.toDTO(savedEntity);
    }

    @Override
    public List<AvailabilityResponseDTO> getAvailability() throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        List<AvailabilityEntity> listEntities = availabilityRepository.findByUser(user);

        return listEntities.stream()
                .map(AvailabilityMapper::toDTO)
                .toList();
    }

    @Override
    public AvailabilityResponseDTO updateAvailability(String id, AvailabilityRequestDTO dto) throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        AvailabilityEntity existingAvailability = availabilityRepository.findById(id).orElse(null);
        if(existingAvailability == null){
            throw new AvailabilityNotFoundException("Availability not found with id: "+id);
        }

        if(!existingAvailability.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to update this Availability.");
        }

        if(!dto.getStartTime().isBefore(dto.getEndTime())){
            throw new BadRequestException("Start time must be before End time");
        }

        if(!existingAvailability.getDayOfWeek().equals(dto.getDayOfWeek())){
            if(availabilityRepository.existsByUserAndDayOfWeek(user, dto.getDayOfWeek())){
                throw new BadRequestException("Availability already set for "+dto.getDayOfWeek());
            }
        }

        existingAvailability.setDayOfWeek(dto.getDayOfWeek());
        existingAvailability.setStartTime(dto.getStartTime());
        existingAvailability.setEndTime(dto.getEndTime());

        AvailabilityEntity savedEntity = availabilityRepository.save(existingAvailability);
        return AvailabilityMapper.toDTO(savedEntity);
    }

    @Override
    public void deleteAvailability(String id) throws Exception {
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        AvailabilityEntity existingAvailability = availabilityRepository.findById(id).orElse(null);
        if(existingAvailability == null){
            throw new AvailabilityNotFoundException("Availability not found with id: "+id);
        }
        if(!existingAvailability.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to delete this Availability.");
        }

        availabilityRepository.delete(existingAvailability);

    }
}
