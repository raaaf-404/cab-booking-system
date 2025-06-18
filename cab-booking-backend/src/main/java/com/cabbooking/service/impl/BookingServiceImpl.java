package com.cabbooking.service.impl;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.model.Booking;
import com.cabbooking.model.User;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    // private final CabService cabService; // Inject if cab status updates are needed

    private UserResponse mapUserToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        if (user.getRole() != null) {
            userResponse.setRole(user.getRole().name());
        }
        userResponse.setCreatedAt(user.getCreatedAt());
        // Add other fields as necessary from your UserResponse DTO
        return userResponse;
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setPassenger(mapUserToUserResponse(booking.getPassenger()));
        response.setDriver(mapUserToUserResponse(booking.getDriver()));
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
    public BookingResponse updateBookingStatus(Long bookingId, Booking.BookingStatus newStatus, Long userId) {
        // TODO: Add authorization logic based on userId
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Basic state transition validation
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update status for a booking that is already " + booking.getStatus());
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

        if (driver.getRole() != User.Role.DRIVER) {
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
        return bookingRepository.findByStatusInAndDriverIsNull(List.of(Booking.BookingStatus.PENDING))
                .stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }
}