package com.cabbooking.service.impl;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.mapper.BookingMapper;
import com.cabbooking.model.Booking;
import com.cabbooking.model.Cab;
import com.cabbooking.model.User;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CabRepository cabRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Cab cab;
    private Booking booking;
    private BookingRegistrationRequest bookingRequest;
    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        // Arrange: Setup common objects for tests
        User passengerUser = new User();
        passengerUser.setId(1L);
        passengerUser.setName("Passenger Pete");
        passengerUser.setEmail("pete@example.com");
        passengerUser.setPassword("password123");
        passengerUser.setPhone("1112223333");
        passengerUser.addRole(User.Role.USER);
    
        // 2. Create a User for the Driver
        User driverUser = new User();
        driverUser.setId(2L);
        driverUser.setName("Driver Dave");
        driverUser.setEmail("dave@example.com");
        driverUser.setPassword("password456");
        driverUser.setPhone("4445556666");
        driverUser.addRole(User.Role.DRIVER);

        cab = new Cab();
        cab.setId(1L);
        cab.setLicensePlateNumber("CAB-1234");
        cab.setVehicleType(Cab.VehicleType.SEDAN);
        cab.setStatus(Cab.AvailabilityStatus.AVAILABLE); // Set a realistic status for booking
        cab.setDriver(user); // Assign the complete 'user' object as the driver
        cab.setSeatingCapacity(4);

        booking = new Booking();
        booking.setId(1L);
        booking.setPassenger(passengerUser);
        booking.setDriver(driverUser);
        booking.setPickupLocation("Point A");
        booking.setDropoffLocation("Point B");
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // The request object sent by the client
        bookingRequest = new BookingRegistrationRequest();
        bookingRequest.setPassengerId(passengerUser.getId()); // Correctly set passenger ID
        bookingRequest.setPickupLocation("Point A");
        bookingRequest.setDropoffLocation("Point B");

         UserResponse passengerResponse = new UserResponse();
        passengerResponse.setId(passengerUser.getId());
        passengerResponse.setName(passengerUser.getName());

        UserResponse driverResponse = new UserResponse();
        driverResponse.setId(driverUser.getId());
        driverResponse.setName(driverUser.getName());
        
        // The response object sent back by the server
        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setPassenger(passengerResponse); // Set the UserResponse object
        bookingResponse.setDriver(driverResponse);       // Set the UserResponse object
        bookingResponse.setPickupLocation("Point A");
        bookingResponse.setDropoffLocation("Point B");
        bookingResponse.setStatus(Booking.BookingStatus.CONFIRMED.toString());

    }

}