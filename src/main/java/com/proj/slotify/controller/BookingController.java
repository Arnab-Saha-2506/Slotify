package com.proj.slotify.controller;

import com.proj.slotify.dto.BookingRequestDTO;
import com.proj.slotify.dto.BookingResponseDTO;
import com.proj.slotify.dto.MyBookingListResponseDTO;
import com.proj.slotify.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) throws Exception{
        BookingResponseDTO response = bookingService.createBooking(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MyBookingListResponseDTO>> getMyBookings() throws Exception{
        List<MyBookingListResponseDTO> myBookings = bookingService.getMyBookings();
        return ResponseEntity.ok().body(myBookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyBookingListResponseDTO> getBookingDetails(@PathVariable String id) throws Exception{
        MyBookingListResponseDTO bookings = bookingService.getBookingDetails(id);
        return ResponseEntity.ok().body(bookings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> cancelBookingById(@PathVariable String id) throws Exception{
        BookingResponseDTO bookings = bookingService.cancelBookingById(id);
        return ResponseEntity.ok().body(bookings);
    }
}
