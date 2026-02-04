package com.cabbooking.service.impl;

import com.cabbooking.dto.request.PassengerSignupRequest;
import com.cabbooking.model.Passenger;
import com.cabbooking.model.User;
import com.cabbooking.repository.PassengerRepository;
import com.cabbooking.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    @Override
    @Transactional
    public Passenger registerPassenger(User user, PassengerSignupRequest request) {
        Passenger passenger = Passenger.builder()
                .user(user)
                .build();

        return passengerRepository.save(passenger);
    }
}