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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.dto.request.ProductCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.ProductUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.service.ProductService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

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
    @ApiMessage("Create a product success")
    ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all products success")
    ResponseEntity<ApiPagination<ProductResponse>> getAllProducts(@Filter Specification<Product> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.productService.getAllProducts(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail product success")
    ResponseEntity<ProductResponse> getDetailProduct(
            @PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.productService.getDetailProduct(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a product success")
    ResponseEntity<ProductResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseEntity.ok().body(this.productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a product success")
    ResponseEntity<String> delete(@PathVariable("id") String id) {
        this.productService.delete(id);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/excel/import")
    @ApiMessage("Import data success")
    public ResponseEntity<?> importData(@RequestParam("file") MultipartFile file) {
        return this.productService.importData(file);
    }
}
