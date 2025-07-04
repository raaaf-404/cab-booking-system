package com.cabbooking.controller;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.ApiResponse;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.model.Booking;
import com.cabbooking.service.BookingService;

import io.micrometer.core.ipc.http.HttpSender.Response;
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
        BookingResponse newBooking = bookingService.createBooking(request);
        return new ResponseEntity<>(ApiResponse.success(newBooking), HttpStatus.CREATED);
    }

      /**
     * Endpoint to retrieve a booking by its ID.
     *
     * @param bookingId The ID of the booking to retrieve.
     * @return ResponseEntity with the BookingResponse if found, or HttpStatus.NOT_FOUND.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long bookingId) {

        BookingResponse booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

       /**
     * Endpoint to retrieve all bookings associated with a specific passenger ID.
     *
     * @param passengerId The ID of the passenger.
     * @return ResponseEntity with a list of BookingResponse.
     */
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByPassengerId(@PathVariable Long passengerId) {
        List<BookingResponse> bookings = bookingService.getBookingsByPassengerId(passengerId);
        return new ResponseEntity<>(ApiResponse.success(bookings), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all bookings associated with a specific driver ID.
     *
     * @param driverId The ID of the driver.
     * @return ResponseEntity with a list of BookingResponse.
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByDriverId(@PathVariable Long driverId) {
        List<BookingResponse> bookings = bookingService.getBookingsByDriverId(driverId);
        return new ResponseEntity<>(ApiResponse.success(bookings), HttpStatus.OK);
    }

      /**
     * Endpoint to update the status of a booking.
     *
     * @param bookingId The ID of the booking to update.
     * @param newStatus The new status for the booking.
     * @return ResponseEntity with the updated BookingResponse.
     */
    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(@PathVariable Long bookingId,
                                                                         @RequestParam Booking.BookingStatus newStatus) {
        BookingResponse updatedBooking = bookingService.updateBookingStatus(bookingId, newStatus);
        return new ResponseEntity<>(ApiResponse.success(updatedBooking), HttpStatus.OK);
    }

     /**
     * Endpoint to assign a driver to a pending booking.
     *
     * @param bookingId The ID of the booking.
     * @param driverId The ID of the driver to assign.
     * @return ResponseEntity with the updated BookingResponse.
     */
    @PatchMapping("/{bookingId}/assignDriver/{driverId}")
    public ResponseEntity<ApiResponse<BookingResponse>> assignDriverToBooking(@PathVariable Long bookingId,
                                                                           @PathVariable Long driverId) {
        BookingResponse updatedBooking = bookingService.assignDriverToBooking(bookingId, driverId);
        return new ResponseEntity<>(ApiResponse.success(updatedBooking), HttpStatus.OK);
    }

    /**
     * Endpoint to cancel a booking.
     *
     * @param bookingId The ID of the booking to cancel.
     * @return ResponseEntity with the cancelled BookingResponse.
     */
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long bookingId) {
        BookingResponse cancelledBooking = bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>(ApiResponse.success(cancelledBooking), HttpStatus.OK);
    }

     /**
     * Endpoint to start a ride for a confirmed booking.
     *
     * @param bookingId The ID of the booking.
     * @param driverId The ID of the driver starting the ride.
     * @return ResponseEntity with the updated BookingResponse.
     */
    @PatchMapping("/{bookingId}/startRide/{driverId}")
    public ResponseEntity<ApiResponse<BookingResponse>> startRide(@PathVariable Long bookingId, @PathVariable Long driverId) {
        BookingResponse startedBooking = bookingService.startRide(bookingId, driverId);
        return new ResponseEntity<>(ApiResponse.success(startedBooking), HttpStatus.OK);
    }

    /**
     * Endpoint to complete a ride.
     *
     * @param bookingId The ID of the booking.
     * @param driverId The ID of the driver completing the ride.
     * @return ResponseEntity with the updated BookingResponse.
     */
    @PatchMapping("/{bookingId}/completeRide/{driverId}")
    public ResponseEntity<ApiResponse<BookingResponse>> completeRide(@PathVariable Long bookingId, @PathVariable Long driverId) {
        BookingResponse completedBooking = bookingService.completeRide(bookingId, driverId);
        return new ResponseEntity<>(ApiResponse.success(completedBooking), HttpStatus.OK);
    }

    /**
     * Endpoint to update payment details for a booking.
     *
     * @param bookingId The ID of the booking.
     * @param paymentStatus The payment status.
     * @param paymentId The payment transaction ID.
     * @return ResponseEntity with the updated BookingResponse.
     */
    @PatchMapping("/{bookingId}/payment")
    public ResponseEntity<ApiResponse<BookingResponse>> updatePaymentDetails(@PathVariable Long bookingId,
                                                                          @RequestParam boolean paymentStatus,
                                                                          @RequestParam(required = false) String paymentId) {
        BookingResponse updatedBooking = bookingService.updatePaymentDetails(bookingId, paymentStatus, paymentId);
        return new ResponseEntity<>(ApiResponse.success(updatedBooking), HttpStatus.OK);
    }

      /**
     * Endpoint to find all pending bookings that are awaiting driver assignment.
     *
     * @return ResponseEntity with a list of pending BookingResponse.
     */
    @GetMapping("/pending-assignment")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> findPendingBookingsForDriverAssignment() {
        List<BookingResponse> pendingBookings = bookingService.findPendingBookingsForDriverAssignment();
        return new ResponseEntity<>(ApiResponse.success(pendingBookings), HttpStatus.OK);
    }

}