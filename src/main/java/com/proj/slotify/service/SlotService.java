package com.proj.slotify.service;

import com.proj.slotify.dto.SlotResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {
    List<SlotResponseDTO> getAvailableSlots(String userId, LocalDate date, Integer duration, String timezone) throws Exception;
}
