package com.cabbooking.controller;

import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.request.CabUpdateRequest;
import com.cabbooking.dto.request.LocationUpdateRequest;
import com.cabbooking.dto.request.CabUpdateAvailabilityStatusRequest;
import com.cabbooking.dto.request.DriverAssignmentRequest;
import com.cabbooking.dto.response.ApiResponse;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.service.CabService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("api/v1/cabs")
@RequiredArgsConstructor
public class CabController {

    private final CabService cabService;

    /**
     * Endpoint to create a new cab.
     *
     * @param request the cab registration request containing cab details
     * @return ResponseEntity with the created CabResponse and HTTPStatus.CREATED
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CabResponse>> registerCab(@Valid @RequestBody CabRegistrationRequest request) {
        // 1. Call the service to create the cab
        CabResponse newCab = cabService.registerCab(request);

        // 2. Build the URI for the newly created resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Starts with the current request URI (/api/v1/cabs)
                .path("/{id}")        // Appends the path variable for the ID
                .buildAndExpand(newCab.getId()) // Populates the ID from the created cab
                .toUri();                       // Converts it to a URI

        // 3. Return a 201 Created response with the Location header and the response body
        return ResponseEntity.created(location).body(ApiResponse.success(newCab));
    }

    /**
     * Endpoint to retrieve a cab by its ID.
     *
     * @param cabId the ID of the cab to retrieve
     * @return ResponseEntity with the CabResponse and HTTPStatus.OK, or HTTPStatus.NOT_FOUND if not found
     */
    @GetMapping("/{cabId}")
    public ResponseEntity<ApiResponse<CabResponse>> getCabById(@PathVariable("cabId") Long cabId) {
        CabResponse cabResponse = cabService.getCabById(cabId);
        return ResponseEntity.ok(ApiResponse.success(cabResponse));
    }

      /**
     * Endpoint to retrieve a cab by its ID.
     *
     * @param licensePlateNumber the license plate number of the cab to retrieve
     * @return ResponseEntity with the CabResponse and HTTPStatus.OK, or HTTPStatus.NOT_FOUND if not found
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CabResponse>> getCabByLicensePlate(@RequestParam("licensePlate") String licensePlate) {
        CabResponse cabResponse = cabService.getCabByLicensePlate(licensePlate);
        return ResponseEntity.ok(ApiResponse.success(cabResponse));
    }

     /**
     * Endpoint to update cab details.
     *
     * @param cabId the ID of the cab to retrieve, and @param request the cab registration request containing cab details
     * @return ResponseEntity with the CabResponse and HTTPStatus.OK, or HTTPStatus.NOT_FOUND if not found
     */
    @PatchMapping("/{cabId}/cab-details")
    public ResponseEntity<ApiResponse<CabResponse>> updateCabDetails(@PathVariable Long cabId, 
                                                                @RequestBody CabUpdateRequest request) {
    CabResponse updatedCab = cabService.updateCabDetails(cabId, request);
    return ResponseEntity.ok(ApiResponse.success(updatedCab));
    }

    @PatchMapping("/{cabId}/location")
    public ResponseEntity<ApiResponse<CabResponse>> updateCabLocation(@PathVariable Long cabId,
                                                                    @Valid @RequestBody LocationUpdateRequest request) {
    CabResponse updatedCabLocation = cabService.updateCabLocation(cabId, request);
    return ResponseEntity.ok(ApiResponse.success(updatedCabLocation));
    }

    @PatchMapping("/{cabId}/cab-availability-status")
    public ResponseEntity<ApiResponse<CabResponse>> updateCabAvailabilityStatus(
           @PathVariable Long cabId,
           @Valid @RequestBody CabUpdateAvailabilityStatusRequest request) {
    CabResponse updatedCabStatus = cabService.updateCabAvailabilityStatus(cabId, request);
    return ResponseEntity.ok(ApiResponse.success(updatedCabStatus));
    }

    @PatchMapping("/{cabId}/assign-driver")
    public ResponseEntity<ApiResponse<CabResponse>> assignDriverToCab(
           @PathVariable Long cabId,
           @Valid @RequestBody DriverAssignmentRequest request) {
    CabResponse assignedCab = cabService.assignDriverToCab(cabId, request);
    return ResponseEntity.ok(ApiResponse.success(assignedCab));
    }

    @PatchMapping("/{cabId}/remove-driver")
    public ResponseEntity<ApiResponse<CabResponse>> removeDriverFromCab(@PathVariable Long cabId) {
        CabResponse removedCabDriver = cabService.removeDriverFromCab(cabId);
        return ResponseEntity.ok(ApiResponse.success(removedCabDriver));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CabResponse>>> findAvailableCabs(
            @RequestParam(required = false) Cab.VehicleType vehicleType) {
        List<CabResponse> availableCabs = cabService.findAvailableCabs(vehicleType);
        return ResponseEntity.ok(ApiResponse.success(availableCabs));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CabResponse>>> getAllCabs(Pageable pageable) {
        Page<CabResponse> allCabsPage = cabService.getAllCabs(pageable);
        return ResponseEntity.ok(ApiResponse.success(allCabsPage));
    }

    @DeleteMapping("/{cabId}")
    public ResponseEntity<ApiResponse<Void>> deleteCab(@PathVariable Long cabId) {
        cabService.deleteCab(cabId);
        return ResponseEntity.noContent().build();
    }
}