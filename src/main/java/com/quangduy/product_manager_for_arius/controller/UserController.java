package com.quangduy.product_manager_for_arius.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.UserUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.service.UserService;
import com.quangduy.product_manager_for_arius.service.export.UserExcelExporter;
import com.quangduy.product_manager_for_arius.service.importfile.UserExcelImport;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;
    UserExcelImport userExcelImport;

    @PostMapping
    @ApiMessage("Create a user success")
    ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all users success")
    ResponseEntity<ApiPagination<UserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok().body(this.userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail user success")
    ResponseEntity<UserResponse> getUser(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.userService.getDetailUser(id));
    }

    @GetMapping("/my-info")
    @ApiMessage("Get my infor success")
    ResponseEntity<UserResponse> getMyInfo() {
        return ResponseEntity.ok().body(this.userService.getMyInfo());
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a user success")
    ResponseEntity<String> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok().body("ok");
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a user success")
    ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok().body(this.userService.update(id, request));
    }

    @GetMapping("/excel/export")
    @ApiMessage("Export all users success")
    public ResponseEntity<String> exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<UserResponse> listUsers = this.userService.getAllUsers();

        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);

        excelExporter.export(response);

        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/excel/import")
    @ApiMessage("Import all users success")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        if (userExcelImport.hasExcelFormat(file)) {
            try {
                List<UserResponse> res = userService.saveFromFileExcel(file);
                message = "The Excel file is uploaded: " + file.getOriginalFilename();

                return ResponseEntity.ok().body(res);
            } catch (Exception exp) {
                message = "The Excel file is not upload: " + file.getOriginalFilename() + "!";
                // return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
