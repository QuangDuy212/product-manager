package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.quangduy.product_manager_for_arius.dto.request.ProductCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.ProductUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.dto.response.es.ESProductResponse;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.es.ESProduct;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);

    Product toProduct(ProductCreationRequest request);

    Product toProduct(ProductUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

    ESProduct toESProduct(Product product);

    ESProductResponse toESProductResponse(ESProduct product);
}
