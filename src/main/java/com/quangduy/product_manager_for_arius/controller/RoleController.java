package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
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

import com.quangduy.product_manager_for_arius.dto.request.RoleRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.RoleResponse;
import com.quangduy.product_manager_for_arius.service.RoleService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    @ApiMessage("Create a role success")
    ResponseEntity<RoleResponse> createUser(@RequestBody @Valid RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all roles success")
    ResponseEntity<ApiPagination<RoleResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAllRoles(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail role success")
    ResponseEntity<RoleResponse> getUser(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.roleService.getDetailRole(id));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a role success")
    ResponseEntity<String> delete(@PathVariable String id) {
        return ResponseEntity.ok().body(roleService.delete(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a role success")
    ResponseEntity<RoleResponse> updateUser(@PathVariable String id, @RequestBody RoleRequest request) {
        return ResponseEntity.ok().body(this.roleService.update(id, request));
    }
}
