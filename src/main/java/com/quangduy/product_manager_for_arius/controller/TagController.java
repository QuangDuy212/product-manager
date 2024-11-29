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

import com.quangduy.product_manager_for_arius.dto.request.TagRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.TagResponse;
import com.quangduy.product_manager_for_arius.service.TagService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TagController {
    TagService tagService;

    @PostMapping
    ApiResponse<TagResponse> create(@RequestBody @Valid TagRequest request) {
        return ApiResponse.<TagResponse>builder()
                .data(this.tagService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<ApiPagination<TagResponse>> getAllCategories(Pageable pageable) {
        return ApiResponse.<ApiPagination<TagResponse>>builder()
                .data(this.tagService.getAllTags(pageable))
                .build();
    }

    @GetMapping("/{tagId}")
    ApiResponse<TagResponse> getDetailCategory(
            @PathVariable("tagId") String tagId) {
        return ApiResponse.<TagResponse>builder()
                .data(this.tagService.getDetailTag(tagId))
                .build();
    }

    @PutMapping("/{tagId}")
    ApiResponse<TagResponse> update(@PathVariable("tagId") String tagId,
            @RequestBody @Valid TagRequest request) {
        return ApiResponse.<TagResponse>builder()
                .data(this.tagService.update(tagId, request))
                .build();
    }

    @DeleteMapping("/{tagId}")
    ApiResponse<String> delete(@PathVariable("tagId") String tagId) {
        this.tagService.delete(tagId);
        return ApiResponse.<String>builder()
                .data("Delete success")
                .build();
    }
}
