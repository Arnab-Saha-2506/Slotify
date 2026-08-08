package com.proj.slotify.service;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<AvailabilityResponseDTO> setAvailability(List<AvailabilityRequestDTO> dtos) throws Exception;
    List<AvailabilityResponseDTO> getAvailability() throws Exception;
    AvailabilityResponseDTO updateAvailability(String id, AvailabilityRequestDTO dto, LocalDate date) throws Exception;
    List<AvailabilityResponseDTO> getAvailabilityByDate(LocalDate date) throws Exception;
    void deleteAvailability(String id) throws Exception;
}
