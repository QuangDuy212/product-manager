package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.CategoryRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.CategoryResponse;
import com.quangduy.product_manager_for_arius.service.CategoryService;

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
    ApiResponse<CategoryResponse> create(@RequestBody @Valid CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<ApiPagination<CategoryResponse>> getAllCategories(Pageable pageable) {
        return ApiResponse.<ApiPagination<CategoryResponse>>builder()
                .data(categoryService.getAllCategories(pageable))
                .build();
    }

    @GetMapping("/{categoryId}")
    ApiResponse<CategoryResponse> getDetailCategory(
            @PathVariable("categoryId") String categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .data(this.categoryService.getDetailCategory(categoryId))
                .build();
    }

    @PutMapping("/{categoryId}")
    ApiResponse<CategoryResponse> update(@PathVariable("categoryId") String categoryId,
            @RequestBody @Valid CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryService.update(categoryId, request))
                .build();
    }

    @DeleteMapping("/{categoryId}")
    ApiResponse<String> delete(@PathVariable("categoryId") String categoryId) {
        this.categoryService.delete(categoryId);
        return ApiResponse.<String>builder()
                .data("Delete success")
                .build();
    }
}
