package com.cabbooking.service;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRegistrationRequest request);

    BookingResponse getBookingById(Long bookingId);

    List<BookingResponse> getBookingsByPassengerId(Long passengerId);

    List<BookingResponse> getBookingsByDriverId(Long driverId);

    BookingResponse updateBookingStatus(Long bookingId, Booking.BookingStatus newStatus);

    BookingResponse assignDriverToBooking(Long bookingId, Long driverId);

    BookingResponse cancelBooking(Long bookingId);

    BookingResponse startRide(Long bookingId, Long driverId);

    BookingResponse completeRide(Long bookingId, Long driverId);

    BookingResponse updatePaymentDetails(Long bookingId, boolean paymentStatus, String paymentId);

    List<BookingResponse> findPendingBookingsForDriverAssignment();
}