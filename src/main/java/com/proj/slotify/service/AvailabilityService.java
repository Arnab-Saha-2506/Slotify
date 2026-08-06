package com.proj.slotify.service;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;

import java.util.List;

public interface AvailabilityService {
    AvailabilityResponseDTO setAvailability(AvailabilityRequestDTO dto) throws Exception;
    List<AvailabilityResponseDTO> getAvailability() throws Exception;
    AvailabilityResponseDTO updateAvailability(String id, AvailabilityRequestDTO dto) throws Exception;
    void deleteAvailability(String id) throws Exception;
}
