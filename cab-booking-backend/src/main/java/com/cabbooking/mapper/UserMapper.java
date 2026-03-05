package com.cabbooking.mapper;

import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * componentModel = "spring" allows for @Autowired injection.
 * We only need to specify mappings where the field names differ.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {


    // are mapped automatically because the names match exactly.
    UserResponse toResponse(User user);

    // Pro-tip: MapStruct handles lists automatically by reusing the method above!
    // List<UserResponse> toResponseList(List<User> users);
}