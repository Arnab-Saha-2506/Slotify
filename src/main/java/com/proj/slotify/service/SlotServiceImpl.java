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
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService{

    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<SlotResponseDTO> getAvailableSlots(String userId, LocalDate date, Integer duration) throws Exception {
        UserEntity user = userRepository.findById(userId).orElse(null);

        if(user == null){
            throw new UserNotFoundException("User not found with this id: "+ userId);
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        AvailabilityEntity availability = availabilityRepository.findByUserAndDayOfWeek(user, dayOfWeek);

        if(availability == null){
            //User has no Availability for the day
            return new ArrayList<>();
        }

        //DEBUGGING
//        System.out.println("DEBUG: availability.getSlotDurationMinutes() = " + availability.getSlotDurationMinutes());
//        System.out.println("DEBUG: duration parameter = " + duration);
//        System.out.println("DEBUG: slotDuration (final) = " + ((duration != null) ? duration : availability.getSlotDurationMinutes()));

        //Use guest's requested duration, or fall back to host's preference
        int slotDuration = (duration != null) ? duration : availability.getSlotDurationMinutes();

        //Validate the requested duration
        if (slotDuration < 15 || slotDuration > 120 || slotDuration % 15 != 0) {
            throw new BadRequestException("Slot duration must be of each 15 mins and max 2 hours");
        }

        List<SlotResponseDTO> allSlots = generateSlots(availability, date, slotDuration);

        List<BookingEntity> bookedSlots = bookingRepository.findBookedByOwner(userId);

        return allSlots.stream()
                .filter(slot -> !isSlotBooked(slot, bookedSlots))
                .toList();
    }

    private boolean isSlotBooked(SlotResponseDTO slot, List<BookingEntity> bookedSlots){
        for(BookingEntity booking : bookedSlots){
            if(slot.getStartTime().isBefore(booking.getEndTime())
            && slot.getEndTime().isAfter(booking.getStartTime())){
                return true;
            }
        }
        return false;
    }

    private List<SlotResponseDTO> generateSlots(AvailabilityEntity availability, LocalDate date, int duration){
        List<SlotResponseDTO> slots = new ArrayList<>();

        LocalDateTime slotStart = date.atTime(availability.getStartTime());
        LocalDateTime slotEndLimit = date.atTime(availability.getEndTime());

        while(slotStart.isBefore(slotEndLimit)){
            LocalDateTime nextSlot = slotStart.plusMinutes(duration);

            if(nextSlot.isAfter(slotEndLimit)){
                break;
            }

            slots.add(SlotResponseDTO.builder()
                            .startTime(slotStart)
                            .endTime(nextSlot)
                            .build());

            slotStart = nextSlot;
        }
        return slots;
    }
}
