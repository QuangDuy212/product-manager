package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.quangduy.product_manager_for_arius.dto.request.RoleRequest;
import com.quangduy.product_manager_for_arius.dto.response.RoleResponse;
import com.quangduy.product_manager_for_arius.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);

    Role toRole(RoleRequest request);
}
