package com.marketplace.marketplaceorderservice.service;

import com.marketplace.grpc.ProductGrpcServiceGrpc;
import com.marketplace.grpc.ProductRequest;
import com.marketplace.grpc.ProductResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class ProductClientService {

    @GrpcClient("product-service")
    private ProductGrpcServiceGrpc.ProductGrpcServiceBlockingStub productStub;

    public ProductResponse checkProduct(String productId, int quantity) {
        try {
            ProductRequest request = ProductRequest.newBuilder()
                    .setProductId(productId)
                    .setQuantity(quantity)
                    .build();

            return productStub.validateProduct(request);
        } catch (Exception e) {
            System.out.println("gRPC xətası: " + e.getMessage());
            return null;
        }
    }
}