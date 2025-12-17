package com.marketplace.marketplaceorderservice.client;

import com.marketplace.marketplaceorderservice.dto.ProductCreateRequest;
import com.marketplace.marketplaceorderservice.dto.ProductResponse;
import com.marketplace.marketplaceorderservice.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/products")
public interface ProductClient {
    @GetMapping
    ProductResponse findAllProducts();
    @GetMapping("/{id}")
    ProductResponse findProductById(@PathVariable String id);
    @PostMapping
    ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest productRequest);
    @PutMapping("/{id}")
    ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest product);
    @DeleteMapping("/{id}")
    ProductResponse deleteProduct(@PathVariable Long id);
}
