package com.cabbooking.service.impl;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateAvailabilityStatusRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.model.Booking;
import com.cabbooking.model.User.Role;
import com.cabbooking.model.Cab;
import com.cabbooking.model.User;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.mapper.BookingMapper;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.security.BookingSecurityService;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.service.BookingService;
import com.cabbooking.service.CabService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final CabRepository cabRepository;
    private final CabService cabService;
    private final BookingSecurityService bookingSecurityService;

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
        return bookingMapper.toBookingResponse(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(bookingMapper::toBookingResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    @Override
    public List<BookingResponse> getBookingsByPassengerId(Long passengerId) {
        if (!userRepository.existsById(passengerId)) {
            throw new ResourceNotFoundException("Passenger not found with id: " + passengerId);
        }
        return bookingRepository.findByPassengerId(passengerId).stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByDriverId(Long driverId) {
        if (!userRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver not found with id: " + driverId);
        }
        return bookingRepository.findByDriverId(driverId).stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize(
        "(hasRole('ADMIN') or " +
        // Passenger-specific rules
        "(#newStatus == T(com.cabbooking.model.Booking.BookingStatus).CANCELLED and " +
        " @bookingSecurityService.isPassengerOfBooking(authentication, #bookingId)) or " +
        // Driver-specific rules
        "((#newStatus == T(com.cabbooking.model.Booking.BookingStatus).REJECTED or " +
        "  #newStatus == T(com.cabbooking.model.Booking.BookingStatus).IN_PROGRESS or " +
        "  #newStatus == T(com.cabbooking.model.Booking.BookingStatus).COMPLETED) and " +
        " @bookingSecurityService.isDriverOfBooking(authentication, #bookingId)))" 

    )
    public BookingResponse updateBookingStatus(Long bookingId, Booking.BookingStatus newStatus) {
        
        bookingSecurityService.validateStatusChange(bookingId, newStatus);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(newStatus);
        booking.setUpdatedAt(LocalDateTime.now());

        // Update cab status
        User driver = booking.getDriver();
        if (driver != null) {
            cabRepository.findByDriver(driver).ifPresent(cab -> {
                Cab.AvailabilityStatus newCabStatus = null;
                switch(newStatus) {
                    case IN_PROGRESS:
                    newCabStatus = Cab.AvailabilityStatus.IN_RIDE;
                        break;
                    case COMPLETED:
                    case CANCELLED:
                    case REJECTED:
                    newCabStatus = Cab.AvailabilityStatus.AVAILABLE;
                        break;
                    default:
                       break;
                }
    
                if (newCabStatus !=null) {
                    CabUpdateAvailabilityStatusRequest request = new CabUpdateAvailabilityStatusRequest();
                    request.setStatus(newCabStatus);
                    cabService.updateCabAvailabilityStatus(cab.getId(), request);
                }
           });
        }
        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')") // Example of securing the method
    public BookingResponse assignDriverToBooking(Long bookingId, Long driverId) {
        // Centralized validation call
        bookingSecurityService.validateDriverAssignment(bookingId, driverId);

        //Fetch Records
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        Cab cab = cabRepository.findByDriver(driver)
            .orElseThrow(() -> new ResourceNotFoundException("driver with id " + driverId + " does not have an assigned cab"));


        //Update Booking
        booking.setDriver(driver);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());

        //Update Cab Status
        CabUpdateAvailabilityStatusRequest statusRequest = new CabUpdateAvailabilityStatusRequest();
        statusRequest.setStatus(Cab.AvailabilityStatus.BOOKED);
        cabService.updateCabAvailabilityStatus(cab.getId(), statusRequest);

        //Return BookingResponse
        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("You must logged in to cancel a booking");
        }

        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUserEmail));
        
         //Authorization Check:
         // Allow cancellation if the user is an ADMIN or is the passenger who owns the booking.
         boolean isOwner = booking.getPassenger().getId().equals(currentUser.getId());
         boolean isAdmin = currentUser.getRole().contains(User.Role.ADMIN);

         if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to cancel this booking.");
         }

         //Business Rule Check:
         // Checking if booking is in a cancellable state
         if(!booking.canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled in its current state: " + booking.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        
       User driver = booking.getDriver();
       if (driver != null) {
            cabRepository.findByDriver(driver).ifPresent(cab -> {
                CabUpdateAvailabilityStatusRequest statusRequest = new CabUpdateAvailabilityStatusRequest();
                statusRequest.setStatus(Cab.AvailabilityStatus.AVAILABLE);
                cabService.updateCabAvailabilityStatus(cab.getId(), statusRequest);
            });
       }

        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
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
        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse completeRide(Long bookingId, Long driverId) {
        //Fetch booking record
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User driver = booking.getDriver();
        //Validation
        if (driver == null || !driver.getId().equals(driverId)) {
            throw new IllegalStateException("Booking not assigned to this driver or no driver assigned.");
        }
        if (booking.getStatus() != Booking.BookingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Ride can only be completed if IN_PROGRESS. Current status: " + booking.getStatus());
        }

        //Update Booking
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        booking.setEndTime(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        //Update Cab Status
        cabRepository.findByDriver(driver).ifPresent(cab -> {
        CabUpdateAvailabilityStatusRequest statusRequest = new CabUpdateAvailabilityStatusRequest();
        statusRequest.setStatus(Cab.AvailabilityStatus.AVAILABLE);
        cabService.updateCabAvailabilityStatus(cab.getId(), statusRequest);
        });
        
        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
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
        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> findPendingBookingsForDriverAssignment() {

        List<Booking.BookingStatus> statuses = List.of(Booking.BookingStatus.PENDING, Booking.BookingStatus.CONFIRMED);
        return bookingRepository.findByStatusInAndDriverIsNull(statuses)
                .stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }
}