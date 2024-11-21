package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.UserUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    // @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
