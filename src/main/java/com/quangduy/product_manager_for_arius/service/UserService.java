package com.quangduy.product_manager_for_arius.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.constant.PredefinedRole;
import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.UserUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.Role;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.UserMapper;
import com.quangduy.product_manager_for_arius.repository.RoleRepository;
import com.quangduy.product_manager_for_arius.repository.UserRepository;
import com.quangduy.product_manager_for_arius.service.importfile.UserExcelImport;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleService roleService;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    UserExcelImport userExcelImport;

    public UserResponse create(UserCreationRequest request) {
        log.info("Create a user");
        User user = this.userMapper.toUser(request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));

        if (request.getRole() == null) {
            request.setRole(PredefinedRole.USER_ROLE);
            Role role = this.roleService.findByName(PredefinedRole.USER_ROLE);
            user.setRole(role);
        } else {
            Role role = this.roleService.findByName(request.getRole());
            user.setRole(role);
        }

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        log.info("Get my info");
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse update(String userId, UserUpdateRequest request) {
        log.info("Update a user");
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() != null) {
            Role role = this.roleService.findByName(request.getRole());
            user.setRole(role);
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public void delete(String userId) {
        log.info("Delete a user");
        userRepository.deleteById(userId);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public ApiPagination<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Get all users");
        Page<User> pageUser = this.userRepository.findAll(pageable);

        List<UserResponse> listUser = pageUser.getContent().stream().map(userMapper::toUserResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        return ApiPagination.<UserResponse>builder()
                .meta(mt)
                .result(listUser)
                .build();
    }

    public List<UserResponse> getAllUsers() {
        log.info("Get all users");
        List<User> entities = this.userRepository.findAll();
        List<UserResponse> res = entities.stream().map(userMapper::toUserResponse).toList();
        return res;
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getDetailUser(String id) {
        log.info("Get detail a user");
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public List<UserResponse> saveFromFileExcel(MultipartFile file) {
        List<User> entites = new ArrayList<User>();
        try {
            List<User> data = userExcelImport.excelToStuList(file.getInputStream());
            entites = userRepository.saveAll(data);
        } catch (IOException ex) {
            throw new RuntimeException("Excel data is failed to store: " + ex.getMessage());
        }
        List<UserResponse> res = entites.stream().map(userMapper::toUserResponse).toList();
        return res;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public void updateUserToken(String token, String username) {
        User currentUser = this.handleGetUserByUsername(username);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public boolean isExistByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public void handleLogout(User user) {
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }

    public User getUserByRefreshTokenAndUsername(String token, String username) {
        return this.userRepository.findByRefreshTokenAndUsername(token, username);
    }
}
