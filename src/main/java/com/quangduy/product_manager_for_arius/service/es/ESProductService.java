package com.quangduy.product_manager_for_arius.service.es;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.dto.response.es.ESProductResponse;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.entity.es.ESProduct;
import com.quangduy.product_manager_for_arius.mapper.ProductMapper;
import com.quangduy.product_manager_for_arius.repository.es.ESProductRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ESProductService {
    ESProductRepository esProductRepository;
    ProductMapper productMapper;

    public ESProductResponse create(ESProduct request) {
        return this.productMapper.toESProductResponse(this.esProductRepository.save(request));
    }

    public ApiPagination<ESProductResponse> searchByName(String query, Pageable pageable) {

        Page<ESProduct> page = this.esProductRepository.searchByQuery(query, pageable);

        List<ESProductResponse> list = page.getContent().stream().map(productMapper::toESProductResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        return ApiPagination.<ESProductResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }
}
