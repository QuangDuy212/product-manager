package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.Mapper;

import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.response.AuthenticationResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthenticationResponse toAuthResponse(User user);

    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);
}
