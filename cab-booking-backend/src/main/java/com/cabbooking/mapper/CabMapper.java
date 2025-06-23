package com.cabbooking.mapper;

import com.cabbooking.dto.response.CabResponse;
import com.cabbooking.model.Cab;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CabMapper {

    @Mapping(target = "status", expression = "java(cab.getStatus().name())")
    @Mapping(target = "vehicleType", expression = "java(cab.getVehicleType().name())")
    CabResponse toCabResponse(Cab cab);
}
