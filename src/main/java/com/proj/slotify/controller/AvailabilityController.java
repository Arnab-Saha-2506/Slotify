package com.proj.slotify.controller;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;
import com.proj.slotify.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityController.class);
    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<List<AvailabilityResponseDTO>> setAvailability(@Valid @RequestBody List<AvailabilityRequestDTO> dtos) throws Exception{
        logger.info("[AvailabilityController] POST /api/v1/availability - batch of {} records", dtos.size());

        List<AvailabilityResponseDTO> responseDTOs = availabilityService.setAvailability(dtos);
        logger.info("[AvailabilityController] Availability created: {} records", responseDTOs.size());

        return ResponseEntity.status(201).body(responseDTOs);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponseDTO>> getAvailability() throws Exception{
        logger.info("[AvailabilityController] GET /api/v1/availability");

        List<AvailabilityResponseDTO> responseDTO = availabilityService.getAvailability();
        logger.info("[AvailabilityController] Returning {} availability records", responseDTO.size());

        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> updateAvailability(@PathVariable String id,
                                                                      @Valid @RequestBody AvailabilityRequestDTO dto,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception{
        logger.info("[AvailabilityController] PUT /api/v1/availability/{} with date={}", id, date);

        AvailabilityResponseDTO responseDTO = availabilityService.updateAvailability(id, dto, date);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable String id) throws Exception{
        logger.info("[AvailabilityController] DELETE /api/v1/availability/{}", id);

        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
