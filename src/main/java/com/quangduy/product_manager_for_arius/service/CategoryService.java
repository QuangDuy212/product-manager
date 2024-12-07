package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.CategoryRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.CategoryResponse;
import com.quangduy.product_manager_for_arius.entity.Category;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.CategoryMapper;
import com.quangduy.product_manager_for_arius.repository.CategoryRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    ProductService productService;

    public CategoryResponse create(CategoryRequest request) {
        log.info("Create a category");
        if (this.categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        Category category = this.categoryMapper.toCategory(request);
        return this.categoryMapper.toCategoryResponse(this.categoryRepository.save(category));
    }

    public CategoryResponse update(String categoryId, CategoryRequest request) {
        log.info("Update a category");
        if (this.categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        Category categoryDB = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        String oldName = categoryDB.getName();
        categoryDB.setName(request.getName());
        List<Product> products = categoryDB.getProducts();
        for (Product product : products) {
            product.setCategory(categoryDB);
            this.productService.save(product); // Cập nhật sản phẩm
        }
        this.categoryRepository.save(categoryDB);
        return this.categoryMapper.toCategoryResponse(categoryDB);
    }

    public CategoryResponse getDetailCategory(String categoryId) {
        log.info("Get detail category");
        Category categoryDB = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.categoryMapper.toCategoryResponse(categoryDB);
    }

    public ApiPagination<CategoryResponse> getAllCategories(Specification<Category> spec, Pageable pageable) {
        log.info("Get all categories");
        Page<Category> pageCategories = this.categoryRepository.findAll(spec, pageable);

        List<CategoryResponse> listCategories = pageCategories.getContent().stream()
                .map(categoryMapper::toCategoryResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCategories.getTotalPages());
        mt.setTotal(pageCategories.getTotalElements());

        return ApiPagination.<CategoryResponse>builder()
                .meta(mt)
                .result(listCategories)
                .build();
    }

    public void delete(String categoryId) {
        log.info("Delete a category");
        Category cate = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<Product> product = cate.getProducts();
        this.productService.deleteAll(product);
        this.categoryRepository.deleteById(categoryId);
    }
}
