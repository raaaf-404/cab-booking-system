package com.cabbooking.service.impl;

import com.cabbooking.mapper.CabMapper;
import com.cabbooking.repository.BookingRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.repository.CabRepository;
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

@BeforeEach
void Setup() {
    driver = new User();
    driver.setId(1L);
    driver.setName("Test Driver");
    driver.setEmail("driver@example.com");
    driver.setPassword("securePassword123");
    driver.setPhone("09171234567");
    driver.setIsActive(true);
    driver.setRole(Set.of(User.Role.DRIVER));


}


}
