package com.cabbooking.service.impl;

import com.cabbooking.dto.request.*;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.mapper.CabMapper;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import com.cabbooking.model.Cab;

import com.cabbooking.service.CabService;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentAccessException;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.cabbooking.exception.CabAlreadyExistException;
import com.cabbooking.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.convert.DataSizeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.math.BigDecimal;


@ExtendWith(MockitoExtension.class)
class CabServiceImplTest {

@Mock
private CabRepository cabRepository;
@Mock
private CabMapper cabMapper;
@Mock
private UserRepository userRepository;
@Mock
private BookingRepository bookingRepository;
@Mock
private UserService userService;
@InjectMocks
private  CabServiceImpl cabService;


private User driver;
private Cab cab;
private CabResponse cabResponse;
private UserResponse driverResponse;
private CabRegistrationRequest cabRequest;

    @BeforeEach
    void setup() {
        // 1. Initialize the Driver (User) object
        driver = new User();
        driver.setId(1L);
        driver.setName("Test Driver");
        driver.setEmail("driver@example.com");
        driver.setPassword("securePassword123");
        driver.setPhone("09171234567");
        driver.setIsActive(true);
        driver.setRole(Set.of(User.Role.DRIVER));

        // 2. Initialize the Cab entity
        // Using the builder pattern provided by Lombok for cleaner instantiation
        cab = Cab.builder()
                .id(10L)
                .licensePlateNumber("NCR-1234")
                .driver(driver) // Associate the driver created above
                .vehicleType(Cab.VehicleType.SEDAN)
                .status(Cab.AvailabilityStatus.AVAILABLE) // A useful default for many tests
                .latitude(14.6091)   // Example coordinates for Quezon City
                .longitude(121.0223)
                .model("Toyota Vios")
                .color("Silver")
                .manufacturingYear(2023)
                .seatingCapacity(4)
                .isAirConditioned(true)
                .isMeterFare(true)
                .baseFare(new BigDecimal("45.00"))
                .ratePerKm(new BigDecimal("15.50"))
                .build();

        // First, create the nested UserResponse object
        driverResponse = new UserResponse();
        driverResponse.setId(driver.getId());
        driverResponse.setName(driver.getName());
        driverResponse.setEmail(driver.getEmail());

        // Now, create the main CabResponse object
        cabResponse = new CabResponse();
        cabResponse.setId(cab.getId());
        cabResponse.setLicensePlateNumber(cab.getLicensePlateNumber());
        cabResponse.setDriver(driverResponse); // Set the nested DTO
        cabResponse.setLatitude(cab.getLatitude());
        cabResponse.setLongitude(cab.getLongitude());
        cabResponse.setStatus(cab.getStatus());
        cabResponse.setVehicleType(cab.getVehicleType().name()); // Convert Enum to String
        cabResponse.setModel(cab.getModel());
        cabResponse.setColor(cab.getColor());
        cabResponse.setManufacturingYear(cab.getManufacturingYear());
        cabResponse.setSeatingCapacity(cab.getSeatingCapacity());
        cabResponse.setIsAirConditioned(cab.getIsAirConditioned());
        cabResponse.setIsMeterFare(cab.getIsMeterFare());
        cabResponse.setBaseFare(cab.getBaseFare());
        cabResponse.setRatePerKm(cab.getRatePerKm());

        cabRequest = CabRegistrationRequest.builder()
                .driverId(driver.getId())
                .licensePlateNumber("NCR-1234")
                .vehicleType(Cab.VehicleType.SEDAN.name())
                .model("Toyota Vios")
                .color("Silver")
                .manufacturingYear(2023)
                .seatingCapacity(4)
                .isAirConditioned(true)
                .isMeterFare(true)
                .baseFare(new BigDecimal("45.00"))
                .ratePerKm(new BigDecimal("15.50"))
                .build();
    }


