package com.cabbooking.service;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingResponse createBooking(BookingRegistrationRequest request);

    Optional<BookingResponse> getBookingById(Long bookingId);

    List<BookingResponse> getBookingsByPassengerId(Long passengerId);

    List<BookingResponse> getBookingsByDriverId(Long driverId);

    /**
     * Updates the status of a booking.
     * @param bookingId The ID of the booking to update.
     * @param newStatus The new status for the booking.
     * @param userId The ID of the user performing the action (for authorization).
     * @return The updated booking details.
     */
    BookingResponse updateBookingStatus(Long bookingId, Booking.BookingStatus newStatus, Long userId);

    BookingResponse assignDriverToBooking(Long bookingId, Long driverId);

    BookingResponse cancelBooking(Long bookingId, Long userId);

    BookingResponse startRide(Long bookingId, Long driverId);

    BookingResponse completeRide(Long bookingId, Long driverId);

    BookingResponse updatePaymentDetails(Long bookingId, boolean paymentStatus, String paymentId);

    List<BookingResponse> findPendingBookingsForDriverAssignment();
}