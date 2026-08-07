package com.proj.slotify.service;

import com.proj.slotify.dto.BookingRequestDTO;
import com.proj.slotify.dto.BookingResponseDTO;
import com.proj.slotify.dto.MyBookingListResponseDTO;
import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.enums.BookingStatus;
import com.proj.slotify.exception.*;
import com.proj.slotify.mapper.BookingMapper;
import com.proj.slotify.repository.BookingRepository;
import com.proj.slotify.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) throws Exception{
        UserEntity ownerDetails = userRepository.findById(dto.getOwnerId()).orElse(null);

        if(ownerDetails == null){
            throw new UserNotFoundException("Owner not found with this ID: "+dto.getOwnerId());
        }

//        if(!dto.getStartTime().isBefore(dto.getEndTime())){
//            throw new Exception("Start time must be before End time!");
//        }
        LocalDateTime endTime = dto.getStartTime().plusMinutes(dto.getDuration());

        //Check conflicts
        List<BookingEntity> conflicts = bookingRepository.findConflictingBookings(dto.getOwnerId(), dto.getStartTime(), endTime);

        if(!conflicts.isEmpty()){
            throw new SlotAlreadyBookedException("Slot already booked for this requested time.");
        }

        BookingEntity booking = BookingMapper.toEntity(dto, ownerDetails, endTime);

        BookingEntity saved = bookingRepository.save(booking);

        return BookingMapper.toDTO(saved);
    }

    @Override
    public List<MyBookingListResponseDTO> getMyBookings() throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found");
        }

        return bookingRepository.findByOwner(user).stream()
                .map(BookingMapper::toListItemDTO)
                .toList();
    }

    @Override
    public MyBookingListResponseDTO getBookingDetails(String id) throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found");
        }

        BookingEntity booking = bookingRepository.findById(id).orElse(null);

        if(booking == null){
            throw new BookingNotFoundException("Booking not found with this ID: "+id);
        }

        if(!booking.getOwner().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to view this booking!");
        }

        return BookingMapper.toListItemDTO(booking);

    }

    @Override
    public BookingResponseDTO cancelBookingById(String id) throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User not found");
        }

        BookingEntity booking = bookingRepository.findById(id).orElse(null);

        if(booking == null){
            throw new BookingNotFoundException("Booking not found with this ID: "+id);
        }

        if(!booking.getOwner().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to view this booking!");
        }

        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new BadRequestException("Booking is already cancelled!");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        BookingEntity saved = bookingRepository.save(booking);

        return BookingResponseDTO.builder()
                .bookingId(saved.getBookingId())
                .status(saved.getStatus().name())
                .message("Booking cancelled successfully.")
                .build();
    }
}
