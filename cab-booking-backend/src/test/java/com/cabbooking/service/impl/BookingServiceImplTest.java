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
    private User passengerUser;
    private User driverUser;
    private BookingRegistrationRequest bookingRequest;
    private BookingResponse bookingResponse;
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
        given(userRepository.findById(1L)).willReturn(Optional.of(passengerUser));
        // The mapper will convert the request to the 'booking' entity
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
    assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingRequest));

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
    // to Mock the repository to return an empty Optional, simulating a not-found scenario
    given(bookingRepository.findById(1L)).willReturn(Optional.empty());

    // Act & Assert
    // Verify that calling the method now throws the expected exception
    assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1L));

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

        // 3. Verify the status and start time were correctly set before saving.
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
}