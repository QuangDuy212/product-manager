package com.quangduy.product_manager_for_arius.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
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
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<ApiPagination<UserResponse>> getUsers(Pageable pageable) {
        return ApiResponse.<ApiPagination<UserResponse>>builder()
                .result(userService.getUsers(pageable))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @GetMapping("/excel/export")
    public ApiResponse<String> exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<UserResponse> listUsers = this.userService.getUsers();

        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);

        excelExporter.export(response);

        return ApiResponse.<String>builder()
                .result("Export success")
                .build();
    }

    @PostMapping("/excel/import")
    public ApiResponse<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        if (userExcelImport.hasExcelFormat(file)) {
            try {
                List<UserResponse> res = userService.saveFromFileExcel(file);
                message = "The Excel file is uploaded: " + file.getOriginalFilename();
                return ApiResponse.<List<UserResponse>>builder()
                        .result(res)
                        .build();
            } catch (Exception exp) {
                message = "The Excel file is not upload: " + file.getOriginalFilename() + "!";
                // return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
                return ApiResponse.<String>builder()
                        .result(message)
                        .build();
            }
        }
        message = "Please upload an excel file!";
        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }
}
