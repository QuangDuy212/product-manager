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

import com.quangduy.product_manager_for_arius.dto.request.ProductCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.ProductUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.service.ProductService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {
    ProductService productService;

    @PostMapping
    ApiResponse<ProductResponse> create(@RequestBody @Valid ProductCreationRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(this.productService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<ApiPagination<ProductResponse>> getAllProducts(Pageable pageable) {
        return ApiResponse.<ApiPagination<ProductResponse>>builder()
                .result(this.productService.getAllProducts(pageable))
                .build();
    }

    @GetMapping("/{productId}")
    ApiResponse<ProductResponse> getDetailProduct(
            @PathVariable("productId") String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(this.productService.getDetailProduct(productId))
                .build();
    }

    @PutMapping("/{productId}")
    ApiResponse<ProductResponse> update(@PathVariable("productId") String productId,
            @RequestBody @Valid ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(this.productService.update(productId, request))
                .build();
    }

    @DeleteMapping("/{productId}")
    ApiResponse<String> delete(@PathVariable("productId") String productId) {
        this.productService.delete(productId);
        return ApiResponse.<String>builder()
                .result("Delete success")
                .build();
    }
}
