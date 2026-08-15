package com.proj.slotify.service;

import com.proj.slotify.dto.BookingRequestDTO;
import com.proj.slotify.dto.BookingResponseDTO;
import com.proj.slotify.dto.MyBookingListResponseDTO;
import com.proj.slotify.entity.AvailabilityEntity;
import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.enums.BookingStatus;
import com.proj.slotify.exception.*;
import com.proj.slotify.mapper.BookingMapper;
import com.proj.slotify.repository.AvailabilityRepository;
import com.proj.slotify.repository.BookingRepository;
import com.proj.slotify.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) throws Exception{
        logger.info("[createBooking] ownerId={}, guestName={}, startTime={}, duration={}",
                dto.getOwnerId(), dto.getGuestName(), dto.getStartTime(), dto.getDuration());
        UserEntity ownerDetails = userRepository.findById(dto.getOwnerId()).orElse(null);

        if(ownerDetails == null){
            logger.warn("[createBooking] Owner not found with id={}", dto.getOwnerId());
            throw new UserNotFoundException("Owner not found with this ID: "+dto.getOwnerId());
        }

//        if(!dto.getStartTime().isBefore(dto.getEndTime())){
//            throw new Exception("Start time must be before End time!");
//        }

//        LocalDateTime endTime = dto.getStartTime().plusMinutes(dto.getDuration());
        Instant bookingStart = dto.getStartTime().toInstant();
        Instant bookingEnd = bookingStart.plus(dto.getDuration(), ChronoUnit.MINUTES);
        logger.info("[createBooking] Owner found: id={}, email={}", ownerDetails.getId(), ownerDetails.getEmail());

        // Check against owner's availability
        DayOfWeek dayOfWeek = dto.getStartTime().getDayOfWeek();
        AvailabilityEntity availability = availabilityRepository.findByUserAndDayOfWeek(ownerDetails, dayOfWeek);

        if (availability == null) {
            logger.warn("[createBooking] Owner id={} has no availability on {}", ownerDetails.getId(), dayOfWeek);
            throw new BadRequestException("Owner is not available on " + dayOfWeek);
        }

//        LocalDateTime availabilityStart = dto.getStartTime().toLocalDate().atTime(availability.getStartTime());
//        LocalDateTime availabilityEnd = dto.getStartTime().toLocalDate().atTime(availability.getEndTime());
//
//        boolean withinAvailability = !dto.getStartTime().isBefore(availabilityStart) && !endTime.isAfter(availabilityEnd);

        ZoneId hostZone = ZoneId.of(ownerDetails.getTimezone());
        ZonedDateTime slotInHostZone = dto.getStartTime().toZonedDateTime().withZoneSameInstant(hostZone);
        LocalTime slotLocalTime = slotInHostZone.toLocalTime();
        LocalTime slotEndLocalTime = slotLocalTime.plusMinutes(dto.getDuration());

        boolean withinAvailability = !slotLocalTime.isBefore(availability.getStartTime())
                && !slotEndLocalTime.isAfter(availability.getEndTime());

        if (!withinAvailability) {
            logger.warn("[createBooking] Slot {}-{} is outside owner's availability window {}-{}",
                    slotLocalTime, slotEndLocalTime, availability.getStartTime(), availability.getEndTime());
            throw new BadRequestException("Selected slot is outside owner's availability hours");
        }

        //Check conflicts
        List<BookingEntity> conflicts = bookingRepository.findConflictingBookings(dto.getOwnerId(), bookingStart, bookingEnd);

        if(!conflicts.isEmpty()){
            logger.warn("[createBooking] Slot conflict detected for ownerId={}, startTime={}, endTime={}, conflictCount={}",
                    dto.getOwnerId(), dto.getStartTime(), bookingEnd, conflicts.size());
            throw new SlotAlreadyBookedException("Slot already booked for this requested time.");
        }

        BookingEntity booking = BookingMapper.toEntity(dto, ownerDetails, bookingStart, bookingEnd);

        BookingEntity saved = bookingRepository.save(booking);

        logger.info("[createBooking] Booking saved successfully: bookingId={}, ownerId={}, startTime={}, endTime={}",
                saved.getBookingId(), saved.getOwner().getId(), saved.getStartTime(), saved.getEndTime());

        // --- Send confirmation email ---
//        try {
//            emailService.sendBookingConfirmation(saved, ownerDetails);
//        } catch (MessagingException e) {
//            logger.error("[createBooking] Failed to send confirmation email for bookingId={}", saved.getBookingId(), e);
//            // Do not fail the booking if email fails; log and continue
//        }
        sendConfirmationAsync(saved, ownerDetails);

        return BookingMapper.toDTO(saved);
    }

    @Override
    public List<MyBookingListResponseDTO> getMyBookings() throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        logger.info("[getMyBookings] Fetching bookings for email={}", email);

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[getMyBookings] User not found for email={}", email);
            throw new UserNotFoundException("User not found");
        }
        logger.info("[getMyBookings] User found: id={}, fetching bookings", user.getId());

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
        logger.info("[getBookingDetails] Fetching booking id={} for email={}", id, email);

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[getBookingDetails] User not found for email={}", email);
            throw new UserNotFoundException("User not found");
        }

        BookingEntity booking = bookingRepository.findById(id).orElse(null);

        if(booking == null){
            logger.warn("[getBookingDetails] Booking not found: id={}", id);
            throw new BookingNotFoundException("Booking not found with this ID: "+id);
        }

        if(!booking.getOwner().getId().equals(user.getId())){
            logger.warn("[getBookingDetails] Unauthorized access: user id={} tried to access booking id={} owned by {}",
                    user.getId(), id, booking.getOwner().getId());
            throw new UnauthorizedException("You are not authorized to view this booking!");
        }

        logger.info("[getBookingDetails] Access granted for user id={} to booking id={}", user.getId(), id);
        return BookingMapper.toListItemDTO(booking);

    }

    @Override
    public BookingResponseDTO cancelBookingById(String id) throws Exception{
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        logger.info("[cancelBookingById] Cancelling booking id={} for email={}", id, email);

        UserEntity user = userRepository.findByEmail(email);

        if(user == null){
            logger.warn("[cancelBookingById] User not found for email={}", email);
            throw new UserNotFoundException("User not found");
        }

        BookingEntity booking = bookingRepository.findById(id).orElse(null);

        if(booking == null){
            logger.warn("[cancelBookingById] Booking not found: id={}", id);
            throw new BookingNotFoundException("Booking not found with this ID: "+id);
        }
        logger.info("[cancelBookingById] Booking found: id={}, ownerId={}, status={}",
                booking.getBookingId(), booking.getOwner().getId(), booking.getStatus());

        if(!booking.getOwner().getId().equals(user.getId())){
            logger.warn("[cancelBookingById] Unauthorized cancellation attempt: user id={} tried to cancel booking id={}",
                    user.getId(), id);
            throw new UnauthorizedException("You are not authorized to view this booking!");
        }

        if(booking.getStatus() == BookingStatus.CANCELLED){
            logger.warn("[cancelBookingById] Booking id={} is already cancelled", id);
            throw new BadRequestException("Booking is already cancelled!");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        BookingEntity saved = bookingRepository.save(booking);
        logger.info("[cancelBookingById] Booking cancelled successfully: bookingId={}", saved.getBookingId());

        // --- Send cancellation email ---
//        try {
//            emailService.sendBookingCancellation(saved, user); // user = owner/host
//        } catch (MessagingException e) {
//            logger.error("[cancelBookingById] Failed to send cancellation email for bookingId={}", saved.getBookingId(), e);
//        }
        sendCancellationAsync(saved, user);

        return BookingResponseDTO.builder()
                .bookingId(saved.getBookingId())
                .status(saved.getStatus().name())
                .message("Booking cancelled successfully.")
                .build();
    }

    @Async
    protected void sendConfirmationAsync(BookingEntity booking, UserEntity host){
        try {
            emailService.sendBookingConfirmation(booking, host);
        } catch (MessagingException e) {
            logger.error("[BookingServiceImpl] Async email failed for bookingId={}", booking.getBookingId(), e);
            // Do not fail the booking if email fails; log and continue
        }
    }

    @Async
    protected void sendCancellationAsync(BookingEntity booking, UserEntity host){
        try {
            emailService.sendBookingCancellation(booking, host); // user = owner/host
        } catch (MessagingException e) {
            logger.error("[BookingServiceImpl] Async email failed for bookingId={}", booking.getBookingId(), e);
        }
    }
}
