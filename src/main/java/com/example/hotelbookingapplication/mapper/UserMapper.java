package com.example.hotelbookingapplication.mapper;

import com.example.hotelbookingapplication.dto.request.UpsertUserRequest;
import com.example.hotelbookingapplication.dto.response.UserResponse;
import com.example.hotelbookingapplication.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User requestToUser(UpsertUserRequest request);

    User requestToUser(Integer id,UpsertUserRequest request);

    UserResponse userToResponse(User user);

    @Mapping(target = "roles",ignore = true)
    void update(@MappingTarget User destination, User root);
}
