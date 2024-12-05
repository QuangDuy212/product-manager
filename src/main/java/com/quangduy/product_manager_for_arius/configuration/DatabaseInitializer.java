package com.quangduy.product_manager_for_arius.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.entity.Permission;
import com.quangduy.product_manager_for_arius.entity.Role;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.repository.PermissionRepository;
import com.quangduy.product_manager_for_arius.repository.RoleRepository;
import com.quangduy.product_manager_for_arius.repository.UserRepository;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a product", "/api/v1/products", "POST", "PRODUCTS", true));
            arr.add(new Permission("Import product from excel", "/api/v1/products/excel/import", "POST", "PRODUCTS",
                    true));
            arr.add(new Permission("Update a product", "/api/v1/products", "PUT", "PRODUCTS", true));
            arr.add(new Permission("Delete a product", "/api/v1/products/{id}", "DELETE", "PRODUCTS", true));
            arr.add(new Permission("Get a product by id", "/api/v1/products/{id}", "GET", "PRODUCTS", true));
            arr.add(new Permission("Get products with pagination", "/api/v1/products", "GET", "PRODUCTS", true));

            arr.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES", true));
            arr.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES", true));
            arr.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES", true));
            arr.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES", true));
            arr.add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES", true));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS", true));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS", true));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS", true));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS", true));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS",
                    true));

            arr.add(new Permission("Create a tag", "/api/v1/tags", "POST", "TAGS", true));
            arr.add(new Permission("Update a tag", "/api/v1/tags", "PUT", "TAGS", true));
            arr.add(new Permission("Delete a tag", "/api/v1/tags/{id}", "DELETE", "TAGS", true));
            arr.add(new Permission("Get a tag by id", "/api/v1/tags/{id}", "GET", "TAGS", true));
            arr.add(new Permission("Get tags with pagination", "/api/v1/tags", "GET", "TAGS", true));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES", true));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES", true));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES", true));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES", true));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES", true));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS", true));
            arr.add(new Permission("Import user", "/api/v1/users/excel/import", "POST", "USERS", true));
            arr.add(new Permission("Export user", "/api/v1/users/excel/export", "GET", "USERS", true));
            arr.add(new Permission("Update a user", "/api/v1/users/{id}", "PUT", "USERS", true));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS", true));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS", true));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS", true));

            arr.add(new Permission("Create a order", "/api/v1/orders", "POST", "ORDERS", true));
            arr.add(new Permission("Export order", "/api/v1/orders/excel/export", "GET", "ORDERS", true));
            arr.add(new Permission("Update a order", "/api/v1/orders", "PUT", "ORDERS", true));
            arr.add(new Permission("Delete a order", "/api/v1/orders/{id}", "DELETE", "ORDERS", true));
            arr.add(new Permission("Get a order by id", "/api/v1/orders/{id}", "GET", "ORDERS", true));
            arr.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS", true));

            arr.add(new Permission("Upload a file", "/api/v1/files/upload", "POST", "FILES", true));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setAddress("hn");
            adminUser.setName("admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN")
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}