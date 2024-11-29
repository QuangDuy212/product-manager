package com.quangduy.product_manager_for_arius.dto.request;

import java.time.LocalDate;

import com.quangduy.product_manager_for_arius.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    String address;
    String role;
}