    @Test
    @DisplayName("Test Register Cab with valid request should Succeed and return CabResponse")
    void whenRegisterCab_withValidRequest_thenReturnCabResponse() {
        //Arrange
        // Mock dependencies to simulate a successful registration path.
        given(userService.findAndValidateDriverById(cabRequest.getDriverId())).willReturn(driver);
        given(cabRepository.findByLicensePlateNumber(cabRequest.getLicensePlateNumber())).willReturn(Optional.empty());

        // When save is called with any Cab object, return that same object.
        given(cabRepository.save(any(Cab.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When the mapper is called with any Cab object, return the pre-configured response DTO.
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        //Act
        CabResponse result = cabService.registerCab(cabRequest);

        //Assert
        // 1. Verify the final result is the one returned by the mocked mapper.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(cabResponse);

        // 2. Capture the Cab entity passed to the save method to verify its contents.
        ArgumentCaptor<Cab> cabCaptor = ArgumentCaptor.forClass(Cab.class);
        verify(cabRepository).save(cabCaptor.capture());
        Cab savedCab = cabCaptor.getValue();

        // 3. Assert that the service correctly built the Cab entity from the request.
        assertThat(savedCab.getLicensePlateNumber()).isEqualTo(cabRequest.getLicensePlateNumber());
        assertThat(savedCab.getDriver()).isEqualTo(driver);
        assertThat(savedCab.getVehicleType()).isEqualTo(Cab.VehicleType.SEDAN);
        assertThat(savedCab.getModel()).isEqualTo(cabRequest.getModel());
        assertThat(savedCab.getStatus()).isEqualTo(Cab.AvailabilityStatus.OFFLINE);
    }

    @Test
    @DisplayName("Test Register Cab with existing license plate should throw CabAlreadyExistException")
    void whenRegisterCab_withExistingLicensePlate_thenThrowCabAlreadyExistException() {
        // Arrange
        given(userService.findAndValidateDriverById(cabRequest.getDriverId())).willReturn(driver);
        given(cabRepository.findByLicensePlateNumber(cabRequest.getLicensePlateNumber())).willReturn(Optional.of(cab));

        // Act & Assert
        assertThatThrownBy(() -> cabService.registerCab(cabRequest))
                .isInstanceOf(CabAlreadyExistException.class)
                .hasMessageContaining("Cab with license plate number " + cabRequest.getLicensePlateNumber() + " already exists.");

        // Verify that save was never called
        verify(cabRepository, never()).save(any(Cab.class));
    }

    @Test
    @DisplayName("Test Register Cab with invalid vehicle type should throw IllegalArgumentException")
    void whenRegisterCab_withInvalidVehicleType_thenThrowIllegalArgumentException() {
        // Arrange
        cabRequest.setVehicleType("MOTORCYCLE"); // Invalid vehicle type
        given(userService.findAndValidateDriverById(cabRequest.getDriverId())).willReturn(driver);
        given(cabRepository.findByLicensePlateNumber(cabRequest.getLicensePlateNumber())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cabService.registerCab(cabRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant");

        // Verify that save was never called
        verify(cabRepository, never()).save(any(Cab.class));
    }

    @Test
    @DisplayName("Test Get Cab by Id with valid ID should return a cab")
    void whenGetCabById_withValidId_thenReturnCabResponse() {
        // Arrange
        given(cabRepository.findById(cab.getId())).willReturn(Optional.of(cab));
        given(cabMapper.toCabResponse(cab)).willReturn(cabResponse);

        // Act
        CabResponse foundCab = cabService.getCabById(cab.getId());

        // Assert
        // 1. Verify the response object has the correct data, asserting against the original source object.
        assertThat(foundCab).isNotNull();
        assertThat(foundCab.getId()).isEqualTo(cab.getId());
        assertThat(foundCab.getLicensePlateNumber()).isEqualTo(cab.getLicensePlateNumber());
        assertThat(foundCab.getVehicleType()).isEqualTo(cab.getVehicleType().name());

        // 2. Verify that the repository and mapper were called correctly.
        verify(cabRepository).findById(cab.getId());
        verify(cabMapper).toCabResponse(cab);
    }

    @Test
    @DisplayName("Test Get Cab by Id with non-existent ID should throw ResourceNotFoundException")
    void whenGetCabById_withNonExistentId_thenThrowResourceNotFoundException() {
        // Arrange
        long nonExistentId = 999L;
        given(cabRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cabService.getCabById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cab not found with id: " + nonExistentId);

        verify(cabMapper, never()).toCabResponse(any(Cab.class));
    }

    @Test
    @DisplayName("Test Get Cab by License Plate with valid License Plate should succeed and return a cab response")
    void whenGetCabByLicensePlate_withValidLicensePlate_thenReturnCabResponse() {
        // Arrange
        String licensePlate = cab.getLicensePlateNumber();

        // Mock the repository to return the specific cab.
        given(cabRepository.findByLicensePlateNumber(licensePlate)).willReturn(Optional.of(cab));

        // Mock the mapper to return the expected response DTO.
        given(cabMapper.toCabResponse(cab)).willReturn(cabResponse);

        // Act

        CabResponse foundCab = cabService.getCabByLicensePlate(licensePlate);

        // Assert
        // 1. Assert that the key fields of the returned object match the source entity.
        assertThat(foundCab).isNotNull();
        assertThat(foundCab.getId()).isEqualTo(cab.getId());
        assertThat(foundCab.getLicensePlateNumber()).isEqualTo(cab.getLicensePlateNumber());
        assertThat(foundCab.getDriver().getId()).isEqualTo(driver.getId());

        // 2. Verify that the repository and mapper methods were called correctly.
        verify(cabRepository).findByLicensePlateNumber(licensePlate);
        verify(cabMapper).toCabResponse(cab);
    }

    @Test
    @DisplayName("Test Update Cab Details with valid data should succeed")
    void whenUpdateCabDetails_withValidData_thenCabIsUpdatedSuccessfully() {
        // Arrange
        // 1. Create a "new" driver to be assigned during the update.
        User newDriver = new User();
        newDriver.setId(2L);
        newDriver.setName("New Driver");
        newDriver.addRole(User.Role.DRIVER);

        // 2. Create the update request object with a representative set of new data.
        CabUpdateRequest updateRequest = new CabUpdateRequest();
        updateRequest.setModel("Honda City"); // Change a String
        updateRequest.setSeatingCapacity(5);  // Change an Integer
        updateRequest.setBaseFare(new BigDecimal("50.00")); // Change a BigDecimal
        updateRequest.setDriverId(newDriver.getId()); // Change the driver relationship

        // 3. Mock the service dependencies.
        given(cabRepository.findById(cab.getId())).willReturn(Optional.of(cab));
        given(userService.findAndValidateDriverById(newDriver.getId())).willReturn(newDriver);
        given(cabRepository.save(any(Cab.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        // Act
        cabService.updateCabDetails(cab.getId(), updateRequest);

        // Assert
        // 1. Capture the Cab entity that was saved to verify its state.
        ArgumentCaptor<Cab> cabCaptor = ArgumentCaptor.forClass(Cab.class);
        verify(cabRepository).save(cabCaptor.capture());
        Cab savedCab = cabCaptor.getValue();

        // 2. Verify that the representative fields were updated correctly from the request.
        assertThat(savedCab.getModel()).isEqualTo(updateRequest.getModel());
        assertThat(savedCab.getSeatingCapacity()).isEqualTo(updateRequest.getSeatingCapacity());
        assertThat(savedCab.getBaseFare()).isEqualByComparingTo(updateRequest.getBaseFare());
        assertThat(savedCab.getDriver()).isEqualTo(newDriver);
    }

    @Test
    @DisplayName("Test Update Cab Location with valid data should succeed")
    void whenUpdateCabLocation_withValidData_thenCabLocationIsUpdatedSuccessfully() {
        // Arrange
        // 1. Create the request object with new, specific location data.
        LocationUpdateRequest updateLocationRequest = new LocationUpdateRequest();
        updateLocationRequest.setLatitude(14.5547);  // New latitude (e.g., Makati City)
        updateLocationRequest.setLongitude(121.0244); // New longitude

        // 2. Mock the service dependencies.
        given(cabRepository.findById(cab.getId())).willReturn(Optional.of(cab));
        given(cabRepository.save(any(Cab.class))).willAnswer(invocation -> invocation.getArgument(0));
        // Correctly return the cabResponse object, not a matcher.
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        // Act
        cabService.updateCabLocation(cab.getId(), updateLocationRequest);

        // Assert
        // 1. Capture the Cab entity that was passed to the save method.
        ArgumentCaptor<Cab> cabCaptor = ArgumentCaptor.forClass(Cab.class);
        verify(cabRepository).save(cabCaptor.capture());
        Cab savedCab = cabCaptor.getValue();

        // 2. Verify that the captured entity's location was updated correctly.
        assertThat(savedCab.getLatitude()).isEqualTo(updateLocationRequest.getLatitude());
        assertThat(savedCab.getLongitude()).isEqualTo(updateLocationRequest.getLongitude());

        // 3. Verify the mapper was called.
        verify(cabMapper).toCabResponse(savedCab);
    }

    @Test
    @DisplayName("Test Update Cab Availability Status with valid data should succeed")
    void whenUpdateCabAvailabilityStatus_withValidData_thenCabStatusIsUpdatedSuccessfully() {
        // Arrange
        // 1. Create the request with the new status.
        CabUpdateAvailabilityStatusRequest statusRequest = new CabUpdateAvailabilityStatusRequest();
        statusRequest.setStatus(Cab.AvailabilityStatus.MAINTENANCE); // Use a different status to test the change

        // 2. Mock the service dependencies.
        given(cabRepository.findById(cab.getId())).willReturn(Optional.of(cab));
        given(cabRepository.save(any(Cab.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        // Act
        cabService.updateCabAvailabilityStatus(cab.getId(), statusRequest);

        // Assert
        // 1. Capture the Cab entity that was passed to the save method.
        ArgumentCaptor<Cab> cabCaptor = ArgumentCaptor.forClass(Cab.class);
        verify(cabRepository).save(cabCaptor.capture());

        // 2. Verify that the captured entity's status was updated correctly.
        assertThat(cabCaptor.getValue().getStatus()).isEqualTo(statusRequest.getStatus());

        // 3. Verify the mapper was called with the updated cab.
        verify(cabMapper).toCabResponse(cabCaptor.getValue());
    }

    @Test
    @DisplayName("Test Assign Driver To Cab with valid data should succeed")
    void whenAssignDriverToCab_withValidData_thenDriverShouldBeAssignedToCabSuccessfully() {
        // Arrange
        // 1. Create the request object for the action.
        DriverAssignmentRequest assignmentRequest = new DriverAssignmentRequest();
        assignmentRequest.setDriverId(driver.getId());

        // 2. Set the initial state of the cab: it must have no driver to start.
        cab.setDriver(null);

        // 3. Mock all the dependencies for the success path.
        given(cabRepository.findById(cab.getId())).willReturn(Optional.of(cab));
        given(userService.findAndValidateDriverById(assignmentRequest.getDriverId())).willReturn(driver);
        given(cabRepository.findByDriver(driver)).willReturn(Optional.empty()); // Simulate driver is not assigned to another cab
        given(cabRepository.save(any(Cab.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        // Act
        // Execute the service method being tested.
        CabResponse updatedCab = cabService.assignDriverToCab(cab.getId(), assignmentRequest);

        // Assert
        // 1. Verify the final response object is correct.
        assertThat(updatedCab).isNotNull();
        assertThat(updatedCab).isEqualTo(cabResponse);

        // 2. Capture the Cab entity that was saved to verify its state.
        ArgumentCaptor<Cab> cabCaptor = ArgumentCaptor.forClass(Cab.class);
        verify(cabRepository).save(cabCaptor.capture());
        Cab savedCab = cabCaptor.getValue();

        // 3. Verify the driver was assigned AND the status was correctly updated.
        assertThat(savedCab.getDriver()).isEqualTo(driver);
        assertThat(savedCab.getStatus()).isEqualTo(Cab.AvailabilityStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Test Remove Driver From Cab with valid cab id should succeed")
    void whenRemoveDriverFromCab_withValidCabId_thenSuccessAndCabHasNoDriver() {
        // Arrange
        // 1. Ensure the cab has a driver to begin with (this is handled by the setup method).
        // 2. Mock the service dependencies.
        given(cabRepository.findById(cab.getId())).willReturn(Optional.of(cab));
        given(cabRepository.save(any(Cab.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        // Act
        cabService.removeDriverFromCab(cab.getId());

        // Assert
        // 1. Capture the Cab entity that was saved to verify its final state.
        ArgumentCaptor<Cab> cabCaptor = ArgumentCaptor.forClass(Cab.class);
        verify(cabRepository).save(cabCaptor.capture());
        Cab savedCab = cabCaptor.getValue();

        // 2. Verify that the driver was removed AND the status was updated to OFFLINE.
        assertThat(savedCab.getDriver()).isNull();
        assertThat(savedCab.getStatus()).isEqualTo(Cab.AvailabilityStatus.OFFLINE);
    }

    @Test
    @DisplayName("Test Find Available Cabs with a specific vehicle type should succeed and return a list of cab responses")
    void whenFindAvailableCabs_withVehicleType_thenSuccessAndCallsCorrectRepositoryMethod() {
        // Arrange
        // 1. Define the specific criteria for the search.
        Cab.VehicleType specificType = Cab.VehicleType.SEDAN;
        Cab.AvailabilityStatus expectedStatus = Cab.AvailabilityStatus.AVAILABLE;

        // 2. Create a mock list of cabs that the repository will return.
        List<Cab> mockCabs = List.of(cab); // Using the 'cab' from setup

        // 3. Mock the repository to return the list for the specific query.
        given(cabRepository.findByVehicleTypeAndStatus(specificType, expectedStatus)).willReturn(mockCabs);
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        // Act
        List<CabResponse> result = cabService.findAvailableCabs(specificType);

        // Assert
        // 1. Verify that the result list contains the mapped objects.
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(cabResponse);

        // 2. Verify the correct repository method was called with the correct arguments.
        verify(cabRepository).findByVehicleTypeAndStatus(specificType, expectedStatus);

        // 3. CRUCIALLY, verify the other repository method was NEVER called.
        verify(cabRepository, never()).findByStatus(any(Cab.AvailabilityStatus.class));

        // 4. Verify the mapper was called.
        verify(cabMapper).toCabResponse(cab);
    }

    @Test
    @DisplayName("Test Find Available Cabs with a null vehicle type succeed and return a list of cab responses")
    void whenFindAvailableCabs_withNullVehicleType_thenSuccessAndCallsCorrectRepositoryMethod() {
        //Arrange
        Cab.AvailabilityStatus expectedStatus = Cab.AvailabilityStatus.AVAILABLE;
        List<Cab> expectedList = List.of(cab);

        given(cabRepository.findByStatus(expectedStatus)).willReturn((expectedList));
        given(cabMapper.toCabResponse(any(Cab.class))).willReturn(cabResponse);

        //Act
        List<CabResponse> result = cabService.findAvailableCabs(null);

        //Assert
        assertThat(result ).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(cabResponse);

        verify(cabRepository).findByStatus(expectedStatus);
        verify(cabRepository, never()).findByVehicleTypeAndStatus(any(Cab.VehicleType.class), any(Cab.AvailabilityStatus.class));
        verify(cabMapper).toCabResponse(cab);
    }
}
