package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.quangduy.product_manager_for_arius.dto.request.TagRequest;
import com.quangduy.product_manager_for_arius.dto.response.TagResponse;
import com.quangduy.product_manager_for_arius.entity.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toTagResponse(Tag tag);

    Tag toTag(TagRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTag(@MappingTarget Tag tag, TagRequest request);
}
