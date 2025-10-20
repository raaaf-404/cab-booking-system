package com.cabbooking.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.cabbooking.repository
        .BookingRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.model.User;
import com.cabbooking.model.Booking;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;


@Service("bookingSecurityService")
@RequiredArgsConstructor
public class BookingSecurityService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CabRepository cabRepository;

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

     /**
     * Validates if a driver can be assigned to a booking.
     *
     * @param bookingId The ID of the booking.
     * @param driverId  The ID of the driver.
     */
    public void validateDriverAssignment(Long bookingId, Long driverId) {
        //Fetch Records
        Booking booking = getBooking(bookingId);
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        //Validation
        if (!driver.getRole().contains(User.Role.DRIVER)) {
            throw new IllegalArgumentException("User with id " + driverId + " is not a DRIVER.");
        }
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalStateException("Driver can only be assigned to PENDING bookings. Current status: " + booking.getStatus());
        }

        //Validate Cab Status
        Cab cab = cabRepository.findByDriver(driver)
            .orElseThrow(() -> new ResourceNotFoundException("driver with id " + driverId + " does not have an assigned cab"));

        if(cab.getStatus() != Cab.AvailabilityStatus.AVAILABLE) {
                throw new IllegalStateException("The assigned driver is not currently available. Status: " + cab.getStatus());
        }
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

    public void validateStatusChange (Long bookingId, Booking.BookingStatus newStatus) {
        Booking booking = getBooking(bookingId);
        Booking.BookingStatus currentStatus = booking.getStatus();

        // Prevent updates on terminal states
        if (currentStatus == Booking.BookingStatus.COMPLETED || currentStatus == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a booking that is already " + currentStatus);
        }

        // Add specific transition rules if needed
        if (newStatus == Booking.BookingStatus.COMPLETED && currentStatus != Booking.BookingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Booking must be IN_PROGRESS to be marked as COMPLETED. Current status: " + currentStatus);
        }
    }
}
