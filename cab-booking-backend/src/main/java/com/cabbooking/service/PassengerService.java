package com.cabbooking.service;

import com.cabbooking.dto.request.PassengerSignupRequest;
import com.cabbooking.model.Passenger;
import com.cabbooking.model.User;

public interface PassengerService {
    Passenger registerPassenger(User user, PassengerSignupRequest request);
}