package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.RoleRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.RoleResponse;
import com.quangduy.product_manager_for_arius.entity.Role;
import com.quangduy.product_manager_for_arius.entity.Tag;
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
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        log.info("Create a role");
        Role role = this.roleMapper.toRole(request);
        return this.roleMapper.toRoleResponse(this.roleRepository.save(role));
    }

    public RoleResponse update(String name, RoleRequest request) {
        log.info("Update a role");
        Role entityDB = this.roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return this.roleMapper.toRoleResponse(this.roleRepository.save(entityDB));
    }

    public RoleResponse getDetailTag(String name) {
        log.info("Get detail role");
        Role entityDB = this.roleRepository.findById(name)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.roleMapper.toRoleResponse(entityDB);
    }

    public ApiPagination<RoleResponse> getAllTags(Pageable pageable) {
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

    public void delete(String name) {
        log.info("Delete a role");
        this.roleRepository.deleteById(name);
    }

    public Role findByName(String name) {
        return this.roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
    }
}
