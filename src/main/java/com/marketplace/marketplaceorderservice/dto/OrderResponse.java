package com.marketplace.marketplaceorderservice.dto;

import com.marketplace.marketplaceorderservice.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;

    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime orderDate;

}
