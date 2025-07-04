package com.cabbooking.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.model.User;
import com.cabbooking.model.Booking;
import com.cabbooking.exception.ResourceNotFoundException;


@Service("bookingSecurityService")
public class BookingSecurityService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // Constructor injection
    public BookingSecurityService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Checks if the authenticated user is the passenger for the given booking.
     */
    public boolean isPassengerOfBooking(Authentication authentication, Long bookingId) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = getBooking(bookingId);
        return booking.getPassenger() != null && booking.getPassenger().getId().equals(user.getId());
    }

    /**
     * Checks if the authenticated user is the driver for the given booking.
     */
    public boolean isDriverOfBooking(Authentication authentication, Long bookingId) {
        User user = getUserFromAuthentication(authentication);
        Booking booking = getBooking(bookingId);
        return booking.getDriver() != null && booking.getDriver().getId().equals(user.getId());
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    public boolean isValidStatusChange(Long bookingId, Booking.BookingStatus newStatus) {
        Booking booking = getBooking(bookingId);
        Booking.BookingStatus currentStatus = booking.getStatus();

        // Prevent updates on terminal states
        if (currentStatus == Booking.BookingStatus.COMPLETED || currentStatus == Booking.BookingStatus.CANCELLED) {
            return false;
        }

        // Add specific transition rules if needed
        if (newStatus == Booking.BookingStatus.COMPLETED) {
            return currentStatus == Booking.BookingStatus.IN_PROGRESS;
        }

        // Default to allow other transitions for simplicity
        return true;
    }
}
