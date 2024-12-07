package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.TagRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.TagResponse;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.Tag;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.TagMapper;
import com.quangduy.product_manager_for_arius.repository.TagRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TagService {
    TagRepository tagRepository;
    TagMapper tagMapper;
    ProductService productService;

    public TagResponse create(TagRequest request) {
        log.info("Create a tag");
        Tag tag = this.tagMapper.toTag(request);
        return this.tagMapper.toTagResponse(this.tagRepository.save(tag));
    }

    public TagResponse update(String categoryId, TagRequest request) {
        log.info("Update a tag");
        Tag tagDB = this.tagRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        this.tagMapper.updateTag(tagDB, request);
        return this.tagMapper.toTagResponse(this.tagRepository.save(tagDB));
    }

    public TagResponse getDetailTag(String categoryId) {
        log.info("Get detail tag");
        Tag tagDB = this.tagRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.tagMapper.toTagResponse(tagDB);
    }

    public ApiPagination<TagResponse> getAllTags(Specification<Tag> spec, Pageable pageable) {
        log.info("Get all tags");
        Page<Tag> page = this.tagRepository.findAll(spec, pageable);

        List<TagResponse> list = page.getContent().stream()
                .map(tagMapper::toTagResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        return ApiPagination.<TagResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    public void delete(String id) {
        log.info("Delete a tag");
        Tag tag = this.tagRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        List<Product> products = tag.getProducts();
        products.forEach(x -> {
            x.setTags(null);
            this.productService.save(x);
        });
        this.tagRepository.deleteById(id);
    }

    public Tag findByName(String name) {
        return this.tagRepository.findByName(name);
    }
}
