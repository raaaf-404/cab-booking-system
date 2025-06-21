package com.cabbooking.service.impl;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.model.Booking;
import com.cabbooking.model.User.Role; // Import Role enum
import com.cabbooking.model.User;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.cabbooking.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    private BookingResponse convertToBookingResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setPassenger(userMapper.mapToUserResponse(booking.getPassenger())); // Use injected mapper
        response.setDriver(userMapper.mapToUserResponse(booking.getDriver()));    // Use injected mapper
        response.setPickupLocation(booking.getPickupLocation());
        response.setDropoffLocation(booking.getDropoffLocation());
        response.setPickupLatitude(booking.getPickupLatitude());
        response.setPickupLongitude(booking.getPickupLongitude());
        response.setDropoffLatitude(booking.getDropoffLatitude());
        response.setDropoffLongitude(booking.getDropoffLongitude());
        response.setDistance(booking.getDistance());
        response.setFare(booking.getFare());
        if (booking.getStatus() != null) {
            response.setStatus(booking.getStatus().name());
        }
        response.setScheduledTime(booking.getScheduledTime());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setPaymentStatus(booking.isPaymentStatus());
        response.setPaymentId(booking.getPaymentId());
        response.setNotes(booking.getNotes());
        return response;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRegistrationRequest request) {
        User passenger = userRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + request.getPassengerId()));

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setPickupLocation(request.getPickupLocation());
        booking.setDropoffLocation(request.getDropoffLocation());
        booking.setPickupLatitude(request.getPickupLatitude());
        booking.setPickupLongitude(request.getPickupLongitude());
        booking.setDropoffLatitude(request.getDropoffLatitude());
        booking.setDropoffLongitude(request.getDropoffLongitude());
        booking.setScheduledTime(request.getScheduledTime());
        booking.setNotes(request.getNotes());
        booking.setStatus(Booking.BookingStatus.PENDING);
        // Fare and distance calculation logic would typically go here or be triggered.
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(savedBooking);
    }

    @Override
    public Optional<BookingResponse> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(this::convertToBookingResponse);
    }

    @Override
    public List<BookingResponse> getBookingsByPassengerId(Long passengerId) {
        if (!userRepository.existsById(passengerId)) {
            throw new ResourceNotFoundException("Passenger not found with id: " + passengerId);
        }
        return bookingRepository.findByPassengerId(passengerId).stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByDriverId(Long driverId) {
        if (!userRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver not found with id: " + driverId);
        }
        return bookingRepository.findByDriverId(driverId).stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, Booking.BookingStatus newStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        String authenticatedUserEmail = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
        User requestingUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + authenticatedUserEmail));
                

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Basic state transition validation
        Booking.BookingStatus currentStatus = booking.getStatus();
        if (currentStatus == Booking.BookingStatus.COMPLETED || currentStatus == Booking.BookingStatus.CANCELLED ) {
            throw new IllegalStateException("Cannot update status for a booking that is already " + booking.getStatus());
        }

        boolean isAuthorized = false;

        if (requestingUser.getRole().contains(Role.ADMIN)) {
            isAuthorized = true;
        } else if (requestingUser.getRole().contains(Role.USER)) {
            // Passenger can cancel their own booking if it's in a cancellable state
            if (booking.getPassenger() != null && booking.getPassenger().getId().equals(requestingUser.getId())) {
                if (newStatus == Booking.BookingStatus.CANCELLED && booking.canBeCancelled()) {
                    isAuthorized = true;
                }
            }
        } else if (requestingUser.getRole().contains(Role.DRIVER)) {
            // Driver can reject an assigned booking
            if (newStatus == Booking.BookingStatus.REJECTED && booking.getDriver() != null && booking.getDriver().getId().equals(requestingUser.getId())) {
                isAuthorized = true;
            }
        }

        if (!isAuthorized) {
            throw new AccessDeniedException("User " + requestingUser.getId() + " is not authorized to update booking " + bookingId + " from " + currentStatus + " to status " + newStatus);
        }

        booking.setStatus(newStatus);
        booking.setUpdatedAt(LocalDateTime.now());
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse assignDriverToBooking(Long bookingId, Long driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        if (!driver.getRole().contains(Role.DRIVER)) { // Check if the Set<User.Role> contains the DRIVER enum constant
            throw new IllegalArgumentException("User with id " + driverId + " is not a DRIVER.");
        }
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
             throw new IllegalStateException("Driver can only be assigned to PENDING bookings. Current status: " + booking.getStatus());
        }

        booking.setDriver(driver);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        // Optionally update cab status:
        // if (driver.getAssignedCab() != null && cabService != null) {
        //    cabService.updateCabAvailabilityStatus(driver.getAssignedCab().getId(), Cab.AvailabilityStatus.BOOKED);
        // }
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        // TODO: Add authorization logic (e.g., passenger can cancel their own booking if PENDING/CONFIRMED)
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == Booking.BookingStatus.IN_PROGRESS || booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a booking that is " + booking.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        // Optionally update cab status if a driver was assigned:
        // if (booking.getDriver() != null && booking.getDriver().getAssignedCab() != null && cabService != null) {
        //    cabService.updateCabAvailabilityStatus(booking.getDriver().getAssignedCab().getId(), Cab.AvailabilityStatus.AVAILABLE);
        // }
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse startRide(Long bookingId, Long driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getDriver() == null || !booking.getDriver().getId().equals(driverId)) {
            throw new IllegalStateException("Booking not assigned to this driver or no driver assigned.");
        }
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Ride can only be started for CONFIRMED bookings. Current status: " + booking.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        booking.setStartTime(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse completeRide(Long bookingId, Long driverId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getDriver() == null || !booking.getDriver().getId().equals(driverId)) {
            throw new IllegalStateException("Booking not assigned to this driver or no driver assigned.");
        }
        if (booking.getStatus() != Booking.BookingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Ride can only be completed if IN_PROGRESS. Current status: " + booking.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        booking.setEndTime(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        // Optionally update cab status:
        // if (booking.getDriver() != null && booking.getDriver().getAssignedCab() != null && cabService != null) {
        //    cabService.updateCabAvailabilityStatus(booking.getDriver().getAssignedCab().getId(), Cab.AvailabilityStatus.AVAILABLE);
        // }
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse updatePaymentDetails(Long bookingId, boolean paymentStatus, String paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setPaymentStatus(paymentStatus);
        if (paymentId != null && !paymentId.isBlank()) {
            booking.setPaymentId(paymentId);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> findPendingBookingsForDriverAssignment() {
        // This assumes PENDING bookings are those needing a driver.
        // You might refine this to include CONFIRMED bookings with no driver.
        return bookingRepository.findByStatusAndDriverIsNull(Booking.BookingStatus.PENDING)
                .stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }
}