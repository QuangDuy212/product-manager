package com.quangduy.product_manager_for_arius.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.dto.request.ProductCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.ProductUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.Category;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.Tag;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.CategoryMapper;
import com.quangduy.product_manager_for_arius.mapper.ProductMapper;
import com.quangduy.product_manager_for_arius.mapper.TagMapper;
import com.quangduy.product_manager_for_arius.repository.CategoryRepository;
import com.quangduy.product_manager_for_arius.repository.ProductRepository;
import com.quangduy.product_manager_for_arius.repository.TagRepository;
import com.quangduy.product_manager_for_arius.service.importfile.ProductExcelImport;

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
    ProductExcelImport productExcelImport;

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

    public List<ProductResponse> saveFromFileExcel(MultipartFile file) {
        List<Product> entites = new ArrayList<Product>();
        try {
            List<Product> data = productExcelImport.excelToStuList(file.getInputStream());
            entites = productRepository.saveAll(data);
        } catch (IOException ex) {
            throw new RuntimeException("Excel data is failed to store: " + ex.getMessage());
        }
        List<ProductResponse> res = entites.stream().map(productMapper::toProductResponse).toList();
        return res;
    }

    public ApiResponse<?> importData(MultipartFile file) {
        String message = "";
        if (productExcelImport.hasExcelFormat(file)) {
            try {
                List<ProductResponse> res = this.saveFromFileExcel(file);
                message = "The Excel file is uploaded: " + file.getOriginalFilename();
                return ApiResponse.<List<ProductResponse>>builder()
                        .data(res)
                        .build();
            } catch (Exception exp) {
                message = "The Excel file is not upload: " + file.getOriginalFilename() + "!";
                // return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
                return ApiResponse.<String>builder()
                        .data(message)
                        .build();
            }
        }
        message = "Please upload an excel file!";
        return ApiResponse.<String>builder()
                .data(message)
                .build();
    }
}
