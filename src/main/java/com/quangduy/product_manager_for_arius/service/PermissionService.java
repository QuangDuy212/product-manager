package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.PermissionRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.PermissionResponse;
import com.quangduy.product_manager_for_arius.entity.Permission;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.PermissionMapper;
import com.quangduy.product_manager_for_arius.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionMapper permissionMapper;
    PermissionRepository permissionRepository;

    public PermissionResponse create(PermissionRequest request) {
        log.info("Create a permission");
        Permission permission = this.permissionMapper.toPermission(request);
        permission.setActive(true);
        return this.permissionMapper.toPermissionResponse(this.permissionRepository.save(permission));
    }

    public PermissionResponse update(String id, PermissionRequest request) {
        log.info("Update a permission");
        Permission entityDB = this.permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        this.permissionMapper.updatePermission(entityDB, request);
        if (request.isActive() == true)
            entityDB.setActive(request.isActive());
        return this.permissionMapper.toPermissionResponse(this.permissionRepository.save(entityDB));
    }

    public PermissionResponse getDetail(String id) {
        log.info("Get detail permission");
        Permission entityDB = this.permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.permissionMapper.toPermissionResponse(entityDB);
    }

    public ApiPagination<PermissionResponse> getAll(Specification<Permission> spec, Pageable pageable) {
        log.info("Get all permissions");
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);

        List<PermissionResponse> list = page.getContent().stream()
                .map(permissionMapper::toPermissionResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        return ApiPagination.<PermissionResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    public void delete(String id) {
        log.info("Delete a permission");
        Permission per = this.permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        per.setActive(false);
        this.permissionRepository.save(per);
    }

    public List<Permission> fetchPersByListIds(List<String> ids) {
        return this.permissionRepository.findByIdIn(ids);
    }
}
