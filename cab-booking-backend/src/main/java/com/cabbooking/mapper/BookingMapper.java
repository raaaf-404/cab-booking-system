package com.cabbooking.mapper;

import com.cabbooking.dto.request.BookingRegistrationRequest;
import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BookingMapper {

    /**
     * Converts a Booking entity to a BookingResponse DTO.
     * It uses the UserMapper to convert the nested passenger and driver objects.
     */
    @Mappings({
        @Mapping(target = "status", expression = "java(booking.getStatus().name())"),
        @Mapping(target = "passenger", source = "passenger"),
        @Mapping(target = "driver", source = "driver")
    })
    BookingResponse toBookingResponse(Booking booking);

    /**
     * Converts a BookingRegistrationRequest DTO to a Booking entity.
     * Note: It ignores fields like id, passenger, and driver, as these
     * will be set by the service layer after fetching the required objects.
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "passenger", ignore = true), // Ignored: Service will fetch and set this from passengerId
        @Mapping(target = "driver", ignore = true),    // Ignored: Service will assign a driver later
        @Mapping(target = "status", ignore = true),   // Ignored: Entity has a default value
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Booking toBookingEntity(BookingRegistrationRequest request);
}