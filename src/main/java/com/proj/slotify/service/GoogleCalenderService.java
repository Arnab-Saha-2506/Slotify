package com.proj.slotify.service;

import com.proj.slotify.entity.BookingEntity;

public interface GoogleCalenderService {
    void createBookingEvent(BookingEntity booking);
}
