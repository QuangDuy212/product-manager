package com.quangduy.product_manager_for_arius.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
public class OrderRequest {
    @Min(value = 1, message = "INVALID_PRICE_ORDER")
    double totalPrice;
    String reciverName;
    String reciverAddress;
    @Pattern(regexp = "(^$|[0-9]{10})")
    String reciverPhone;
    String status;
    String userId;
}
