package com.cabbooking.controller;

import org.springframework.web.bind.annotation.RestController;
import com.cabbooking.dto.request.CabRegistrationRequest;
import com.cabbooking.dto.response.ApiResponse;
import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.service.CabService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

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
}