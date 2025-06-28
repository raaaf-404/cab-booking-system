package com.cabbooking.controller;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.ApiResponse;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.model.Booking;
import com.cabbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor // Lombok annotation for constructor injection of final fields
public class BookingController {

    private final BookingService bookingService;

    /**
     * Endpoint to create a new booking.
     *
     * @param request The booking registration request containing booking details.
     * @return ResponseEntity with the created BookingResponse and HttpStatus.CREATED.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRegistrationRequest request) {
        try {
            BookingResponse newBooking = bookingService.createBooking(request);
            return new ResponseEntity<>(ApiResponse.success(newBooking), HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}