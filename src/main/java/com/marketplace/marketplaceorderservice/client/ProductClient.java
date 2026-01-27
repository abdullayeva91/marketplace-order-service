package com.marketplace.marketplaceorderservice.client;

import com.marketplace.marketplaceorderservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductResponse findProductById(@PathVariable("id") String id);
}

