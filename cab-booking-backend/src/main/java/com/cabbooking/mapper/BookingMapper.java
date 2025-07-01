package com.cabbooking.mapper;

import com.cabbooking.dto.response.BookingResponse;
import com.cabbooking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BookingMapper {

    @Mappings({
        @Mapping(target = "status", expression = "java(booking.getStatus().name())"),
        @Mapping(target = "passenger", source = "passenger"),
        @Mapping(target = "driver", source = "driver")
    })
    BookingResponse toBookingResponse(Booking booking);

}