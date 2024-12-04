package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.PermissionRequest;
import com.quangduy.product_manager_for_arius.dto.request.RoleRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiString;
import com.quangduy.product_manager_for_arius.dto.response.PermissionResponse;
import com.quangduy.product_manager_for_arius.entity.Permission;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.dto.response.PermissionResponse;
import com.quangduy.product_manager_for_arius.service.PermissionService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    @ApiMessage("Create a permission success")
    ResponseEntity<PermissionResponse> createUser(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all permissions success")
    ResponseEntity<ApiPagination<PermissionResponse>> getUsers(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail permission success")
    ResponseEntity<PermissionResponse> getUser(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.permissionService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a permission success")
    ResponseEntity<ApiString> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ResponseEntity.ok().body(ApiString.builder()
                .message("success")
                .build());
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a permission success")
    ResponseEntity<PermissionResponse> updateUser(@PathVariable String id, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok().body(this.permissionService.update(id, request));
    }
}
