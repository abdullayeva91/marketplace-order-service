package com.marketplace.marketplaceorderservice.mapper;

import com.marketplace.marketplaceorderservice.dto.OrderResponse;
import com.marketplace.marketplaceorderservice.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}
