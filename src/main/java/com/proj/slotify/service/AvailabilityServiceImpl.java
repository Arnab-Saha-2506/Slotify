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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService{

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityServiceImpl.class);
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Override
    public AvailabilityResponseDTO setAvailability(AvailabilityRequestDTO dto) throws Exception{
        logger.info("[setAvailability] day={}, startTime={}, endTime={}", dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        logger.info("[setAvailability] Authenticated user: email={}", email);
        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[setAvailability] User not found with email={}", email);
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        logger.info("[setAvailability] User found: id={}", user.getId());

        if(!dto.getStartTime().isBefore(dto.getEndTime())){
            logger.warn("[setAvailability] Invalid time range: startTime={} is not before endTime={}", dto.getStartTime(), dto.getEndTime());
            throw new BadRequestException("Start time must be before End time");
        }

        if(availabilityRepository.existsByUserAndDayOfWeek(user, dto.getDayOfWeek())){
            logger.warn("[setAvailability] Availability already exists for user id={} on {}", user.getId(), dto.getDayOfWeek());
            throw new BadRequestException("Availability already set for "+dto.getDayOfWeek());
        }

        AvailabilityEntity entity = AvailabilityMapper.toEntity(dto, user);

        AvailabilityEntity savedEntity = availabilityRepository.save(entity);

        logger.info("[setAvailability] Availability saved: id={}, userId={}, day={}, startTime={}, endTime={}",
                savedEntity.getId(), user.getId(), savedEntity.getDayOfWeek(), savedEntity.getStartTime(), savedEntity.getEndTime());

        return AvailabilityMapper.toDTO(savedEntity);
    }

    @Override
    public List<AvailabilityResponseDTO> getAvailability() throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        logger.info("[getAvailability] Fetching availability for email={}", email);

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[getAvailability] User not found with email={}", email);
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        List<AvailabilityEntity> listEntities = availabilityRepository.findByUser(user);
        logger.info("[getAvailability] Found {} availability records for user id={}", listEntities.size(), user.getId());

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

        logger.info("[updateAvailability] Updating availability id={} for email={}", id, email);

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[updateAvailability] User not found with email={}", email);
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        AvailabilityEntity existingAvailability = availabilityRepository.findById(id).orElse(null);
        if(existingAvailability == null){
            logger.warn("[updateAvailability] Availability not found: id={}", id);
            throw new AvailabilityNotFoundException("Availability not found with id: "+id);
        }

        logger.info("[updateAvailability] Availability found: id={}, ownerId={}", existingAvailability.getId(), existingAvailability.getUser().getId());
        if(!existingAvailability.getUser().getId().equals(user.getId())){
            logger.warn("[updateAvailability] Unauthorized update attempt: user id={} tried to update availability id={} owned by {}",
                    user.getId(), id, existingAvailability.getUser().getId());
            throw new UnauthorizedException("You are not authorized to update this Availability.");
        }

        if(!dto.getStartTime().isBefore(dto.getEndTime())){
            logger.warn("[updateAvailability] Invalid time range: startTime={} is not before endTime={}", dto.getStartTime(), dto.getEndTime());
            throw new BadRequestException("Start time must be before End time");
        }

        if(!existingAvailability.getDayOfWeek().equals(dto.getDayOfWeek())){
            if(availabilityRepository.existsByUserAndDayOfWeek(user, dto.getDayOfWeek())){
                logger.warn("[updateAvailability] Availability already set for {}", dto.getDayOfWeek());
                throw new BadRequestException("Availability already set for "+dto.getDayOfWeek());
            }
        }

        existingAvailability.setDayOfWeek(dto.getDayOfWeek());
        existingAvailability.setStartTime(dto.getStartTime());
        existingAvailability.setEndTime(dto.getEndTime());

        AvailabilityEntity savedEntity = availabilityRepository.save(existingAvailability);
        logger.info("[updateAvailability] Availability updated: id={}, new day={}, startTime={}, endTime={}",
                savedEntity.getId(), savedEntity.getDayOfWeek(), savedEntity.getStartTime(), savedEntity.getEndTime());
        return AvailabilityMapper.toDTO(savedEntity);
    }

    @Override
    public void deleteAvailability(String id) throws Exception {
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        logger.info("[deleteAvailability] Deleting availability id={} for email={}", id, email);

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[deleteAvailability] User not found with email={}", email);
            throw new UserNotFoundException("User not found with this mail id: "+ email);
        }

        AvailabilityEntity existingAvailability = availabilityRepository.findById(id).orElse(null);
        if(existingAvailability == null){
            logger.warn("[deleteAvailability] Availability not found: id={}", id);
            throw new AvailabilityNotFoundException("Availability not found with id: "+id);
        }
        logger.info("[deleteAvailability] Availability found: id={}, ownerId={}", existingAvailability.getId(), existingAvailability.getUser().getId());
        if(!existingAvailability.getUser().getId().equals(user.getId())){
            logger.warn("[deleteAvailability] Unauthorized delete attempt: user id={} tried to delete availability id={}",
                    user.getId(), id);
            throw new UnauthorizedException("You are not authorized to delete this Availability.");
        }

        availabilityRepository.delete(existingAvailability);
        logger.info("[deleteAvailability] Availability deleted: id={}", id);
    }
}
