package com.cabbooking.service.impl;

import com.cabbooking.mapper.CabMapper;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import com.cabbooking.model.Cab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@InjectMocks
private  CabServiceImpl cabService;


private User driver;
private Cab cab;
private CabResponse cabResponse;
private UserResponse driverResponse;

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
    }




}
