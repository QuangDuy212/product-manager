package com.quangduy.product_manager_for_arius.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.service.S3FileUploadService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileUploadController {

    S3FileUploadService fileUploadService;

    @PostMapping("/upload")
    @ApiMessage("Upload file success")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder)
            throws IOException {
        return ResponseEntity.ok().body(fileUploadService.uploadFile(file, "quangduy/" + folder));
    }
}
