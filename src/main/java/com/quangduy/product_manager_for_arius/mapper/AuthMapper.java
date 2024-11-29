package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.response.AuthenticationResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);
}
