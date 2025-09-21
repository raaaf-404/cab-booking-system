package com.cabbooking.service.impl;

import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateRequest;
import com.cabbooking.dto.request.LocationUpdateRequest;
import com.cabbooking.mapper.CabMapper;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import com.cabbooking.model.Cab;

import com.cabbooking.service.CabService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
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
        cabResponse.setStatus(cab.getStatus().name()); // Convert Enum to String
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

}
