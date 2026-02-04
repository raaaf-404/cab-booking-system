package com.cabbooking.service;

import com.cabbooking.dto.request.DriverSignupRequest;
import com.cabbooking.model.Driver;
import com.cabbooking.model.User;

public interface DriverService {
    Driver registerDriver(User user, DriverSignupRequest request);
}