package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.Response;
import com.quangduy.product_manager_for_arius.dto.request.CategoryRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.ApiString;
import com.quangduy.product_manager_for_arius.dto.response.CategoryResponse;
import com.quangduy.product_manager_for_arius.entity.Category;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.service.CategoryService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    @ApiMessage("Create a category success")
    ResponseEntity<CategoryResponse> create(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all categories success")
    ResponseEntity<ApiPagination<CategoryResponse>> getAllCategories(@Filter Specification<Category> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.categoryService.getAllCategories(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail category success")
    ResponseEntity<CategoryResponse> getDetailCategory(
            @PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.categoryService.getDetailCategory(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a category success")
    ResponseEntity<CategoryResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok().body(this.categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a category success")
    ResponseEntity<ApiString> delete(@PathVariable("id") String id) {
        this.categoryService.delete(id);
        return ResponseEntity.ok().body(ApiString.builder()
                .message("success")
                .build());
    }
}
