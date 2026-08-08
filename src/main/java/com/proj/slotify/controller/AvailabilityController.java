package com.proj.slotify.controller;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;
import com.proj.slotify.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityController.class);
    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> setAvailability(@Valid @RequestBody AvailabilityRequestDTO dto) throws Exception{
        logger.info("[AvailabilityController] POST /api/v1/availability - day={}", dto.getDayOfWeek());

        AvailabilityResponseDTO responseDTO = availabilityService.setAvailability(dto);
        logger.info("[AvailabilityController] Availability created: id={}, day={}", responseDTO.getId(), responseDTO.getDay());

        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponseDTO>> getAvailability() throws Exception{
        logger.info("[AvailabilityController] GET /api/v1/availability");

        List<AvailabilityResponseDTO> responseDTO = availabilityService.getAvailability();
        logger.info("[AvailabilityController] Returning {} availability records", responseDTO.size());

        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> updateAvailability(@PathVariable String id, @Valid @RequestBody AvailabilityRequestDTO dto) throws Exception{
        logger.info("[AvailabilityController] PUT /api/v1/availability/{}", id);

        AvailabilityResponseDTO responseDTO = availabilityService.updateAvailability(id, dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable String id) throws Exception{
        logger.info("[AvailabilityController] DELETE /api/v1/availability/{}", id);

        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
