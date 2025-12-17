package com.marketplace.marketplaceorderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCreateRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;
}
