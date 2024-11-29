package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.UserUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
