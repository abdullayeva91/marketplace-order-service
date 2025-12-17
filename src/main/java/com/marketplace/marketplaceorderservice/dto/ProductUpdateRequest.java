package com.marketplace.marketplaceorderservice.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductUpdateRequest {
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
