package com.marketplace.marketplaceorderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
}
