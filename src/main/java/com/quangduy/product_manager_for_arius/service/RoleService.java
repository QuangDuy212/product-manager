package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.RoleRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.RoleResponse;
import com.quangduy.product_manager_for_arius.entity.Permission;
import com.quangduy.product_manager_for_arius.entity.Role;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.RoleMapper;
import com.quangduy.product_manager_for_arius.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    PermissionService permissionService;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        log.info("Create a role");
        Role role = this.roleMapper.toRole(request);
        if (request.getPerIds() != null) {
            List<Permission> pers = this.permissionService.fetchPersByListIds(request.getPerIds());
            role.setPermissions(pers);
        }
        role.setActive(true);
        return this.roleMapper.toRoleResponse(this.roleRepository.save(role));
    }

    public RoleResponse update(String id, RoleRequest request) {
        log.info("Update a role");
        Role entityDB = this.roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        if (request.getPerIds() != null) {
            List<Permission> pers = this.permissionService.fetchPersByListIds(request.getPerIds());
            entityDB.setPermissions(pers);
        }
        boolean active = entityDB.isActive();
        this.roleMapper.updateRole(entityDB, request);
        if (request.isActive() == true)
            entityDB.setActive(true);
        return this.roleMapper.toRoleResponse(this.roleRepository.save(entityDB));
    }

    public RoleResponse getDetailRole(String id) {
        log.info("Get detail role");
        Role entityDB = this.roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.roleMapper.toRoleResponse(entityDB);
    }

    public ApiPagination<RoleResponse> getAllRoles(Pageable pageable) {
        log.info("Get all roles");
        Page<Role> page = this.roleRepository.findAll(pageable);

        List<RoleResponse> list = page.getContent().stream()
                .map(roleMapper::toRoleResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        return ApiPagination.<RoleResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    public String delete(String id) {
        log.info("Delete a role");
        Role role = this.roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        role.setActive(false);
        this.roleRepository.save(role);
        return "Delete success";
    }

    public Role findByName(String name) {
        return this.roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
    }
}
