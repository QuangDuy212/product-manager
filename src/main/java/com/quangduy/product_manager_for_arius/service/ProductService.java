package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.ProductCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.ProductUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.entity.Category;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.Tag;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.CategoryMapper;
import com.quangduy.product_manager_for_arius.mapper.ProductMapper;
import com.quangduy.product_manager_for_arius.mapper.TagMapper;
import com.quangduy.product_manager_for_arius.repository.CategoryRepository;
import com.quangduy.product_manager_for_arius.repository.ProductRepository;
import com.quangduy.product_manager_for_arius.repository.TagRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    TagRepository tagRepository;
    ProductMapper productMapper;
    TagMapper tagMapper;
    CategoryMapper categoryMapper;

    public ProductResponse create(ProductCreationRequest request) {
        log.info("Create a product");
        Product product = this.productMapper.toProduct(request);
        if (request.getCategoryId() != null) {
            Category cate = this.categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(cate);
        }
        if (request.getTagsId() != null) {
            List<Tag> tags = this.tagRepository.findByIdIn(request.getTagsId());
            product.setTags(tags);
        }
        ProductResponse res = this.productMapper.toProductResponse(this.productRepository.save(product));
        if (product.getCategory() != null) {
            res.setCategory(this.categoryMapper.toCategoryResponse(product.getCategory()));
        }
        if (product.getTags() != null)
            res.setTags(product.getTags().stream().map(tagMapper::toTagResponse).toList());
        return res;
    }

    public ProductResponse update(String productId, ProductUpdateRequest request) {
        log.info("Update a product");
        Product productDB = this.productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        this.productMapper.updateProduct(productDB, request);
        if (request.getCategoryId() != null) {
            Category cate = this.categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            productDB.setCategory(cate);
        }
        if (request.getTagsId() != null) {
            List<Tag> tags = this.tagRepository.findByIdIn(request.getTagsId());
            productDB.setTags(tags);
        }
        ProductResponse res = this.productMapper.toProductResponse(this.productRepository.save(productDB));
        if (productDB.getCategory() != null) {
            res.setCategory(this.categoryMapper.toCategoryResponse(productDB.getCategory()));
        }
        if (productDB.getTags() != null)
            res.setTags(productDB.getTags().stream().map(tagMapper::toTagResponse).toList());
        return res;
    }

    public ProductResponse getDetailProduct(String productId) {
        log.info("Get detail product");
        Product tagDB = this.productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.productMapper.toProductResponse(tagDB);
    }

    public ApiPagination<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Get all products");
        Page<Product> page = this.productRepository.findAll(pageable);

        List<ProductResponse> list = page.getContent().stream()
                .map(productMapper::toProductResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        return ApiPagination.<ProductResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    public void delete(String productId) {
        log.info("Delete a product");
        this.productRepository.deleteById(productId);
    }
}
