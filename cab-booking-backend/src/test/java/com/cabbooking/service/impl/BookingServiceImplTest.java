package com.cabbooking.service.impl;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateAvailabilityStatusRequest;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;

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

    private Cab cab;
    private Booking booking;
    private Booking bookingPending;
    private User passengerUser;
    private User driverUser;
    private BookingRegistrationRequest bookingRequest;
    private BookingResponse bookingResponse;
    private BookingResponse bookingPendingResponse;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Arrange: Set up common objects for tests
        passengerUser = new User();
        passengerUser.setId(1L);
        passengerUser.setName("Passenger Pete");
        passengerUser.setEmail("pete@example.com");
        passengerUser.setPassword("password123");
        passengerUser.setPhone("1112223333");
        passengerUser.addRole(User.Role.USER);
    
        // 2. Create a User for the Driver
        driverUser = new User();
        driverUser.setId(2L);
        driverUser.setName("Driver Dave");
        driverUser.setEmail("dave@example.com");
        driverUser.setPassword("password456");
        driverUser.setPhone("4445556666");
        driverUser.addRole(User.Role.DRIVER);

        adminUser = new User();
        adminUser.setId(99L); // Use a distinct ID
        adminUser.setName("AdminUser");
        adminUser.setEmail("admin@example.com");
        passengerUser.setPassword("password123");
        passengerUser.setPhone("1112223333");
        adminUser.setRole(Collections.singleton(User.Role.ADMIN));

        cab = new Cab();
        cab.setId(1L);
        cab.setLicensePlateNumber("CAB-1234");
        cab.setVehicleType(Cab.VehicleType.SEDAN);
        cab.setStatus(Cab.AvailabilityStatus.AVAILABLE); // Set a realistic status for booking
        cab.setDriver(driverUser); // Assign the complete 'driverUser' object as the driver
        cab.setSeatingCapacity(4);

        booking = new Booking();
        booking.setId(1L);
        booking.setPassenger(passengerUser);
        booking.setDriver(driverUser);
        booking.setPickupLocation("Point A");
        booking.setDropoffLocation("Point B");
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        bookingPending = new Booking();
        bookingPending.setId(1L);
        bookingPending.setPassenger(passengerUser);
        bookingPending.setDriver(null);
        bookingPending.setPickupLocation("Point A");
        bookingPending.setDropoffLocation("Point B");
        bookingPending.setStatus(Booking.BookingStatus.PENDING);

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


        bookingPendingResponse = new BookingResponse();
        bookingPendingResponse.setId(1L);
        bookingPendingResponse.setPassenger(passengerResponse);
        bookingPendingResponse.setDriver(null);
        bookingPendingResponse.setPickupLocation("Point A");
        bookingPendingResponse.setDropoffLocation("Point B");
        bookingPendingResponse.setStatus(Booking.BookingStatus.PENDING.toString());
        
        // The response object sent back by the server
        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setPassenger(passengerResponse);
        bookingResponse.setDriver(driverResponse);
        bookingResponse.setPickupLocation("Point A");
        bookingResponse.setDropoffLocation("Point B");
        bookingResponse.setStatus(Booking.BookingStatus.CONFIRMED.toString());
    }

        @Test
        @DisplayName("Test Create Booking with valid data should succeed")
        void whenCreateBooking_withValidData_thenReturnsBookingResponse() {
            // Arrange
            given(userRepository.findById(bookingRequest.getPassengerId())).willReturn(Optional.of(passengerUser));
            given(bookingMapper.toBookingEntity(bookingRequest)).willReturn(bookingPending);
            // The mapper will convert the request to the 'booking' entity
            given(bookingRepository.save(any(Booking.class))).willReturn(bookingPending);
            // The mapper will convert the 'booking' entity to the response
            given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingPendingResponse);
        
            // Act
            // Call the correct method: createBooking
            BookingResponse savedBooking = bookingService.createBooking(bookingRequest);
        
            // Assert
            assertThat(savedBooking).isNotNull();
            assertThat(savedBooking.getId()).isEqualTo(bookingPendingResponse.getId());
            assertThat(savedBooking.getPickupLocation()).isEqualTo("Point A");
            assertThat(savedBooking.getDropoffLocation()).isEqualTo("Point B");
            assertThat(savedBooking.getStatus()).isEqualTo(Booking.BookingStatus.PENDING.toString());
            assertThat(savedBooking.getDriver()).isNull();
            
            verify(bookingRepository, times(1)).save(any(Booking.class));
        }

    @Test
    @DisplayName("Test Create Booking with invalid user ID should fail")
    void whenCreateBooking_withInvalidUserId_thenThrowsResourceNotFoundException() {
    // Arrange
    // We tell the repository to find nothing for the given ID
    given(userRepository.findById(bookingRequest.getPassengerId())).willReturn(Optional.empty());

    // Act & Assert
    // We expect the service to throw a ResourceNotFoundException
    ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> bookingService.createBooking(bookingRequest)
    );

    String expectedMessage = "Passenger not found with id: " + bookingRequest.getPassengerId();
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);

    // Verify that no mapping or saving occurred
    verify(bookingMapper, never()).toBookingEntity(any(BookingRegistrationRequest.class));
    verify(bookingRepository, never()).save(any(Booking.class));
    verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
   }

    @Test
    @DisplayName("Test Create Booking when database save fails")
    void whenCreateBooking_andRepositorySaveFails_thenThrowsException() {
        // Arrange
        String errorMessage = "Failed to save booking";
        given(userRepository.findById(bookingRequest.getPassengerId())).willReturn(Optional.of(passengerUser));
        given(bookingMapper.toBookingEntity(bookingRequest)).willReturn(bookingPending);
        given(bookingRepository.save(any(Booking.class)))
                .willThrow(new DataIntegrityViolationException(errorMessage));

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        // Assert that the message is correct
        assertThat(exception.getMessage()).isEqualTo(errorMessage);

        // Verify that the save method was attempted exactly one time
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    @DisplayName("Test Get Booking By valid ID should return booking")
    void whenGetBookingById_withValidId_thenReturnsBookingResponse() {
        // Arrange
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

        // Act
        BookingResponse foundBooking = bookingService.getBookingById(booking.getId()); // Use booking.getId() here

        // Assert
        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(bookingResponse.getId()); // Assert against bookingResponse's ID
        assertThat(foundBooking.getPassenger().getName()).isEqualTo(bookingResponse.getPassenger().getName());
        assertThat(foundBooking.getPickupLocation()).isEqualTo(bookingResponse.getPickupLocation());
        assertThat(foundBooking.getDropoffLocation()).isEqualTo(bookingResponse.getDropoffLocation());
        assertThat(foundBooking.getStatus()).isEqualTo(bookingResponse.getStatus());

        // Verify
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingMapper, times(1)).toBookingResponse(booking);
    }

    @Test
    @DisplayName("Test Get Booking By invalid ID should throw ResourceNotFoundException")
    void whenGetBookingById_withInvalidId_thenThrowsResourceNotFoundException() {
    // Arrange
    given(bookingRepository.findById(booking.getId())).willReturn(Optional.empty());

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(booking.getId()));

    //Verify
    verify(bookingRepository, times(1)).findById(booking.getId());
    verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    @DisplayName("Test Get Bookings By valid Passenger ID should return booking list")
    void whenGetBookingsByPassengerId_withValidId_thenReturnsBookingResponseList() {
    // Arrange
    Long passengerId = passengerUser.getId();
    given(userRepository.existsById(passengerId)).willReturn(true);
    given(bookingRepository.findByPassengerId(passengerId)).willReturn(Collections.singletonList(booking));
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    List<BookingResponse> results = bookingService.getBookingsByPassengerId(passengerId);

    // Assert
    assertThat(results)
        .isNotNull()
        .hasSize(1);

    assertThat(results.get(0).getId()).isEqualTo(bookingResponse.getId());
    assertThat(results.get(0).getPassenger().getId()).isEqualTo(bookingResponse.getPassenger().getId());
    assertThat(results.get(0).getPickupLocation()).isEqualTo(bookingResponse.getPickupLocation());
    assertThat(results.get(0).getDropoffLocation()).isEqualTo(bookingResponse.getDropoffLocation());
    assertThat(results.get(0).getStatus()).isEqualTo(bookingResponse.getStatus());

    //Verify
    verify(userRepository, times(1)).existsById(passengerId);
    verify(bookingRepository, times(1)).findByPassengerId(passengerId);
    verify(bookingMapper, times(1)).toBookingResponse(booking);
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
    assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingsByPassengerId(invalidPassengerId));

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
    assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingsByDriverId(invalidDriverId));

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
        booking.setDriver(driverUser);
        given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
        given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

        // Act
        bookingService.updateBookingStatus(1L, Booking.BookingStatus.IN_PROGRESS);

        // Assert
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.IN_PROGRESS);

        ArgumentCaptor<CabUpdateAvailabilityStatusRequest> captor = ArgumentCaptor.forClass(CabUpdateAvailabilityStatusRequest.class);
        verify(cabService).updateCabAvailabilityStatus(eq(cab.getId()), captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Cab.AvailabilityStatus.IN_RIDE);

        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Test Update Status to COMPLETED should update cab to AVAILABLE")
    void whenUpdateStatus_toCompleted_thenUpdatesCabStatusToAvailable() {
        // Arrange
        booking.setDriver(driverUser);
        given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
        given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

        // Act
        bookingService.updateBookingStatus(1L, Booking.BookingStatus.COMPLETED);

        // Assert
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.COMPLETED);

        ArgumentCaptor<CabUpdateAvailabilityStatusRequest> captor = ArgumentCaptor.forClass(CabUpdateAvailabilityStatusRequest.class);
        verify(cabService).updateCabAvailabilityStatus(eq(cab.getId()), captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Cab.AvailabilityStatus.AVAILABLE);

        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Test Update Status for booking with no driver should not fail")
    void whenUpdateStatus_withNoDriver_thenSkipsCabUpdate() {
    // Arrange
    // to Ensure the booking has no driver
    booking.setDriver(null); 
    
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
    given(bookingRepository.save(any(Booking.class))).willReturn(booking);
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    bookingService.updateBookingStatus(1L, Booking.BookingStatus.CANCELLED);

    // Assert
    assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CANCELLED);
    
    // Verify that the cab service was NEVER called because there was no driver
    verify(cabService, never()).updateCabAvailabilityStatus(any(), any(CabUpdateAvailabilityStatusRequest.class));
    verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Test Assign Driver To Booking should succeed")
    void whenAssignDriverToBooking_withValidData_thenReturnsBookingResponse() {
    // Arrange
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
    given(userRepository.findById(2L)).willReturn(Optional.of(driverUser));
    given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));
    given(bookingRepository.save(any(Booking.class))).willReturn(booking);
    given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

    // Act
    BookingResponse updatedBooking = bookingService.assignDriverToBooking(1L, 2L);

    // Assert
    assertThat(updatedBooking).isNotNull();
    assertThat(updatedBooking.getStatus()).isEqualTo("CONFIRMED");
    verify(cabService).updateCabAvailabilityStatus(eq(cab.getId()), any(CabUpdateAvailabilityStatusRequest.class));
    }

    @Test
    @DisplayName("Test Assign Driver To Booking with invalid booking id")
    void whenAssignDriverToBooking_withInvalidBookingId_thenThrowsResourceNotFoundException() {
    // Arrange
    given(bookingRepository.findById(1L)).willReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> bookingService.assignDriverToBooking(1L, 2L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Booking not found with id: 1");
    }

    @Test
    @DisplayName("Test Assign Driver To Booking with invalid driver id")
    void whenAssignDriverToBooking_withInvalidDriverId_thenThrowsResourceNotFoundException() {
    // Arrange
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
    given(userRepository.findById(2L)).willReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> bookingService.assignDriverToBooking(1L, 2L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Driver not found with id: 2");
    }

    @Test
    @DisplayName("Test Assign Driver To Booking with a user who is not a driver")
    void whenAssignDriverToBooking_withUserWhoIsNotADriver_thenThrowsIllegalArgumentException() {
        // Arrange
        // 1. Create a user with the 'USER' role, which is not a 'DRIVER'
        User nonDriverUser = new User();
        nonDriverUser.setId(2L);
        // Assign the USER role, which is a valid, non-driver role in your system
        nonDriverUser.setRole(Collections.singleton(User.Role.USER));
    
        // 2. Mock the booking repository to return a PENDING booking
        given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
        booking.setStatus(Booking.BookingStatus.PENDING); // Ensure booking is pending for the validation to proceed
    
        // 3. Mock the user repository to return our non-driver user
        given(userRepository.findById(2L)).willReturn(Optional.of(nonDriverUser));
    
        // Act & Assert
        // The service should now correctly identify that this user is not a driver
        // and throw the exception from within your validateDriverAssignment method.
        assertThatThrownBy(() -> bookingService.assignDriverToBooking(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with id 2 is not a DRIVER.");
    
        // Verify that the booking was not saved or altered
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Assign Driver To Booking when driver has no cab")
    void whenAssignDriverToBooking_withDriverWithoutCab_thenThrowsResourceNotFoundException() {
    // Arrange
    // 1. Ensure the booking is in a PENDING state for the check to proceed
    booking.setStatus(Booking.BookingStatus.PENDING);
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));

    // 2. Mock a valid driver
    given(userRepository.findById(2L)).willReturn(Optional.of(driverUser));

    // 3. Mock the cab repository to find no cab for the given driver
    given(cabRepository.findByDriver(driverUser)).willReturn(Optional.empty());

    // Act & Assert
    // The validation logic should now fail when it cannot find a cab for the driver.
    assertThatThrownBy(() -> bookingService.assignDriverToBooking(1L, 2L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("driver with id " + driverUser.getId() + " does not have an assigned cab");

    // Verify the booking was not updated
    verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Assign Driver To Booking when cab is not available")
    void whenAssignDriverToBooking_withUnavailableCab_thenThrowsIllegalStateException() {
    // Arrange
    // 1. Ensure the booking is PENDING to pass the initial checks
    booking.setStatus(Booking.BookingStatus.PENDING);
    given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));

    // 2. Mock a valid driver
    given(userRepository.findById(2L)).willReturn(Optional.of(driverUser));

    // 3. Set the driver's cab to a non-available status
    cab.setStatus(Cab.AvailabilityStatus.BOOKED);
    given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));

    // Act & Assert
    // The validation should now fail because the cab's status is not AVAILABLE.
    assertThatThrownBy(() -> bookingService.assignDriverToBooking(1L, 2L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("The assigned driver is not currently available. Status: " + cab.getStatus());

    // Verify the booking was not saved or modified
    verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Cancel Booking - Success")
    void whenCancelBooking_isSuccessful_thenBookingIsCancelledAndCabIsAvailable() {
        // Arrange
        // 1. Set the booking to a state where it can be canceled.
        // We assume the canBeCancelled() method on the Booking entity will return true.
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // 2. Mock the repository calls needed for the business logic.
        // Note: We no longer mock SecurityContext or userRepository.findByEmail.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);
        bookingResponse.setStatus(Booking.BookingStatus.CANCELLED.toString()); // Ensure response has correct status

        // Act
        BookingResponse cancelledBookingResponse = bookingService.cancelBooking(booking.getId());

        // Assert
        // 1. Verify the final response object is correct.
        assertThat(cancelledBookingResponse).isNotNull();
        assertThat(cancelledBookingResponse.getStatus()).isEqualTo("CANCELLED");

        // 2. Capture the booking passed to the save method and verify its status was updated.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(bookingCaptor.getValue().getStatus()).isEqualTo(Booking.BookingStatus.CANCELLED);

        // 3. Verify the cab was made available again.
        ArgumentCaptor<CabUpdateAvailabilityStatusRequest> statusRequestCaptor =
                ArgumentCaptor.forClass(CabUpdateAvailabilityStatusRequest.class);
        verify(cabService).updateCabAvailabilityStatus(eq(cab.getId()), statusRequestCaptor.capture());
        assertThat(statusRequestCaptor.getValue().getStatus()).isEqualTo(Cab.AvailabilityStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Test Cancel Booking when booking does not exist")
    void whenCancelBooking_withInvalidBookingId_thenThrowsResourceNotFoundException() {
        // Arrange
        // 1. Define a booking ID that does not exist.
        long nonExistentBookingId = 999L;

        // 2. Mock the repository to return an empty Optional, simulating that the booking was not found.
        given(bookingRepository.findById(nonExistentBookingId)).willReturn(Optional.empty());

        // Act & Assert
        // Verify that calling the service method with the invalid ID throws the correct exception
        // with the expected message.
        assertThatThrownBy(() -> bookingService.cancelBooking(nonExistentBookingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found with id: " + nonExistentBookingId);

        // Verify that no other interactions occurred, like trying to save.
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(cabService, never()).updateCabAvailabilityStatus(anyLong(), any());
    }

    @ParameterizedTest
    @EnumSource(value = Booking.BookingStatus.class, names = {"COMPLETED", "CANCELLED"})
    @DisplayName("Test Cancel Booking when booking is in a non-cancellable state")
    void whenCancelBooking_withNonCancellableStatus_thenThrowsIllegalStateException(Booking.BookingStatus status) {
        // Arrange
        // 1. Set the booking to a non-cancellable state from the test parameters.
        // We will assume the entity's canBeCancelled() method returns false for these states.
        booking.setStatus(status);

        // 2. Mock the repository to return this booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // Act & Assert
        // Verify that the service method throws the correct exception.
        assertThatThrownBy(() -> bookingService.cancelBooking(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking cannot be cancelled in its current state: " + status);

        // Verify no state-changing methods were called.
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(cabService, never()).updateCabAvailabilityStatus(anyLong(), any());
    }

    @Test
    @DisplayName("Test Cancel Booking when booking has no driver")
    void whenCancelBooking_withNoDriverAssigned_thenSucceedsWithoutError() {
        // Arrange
        // 1. Set the booking to a cancellable state.
        booking.setStatus(Booking.BookingStatus.PENDING);
        // 2. Crucially, ensure no driver is assigned to this booking.
        booking.setDriver(null);

        // 3. Mock the repository to return our driverless booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);
        bookingResponse.setStatus(Booking.BookingStatus.CANCELLED.toString());


        // Act
        // Execute the cancellation. We expect this to complete without throwing an exception.
        BookingResponse cancelledBookingResponse = bookingService.cancelBooking(booking.getId());


        // Assert
        // 1. Verify the booking was successfully canceled.
        assertThat(cancelledBookingResponse).isNotNull();
        assertThat(cancelledBookingResponse.getStatus()).isEqualTo("CANCELLED");

        // 2. Verify the booking's status was updated to CANCELLED before saving.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(bookingCaptor.getValue().getStatus()).isEqualTo(Booking.BookingStatus.CANCELLED);
        assertThat(bookingCaptor.getValue().getDriver()).isNull();

        // 3. Most importantly, verify that no attempt was made to find or update a cab.
        verify(cabRepository, never()).findByDriver(any());
        verify(cabService, never()).updateCabAvailabilityStatus(anyLong(), any());
    }

    @Test
    @DisplayName("Test Start Ride - Success")
    void whenStartRide_withCorrectDriverAndConfirmedBooking_thenRideStarts() {
        // Arrange
        // 1. The booking is already in a 'CONFIRMED' state from the setUp method.
        // We'll ensure the driver is correctly assigned.
        booking.setDriver(driverUser);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // 2. Mock the necessary repository calls.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);
        bookingResponse.setStatus(Booking.BookingStatus.IN_PROGRESS.toString()); // Update response mock

        // Act
        BookingResponse rideResponse = bookingService.startRide(booking.getId(), driverUser.getId());

        // Assert
        // 1. Check the final response object.
        assertThat(rideResponse).isNotNull();
        assertThat(rideResponse.getStatus()).isEqualTo("IN_PROGRESS");

        // 2. Capture the booking object passed to the save method to inspect its state.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        // 3. Verify the status and start time we're correctly set before saving.
        assertThat(savedBooking.getStatus()).isEqualTo(Booking.BookingStatus.IN_PROGRESS);
        assertThat(savedBooking.getStartTime()).isNotNull();
        assertThat(savedBooking.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Test Start Ride when booking does not exist")
    void whenStartRide_withInvalidBookingId_thenThrowsResourceNotFoundException() {
        // Arrange
        // 1. Define a booking ID and a driver ID that will be used in the request.
        long nonExistentBookingId = 999L;
        long anyDriverId = 2L;

        // 2. Mock the repository to return an empty Optional, simulating that no booking was found.
        given(bookingRepository.findById(nonExistentBookingId)).willReturn(Optional.empty());

        // Act & Assert
        // Verify that calling the service method with the invalid booking ID
        // throws the correct exception with the expected message.
        assertThatThrownBy(() -> bookingService.startRide(nonExistentBookingId, anyDriverId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found with id: " + nonExistentBookingId);

        // Verify that no save operation was attempted.
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    @DisplayName("Test Start Ride with incorrect driver")
    void whenStartRide_withIncorrectDriver_thenThrowsIllegalStateException() {
        // Arrange
        // 1. Create a second driver to represent the "incorrect" one.
        User incorrectDriver = new User();
        incorrectDriver.setId(99L); // A different ID from the assigned driver.

        // 2. The booking from setUp is assigned to driverUser (ID 2L).
        // Ensure it's in a CONFIRMED state.
        booking.setDriver(driverUser);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // 3. Mock the repository to return the booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // Act & Assert
        // Verify that attempting to start the ride with the incorrect driver's ID
        // throws the expected exception.
        assertThatThrownBy(() -> bookingService.startRide(booking.getId(), incorrectDriver.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking not assigned to this driver or no driver assigned.");

        // Ensure the booking's state was not changed.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Start Ride when no driver is assigned")
    void whenStartRide_withNoDriverAssigned_thenThrowsIllegalStateException() {
        // Arrange
        // 1. Explicitly set the driver to null for this test case.
        // The booking is CONFIRMED, so it will pass the status check.
        booking.setDriver(null);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // 2. Mock the repository to return our booking without a driver.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // 3. Define an arbitrary driver ID for the request.
        long anyDriverId = 2L;

        // Act & Assert
        // Verify that attempting to start the ride throws the correct exception.
        assertThatThrownBy(() -> bookingService.startRide(booking.getId(), anyDriverId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking not assigned to this driver or no driver assigned.");

        // Verify the booking's state was not altered.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @ParameterizedTest
    @EnumSource(value = Booking.BookingStatus.class, names = {"PENDING", "CANCELLED", "COMPLETED", "IN_PROGRESS"})
    @DisplayName("Test Start Ride when booking is not in a confirmed state")
    void whenStartRide_withNonConfirmedStatus_thenThrowsIllegalStateException(Booking.BookingStatus status) {
        // Arrange
        // 1. Assign the correct driver, so it passes the first check.
        booking.setDriver(driverUser);
        // 2. Set the booking to the invalid status provided by the test parameter.
        booking.setStatus(status);

        // 3. Mock the repository to return this booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // Act & Assert
        // Verify that the service method throws the correct exception because the status is not CONFIRMED.
        assertThatThrownBy(() -> bookingService.startRide(booking.getId(), driverUser.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ride can only be started for CONFIRMED bookings. Current status: " + status);

        // Verify the booking's state was not altered.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Complete Ride - Success")
    void whenCompleteRide_withCorrectDriverAndInProgressBooking_thenRideIsCompleted() {
        // Arrange
        // 1. Set the booking to the required IN_PROGRESS state.
        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        booking.setDriver(driverUser);

        // 2. Mock the repository and service calls.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(cabRepository.findByDriver(driverUser)).willReturn(Optional.of(cab));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);
        bookingResponse.setStatus(Booking.BookingStatus.COMPLETED.toString()); // Update response mock

        // Act
        BookingResponse rideResponse = bookingService.completeRide(booking.getId(), driverUser.getId());

        // Assert
        // 1. Check the final response object.
        assertThat(rideResponse).isNotNull();
        assertThat(rideResponse.getStatus()).isEqualTo("COMPLETED");

        // 2. Capture the booking object to verify its state before saving.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertThat(savedBooking.getStatus()).isEqualTo(Booking.BookingStatus.COMPLETED);
        assertThat(savedBooking.getEndTime()).isNotNull();
        assertThat(savedBooking.getUpdatedAt()).isNotNull();

        // 3. Verify the cab's availability was updated to AVAILABLE.
        ArgumentCaptor<CabUpdateAvailabilityStatusRequest> statusRequestCaptor =
                ArgumentCaptor.forClass(CabUpdateAvailabilityStatusRequest.class);
        verify(cabService).updateCabAvailabilityStatus(eq(cab.getId()), statusRequestCaptor.capture());
        assertThat(statusRequestCaptor.getValue().getStatus()).isEqualTo(Cab.AvailabilityStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Test Complete Ride when booking does not exist")
    void whenCompleteRide_withInvalidBookingId_thenThrowsResourceNotFoundException() {
        // Arrange
        // 1. Define a booking ID that we will pretend does not exist.
        long nonExistentBookingId = 999L;
        long anyDriverId = 2L;

        // 2. Mock the repository to return an empty Optional for the non-existent ID.
        given(bookingRepository.findById(nonExistentBookingId)).willReturn(Optional.empty());

        // Act & Assert
        // Verify that calling the service with the invalid ID throws the correct exception
        // and that the exception message is as expected.
        assertThatThrownBy(() -> bookingService.completeRide(nonExistentBookingId, anyDriverId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found with id: " + nonExistentBookingId);

        // Verify that no interactions that change state occurred.
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(cabService, never()).updateCabAvailabilityStatus(anyLong(), any());
    }

    @Test
    @DisplayName("Test Complete Ride with incorrect driver")
    void whenCompleteRide_withIncorrectDriver_thenThrowsIllegalStateException() {
        // Arrange
        // 1. Create a separate driver object to represent the unauthorized user.
        User incorrectDriver = new User();
        incorrectDriver.setId(101L); // Use a distinct ID.

        // 2. Set the booking to its required state: IN_PROGRESS and assigned to the correct driver.
        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        booking.setDriver(driverUser); // driverUser has ID 2L from setUp.

        // 3. Mock the repository to return the booking when queried.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // Act & Assert
        // Verify that calling the method with the incorrect driver's ID throws the correct exception.
        assertThatThrownBy(() -> bookingService.completeRide(booking.getId(), incorrectDriver.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking not assigned to this driver or no driver assigned.");

        // Verify that the booking's state was not changed and no save was attempted.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @ParameterizedTest
    @EnumSource(value = Booking.BookingStatus.class, names = {"PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"})
    @DisplayName("Test Complete Ride when booking is not in progress")
    void whenCompleteRide_withNonInProgressStatus_thenThrowsIllegalStateException(Booking.BookingStatus status) {
        // Arrange
        // 1. Assign the correct driver to pass the initial authorization check.
        booking.setDriver(driverUser);
        // 2. Set the booking to the invalid status provided by the test parameter.
        booking.setStatus(status);

        // 3. Mock the repository to return this booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // Act & Assert
        // Verify that the service method throws the correct exception because the status is not IN_PROGRESS.
        assertThatThrownBy(() -> bookingService.completeRide(booking.getId(), driverUser.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ride can only be completed if IN_PROGRESS. Current status: " + status);

        // Verify the booking's state was not altered and no save was attempted.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Complete Ride when no driver is assigned")
    void whenCompleteRide_withNoDriverAssigned_thenThrowsIllegalStateException() {
        // Arrange
        // 1. Set the booking to its required state for the test.
        // It's IN_PROGRESS, so it passes the status check.
        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        // 2. Crucially, ensure the driver is null.
        booking.setDriver(null);

        // 3. Mock the repository to return our booking without a driver.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // 4. Define an arbitrary driver ID for the request, as one would still be sent.
        long anyDriverId = 2L;

        // Act & Assert
        // Verify that attempting to complete the ride throws the correct exception
        // due to the null driver.
        assertThatThrownBy(() -> bookingService.completeRide(booking.getId(), anyDriverId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking not assigned to this driver or no driver assigned.");

        // Verify the booking's state was not altered.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Complete Ride when driver's cab is not found")
    void whenCompleteRide_butDriverCabNotFound_thenBookingIsStillCompleted() {
        // Arrange
        // 1. Set the booking to its required IN_PROGRESS state with a driver.
        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        booking.setDriver(driverUser);

        // 2. Mock the booking repository to return our booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        // 3. Critically, mock the cab repository to return empty, simulating no cab found for the driver.
        given(cabRepository.findByDriver(driverUser)).willReturn(Optional.empty());

        // 4. Mock the save and map operations for a successful completion.
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);
        bookingResponse.setStatus(Booking.BookingStatus.COMPLETED.toString());

        // Act
        // We expect this call to succeed without throwing any exceptions.
        BookingResponse rideResponse = bookingService.completeRide(booking.getId(), driverUser.getId());

        // Assert
        // 1. Verify the booking was successfully completed.
        assertThat(rideResponse).isNotNull();
        assertThat(rideResponse.getStatus()).isEqualTo("COMPLETED");

        // 2. Verify the booking object was saved with the correct COMPLETED status.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(bookingCaptor.getValue().getStatus()).isEqualTo(Booking.BookingStatus.COMPLETED);

        // 3. Most importantly, verify that no attempt was made to update a cab's status.
        verify(cabService, never()).updateCabAvailabilityStatus(anyLong(), any());
    }

    @Test
    @DisplayName("Test Update Payment Details - Success with Payment ID")
    void whenUpdatePaymentDetails_withValidData_thenDetailsAreUpdated() {
        // Arrange
        // 1. Define the payment details to be updated.
        boolean paymentStatus = true;
        String paymentId = "txn_12345ABCDE";

        // 2. Mock the repository to return the booking.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

        // Act
        BookingResponse updatedBookingResponse = bookingService.updatePaymentDetails(booking.getId(), paymentStatus, paymentId);

        // Assert
        // 1. Verify the response object is not null.
        assertThat(updatedBookingResponse).isNotNull();

        // 2. Capture the booking object passed to the save method for detailed inspection.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        // 3. Verify that the payment status, payment ID, and timestamp were correctly set.
        assertThat(savedBooking.isPaymentStatus()).isEqualTo(paymentStatus);
        assertThat(savedBooking.getPaymentId()).isEqualTo(paymentId);
        assertThat(savedBooking.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Payment Details with null Payment ID")
    void whenUpdatePaymentDetails_withNullPaymentId_thenOnlyStatusIsUpdated() {
        // Arrange
        // 1. Give the booking an existing payment ID to ensure it's not overwritten.
        String originalPaymentId = "existing_txn_abcde";
        booking.setPaymentId(originalPaymentId);

        // 2. Define the new payment status. The paymentId parameter will be null.
        boolean newPaymentStatus = true;

        // 3. Mock the repository calls.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

        // Act
        bookingService.updatePaymentDetails(booking.getId(), newPaymentStatus, null);

        // Assert
        // 1. Capture the booking object to inspect its state before saving.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        // 2. Verify the payment status was updated, but the original payment ID was preserved.
        assertThat(savedBooking.isPaymentStatus()).isEqualTo(newPaymentStatus);
        assertThat(savedBooking.getPaymentId()).isEqualTo(originalPaymentId);
        assertThat(savedBooking.getUpdatedAt()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Test Update Payment Details with blank Payment ID")
    void whenUpdatePaymentDetails_withBlankPaymentId_thenOnlyStatusIsUpdated(String blankPaymentId) {
        // Arrange
        // 1. Set an initial payment ID to ensure it is not overwritten.
        String originalPaymentId = "existing_txn_zyxw";
        booking.setPaymentId(originalPaymentId);

        // 2. Define the new payment status to be updated.
        boolean newPaymentStatus = false;

        // 3. Mock the repository calls.
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(bookingResponse);

        // Act
        bookingService.updatePaymentDetails(booking.getId(), newPaymentStatus, blankPaymentId);

        // Assert
        // 1. Capture the booking object sent to the save method.
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        // 2. Verify the status was updated, but the original payment ID remains unchanged.
        assertThat(savedBooking.isPaymentStatus()).isEqualTo(newPaymentStatus);
        assertThat(savedBooking.getPaymentId()).isEqualTo(originalPaymentId);
        assertThat(savedBooking.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Payment Details when booking does not exist")
    void whenUpdatePaymentDetails_withInvalidBookingId_thenThrowsResourceNotFoundException() {
        // Arrange
        // 1. Define a booking ID that does not exist in the repository.
        long nonExistentBookingId = 999L;

        // 2. Mock the repository to return an empty Optional for this ID.
        given(bookingRepository.findById(nonExistentBookingId)).willReturn(Optional.empty());

        // Act & Assert
        // Verify that calling the service with the non-existent ID throws the correct exception
        // with the expected message.
        assertThatThrownBy(() -> bookingService.updatePaymentDetails(nonExistentBookingId, true, "some-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found with id: " + nonExistentBookingId);

        // Verify that no save operation was attempted since the booking was not found.
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test Find Pending Bookings for Driver Assignment - Success")
    void whenFindPendingBookingsForDriverAssignment_andBookingsExist_thenReturnsBookingResponseList() {
        // Arrange
        // 1. Create mock bookings that match the criteria (PENDING/CONFIRMED and no driver).
        Booking pendingBooking = new Booking();
        pendingBooking.setId(10L);
        pendingBooking.setStatus(Booking.BookingStatus.PENDING);
        pendingBooking.setDriver(null);

        Booking confirmedBooking = new Booking();
        confirmedBooking.setId(11L);
        confirmedBooking.setStatus(Booking.BookingStatus.CONFIRMED);
        confirmedBooking.setDriver(null);

        List<Booking> mockBookings = List.of(pendingBooking, confirmedBooking);

        // 2. Mock the repository to return our list when the specific query is called.
        List<Booking.BookingStatus> expectedStatuses = List.of(Booking.BookingStatus.PENDING, Booking.BookingStatus.CONFIRMED);
        given(bookingRepository.findByStatusInAndDriverIsNull(expectedStatuses)).willReturn(mockBookings);

        // 3. Mock the mapper to return a response for any booking object.
        // We can use a generic response since we're not testing the mapping logic itself here.
        given(bookingMapper.toBookingResponse(any(Booking.class))).willReturn(new BookingResponse());

        // Act
        List<BookingResponse> result = bookingService.findPendingBookingsForDriverAssignment();

        // Assert
        // 1. Verify that the result is not null and contains the correct number of items.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // 2. Verify that the repository method was called exactly once with the correct status list.
        verify(bookingRepository, times(1)).findByStatusInAndDriverIsNull(expectedStatuses);

        // 3. Verify that the mapper was called for each booking found.
        verify(bookingMapper, times(2)).toBookingResponse(any(Booking.class));
    }

    @Test
    @DisplayName("Test Find Pending Bookings for Driver Assignment when none exist")
    void whenFindPendingBookingsForDriverAssignment_andNoBookingsExist_thenReturnsEmptyList() {
        // Arrange
        // 1. Define the list of statuses the method will query for.
        List<Booking.BookingStatus> expectedStatuses = List.of(Booking.BookingStatus.PENDING, Booking.BookingStatus.CONFIRMED);

        // 2. Mock the repository to return an empty list for this specific query.
        given(bookingRepository.findByStatusInAndDriverIsNull(expectedStatuses)).willReturn(Collections.emptyList());

        // Act
        List<BookingResponse> result = bookingService.findPendingBookingsForDriverAssignment();

        // Assert
        // 1. Verify that the result is not null and is empty.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 2. Verify that the repository method was called.
        verify(bookingRepository, times(1)).findByStatusInAndDriverIsNull(expectedStatuses);

        // 3. Most importantly, verify that the mapper was never invoked since there were no bookings to map.
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    @DisplayName("Test Find Pending Bookings for Driver Assignment verifies correct query method is called")
    void whenFindPendingBookingsForDriverAssignment_thenVerifiesCorrectRepositoryMethodCall() {
        // Arrange
        // 1. Define the exact list of statuses your service method should be querying for.
        List<Booking.BookingStatus> expectedStatuses = List.of(Booking.BookingStatus.PENDING, Booking.BookingStatus.CONFIRMED);

        // 2. We don't need to create mock bookings. We'll simply mock the repository to return an empty list
        // for this specific query, as our primary goal is to verify the call itself.
        given(bookingRepository.findByStatusInAndDriverIsNull(expectedStatuses)).willReturn(Collections.emptyList());

        // Act
        List<BookingResponse> result = bookingService.findPendingBookingsForDriverAssignment();

        // Assert
        // 1. First, confirm the result is what you'd expect when the repository finds nothing.
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 2. Most importantly, verify that the service method called the repository with the
        // exact list of statuses we defined. This proves the filtering logic is being correctly invoked.
        verify(bookingRepository, times(1)).findByStatusInAndDriverIsNull(expectedStatuses);

        // 3. Ensure no mapping occurred.
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }
}