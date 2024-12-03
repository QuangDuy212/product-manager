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
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.TagRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.TagResponse;
import com.quangduy.product_manager_for_arius.entity.Tag;
import com.quangduy.product_manager_for_arius.service.TagService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

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
    @ApiMessage("Create a tag success")
    ResponseEntity<TagResponse> create(@RequestBody @Valid TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.tagService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all tags success")
    ResponseEntity<ApiPagination<TagResponse>> getAllTags(@Filter Specification<Tag> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.tagService.getAllTags(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail tag success")
    ResponseEntity<TagResponse> getDetailTag(
            @PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.tagService.getDetailTag(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a tag success")
    ResponseEntity<TagResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid TagRequest request) {
        return ResponseEntity.ok().body(this.tagService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a tag success")
    ResponseEntity<String> delete(@PathVariable("id") String id) {
        this.tagService.delete(id);
        return ResponseEntity.ok().body("ok");
    }
}
