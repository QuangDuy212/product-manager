package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
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
import com.quangduy.product_manager_for_arius.dto.response.CategoryResponse;
import com.quangduy.product_manager_for_arius.service.CategoryService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

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
    ResponseEntity<ApiPagination<CategoryResponse>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok().body(this.categoryService.getAllCategories(pageable));
    }

    @GetMapping("/{categoryId}")
    @ApiMessage("Get detail category success")
    ResponseEntity<CategoryResponse> getDetailCategory(
            @PathVariable("categoryId") String categoryId) {
        return ResponseEntity.ok().body(this.categoryService.getDetailCategory(categoryId));
    }

    @PutMapping("/{categoryId}")
    @ApiMessage("Update a category success")
    ResponseEntity<CategoryResponse> update(@PathVariable("categoryId") String categoryId,
            @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok().body(this.categoryService.update(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    @ApiMessage("Delete a category success")
    ResponseEntity<String> delete(@PathVariable("categoryId") String categoryId) {
        this.categoryService.delete(categoryId);
        return ResponseEntity.ok().body(null);
    }
}
