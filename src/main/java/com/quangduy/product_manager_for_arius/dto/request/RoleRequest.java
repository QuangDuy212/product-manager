package com.quangduy.product_manager_for_arius.dto.request;

import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @Size(min = 3, message = "INVALID_ROLE_NAME")
    String name;
    String description;
    boolean active;
    List<String> perIds;
}
