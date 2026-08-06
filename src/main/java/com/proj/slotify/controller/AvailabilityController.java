package com.proj.slotify.controller;

import com.proj.slotify.dto.AvailabilityRequestDTO;
import com.proj.slotify.dto.AvailabilityResponseDTO;
import com.proj.slotify.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> setAvailability(@Valid @RequestBody AvailabilityRequestDTO dto) throws Exception{
        AvailabilityResponseDTO responseDTO = availabilityService.setAvailability(dto);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponseDTO>> getAvailability() throws Exception{
        List<AvailabilityResponseDTO> responseDTO = availabilityService.getAvailability();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> updateAvailability(@PathVariable String id, @Valid @RequestBody AvailabilityRequestDTO dto) throws Exception{
        AvailabilityResponseDTO responseDTO = availabilityService.updateAvailability(id, dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable String id) throws Exception{
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
