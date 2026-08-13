package com.proj.slotify.service;


import com.proj.slotify.dto.SlotResponseDTO;
import com.proj.slotify.entity.AvailabilityEntity;
import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.exception.BadRequestException;
import com.proj.slotify.exception.UserNotFoundException;
import com.proj.slotify.repository.AvailabilityRepository;
import com.proj.slotify.repository.BookingRepository;
import com.proj.slotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService{

    private static final Logger logger = LoggerFactory.getLogger(SlotServiceImpl.class);
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    public static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    @Override
    public List<SlotResponseDTO> getAvailableSlots(String userId, LocalDate date, Integer duration, String timezone) throws Exception {
        logger.info("[getAvailableSlots] userId={}, date={}, duration={}, timezone={}", userId, date, duration, timezone);

        String targetTimezone = (timezone != null && !timezone.isBlank()) ? timezone : DEFAULT_TIMEZONE;
        ZoneId targetZone;
        try{
            targetZone = ZoneId.of(targetTimezone);
        }
        catch (DateTimeException e){
            logger.warn("[getAvailableSlots] Invalid timezone: {}", timezone);
            throw new BadRequestException("Invalid timezone: "+timezone);
        }

        logger.info("[getAvailableSlots] Using target timezone: {}", targetZone);

        UserEntity user = userRepository.findById(userId).orElse(null);

        if(user == null){
            logger.warn("[getAvailableSlots] User not found: userId={}", userId);
            throw new UserNotFoundException("User not found with this id: "+ userId);
        }

        logger.info("[getAvailableSlots] User found: id={}, name={}", user.getId(), user.getName());
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        logger.info("[getAvailableSlots] Day of week for {}: {}", date, dayOfWeek);

        AvailabilityEntity availability = availabilityRepository.findByUserAndDayOfWeek(user, dayOfWeek);

        if(availability == null){
            logger.info("[getAvailableSlots] No availability set for user id={} on {}", userId, dayOfWeek);
            //User has no Availability for the day
            return new ArrayList<>();
        }

        logger.info("[getAvailableSlots] Availability found: startTime={}, endTime={}, slotDuration={}",
                availability.getStartTime(), availability.getEndTime(), availability.getSlotDurationMinutes());

        //DEBUGGING
//        System.out.println("DEBUG: availability.getSlotDurationMinutes() = " + availability.getSlotDurationMinutes());
//        System.out.println("DEBUG: duration parameter = " + duration);
//        System.out.println("DEBUG: slotDuration (final) = " + ((duration != null) ? duration : availability.getSlotDurationMinutes()));

        //Use guest's requested duration, or fall back to host's preference
        int slotDuration = (duration != null) ? duration : availability.getSlotDurationMinutes();
        logger.info("[getAvailableSlots] Using slot duration: {} minutes", slotDuration);

        //Validate the requested duration
        if (slotDuration < 15 || slotDuration > 120 || slotDuration % 15 != 0) {
            logger.warn("[getAvailableSlots] Invalid slot duration requested: {} minutes", slotDuration);
            throw new BadRequestException("Slot duration must be of each 15 mins and max 2 hours");
        }

        List<SlotResponseDTO> allSlots = generateSlots(availability, date, slotDuration);
        logger.info("[getAvailableSlots] Generated {} total slots", allSlots.size());

        List<BookingEntity> bookedSlots = bookingRepository.findBookedByOwner(userId);
        logger.info("[getAvailableSlots] Found {} booked slots for user", bookedSlots.size());

// Filter booked and past slots BEFORE converting to requester timezone
        List<SlotResponseDTO> availableSlots = allSlots.stream()
                .filter(slot -> !isSlotBooked(slot, bookedSlots))
                .filter(slot -> slot.getEndTime().toInstant().isAfter(Instant.now()))
                .toList();

        logger.info("[getAvailableSlots] Returning {} available slots out of {}", availableSlots.size(), allSlots.size());

// Convert available slots to requester's timezone
        ZoneId hostZone = ZoneId.of(availability.getUser().getTimezone());
        if (!targetZone.equals(hostZone)) {
            availableSlots = availableSlots.stream()
                    .map(slot -> SlotResponseDTO.builder()
                            .startTime(slot.getStartTime().withZoneSameInstant(targetZone))
                            .endTime(slot.getEndTime().withZoneSameInstant(targetZone))
                            .build())
                    .toList();
            logger.info("[getAvailableSlots] Converted slots from {} to {}", hostZone, targetZone);
        }

        return availableSlots;
    }

    private boolean isSlotBooked(SlotResponseDTO slot, List<BookingEntity> bookedSlots){
        for(BookingEntity booking : bookedSlots){

            Instant slotStart = slot.getStartTime().toInstant();
            Instant slotEnd = slot.getEndTime().toInstant();
            Instant bookingStart = booking.getStartTime();
            Instant bookingEnd = booking.getEndTime();

            if(slotStart.isBefore(bookingEnd)
            && slotEnd.isAfter(bookingStart)){
                logger.debug("[isSlotBooked] Slot {}-{} overlaps with booking {}-{} (bookingId={})",
                        slotStart, slotEnd,
                        bookingStart, bookingEnd, booking.getBookingId());
                return true;
            }
        }
        return false;
    }

//    private List<SlotResponseDTO> generateSlotsv1(AvailabilityEntity availability, LocalDate date, int duration){
//        List<SlotResponseDTO> slots = new ArrayList<>();
//
//        LocalDateTime slotStart = date.atTime(availability.getStartTime());
//        LocalDateTime slotEndLimit = date.atTime(availability.getEndTime());
//
//        logger.debug("[generateSlots] Generating slots from {} to {} with duration {} minutes",
//                slotStart, slotEndLimit, duration);
//
//
//        while(slotStart.isBefore(slotEndLimit)){
//            LocalDateTime nextSlot = slotStart.plusMinutes(duration);
//
//            if(nextSlot.isAfter(slotEndLimit)){
//                break;
//            }
//
//            slots.add(SlotResponseDTO.builder()
//                            .startTime(slotStart)
//                            .endTime(nextSlot)
//                            .build());
//
//            slotStart = nextSlot;
//        }
//
//        logger.debug("[generateSlots] Generated {} slots", slots.size());
//        return slots;
//    }

    private List<SlotResponseDTO> generateSlots(AvailabilityEntity availability, LocalDate date, int duration){
        List<SlotResponseDTO> slots = new ArrayList<>();

        ZoneId hostZone = ZoneId.of(availability.getUser().getTimezone());

        ZonedDateTime slotStart = ZonedDateTime.of(date, availability.getStartTime(), hostZone);

        ZonedDateTime slotEndLimit = ZonedDateTime.of(date, availability.getEndTime(), hostZone);

        logger.debug("[generateSlots] Generating slots from {} to {} with duration {} minutes", slotStart, slotEndLimit, duration);

        while(slotStart.isBefore(slotEndLimit)){
            ZonedDateTime nextSlot = slotStart.plusMinutes(duration);

            if(nextSlot.isAfter(slotEndLimit)){
                break;
            }

            slots.add(SlotResponseDTO.builder()
                    .startTime(slotStart)         // converted back to local date time
                    .endTime(nextSlot)            // converted back to local date time
                    .build());

            slotStart = nextSlot;
        }

        logger.debug("[generateSlots] Generated {} slots", slots.size());
        return slots;
    }
}
