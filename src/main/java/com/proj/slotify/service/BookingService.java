package com.proj.slotify.service;

import com.proj.slotify.dto.BookingRequestDTO;
import com.proj.slotify.dto.BookingResponseDTO;
import com.proj.slotify.dto.MyBookingListResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO dto) throws Exception;
    List<MyBookingListResponseDTO> getMyBookings() throws Exception;
    MyBookingListResponseDTO getBookingDetails(String id) throws Exception;
    BookingResponseDTO cancelBookingById(String id) throws Exception;
}
