package com.proj.slotify.controller;

import com.proj.slotify.dto.BookingRequestDTO;
import com.proj.slotify.dto.BookingResponseDTO;
import com.proj.slotify.dto.MyBookingListResponseDTO;
import com.proj.slotify.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) throws Exception{
        logger.info("[BookingController] POST /api/v1/bookings - ownerId={}", dto.getOwnerId());
        BookingResponseDTO response = bookingService.createBooking(dto);
        logger.info("[BookingController] Booking created: bookingId={}, status={}", response.getBookingId(), response.getStatus());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MyBookingListResponseDTO>> getMyBookings() throws Exception{
        logger.info("[BookingController] GET /api/v1/bookings");
        List<MyBookingListResponseDTO> myBookings = bookingService.getMyBookings();
        logger.info("[BookingController] Returning {} bookings", myBookings.size());
        return ResponseEntity.ok().body(myBookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyBookingListResponseDTO> getBookingDetails(@PathVariable String id) throws Exception{
        logger.info("[BookingController] GET /api/v1/bookings/{}", id);
        MyBookingListResponseDTO bookings = bookingService.getBookingDetails(id);
        return ResponseEntity.ok().body(bookings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> cancelBookingById(@PathVariable String id) throws Exception{
        logger.info("[BookingController] DELETE /api/v1/bookings/{}", id);
        BookingResponseDTO bookings = bookingService.cancelBookingById(id);
        return ResponseEntity.ok().body(bookings);
    }
}
