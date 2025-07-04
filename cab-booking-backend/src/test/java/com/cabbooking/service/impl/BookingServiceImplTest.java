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
import com.cabbooking.service.CabService;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
    private CabService cabService;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Cab cab;
    private Booking booking;
    private User passengerUser;
    private User driverUser;
    private BookingRegistrationRequest bookingRequest;
    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        // Arrange: Setup common objects for tests
        passengerUser = new User();
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

    @Test
    @DisplayName("Test Create Booking with valid data should succeed")
    void whenCreateBooking_withValidData_thenReturnsBookingResponse() {
        // Arrange
        // Note: We need a 'passenger' user object, which is created in our setUp()
        User passengerUser = new User();
        passengerUser.setId(1L); 
        
        given(userRepository.findById(1L)).willReturn(Optional.of(passengerUser));
        // The mapper will convert the request to the 'booking' entity
        given(bookingMapper.toBookingEntity(any(BookingRegistrationRequest.class))).willReturn(booking);
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        // The mapper will convert the 'booking' entity to the response
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);
    
        // Act
        // Call the correct method: createBooking
        BookingResponse savedBooking = bookingService.createBooking(bookingRequest);
    
        // Assert
        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isEqualTo(booking.getId());
        assertThat(savedBooking.getPickupLocation()).isEqualTo("Point A");
    }

    @Test
    @DisplayName("Test Create Booking with invalid user ID should fail")
    void whenCreateBooking_withInvalidUserId_thenThrowsResourceNotFoundException() {
    // Arrange
    // We tell the repository to find nothing for the given ID
    given(userRepository.findById(bookingRequest.getPassengerId())).willReturn(Optional.empty());

    // Act & Assert
    // We expect the service to throw a ResourceNotFoundException
    assertThrows(ResourceNotFoundException.class, () -> {
        bookingService.createBooking(bookingRequest);
    });

    // Verify that no mapping or saving occurred
    verify(bookingMapper, never()).toBookingEntity(any());
    verify(bookingRepository, never()).save(any());
   }

   @Test
   @DisplayName("Test Get Booking By valid ID should return booking")
   void whenGetBookingById_withValidId_thenReturnsBookingResponse() {
    // Arrange
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    // The method now returns a direct BookingResponse object
    BookingResponse foundBooking = bookingService.getBookingById(1L);

    // Assert
    // We can now assert directly on the returned object
    assertThat(foundBooking).isNotNull();
    assertThat(foundBooking.getId()).isEqualTo(1L);
    assertThat(foundBooking.getPassenger().getName()).isEqualTo("Passenger Pete");
   }

    @Test
    @DisplayName("Test Get Booking By invalid ID should throw ResourceNotFoundException")
    void whenGetBookingById_withInvalidId_thenThrowsResourceNotFoundException() {
    // Arrange
    // Mock the repository to return an empty Optional, simulating a not-found scenario
    given(bookingRepository.findById(1L)).willReturn(Optional.empty());

    // Act & Assert
    // Verify that calling the method now throws the expected exception
    assertThrows(ResourceNotFoundException.class, () -> {
        bookingService.getBookingById(1L);
    });

    // Also, verify the mapper was never used, as the process fails before mapping
    verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    @DisplayName("Test Get Bookings By valid Passenger ID should return booking list")
    void whenGetBookingsByPassengerId_withValidId_thenReturnsBookingResponseList() {
    // Arrange
    Long passengerId = 1L;
    // Assume the user exists
    given(userRepository.existsById(passengerId)).willReturn(true);
    // Mock the repository to return a list containing our test booking
    given(bookingRepository.findByPassengerId(passengerId)).willReturn(Collections.singletonList(booking));
    // Mock the mapper to convert the booking entity to a response DTO
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    List<BookingResponse> results = bookingService.getBookingsByPassengerId(passengerId);

    // Assert
    assertThat(results)
        .isNotNull()
        .hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(bookingResponse.getId());
    }

    @Test
    @DisplayName("Test Get Bookings By invalid Passenger ID should throw exception")
    void whenGetBookingsByPassengerId_withInvalidId_thenThrowsResourceNotFoundException() {
    // Arrange
    Long invalidPassengerId = 99L;
    // Mock the repository to indicate the user does not exist
    given(userRepository.existsById(invalidPassengerId)).willReturn(false);

    // Act & Assert
    // Verify that the expected exception is thrown
    assertThrows(ResourceNotFoundException.class, () -> {
        bookingService.getBookingsByPassengerId(invalidPassengerId);
    });

    // Verify that the booking repository was never queried, as the process failed early
    verify(bookingRepository, never()).findByPassengerId(anyLong());
    }

    @Test
    @DisplayName("Test Get Bookings By Passenger ID with no bookings should return empty list")
    void whenGetBookingsByPassengerId_withNoBookings_thenReturnsEmptyList() {
    // Arrange
    Long passengerId = 1L;
    // Assume the user exists
    given(userRepository.existsById(passengerId)).willReturn(true);
    // Mock the repository to return an empty list of bookings
    given(bookingRepository.findByPassengerId(passengerId)).willReturn(Collections.emptyList());

    // Act
    List<BookingResponse> results = bookingService.getBookingsByPassengerId(passengerId);

    // Assert
    // Verify that the returned list is not null but is empty
    assertThat(results)
        .isNotNull()
        .isEmpty();
        
    // Verify the mapper was never called since there was nothing to map
    verify(bookingMapper, never()).toBookingResponse(any());
    }

    @Test
    @DisplayName("Test Get Bookings By valid Driver ID should return booking list")
    void whenGetBookingsByDriverId_withValidId_thenReturnsBookingResponseList() {
    // Arrange
    Long driverId = 2L; // Using the driver's ID from our setUp method
    // Assume the driver user exists
    given(userRepository.existsById(driverId)).willReturn(true);
    // Mock the repository to return a list with our test booking
    given(bookingRepository.findByDriverId(driverId)).willReturn(Collections.singletonList(booking));
    // Mock the mapper to handle the conversion
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    List<BookingResponse> results = bookingService.getBookingsByDriverId(driverId);

    // Assert
    assertThat(results)
        .isNotNull()
        .hasSize(1);
    assertThat(results.get(0).getDriver().getId()).isEqualTo(driverId);
    }

    @Test
    @DisplayName("Test Get Bookings By invalid Driver ID should throw exception")
    void whenGetBookingsByDriverId_withInvalidId_thenThrowsResourceNotFoundException() {
    // Arrange
    Long invalidDriverId = 99L;
    // Mock the user repository to indicate the driver does not exist
    given(userRepository.existsById(invalidDriverId)).willReturn(false);

    // Act & Assert
    // Verify that the correct exception is thrown
    assertThrows(ResourceNotFoundException.class, () -> {
        bookingService.getBookingsByDriverId(invalidDriverId);
    });

    // Verify the booking repository was never called because the check failed first
    verify(bookingRepository, never()).findByDriverId(anyLong());
    }

    @Test
    @DisplayName("Test Get Bookings By Driver ID with no bookings should return empty list")
    void whenGetBookingsByDriverId_withNoBookings_thenReturnsEmptyList() {
    // Arrange
    Long driverId = 2L;
    // Assume the driver user exists
    given(userRepository.existsById(driverId)).willReturn(true);
    // Mock the booking repository to return an empty list
    given(bookingRepository.findByDriverId(driverId)).willReturn(Collections.emptyList());

    // Act
    List<BookingResponse> results = bookingService.getBookingsByDriverId(driverId);

    // Assert
    // Verify the list is not null and is empty
    assertThat(results)
        .isNotNull()
        .isEmpty();

    // Verify the mapper was never called since there were no bookings to map
    verify(bookingMapper, never()).toBookingResponse(any());
    }

    @Test
    @DisplayName("Test Update Status to IN_PROGRESS should update cab to IN_RIDE")
    void whenUpdateStatus_toInProgress_thenUpdatesCabStatus() {
    // Arrange
    // Ensure the booking has a driver so the cab logic is triggered
    booking.setDriver(driverUser); 
    
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
    given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));
    given(bookingRepository.save(any(Booking.class))).willReturn(booking);
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    bookingService.updateBookingStatus(1L, Booking.BookingStatus.IN_PROGRESS);

    // Assert
    // Verify the booking's status was set correctly
    assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.IN_PROGRESS);
    
    // Verify the cabService was called with the correct new status for the cab
    verify(cabService).updateCabAvailabilityStatus(cab.getId(), Cab.AvailabilityStatus.IN_RIDE);
    verify(bookingRepository).save(booking); // Ensure the booking was saved
    }
}